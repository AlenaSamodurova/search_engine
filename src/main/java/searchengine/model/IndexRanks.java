package searchengine.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class IndexRanks {
    private Page page;
    private Map<String, Float> ranks;
    private float absRank;
    private float relRank;
    private float max;

    public IndexRanks() {
        ranks = new HashMap<>();
    }

    public void setRanks(String word, Float rank) {
        ranks.put(word, rank);
    }

    public void setAbsRank() {
        ranks.forEach((key, value) -> {
            this.absRank += value;
        });

        if (this.absRank > max) {
            max = absRank;
        }
    }

    public void setRelRank() {
        relRank = max / absRank;
    }

}
