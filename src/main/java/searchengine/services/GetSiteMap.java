package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.SiteLink;
import searchengine.model.SiteEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;


@Slf4j
public class GetSiteMap extends RecursiveTask<SiteLink> {

    private static final Set<String> uniqueLinks = ConcurrentHashMap.newKeySet();

    private final SiteLink siteLink;

    private final SiteEntity site;

    private final PageService pageService;

    public GetSiteMap(PageService pageService, SiteEntity site, SiteLink siteLink) {
        this.site = site;
        this.siteLink = siteLink;
        this.pageService = pageService;
    }

    @Override
    protected SiteLink compute() {
        SiteLink sLink = getLinks(siteLink);
        List<GetSiteMap> taskList = new ArrayList<>();
        for (SiteLink l : sLink.getLinks()) {
            GetSiteMap task = new GetSiteMap(pageService, site, l);
            task.fork();
            taskList.add(task);
        }
        Set<SiteLink> resultLinks = new HashSet<>();
        for (GetSiteMap task : taskList) {
            resultLinks.add(task.join());
        }
        siteLink.setLinks(resultLinks);
        return siteLink;
    }

    private SiteLink getLinks(SiteLink link) {
        Document document = null;
        try {
            document = DocumentUtils.getDocument(link.getUrl());
            Thread.sleep(150);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Set<String> result = new HashSet<>();
        Elements elements = document.select("a[href]");
        for (Element element : elements) {
            String url = element.attr("abs:href");
            log.info(site.getUrl() + " - Нашли ссылку " + url);
            if (checkLinks(url)) {
                result.add(url);
                log.info(site.getUrl() + " - Добавили ссылку " + url);
                uniqueLinks.add(url);
            }
        }
        try{
            for (String pageLink : result) {
                pageService.indexPage(site, pageLink);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

        link.setLinks(
                result.stream().map(SiteLink::new).collect(Collectors.toSet())
        );
        return link;
    }

    private boolean checkLinks(String url) {
        return url.startsWith(site.getUrl())
                && !url.contains("#")
                && !uniqueLinks.contains(url)
                && !url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)");
    }

}
