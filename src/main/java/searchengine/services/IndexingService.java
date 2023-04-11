package searchengine.services;

import searchengine.dto.statistics.IndexingResult;

public interface IndexingService {
    IndexingResult startIndexing() throws InterruptedException;
    IndexingResult stopIndexing() throws InterruptedException;
    IndexingResult indexPage(String url) throws InterruptedException;
}
