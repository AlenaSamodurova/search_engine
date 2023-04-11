package searchengine.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.SiteEntity;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    boolean existsByLemma(String lemma);
    Lemma findByLemma(String lemma);
    Lemma findByLemmaAndSiteEntity(String lemma, SiteEntity site);
    public void deleteBySiteEntity (SiteEntity siteEntity);
    Integer countBySiteEntity(SiteEntity siteEntity);
}
