package com.calimero.knx.knxoncalimero;

import com.calimero.knx.knxoncalimero.core.KnxAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 29.11.2014.
 */
public class VoiceCommand {
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActions(List<KnxAction> actions) {
        this.actions = actions;
    }

    String id;
    String name;
    List<KnxAction> actions = new ArrayList<KnxAction>();

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public List<KnxAction> getActions() {
        return actions;
    }

    public String getId() {

        return id;
    }
}
