package searchengine.model;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import searchengine.config.Site;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="lemma")
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn (name="site_id", nullable = false)
    private SiteEntity siteEntity;

    @Column(nullable = false)
    private int frequency;

    public static Lemma newLemma(SiteEntity siteEntity, String lemma) {
        return Lemma.builder()
                .siteEntity(siteEntity)
                .lemma(lemma)
                .build();
    }

}
