package com.calimero.knx.knxoncalimero;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 29.11.2014.
 */
public class VoiceCommand {
    String id;
    String name;
    List<KnxAction> actions = new ArrayList<KnxAction>();

    @Override
    public String toString() {
        return name;
    }

}
