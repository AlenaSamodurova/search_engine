package searchengine.model;

import lombok.*;
import searchengine.config.Site;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="sites")
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name ="status_time", nullable = false)
    private LocalDateTime statusTime;

    @Column(name ="last_error")
    private String lastError;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    private String url;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    private String name;

    public static SiteEntity newSite(Site site){
        return SiteEntity.builder()
                .name(site.getName())
                .url(site.getUrl())
                .status(Status.INDEXING)
                .statusTime(LocalDateTime.now())
                .build();
    }
}
