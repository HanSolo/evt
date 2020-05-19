package eu.hansolo.evt;

public enum EvtPriority {
    LOW(0), NORMAL(1), HIGH(2);

    private int value;


    // ******************** Constructor ***************************************
    EvtPriority(final int value) {
        this.value = value;
    }


    // ******************** Methods *******************************************
    public int getValue() { return value; }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("\"class\":\"").append(getClass().getName()).append("\",")
                                  .append("\"value\":").append(getValue())
                                  .append("}")
                                  .toString();
    }
}
