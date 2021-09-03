## EVT - Custom Java Events with priority <a href="https://foojay.io/today/works-with-openjdk"><img align="right" src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png" width="100"></a>
<br>

This projects simply offers custom Java events that supports priorities if you like.

One can create a custom event as shown below

```Java
public class MyEvt extends Evt {

    public static final EvtType<MyEvt> ANY = new EvtType<>(Evt.ANY, "MY_EVENT");

    public MyEvt(final EvtType<? extends MyEvt> evtType) {
        super(evtType);
    }
    public MyEvt(final Object source, final EvtType<? extends MyEvt> evtType) {
        super(source, evtType);
    }
    public MyEvt(final Object source, final EvtType<? extends MyEvt> evtType, final EvtPriority priority) {
        super(source, evtType, priority);
    }


    public EvtType<? extends MyEvt> getEvtType() {
        return (EvtType<? extends MyEvt>) super.getEvtType();
    }
}
```

Now with your event in place you can create specific events extending MyEvt as follows:

```Java
public class MyValueEvt extends MyEvt {
    public  static final EvtType<MyValueEvt> ANY          = new EvtType<>(MyEvt.ANY, "VALUE");
    public  static final EvtType<MyValueEvt> VALUE_TYPE_1 = new EvtType<>(MyValueEvt.ANY, "VALUE_TYPE_1");
    public  static final EvtType<MyValueEvt> VALUE_TYPE_2 = new EvtType<>(MyValueEvt.ANY, "VALUE_TYPE_2");
    private              double              value;


    // ******************** Constructors **************************************
    public MyValueEvt(final Object source, final EvtType<? extends MyValueEvt> evtType, final double value) {
        this(source, evtType, value, EvtPriority.NORMAL);
    }
    public MyValueEvt(final Object source, final EvtType<? extends MyValueEvt> evtType, final double value, final EvtPriority priority) {
        super(source, evtType, priority);
        this.value = value;
    }


    // ******************** Methods *******************************************
    public EvtType<? extends MyValueEvt> getEvtType() {
        return (EvtType<? extends MyValueEvt>) super.getEvtType();
    }

    public double getValue() { return value; }
} 
```

As an example we use a control with a value property as follows:

```Java
public interface MyControl {
    void fireEvt(final Evt evt);

    void dispose();
}

public class MyClass implements MyControl {
    private final Map<String, List<EvtObserver>> observers = new ConcurrentHashMap<>();
    private double                               value;
    private String string;


    // ******************** Constructors **************************************
    public MyClass() {
        this.value  = 0;
        this.string = "";
    }


    // ******************** Methods *******************************************
    public double getValue() { return value; }
    public void setValue(final double value) {
        this.value = value;
        fireEvt(new MyValueEvt(MyClass.this, MyValueEvt.VALUE_TYPE_1, value));
    }

    public String getString() { return string; }
    public void setString(final String string) {
        this.string = string;
        fireEvt(new MyStringEvt(MyClass.this, MyStringEvt.STRING_TYPE_1, string));
    }


    // ******************** EventHandling *************************************
    public void setOnEvt(final EvtType<? extends Evt> type, final EvtObserver observer) {
        if (!observers.keySet().contains(type.getName())) { observers.put(type.getName(), new CopyOnWriteArrayList<>()); }
        if (!observers.get(type.getName()).contains(observer)) { observers.get(type.getName()).add(observer); }
    }
    public void removeOnEvt(final EvtType<? extends Evt> type, final EvtObserver observer) {
        if (!observers.keySet().contains(type.getName())) { return; }
        if (observers.get(type.getName()).contains(observer)) { observers.get(type.getName()).remove(observer); }
    }
    public void removeAllObservers() { observers.entrySet().forEach(entry -> entry.getValue().clear()); }

    @Override public void fireEvt(final Evt evt) {
        final EvtType<? extends Evt> type = evt.getEvtType();
        if (observers.containsKey(type.getName())) {
            observers.get(type.getName()).forEach(observer -> observer.handle(evt));
        }
    }

    @Override public void dispose() {

    }
}
```

Now if we would like to listen to those events we can create an instance of it and set a value as follows:

```Java
public class Main {
    private MyClass                      myClass;
    private MyOtherClass             myOtherClass;
    private EvtObserver<MyValueEvt>  valueEventEvtObserver;
    private EvtObserver<MyStringEvt> stringEventEvtObserver;


    public Main() {
        myClass      = new MyClass();        

        valueEventEvtObserver = e -> System.out.println("New Value Event with priority " + e.getPriority().name() + " and value: " + e.getValue() + " from class " + e.getSource().getClass());
        myClass.setOnEvt(MyValueEvt.VALUE_TYPE_1, valueEventEvtObserver);        

        stringEventEvtObserver = e -> System.out.println("New String Event with priority " + e.getPriority().name() + " and string: " + e.getString() + " from class " + e.getSource().getClass());
        myClass.setOnEvt(MyStringEvt.STRING_TYPE_1, stringEventEvtObserver);        


        myClass.setValue(312);
        myClass.setString("Gerrit");
    }
    
    public static void main(String[] args) {
        new Main();
    }
}
```

In the example above we simply set the value and fire a MyValueEvt with priority NORMAL.


In case we would like to make use of the priorities we can create another control as follows:

```Java
public class MyOtherClass implements MyControl {
    private final Map<String, List<EvtObserver>> observers       = new ConcurrentHashMap<>();
    private final BlockingQueue<Evt>             evtQueue        = new PriorityBlockingQueue<>();
    private final Callable<Boolean>              consumerTask    = () -> {
        while (true) {
            final Evt evt;
            try {
                evt = evtQueue.take();
                final EvtType<? extends Evt> type = evt.getEvtType();
                if (observers.containsKey(type.getName())) {
                    observers.get(type.getName()).forEach(observer -> observer.handle(evt));
                }
            } catch (InterruptedException ex) {
                return false;
            }
        }
    };
    private ScheduledExecutorService             consumerService = new ScheduledThreadPoolExecutor(1, runnable -> new Thread(runnable, "EventConsumerThread"));
    private double                               value;
    private String                               string;


    // ******************** Constructors **************************************
    public MyOtherClass() {
        this.value  = 0;
        this.string = "";

        consumerService.schedule(consumerTask, 0, TimeUnit.MILLISECONDS);
    }


    // ******************** Methods *******************************************
    public double getValue() { return value; }
    public void setValue(final double value) {
        this.value = value;
        // Fires events with Priority LOW which will be served last
        fireEvt(new MyValueEvt(MyOtherClass.this, MyValueEvt.VALUE_TYPE_2, value, EvtPriority.LOW));
    }

    public String getString() { return string; }
    public void setString(final String string) {
        this.string = string;
        // Fires events with Priority HIGH which will be served first
        fireEvt(new MyStringEvt(MyOtherClass.this, MyStringEvt.STRING_TYPE_2, string, EvtPriority.HIGH));
    }


    // ******************** EventHandling *************************************
    public void setOnEvent(final EvtType<? extends Evt> type, final EvtObserver observer) {
        if (!observers.keySet().contains(type.getName())) { observers.put(type.getName(), new CopyOnWriteArrayList<>()); }
        if (!observers.get(type.getName()).contains(observer)) { observers.get(type.getName()).add(observer); }
    }
    public void removeOnEvent(final EvtType<? extends Evt> type, final EvtObserver observer) {
        if (!observers.keySet().contains(type.getName())) { return; }
        if (observers.get(type.getName()).contains(observer)) { observers.get(type.getName()).remove(observer); }
    }
    public void removeAllObservers() { observers.entrySet().forEach(entry -> entry.getValue().clear()); }

    @Override public void fireEvt(final Evt evt) {
        evtQueue.add(evt);
    }

    @Override public void dispose() {
        consumerService.shutdown();
        try {
            if (!consumerService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                consumerService.shutdownNow();
            }
        } catch (InterruptedException e) {
            consumerService.shutdownNow();
        }
    }
}
```

In this class we put the events on a concurrent priority queue so that they will be ordered by priority.
Here we fire an HIGH priority event in case the string property was changed and a LOW priority event in case the value changed.

The Main class to observe the priority based events will look as follows:

```Java
public class Main {
    private MyClass                      myClass;
    private MyOtherClass             myOtherClass;
    private EvtObserver<MyValueEvt>  valueEventEvtObserver;
    private EvtObserver<MyStringEvt> stringEventEvtObserver;


    public Main() {
        myClass      = new MyClass();
        myOtherClass = new MyOtherClass();

        valueEventEvtObserver = e -> System.out.println("New Value Event with priority " + e.getPriority().name() + " and value: " + e.getValue() + " from class " + e.getSource().getClass());
        myClass.setOnEvt(MyValueEvt.VALUE_TYPE_1, valueEventEvtObserver);
        myOtherClass.setOnEvent(MyValueEvt.VALUE_TYPE_2, valueEventEvtObserver);

        stringEventEvtObserver = e -> System.out.println("New String Event with priority " + e.getPriority().name() + " and string: " + e.getString() + " from class " + e.getSource().getClass());
        myClass.setOnEvt(MyStringEvt.STRING_TYPE_1, stringEventEvtObserver);
        myOtherClass.setOnEvent(MyStringEvt.STRING_TYPE_2, stringEventEvtObserver);


        myClass.setValue(312);
        myClass.setString("Gerrit");

        Random rnd = new Random();
        for (int i = 0 ; i < 100 ; i++) {
            boolean callValue = rnd.nextBoolean();
            if (callValue) {
                myOtherClass.setValue(rnd.nextInt(100));
            } else {
                myOtherClass.setString("Neo " + rnd.nextInt(100));
            }
        }

        stop();
    }

    private void stop() {
        myClass.dispose();
        myOtherClass.dispose();
    }

    public static void main(String[] args) {
        new Main();
    }
}
```

In the output of the last example you will see that first all HIGH priority events will be handled followed by the LOW priority events.
