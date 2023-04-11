package searchengine.dto.statistics;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult extends SearchTotalResult{

    protected int count;
    private List<DataResult> data;

    public SearchResult() {
        this.result=true;
    }

}
