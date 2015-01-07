package com.calimero.knx.knxvc.calimero;

import android.app.Activity;
import android.widget.Toast;

import com.calimero.knx.knxoncalimero.Container;
import com.calimero.knx.knxoncalimero.KnxBusConnection;
import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;
import com.calimero.knx.knxvc.core.KnxAction;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

/**
 * {@link Thread} that handles outgoing {@link KnxAction} objects.
 *
 * Created by Jonas on 03.12.2014.
 */
public class IOHandler extends Thread implements Observer{

    /** Collection of telegram data to be sent on the bus. Must be filled outside of this class. */
    private final BlockingQueue<KnxAction> outboundData;
    private final BlockingQueue<String> inboundData;
    private final Activity activity;

    private String hostIp = "192.168.10.132", gatewayIp;
    private KNXNetworkLinkIP networkLinkIp;
    private ProcessCommunicator communicator;
    private KnxBusConnection connectionRunnable;
    private Container busActionContainer;
    private Container resultContainer;

    /**
     * Creates a new {@lnk IOHandler} with an open connection.
     * Once this {@link Thread} is started, it will constantly check the given
     * {@link BlockingQueue} for new {@link KnxAction} objects ready to be sent on the bus.
     *
     * @param gatewayIp - the target ip address
     * @param outboundData - the {@link java.util.concurrent.BlockingQueue} that {@link com.calimero.knx.knxvc.core.KnxAction} objects will
     *                     be added to
     * @param activity
     * @throws KNXException - if the KNX connection could not be established
     * @throws UnknownHostException - if the host data is invalid
     */
    public IOHandler(String gatewayIp, BlockingQueue<KnxAction> outboundData, Activity activity) throws KNXException, UnknownHostException {

        this.gatewayIp = gatewayIp;
        this.outboundData = outboundData;
        this.activity = activity;
        this.inboundData = new ArrayBlockingQueue<String>(4096);
    }

    private void openConnection() throws KNXException, UnknownHostException {

        this.networkLinkIp = new KNXNetworkLinkIP(
                KNXNetworkLinkIP.TUNNEL,
                new InetSocketAddress(hostIp, 0),
                new InetSocketAddress(gatewayIp, KNXnetIPConnection.IP_PORT),
                false,
                new TPSettings(false));
        this.communicator = new ProcessCommunicatorImpl(networkLinkIp);


        busActionContainer = new Container();
        resultContainer = new Container();
        resultContainer.addObserver(this);
        connectionRunnable = new KnxBusConnection("192.168.10.123", this.gatewayIp, busActionContainer, resultContainer);
        connectionRunnable.addObserver(this);
        Thread connectionThread = new Thread(connectionRunnable);
        connectionThread.start();
    }

    @Override
    public void run() {

        if (this.networkLinkIp == null) {

            try {
                openConnection();
            } catch (KNXException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        if (!(this.communicator == null)) {
            // cancel() has not been called yet

            if (!this.outboundData.isEmpty()) {

                KnxAction action = outboundData.poll();
                if (action != null) {

                    sendOnBus(action);
                }
            }
            if (!this.inboundData.isEmpty()) {

                // notify system
            }
        }
    }

    /**
     * Closes all allocated resources and prevents further usage. An {@link IOHandler}
     * that had its {@code cancel()} method called may still run, but will perform no further
     * operations.
     */
    public void cancel() {

        this.networkLinkIp.close();
        this.communicator = null;
        this.interrupt();
    }

    private void sendOnBus(KnxAction action) {

        try {
            GroupAddress address = new GroupAddress(action.getGroupAddress());
            String data = action.getData();
            if ("0".equals(data)) {

                communicator.write(address, false);
            } else if ("1".equals(data)) {

                communicator.write(address, true);
            }
        } catch (KNXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        // connection status message
        String message = null;
        if (observable.equals(connectionRunnable)) {
            message = "KNX Connection "
                    + ((connectionRunnable.isConnected()) ? "" : "not ")
                    + " successful.";
        } else if (observable.equals(resultContainer) && data instanceof KnxBooleanObject) {
            final boolean read = ((KnxBooleanObject) data).getValue();
            message = "KNX data '" + read + "' received.";
        }
        if (message != null) {
            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
