package com.calimero.knx.knxvc.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.calimero.knx.knxvc.calimero.connection.sys.KnxCommunicationObject;


import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

//import sys.KnxCommunicationObject;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;

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

    private static KnxAdapter knxAdapter = null;

    //private KnxCommunicationObject knxCommunicationObject;

    private static Activity activity;
    private String knx_gateway_ip = null;
    private  KnxCommunicationObject knxCommunicationObject = null;
    private BlockingQueue<KnxAction> bq = new ArrayBlockingQueue<KnxAction>(50);

    public KnxAdapter(Activity activity) {
        this.activity = activity;
        //SharedPreferences defaultSharedPreferences = activity.getSharedPreferences("knx_settings",Context.MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        knx_gateway_ip = defaultSharedPreferences.getString("knx_gateway_ip", null);
        knx_gateway_ip = "192.168.10.28";

        try {
        //this.io = new IOHandler("192.168.10.28", bq, this.activity);
            //io.start();
            Log.d("KnxAdapter", "KnxGatewayIp: " + knx_gateway_ip);
            knxCommunicationObject = KnxCommunicationObject.getInstance(getIP(), knx_gateway_ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        knxAdapter = this;
    }

    public static KnxAdapter getInstanceIfExists(){
        return knxAdapter;
    }

    public static String getIP(){
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String hostIp = String.format("%d.%d.%d.%d",(ip & 0xff),(ip >> 8 & 0xff),(ip >> 16 & 0xff),(ip >> 24 & 0xff));
        return hostIp;
    }

    public void executeKnxAction(KnxAction action){
        try {
            GroupAddress groupAddress = new GroupAddress(action.getGroupAddress());
//            GroupAddress ga = new GroupAddress(1,5,11);
            Boolean data = action.getData().equals("0") ? false : true;
            knxCommunicationObject.writeBoolean(groupAddress, data);
//            knxCommunicationObject.writeBoolean(ga,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //bq.add(action);
    }

    public void executeKnxActions(List<KnxAction> actions){
        for(KnxAction action : actions){
            executeKnxAction(action);
        }
    }
}
