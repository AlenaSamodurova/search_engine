package searchengine.repository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteEntity;
import searchengine.model.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<SiteEntity, Integer> {


    List<SiteEntity> findAll();

    List<SiteEntity> findAllByStatus (Status status);

    void deleteByUrl(String url);

    boolean existsByUrl(String url);

    Optional<SiteEntity> findByUrl(String url);

}
