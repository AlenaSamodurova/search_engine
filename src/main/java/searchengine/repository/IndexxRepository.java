package searchengine.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Indexx;
import searchengine.model.Lemma;
import searchengine.model.Page;

@Repository
public interface IndexxRepository extends CrudRepository<Indexx, Integer> {

    void deleteByPage(Page page);

    Indexx findByLemmaAndPage(Lemma lemma, Page page);
}
