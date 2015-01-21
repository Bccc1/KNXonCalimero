package com.calimero.knx.knxvc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

            kal.add(newAction("Licht Oben Ein","1/5/10","1"));
            kal.add(newAction("Licht Oben Aus","1/5/10","0"));
            kal.add(newAction("Licht Mitte/Unten Ein","1/5/11","1"));
            kal.add(newAction("Licht Mitte/Unten Aus","1/5/11","0"));

            kal.add(newAction("Jalousie-Langezeitfahren hoch","1/1/5","1"));
            kal.add(newAction("Jalousie-Langezeitfahren runter","1/1/5","0"));
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

    private static KnxAction newAction(String name, String groupAddress, String data){
        KnxAction ac = newAction();
        ac.setName(name);
        ac.setGroupAddress(groupAddress);
        ac.setData(data);
        return ac;
    }

    private static KnxAction newAction(){
        KnxAction ac = new KnxAction();
        ac.setId(actionIdCounter++);
        return ac;
    }

    public static HashMap<String, KnxAction> getKNXActionsAsMap(){
        return convertKnxActionListTosMap(getKNXActionsAsList());
    }

    public static HashMap<String, KnxAction> convertKnxActionListTosMap(List<KnxAction> list) {
        HashMap<String, KnxAction> kal = new HashMap<String, KnxAction>();
        for (KnxAction action : list) {
            kal.put(action.name, action);
        }
        return kal;
    }

    }
