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
@Table(name="pages", indexes=@Index(name="path_index", columnList="path"))
public class Page {
     @Id
     @GeneratedValue(strategy = GenerationType.AUTO)
     int id;

     @ManyToOne(cascade = CascadeType.DETACH)
     @JoinColumn (name="site_id", nullable = false)
     private SiteEntity site;

     @Column(columnDefinition = "VARCHAR(160)", nullable = false)
     private String path;

     @Column(nullable = false)
     private int code;

     @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
     private String content;


}

