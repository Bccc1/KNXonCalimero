package com.calimero.knx.knxoncalimero.calimero;

import com.calimero.knx.knxoncalimero.core.KnxAction;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

/**
 * {@link Thread} that handles outgoing {@link KnxAction} objects.
 *
 * Created by Jonas on 03.12.2014.
 */
public class IOHandler extends Thread {

    /** Collection of telegram data to be sent on the bus. Must be filled outside of this class. */
    private final BlockingQueue<KnxAction> outboundData;
    private final BlockingQueue<String> inboundData;

    private String hostIp = "", gatewayIp = "";
    private KNXNetworkLinkIP networkLinkIp;
    private ProcessCommunicator communicator;

    /**
     * Creates a new {@lnk IOHandler} with an open connection.
     * Once this {@link Thread} is started, it will constantly check the given
     * {@link BlockingQueue} for new {@link KnxAction} objects ready to be sent on the bus.
     *
     * @param outboundData - the {@link BlockingQueue} that {@link KnxAction} objects will
     *                     be added to.
     * @throws KNXException - if the KNX connection could not be established
     * @throws UnknownHostException - if the host data is invalid
     */
    public IOHandler (BlockingQueue<KnxAction> outboundData) throws KNXException, UnknownHostException {

        this.outboundData = outboundData;
        this.inboundData = new ArrayBlockingQueue<String>(4096);
        openConnection();
    }

    private void openConnection() throws KNXException, UnknownHostException {

        this.networkLinkIp = new KNXNetworkLinkIP(
                KNXNetworkLinkIP.TUNNEL,
                new InetSocketAddress(InetAddress.getByName(null), 0),
                new InetSocketAddress(InetAddress.getByName(gatewayIp), KNXnetIPConnection.IP_PORT),
                false,
                new TPSettings(false));
        this.communicator = new ProcessCommunicatorImpl(networkLinkIp);
    }

    @Override
    public void run() {

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

        // Calimero API usage
    }
}