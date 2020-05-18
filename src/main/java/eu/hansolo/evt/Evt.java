package eu.hansolo.evt;

public class Evt implements Comparable<Evt> {
    public    static final EvtType<Evt>           ANY = EvtType.ROOT;
    protected        final Object                 source;
    protected        final EvtType<? extends Evt> evtType;
    private          final EvtPriority            priority;


    // ******************** Constructors **************************************
    public Evt(final EvtType<? extends Evt> evtType) {
        this(null, evtType, EvtPriority.NORMAL);
    }
    public Evt(final Object source, final EvtType<? extends Evt> evtType) {
        this(source, evtType, EvtPriority.NORMAL);
    }
    public Evt(final Object source, final EvtType<? extends Evt> evtType, final EvtPriority priority) {
        this.source    = source;
        this.evtType = evtType;
        this.priority  = priority;
    }


    // ******************** Methods *******************************************
    public Object getSource() { return source; }

    public EvtType<? extends Evt> getEvtType() {  return evtType; }

    public EvtPriority getPriority() { return priority; }

    public int compareTo(final Evt evt) {
        return (evt.getPriority().getValue() - this.priority.getValue());
    }
}
