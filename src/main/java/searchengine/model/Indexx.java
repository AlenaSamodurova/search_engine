package searchengine.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="indexx")
public class Indexx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn (name="page_id", nullable = false)
    private Page page;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn (name="lemma_id", nullable = false)
    private Lemma lemma;

    @Column(name="lemma_rank", nullable = false)
    private float rank;

    public static Indexx newIndexx(Page page, Lemma lemma, Integer i) {
        return Indexx.builder()
                .page(page)
                .lemma(lemma)
                .rank(i.floatValue())
                .build();
    }

}
