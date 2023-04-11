package searchengine.dto.statistics;

import lombok.Data;

@Data
public class DataResult {
    String site;
    String siteName;
    String url;
    String title;
    String snippet;
    Float relevance;

}
