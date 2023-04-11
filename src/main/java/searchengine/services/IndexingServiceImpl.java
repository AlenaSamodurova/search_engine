package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteLink;
import searchengine.config.SitesList;
import searchengine.dto.statistics.IndexingErrorResult;
import searchengine.dto.statistics.IndexingResult;
import searchengine.model.SiteEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private static final int THREADS = 3;

    private final Map<String, ReentrantLock> lockers = new ConcurrentHashMap<>();
    private final List<ForkJoinPool> pools = new CopyOnWriteArrayList<>();
    private final List<CompletableFuture> completableFutures = new CopyOnWriteArrayList<>();
    private final AtomicBoolean isWorking = new AtomicBoolean(false);

    private final PageService pageService;
    private final SitesList sitesList;
    private final SiteService siteService;

    @Override
    public IndexingResult startIndexing() {
        if (!isWorking.compareAndSet(false, true)) {
            return indexingErrorResult("Индексация уже запущена");
        }

        clearData();
        List<SiteEntity> sites = sitesList.getSites().stream()
                .map(site -> siteService.getOrCreate(site.getUrl())).toList();

        pools.clear();
        completableFutures.clear();

        completableFutures.addAll(
                sites.stream()
                        .map(this::siteIndexingRunnable)
                        .map(CompletableFuture::runAsync).toList()
        );

        completableFutures.forEach(CompletableFuture::join);
        isWorking.set(false);

        return new IndexingResult();
    }

    @Override
    public IndexingResult stopIndexing() {
        if (!isWorking.get()) {
            return indexingErrorResult("Индексация не запущена");
        }
        pools.forEach(ForkJoinPool::shutdownNow);
        for (CompletableFuture completableFuture : completableFutures) {
            completableFuture.cancel(true);
        }
        siteService.stopIndexingAll();
        pools.clear();
        completableFutures.clear();
        isWorking.set(false);
        return new IndexingResult();
    }

    private Runnable siteIndexingRunnable(SiteEntity site) {
        return () -> {
            ReentrantLock locker = getLock(site.getUrl());
            locker.lock();
            try {
                siteService.indexing(site);
                ForkJoinPool forkJoinPool = new ForkJoinPool(THREADS);
                pools.add(forkJoinPool);
                GetSiteMap siteParsing = new GetSiteMap(pageService, site, new SiteLink(site.getUrl()));
                forkJoinPool.execute(siteParsing);
                siteParsing.join();
                siteService.successIndexing(site);
            } catch (CancellationException ex) {
                siteService.failedIndexing(site, "Ошибка индексации: " + ex.getMessage());
            } finally {
                locker.unlock();
            }
        };
    }

    @Override
    public IndexingResult indexPage(String link) throws InterruptedException {
        if (!isWorking.compareAndSet(false, true)) {
            return indexingErrorResult("Индексация уже запущена");
        }
        SiteEntity site = siteService.getOrCreate(link);
        ReentrantLock locker = getLock(site.getUrl());
        try {
            locker.tryLock(10, TimeUnit.MINUTES);
            siteService.indexing(site);
            pageService.indexPage(site, link);
            siteService.successIndexing(site);
            IndexingResult indexingResult = new IndexingResult();
            indexingResult.setResult(true);
            return indexingResult;
        } catch (Exception ex) {
            siteService.failedIndexing(site, ex.getMessage());
            return indexingErrorResult(ex.getMessage());
        } finally {
            locker.unlock();
            isWorking.set(false);
        }
    }

    private static IndexingErrorResult indexingErrorResult(String message) {
        IndexingErrorResult indexingErrorResult = new IndexingErrorResult();
        indexingErrorResult.setError(message);
        return indexingErrorResult;
    }

    private ReentrantLock getLock(String domain) {
        if (!lockers.containsKey(domain)) {
            lockers.put(domain, new ReentrantLock());
        }
        return lockers.get(domain);
    }

    private void clearData() {
        pageService.deleteAll();
        siteService.deleteAll();
    }
}
