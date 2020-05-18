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
import eu.hansolo.evt.EvtType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


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
