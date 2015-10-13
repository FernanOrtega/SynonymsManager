package es.us.lsi.fogallego.lenguajedotcomdownloader;

import java.util.List;

public class SynonymSense {

    private String posTag;

    private List<Word> lstSynonyms;

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public List<Word> getLstSynonyms() {
        return lstSynonyms;
    }

    public void setLstSynonyms(List<Word> lstSynonyms) {
        this.lstSynonyms = lstSynonyms;
    }
}
