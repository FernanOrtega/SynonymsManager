package es.us.lsi.fogallego.lenguajedotcomdownloader;

import java.util.List;

public class WordResults {

    private Word word;

    private List<SynonymSense> lstSynonymSense;

    private List<Trio<String,String,String>> lstAntonyms;

    public WordResults() {
        super();
    }

    public WordResults(Word word, List<SynonymSense> lstSynonymSense, List<Trio<String, String, String>> lstAntonyms) {
        this.word = word;
        this.lstSynonymSense = lstSynonymSense;
        this.lstAntonyms = lstAntonyms;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public List<SynonymSense> getLstSynonymSense() {
        return lstSynonymSense;
    }

    public void setLstSynonymSense(List<SynonymSense> lstSynonymSense) {
        this.lstSynonymSense = lstSynonymSense;
    }

    public List<Trio<String, String, String>> getLstAntonyms() {
        return lstAntonyms;
    }

    public void setLstAntonyms(List<Trio<String, String, String>> lstAntonyms) {
        this.lstAntonyms = lstAntonyms;
    }
}

