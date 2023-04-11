package searchengine.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.SiteEntity;


import java.util.List;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {
    List<Page> findAll();
    void deleteByPath(String url);
    boolean existsByPath(String url);
    boolean existsBySite(SiteEntity site);

    Page findByPathAndSite(String path, SiteEntity siteEntity);

    Page findBySite(SiteEntity siteEntity);

    default boolean existsByPathAndEntity(String path, SiteEntity siteEntity) {
        if (existsBySite(siteEntity) && existsByPath(path)) return true;
        return false;
    };

    Integer countBySite(SiteEntity siteEntity);

    List<Page> findAllBySite(SiteEntity siteEntity);


}
