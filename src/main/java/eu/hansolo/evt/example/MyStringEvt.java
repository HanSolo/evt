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


public class MyStringEvt extends MyEvt {
    public  static final EvtType<MyStringEvt> ANY           = new EvtType<>(MyEvt.ANY, "STRING");
    public  static final EvtType<MyStringEvt> STRING_TYPE_1 = new EvtType<>(MyStringEvt.ANY, "STRING_TYPE_1");
    public  static final EvtType<MyStringEvt> STRING_TYPE_2 = new EvtType<>(MyStringEvt.ANY, "STRING_TYPE_2");

    private              String                   string;


    public MyStringEvt(final Object source, final EvtType<? extends MyStringEvt> evtType, final String string) {
        this(source, evtType, string, EvtPriority.NORMAL);
    }
    public MyStringEvt(final Object source, final EvtType<? extends MyStringEvt> evtType, final String string, final EvtPriority priority) {
        super(source, evtType, priority);
        this.string = string;
    }


    public EvtType<? extends MyStringEvt> getEvtType() {
        return (EvtType<? extends MyStringEvt>) super.getEvtType();
    }

    public String getString() { return string; }
}
