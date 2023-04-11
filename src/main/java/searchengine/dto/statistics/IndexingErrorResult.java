package searchengine.dto.statistics;

import lombok.Data;

@Data
public class IndexingErrorResult extends IndexingResult{
    private String error;

    public IndexingErrorResult() {
        this.result=false;
        error="Задан пустой поисковый запрос";
    }
}
