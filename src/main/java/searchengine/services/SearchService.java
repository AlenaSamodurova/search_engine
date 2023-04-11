package searchengine.services;

import searchengine.dto.statistics.SearchTotalResult;

public interface SearchService {
    public  SearchTotalResult searchLemma(String query, String site, int offset, int limit);
}
