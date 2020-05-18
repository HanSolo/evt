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

import eu.hansolo.evt.EvtObserver;

import java.util.Random;


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
