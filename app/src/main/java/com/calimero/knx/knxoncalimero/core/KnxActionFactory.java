package com.calimero.knx.knxoncalimero.core;

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

    public static ArrayList<KnxAction> getKNXActionsAsList(){
        //TODO Echte Objekte des Aufbaus verwenden und Gruppenadresse und Telegramminhalt setzen.
        ArrayList<KnxAction> kal = new ArrayList<KnxAction>();
        //kal.add(new KnxAction("Licht anschalten"));
        KnxAction lichtAnschalten = new KnxAction("Licht anschalten");
        lichtAnschalten.groupAddress="1/5/1";
        lichtAnschalten.data = "1";
        kal.add(lichtAnschalten);
        kal.add(new KnxAction("Licht dimmen"));
        //kal.add(new KnxAction("Licht ausschalten"));
        KnxAction lichtAusschalten = new KnxAction("Licht ausschalten");
        lichtAusschalten.groupAddress = "1/5/1";
        lichtAusschalten.data = "0";
        kal.add(lichtAusschalten);
        kal.add(new KnxAction("Jalousien hochfahren"));
        kal.add(new KnxAction("Jalousien herunterfahren"));
        kal.add(new KnxAction("Kamin entfachen"));
        kal.add(new KnxAction("Kamin löschen"));
        return kal;
    }

    public static HashMap<String, KnxAction> getKNXActionsAsMap(){
        HashMap<String, KnxAction> kal = new HashMap<String, KnxAction>();
        for(KnxAction action : getKNXActionsAsList()){
            kal.put(action.name,action);
        }
        return kal;
    }
}
