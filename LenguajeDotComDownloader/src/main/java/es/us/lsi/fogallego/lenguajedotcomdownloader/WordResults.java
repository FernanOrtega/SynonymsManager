package es.us.lsi.fogallego.lenguajedotcomdownloader;

import java.util.List;

public class WordResults {

    private String word;

    private List<SynonymSense> lstSynonymSense;

    private List<AntonymSense> lstAntonyms;

    public WordResults() {
        super();
    }

    public WordResults(String word, List<SynonymSense> lstSynonymSense, List<AntonymSense> lstAntonyms) {
        this.word = word;
        this.lstSynonymSense = lstSynonymSense;
        this.lstAntonyms = lstAntonyms;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<SynonymSense> getLstSynonymSense() {
        return lstSynonymSense;
    }

    public void setLstSynonymSense(List<SynonymSense> lstSynonymSense) {
        this.lstSynonymSense = lstSynonymSense;
    }

    public List<AntonymSense> getLstAntonyms() {
        return lstAntonyms;
    }

    public void setLstAntonyms(List<AntonymSense> lstAntonyms) {
        this.lstAntonyms = lstAntonyms;
    }
}

