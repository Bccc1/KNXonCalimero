package com.calimero.knx.knxvc.core;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David on 26.11.2014.
 *
 * Das hier ist gar keine KNXActionFactory :P
 *
 * Sondern hier haue ich die Liste von vorkonfigurierten KNXActions raus.
 *
 */
public class KnxActionFactory {

    public static int actionIdCounter = 0;

    private static ArrayList<KnxAction> actionList;

    public static ArrayList<KnxAction> getKNXActionsAsList(){
        if(actionList == null) {
            //TODO Echte Objekte des Aufbaus verwenden und Gruppenadresse und Telegramminhalt setzen.
            ArrayList<KnxAction> kal = new ArrayList<KnxAction>();

            KnxAction lichtAnschalten = newAction("Licht anschalten");
            lichtAnschalten.groupAddress = "1/5/1";
            lichtAnschalten.data = "1";
            kal.add(lichtAnschalten);
            kal.add(dummyAction("Licht dimmen"));
            KnxAction lichtAusschalten = newAction("Licht ausschalten");
            lichtAusschalten.groupAddress = "1/5/1";
            lichtAusschalten.data = "0";
            kal.add(lichtAusschalten);
            kal.add(dummyAction("Jalousien hochfahren"));
            kal.add(dummyAction("Jalousien herunterfahren"));
            kal.add(dummyAction("Kamin entfachen"));
            kal.add(dummyAction("Kamin löschen"));
            actionList = kal;
        }
        return actionList;
    }

    private static KnxAction dummyAction(String name){
        KnxAction ac = newAction(name);
        ac.setData("1");
        ac.setGroupAddress("0/0/0");
        return ac;
    }

    private static KnxAction newAction(String name){
        KnxAction ac = newAction();
        ac.setName(name);
        return ac;
    }

    private static KnxAction newAction(){
        KnxAction ac = new KnxAction();
        ac.setId(actionIdCounter++);
        return ac;
    }

    public static HashMap<String, KnxAction> getKNXActionsAsMap(){
        HashMap<String, KnxAction> kal = new HashMap<String, KnxAction>();
        for(KnxAction action : getKNXActionsAsList()){
            kal.put(action.name,action);
        }
        return kal;
    }
}
