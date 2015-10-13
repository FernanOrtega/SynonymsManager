package es.us.lsi.fogallego.lenguajedotcomdownloader;

public class Word {

    private String lemma;

    private int priority;

    public Word() {
        super();
    }

    public Word(String lemma, int priority) {
        this.lemma = lemma;
        this.priority = priority;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Word{" +
                "lemma='" + lemma + '\'' +
                ", priority=" + priority +
                '}';
    }
}
