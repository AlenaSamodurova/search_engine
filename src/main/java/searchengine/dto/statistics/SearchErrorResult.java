package searchengine.dto.statistics;

import lombok.Data;

@Data
public class SearchErrorResult extends SearchTotalResult{

    private String error="Задан пустой поисковый запрос";

    public SearchErrorResult() {
        this.result=false;
    }

}
