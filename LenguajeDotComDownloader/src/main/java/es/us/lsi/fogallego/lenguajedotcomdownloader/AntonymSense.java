package es.us.lsi.fogallego.lenguajedotcomdownloader;

public class AntonymSense {

    private String posTag;
    private String lemma;
    private String antonym;

    public AntonymSense() {
        super();
    }

    public AntonymSense(String posTag, String lemma, String antonym) {
        this.posTag = posTag;
        this.lemma = lemma;
        this.antonym = antonym;
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getAntonym() {
        return antonym;
    }

    public void setAntonym(String antonym) {
        this.antonym = antonym;
    }
}
