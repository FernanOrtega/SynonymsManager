package es.us.lsi.fogallego.lenguajedotcomdownloader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LenguajeDotComDownloader {

    protected static final int TIMEOUT = 30000;
    protected static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    protected static final String BASE_URL = "http://lenguaje.com/cgi-bin/Thesauro.exe?edition_field=";

    public static WordResults download(String word) throws IOException {

        Word mainWord = new Word(word,0);
        List<SynonymSense> lstSynonymSense = new ArrayList<>();
        List<Trio<String,String,String>> lstAntonyms = new ArrayList<>();

        Document doc = Jsoup.connect(BASE_URL+word).userAgent(USER_AGENT).timeout(TIMEOUT).get();

        Elements elLemma = doc.select(".Lemma");

        if (elLemma.size() > 0) {

            List<Node> lstNode = elLemma.parents().get(0).childNodes();
            String currentTag = null;

            for (int i=1;i<lstNode.size()-3;i++) {

                Node node = lstNode.get(i);

                if (node instanceof TextNode && !((TextNode) node).text().equals("")) {
                    // Lemma tag
                    currentTag = ((TextNode) node).getWholeText().replace(" - ","");
                } else {

                    String classNode = node.attr("class");

                    switch (classNode) {
                        case "Synonyms":
                            List<Node> lstNodeSense = node.childNodes();
                            for (Node sense : lstNodeSense) {

                                String synonymsStrList = StringEscapeUtils.unescapeHtml4(sense.outerHtml().replaceAll("<li>|</li>",""));
//                                System.out.println(currentTag+", "+mainWord.getLemma()+", "+synonymsStrList);

                                String[] synonyms = synonymsStrList.split(", ");
                                List<Word> lstWord = new ArrayList<>();
                                lstWord.add(mainWord);
                                for (String synonym : synonyms) {
                                    Word synonymWord = new Word(synonym, 1);
                                    lstWord.add(synonymWord);
                                }

                                SynonymSense synonymSense = new SynonymSense();
                                synonymSense.setPosTag(currentTag);
                                synonymSense.setLstSynonyms(lstWord);
                                lstSynonymSense.add(synonymSense);
                            }
                            break;
                        case "Antonyms":
                            lstNodeSense = node.childNodes();
                            for (Node sense : lstNodeSense) {
                                String antonymsStrList = sense.outerHtml().replaceAll("<li>|</li>","");
                                String[] antonyms = antonymsStrList.split(", ");

                                for (String antonym : antonyms) {
                                    lstAntonyms.add(new Trio<>(currentTag, word, antonym));
                                }
//                                System.out.println(currentTag+", "+mainWord.getLemma()+", "+antonymsStrList);
                            }
                    }
                }
            }

        }

        return new WordResults(mainWord,lstSynonymSense,lstAntonyms);
    }

}
