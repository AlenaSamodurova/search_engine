package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.SiteEntity;
import searchengine.repository.PageRepository;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class PageService {

    private final LemmaService lemmaService;
    private final PageRepository pageRepository;

    public Page indexPage(SiteEntity site, String link) throws IOException {
        try {
            String path = getRelativePath(site, link);
            Page p = pageRepository.findByPathAndSite(path, site);
            if (p != null) {
                pageRepository.delete(p);
            }

            Document document = DocumentUtils.getDocument(link);
            String contentHtml = document.outerHtml();
            int code = document.connection().response().statusCode();
            Page page = new Page();
            page.setSite(site);
            page.setPath(path);
            page.setCode(code);
            page.setContent(contentHtml);
            page = pageRepository.save(page);
            lemmaService.saveLemmaAndIndex(page);
            return page;
        } catch (Exception ex) {
            log.error("Не смогли поиндексировать страницу {}", link, ex);
            throw ex;
        }
    }

    public void deleteAll() {
        lemmaService.deleteAll();
        pageRepository.deleteAll();
    }

    private String getRelativePath(SiteEntity site, String url) {
        return url.substring(site.getUrl().length());
    }

    public static boolean checkStatus(Page page) {
        int status = page.getCode();
        String stringStatus = Integer.toString(status);
        if (stringStatus.startsWith("4") || stringStatus.startsWith("5")) return false;
        return true;
    }
}
