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
import eu.hansolo.evt.EvtPriority;
import eu.hansolo.evt.EvtType;


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
