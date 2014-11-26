package com.calimero.knx.knxoncalimero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David on 26.11.2014.
 *
 * Das hier ist gar keine KNXActionFactory :P
 *
 * Sondern hier haue ich die Liste von vorkonfigurierten KNXActions raus.
 */
public class KNXActionFactory {

    public static ArrayList<KNXAction> getKNXActionsAsList(){
        //TODO Echte Objekte des Aufbaus verwenden und Gruppenadresse und Telegramminhalt setzen.
        ArrayList<KNXAction> kal = new ArrayList<KNXAction>();
        kal.add(new KNXAction("Licht anschalten"));
        kal.add(new KNXAction("Licht dimmen"));
        kal.add(new KNXAction("Licht ausschalten"));
        kal.add(new KNXAction("Jalousien hochfahren"));
        kal.add(new KNXAction("Jalousien herunterfahren"));
        kal.add(new KNXAction("Kamin entfachen"));
        kal.add(new KNXAction("Kamin löschen"));
        return kal;
    }

    public static HashMap<String, KNXAction> getKNXActionsAsMap(){
        HashMap<String, KNXAction> kal = new HashMap<String, KNXAction>();
        for(KNXAction action : getKNXActionsAsList()){
            kal.put(action.name,action);
        }
        return kal;
    }
}
