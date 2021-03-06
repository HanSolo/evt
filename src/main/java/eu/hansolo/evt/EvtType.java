package eu.hansolo.evt;


import java.util.Objects;


public final class EvtType<T extends Evt> {
    public  static final EvtType<Evt>       ROOT = new EvtType<>("EVENT", null);
    private        final EvtType<? super T> superType;
    private        final String             name;


    // ******************** Constructors **************************************
    public EvtType(final EvtType<? super T> superType) {
        this(superType, null);
    }
    public EvtType(final String name) {
        this(ROOT, name);
    }
    public EvtType(final EvtType<? super T> superType, final String name) {
        if (null == superType) { throw new NullPointerException("Event super type must not be null (EvtType.name: " + name + ")"); }
        this.superType = superType;
        this.name      = name;
    }
    EvtType(final String name, final EvtType<? super T> superType) {
        this.superType = superType;
        this.name      = name;
    }


    // ******************** Methods *******************************************
    public final EvtType<? super T> getSuperType() { return superType; }

    public final String getName() { return name; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        EvtType<?> evtType = (EvtType<?>) o;
        return superType.equals(evtType.superType) && name.equals(evtType.name);
    }

    @Override public int hashCode() {
        return Objects.hash(superType, name);
    }

    @Override public String toString() {
        return (null != name) ? new StringBuilder().append("{")
                                                   .append("\"class\":\"").append(getClass().getName()).append("\",")
                                                   .append("\"name\":\"").append(getName()).append("\",")
                                                   .append("\"supertype\":\"").append(getSuperType().name).append("\",")
                                                   .append("}")
                                                   .toString() :
                                super.toString();
    }
}
