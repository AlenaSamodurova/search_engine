package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import searchengine.dto.statistics.SearchErrorResult;
import searchengine.dto.statistics.SearchTotalResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final SearchService searchService;
    private Object HttpStatus;

    public ApiController(StatisticsService statisticsService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
            return ResponseEntity.ok(statisticsService.getStatistics());
        }


    @GetMapping("/search")
    public SearchTotalResult search(@RequestParam String query,
                                    @RequestParam(required = false) String site,
                                    @RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue ="3") int limit) {
        if (!checkSearch(offset, limit))
            throw new IllegalArgumentException("Указаны неверные параметры");
        if (query==null || query.isBlank()) {
            return new SearchErrorResult();
        }
        return searchService.searchLemma(query, site, offset, limit);
    }

    private boolean checkSearch(int offset, int limit) {
            return (offset<0 || limit<0 ) ? false : true;
    }
}
