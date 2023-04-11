package searchengine.dto.statistics;

import lombok.Data;

@Data
public class IndexingResult {
    protected boolean result;

    public IndexingResult() {
        this.result = true;
    }
}
