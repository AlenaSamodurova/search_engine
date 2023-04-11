package searchengine.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteLink {
    private Set<SiteLink> links = new HashSet<>();
    private String url;

    private int code;

    public SiteLink(String url) {
        this.url = url;
    }

}
