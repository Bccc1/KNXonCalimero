package com.calimero.knx.knxvc;

import com.calimero.knx.knxvc.core.KnxAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 29.11.2014.
 */
public class VoiceCommand {
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActions(List<KnxAction> actions) {
        this.actions = actions;
    }

    Integer id;
    String name;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    String profile;
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

    public Integer getId() {

        return id;
    }
}
