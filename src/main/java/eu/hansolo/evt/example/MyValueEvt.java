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

import eu.hansolo.evt.EvtPriority;
import eu.hansolo.evt.EvtType;


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
