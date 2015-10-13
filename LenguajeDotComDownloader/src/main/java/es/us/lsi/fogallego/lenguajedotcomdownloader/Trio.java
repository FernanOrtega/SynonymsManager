package es.us.lsi.fogallego.lenguajedotcomdownloader;

public class Trio<A, B, C> implements Comparable<Trio<A, B, C>> {
    private final A	mFirst;
    private final B	mSecond;
    private final C	mThird;

    public Trio(A pFirst, B pSecond, C pThird) {
        super();
        mFirst = pFirst;
        mSecond = pSecond;
        mThird = pThird;
    }

    public int hashCode() {
        final int PRIME = 31;
        int hashFirst = mFirst != null ? mFirst.hashCode() : 0;
        int hashSecond = mSecond != null ? mSecond.hashCode() : 0;
        int hashThird = mThird != null ? mThird.hashCode() : 0;
        return PRIME * (hashFirst + hashSecond + hashThird);
    }

    public boolean equals(Object pOther) {
        if (pOther instanceof Trio<?, ?, ?>) {
            @SuppressWarnings("unchecked")
            Trio<?, ?, ?> otherTrio = (Trio<A, B, C>) pOther;
            return (((mFirst == null && otherTrio.mFirst == null) || (mFirst != null && otherTrio.mFirst != null && mFirst.equals(otherTrio.mFirst)))
                    && ((mSecond == null && otherTrio.mSecond == null) || (mSecond != null && otherTrio.mSecond != null && mSecond.equals(otherTrio.mSecond)))
                    && ((mThird == null && otherTrio.mThird == null) || (mThird != null && otherTrio.mThird != null && mThird.equals(otherTrio.mThird))));
        }
        return false;
    }

    public String toString() {
        return "[" + mFirst + ", " + mSecond + ", " + mThird + "]";
    }

    public A getFirst() {
        return mFirst;
    }

    public B getSecond() {
        return mSecond;
    }

    public C getThird() {
        return mThird;
    }

    @SuppressWarnings("unchecked")
    public int compareTo(Trio<A, B, C> pOther) {
        int result = 0;
        try {
            result = ((Comparable<A>) mFirst).compareTo(pOther.getFirst());
            if (result == 0) {
                result = ((Comparable<B>) mSecond).compareTo(pOther.getSecond());
            }
            if (result == 0) {
                result = ((Comparable<C>) mThird).compareTo(pOther.getThird());
            }
        } catch (ClassCastException e) {
            result = 0;
        }
        return result;
    }

}
