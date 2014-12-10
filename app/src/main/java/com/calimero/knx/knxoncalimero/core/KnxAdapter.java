package com.calimero.knx.knxvc.core;

import com.calimero.knx.knxvc.calimero.IOHandler;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

import tuwien.auto.calimero.exception.KNXException;

/**
 * Created by David on 01.12.14.
 * Diese Klasse soll die Schnittstelle zum KNX Bus darstellen.
 * Nach aktueller Aufgabenteilung müsste sie von Jonas mit Leben gefüllt werden.
 *
 * In meiner aktuellen Unkenntiss von Calimero stell ich mir die beiden schon angelegten Methoden
 * in ihrer aktuellen Signatur so vor. Ob das sinnvoll ist,
 * muss dann der implementierende entscheiden.
 *
 * Evtl kann man hier auch von den Fortschritten von Gerrits Gruppe profitieren.
 * Die haben schon was gecodet was potentiell funktionieren könnte, nur noch nicht getestet.
 *
 * Das läuft bei denen dann mit drei Containern und einem Daemon-Thread der die abarbeitet.
 * Ein Container für Daten die Gesendet werden sollen,
 * ein Container für den Empfang
 * und ein Container wo in der Vergangenheit empfangene Daten abgelegt werden.
 */
public class KnxAdapter {
    private IOHandler io;
    private BlockingQueue<KnxAction> bq = new ArrayBlockingQueue<KnxAction>(50);
    public KnxAdapter() {
        try {
            this.io = new IOHandler("192.168.10.28",bq);
            io.start();
        } catch (KNXException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void executeKnxAction(KnxAction action){
        //TODO Implement
        bq.add(action);
    }

    public void executeKnxActions(List<KnxAction> actions){
        for(KnxAction action : actions){
            executeKnxAction(action);
        }
    }
}
