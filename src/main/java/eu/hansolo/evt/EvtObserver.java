package eu.hansolo.evt;

@FunctionalInterface
public interface EvtObserver<T extends Evt> {
    void handle(T event);
}
