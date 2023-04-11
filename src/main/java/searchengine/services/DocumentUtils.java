package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DocumentUtils {

    public static Document getDocument(String url) throws IOException {
        Document document = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true).
                userAgent("Chrome/81.0.4044.138.").get();
        return document;
    }

}
