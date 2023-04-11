package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Indexx;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteEntity;
import searchengine.repository.IndexxRepository;
import searchengine.repository.LemmaRepository;

import java.io.IOException;
import java.util.Map;

@Service
public class LemmaService {

    @Autowired
    LemmaRepository lemmaRepository;

    @Autowired
    IndexxRepository indexxRepository;

    GetLemma getLemma = GetLemma.getInstance();

    public LemmaService() throws IOException {
    }

    public synchronized void saveLemmaAndIndex(Page page) {
        String content = Jsoup.clean(page.getContent(), Safelist.simpleText());
        if (PageService.checkStatus(page)) {
            Map<String, Integer> lemmas = getLemma.collectLemmas(content);
            lemmas.entrySet().stream().forEach(lemma ->
            {
                if (!lemmaRepository.existsByLemma(lemma.getKey())) {
                    SiteEntity siteEntity = page.getSite();
                    lemmaRepository.save(Lemma.newLemma(siteEntity, lemma.getKey()));
                }
                Lemma l = lemmaRepository.findByLemma(lemma.getKey());
                l.setFrequency(l.getFrequency() + 1);
                lemmaRepository.save(l);
                indexxRepository.save(Indexx.newIndexx(page, l, lemma.getValue()));
            });
        }
    }

    public void deleteAll(){
        indexxRepository.deleteAll();
        lemmaRepository.deleteAll();
    }
}
