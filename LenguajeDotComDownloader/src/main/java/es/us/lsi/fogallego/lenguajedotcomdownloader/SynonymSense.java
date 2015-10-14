package es.us.lsi.fogallego.lenguajedotcomdownloader;

import java.util.List;

public class SynonymSense {

    private String posTag;

    private String lemma;

    private List<String> lstSynonyms;

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public List<String> getLstSynonyms() {
        return lstSynonyms;
    }

    public void setLstSynonyms(List<String> lstSynonyms) {
        this.lstSynonyms = lstSynonyms;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }
}
