package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.dto.statistics.DataResult;
import searchengine.dto.statistics.SearchErrorResult;
import searchengine.dto.statistics.SearchResult;
import searchengine.dto.statistics.SearchTotalResult;
import searchengine.model.*;
import searchengine.repository.IndexxRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchService {

    GetLemma getLemma = GetLemma.getInstance();

    public SearchServiceImpl() throws IOException {
    }



    @Autowired
    SiteRepository siteRepository;
    @Autowired
    PageRepository pageRepository;
    @Autowired
    IndexxRepository indexRepository;
    @Autowired
    LemmaRepository lemmaRepository;

    @Override
    public SearchTotalResult searchLemma(String query, String site, int offset, int limit) {

        if (query.isBlank()) {
            return new SearchErrorResult();
        }
        SearchResult result = new SearchResult();
        List<DataResult> dataResultList = new ArrayList<>();



        if (site == null) {
            siteRepository.findAll().forEach(s -> {
                dataResultList .addAll(getSearchesBySite(s, query));
            });
        } else {
            Optional<SiteEntity> s = siteRepository.findByUrl(site);
            if (s.isPresent()) {
                dataResultList.addAll(getSearchesBySite(s.get(), query));
            }
        }
        setData(result, dataResultList, offset, limit);
        result.setCount(dataResultList.size());
        return result;


    }


    private List<DataResult> getSearchesBySite(SiteEntity site, String query) {
        List<Page> pages = pageRepository.findAllBySite(site);
        return addSearchQuery(query, site, pages);
    }

    private List<DataResult> addSearchQuery(String query, SiteEntity site, List<Page> pages) {


        List<Lemma> sortedLemmas = Arrays.stream(query.split(" ")).map(l ->
                        lemmaRepository.findByLemmaAndSiteEntity(l, site)
                ).sorted(Comparator.comparing(Lemma::getFrequency))
                .collect(Collectors.toList());

        List<IndexRanks> indexRanks = getIndexRanks(sortedLemmas, pages);
        return getSearchResults(indexRanks, sortedLemmas, site);
    }

    private List<IndexRanks> getIndexRanks(List<Lemma> lemmas, List<Page> pages) {
        List<IndexRanks> indexRanks = new ArrayList<>();

        lemmas.forEach(lemma -> {
            int count = 0;
            while (pages.size() > count) {
                Indexx index = indexRepository.findByLemmaAndPage(lemma, pages.get(count));
                if (index == null) {
                    pages.remove(count);
                } else {
                    IndexRanks indexRank = new IndexRanks();

                    indexRank.setPage(pages.get(count));
                    indexRank.setRanks(lemma.getLemma(), index.getRank());
                    indexRank.setAbsRank();
                    indexRanks.add(indexRank);
                    count++;
                }
                }
        });
        indexRanks.forEach(IndexRanks::setRelRank);
        return indexRanks;
    }


    private List<DataResult> getSearchResults(List<IndexRanks> indexRanks, List<Lemma> lemmas, SiteEntity site) {
        List<DataResult> dataResultList =
        indexRanks.stream().sorted(Comparator.comparing((IndexRanks::getRelRank), Comparator.reverseOrder()))
                .map(ir->{
                    DataResult dataResult=new DataResult();
                    dataResult.setSite(ir.getPage().getSite().getUrl());
                    dataResult.setSiteName(ir.getPage().getSite().getName());
                    dataResult.setUrl(ir.getPage().getPath());
                    dataResult.setTitle(getTitle(ir.getPage().getContent()));
                    dataResult.setSnippet(getSnippet(ir.getPage().getContent(), lemmas));
                    return dataResult;
                }).collect(Collectors.toList());
        return dataResultList;
    }




    private String getTitle(String content) {
        Document doc = Jsoup.parse(content);
        String title=doc.title();
        return title;
    }

    private String getSnippet(String content, List<Lemma> lemmas) {
        String str = content.toLowerCase();
                    int count = 0;
                    for (Lemma lem : lemmas.stream().toList()) {
                        String l = lem.getLemma();
                        if (str.contains(l)) {
                            count++;
                            str = str.replaceAll("(?i)" + l,
                                    "<b>" + l + "</b>");
                        } else {
                            lemmas.remove(lem);
                        }
                    }
                    return str;
    }

    private void setData(SearchResult result, List<DataResult> dataResultList, int offset, int limit ) {
        List<DataResult> dataResultListlimit = new ArrayList<>();
        if (offset != 0) {
            offset = 0;
        }
        if (limit != 0) {
            for (int i = offset; i < limit; i++) {
                dataResultListlimit.add(dataResultList.get(i));
            }
            result.setData(dataResultListlimit);
        } else {
            result.setData(dataResultList);
        }
    }

}