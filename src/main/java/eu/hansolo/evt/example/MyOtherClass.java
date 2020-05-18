/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.evt.example;

import eu.hansolo.evt.Evt;
import eu.hansolo.evt.EvtObserver;
import eu.hansolo.evt.EvtPriority;
import eu.hansolo.evt.EvtType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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
