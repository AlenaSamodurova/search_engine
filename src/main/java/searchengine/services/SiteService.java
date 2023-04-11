package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.repository.SiteRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SiteService {

    private final SitesList sitesList;
    private final SiteRepository siteRepository;

    public synchronized SiteEntity getOrCreate(String link) {
        String domain = getDomain(link);
        Site site = findSiteInList(domain).orElseThrow(
                () -> new RuntimeException("Данная страница находится за пределами сайтов, " +
                        "указанных в конфигурационном файле")
        );
        return siteRepository.findByUrl(site.getUrl())
                .orElse(createSite(site));
    }

    public SiteEntity get(String link) {
        String domain = getDomain(link);
        Site site = findSiteInList(domain).orElseThrow(
                () -> new RuntimeException("Данная страница находится за пределами сайтов, " +
                        "указанных в конфигурационном файле")
        );
        return siteRepository.findByUrl(site.getUrl()).get();
    }

    public void deleteAll() {
        siteRepository.deleteAll();
    }

    public void indexing(SiteEntity site) {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
    }

    public void successIndexing(SiteEntity site) {
        site.setStatus(Status.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
    }

    public void failedIndexing(SiteEntity site, String message) {
        site.setStatus(Status.FAILED);
        site.setLastError(message);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
    }

    public void stopIndexingAll() {
        siteRepository.findAll().forEach(site -> {
            site.setStatus(Status.FAILED);
            site.setLastError("Индексация остановлена");
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
        });
    }

    private SiteEntity createSite(Site site) {
        return siteRepository.save(SiteEntity.newSite(site));
    }

    private Optional<Site> findSiteInList(String domain) {
        return sitesList.getSites().stream().filter(site -> site.getUrl().contains(domain)).findFirst();
    }

    private String getDomain(String link) {
        try {
            return new URL(link).getHost().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)", "");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Не смогли получить домен для ссылки " + link);
        }
    }
}
