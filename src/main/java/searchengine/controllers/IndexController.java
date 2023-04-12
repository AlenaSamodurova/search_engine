package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.IndexingResult;
import searchengine.services.IndexingService;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IndexController {

    @Autowired
    private IndexingService indexingService;

    @GetMapping("/startIndexing")
    public IndexingResult startIndexing() throws InterruptedException {
        return indexingService.startIndexing();
    }

    @GetMapping("/stopIndexing")
    public IndexingResult stopIndexing() throws InterruptedException {
        return indexingService.stopIndexing();
    }

    @PostMapping("/indexPage")
    public IndexingResult indexPage(@RequestParam String url) throws InterruptedException {
        return indexingService.indexPage(url);
    }
}
