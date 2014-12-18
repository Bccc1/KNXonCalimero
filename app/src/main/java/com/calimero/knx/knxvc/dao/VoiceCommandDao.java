package com.calimero.knx.knxvc.dao;

import android.util.Log;

import com.calimero.knx.knxvc.VoiceCommand;
import com.calimero.knx.knxvc.core.KnxAction;
import com.calimero.knx.knxvc.core.KnxActionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 29.11.2014.
 */
public class VoiceCommandDao {

    private static VoiceCommandDao voiceCommandDao;

    private static int idcounter = 0;

    public List<VoiceCommand> getVoiceCommands() {
        return voiceCommands;
    }

    /* Hier kommen dann als Key das Sprachkommando und als Value eine Liste von auszuführenden Befehlen rein.
        */
    List<VoiceCommand> voiceCommands = new ArrayList<VoiceCommand>();
    Map<String, VoiceCommand> voiceCommandsMapping = new HashMap<String, VoiceCommand>();

    public static VoiceCommandDao getInstance(){
        if(voiceCommandDao == null) {
            voiceCommandDao = new VoiceCommandDao();
            voiceCommandDao.createVoiceCommandMapping();
        }
        return voiceCommandDao;
    }

    private VoiceCommandDao() {
    }

    private void createVoiceCommandMapping(){
        //Hier ist erstmal ein hardgecodetes Mapping von Sprachbefehl zu KNXBefehl
        HashMap<String, KnxAction> knxActions = KnxActionFactory.getKNXActionsAsMap();

        addSingleActionVoiceCommand("an","Licht anschalten");
        addSingleActionVoiceCommand("aus", "Licht ausschalten");
        addSingleActionVoiceCommand("Licht an", "Licht anschalten");
        addSingleActionVoiceCommand("Licht aus", "Licht ausschalten");
        addSingleActionVoiceCommand("Licht dimmen", "Licht dimmen");
        addSingleActionVoiceCommand("Jalousie hoch", "Jalousien hochfahren");
        addSingleActionVoiceCommand("Jalousie runter", "Jalousien herunterfahren");
        addSingleActionVoiceCommand("Kamin an", "Kamin entfachen");
        addSingleActionVoiceCommand("Kamin aus", "Kamin löschen");

        //TODO Noch romantischer gestalten ;)
        ArrayList<KnxAction> actionListRomantisch = new ArrayList<KnxAction>();
        actionListRomantisch.add(knxActions.get("Licht dimmen"));
        actionListRomantisch.add(knxActions.get("Kamin entfachen"));
        addVoiceCommand("romantisch",actionListRomantisch);
    }

    public void addSingleActionVoiceCommand(String name, String action){
        VoiceCommand voiceCommand = singleActionVoiceCommand(name, action);
        voiceCommandsMapping.put(name,voiceCommand);
        voiceCommands.add(voiceCommand);
    }

    public void addSingleActionVoiceCommand(String name, KnxAction action){
        VoiceCommand voiceCommand = singleActionVoiceCommand(name, action);
        voiceCommandsMapping.put(name,voiceCommand);
        voiceCommands.add(voiceCommand);
    }

    public void addVoiceCommand(String name, List<KnxAction> actions){
        VoiceCommand vc = new VoiceCommand();
        vc.setActions(actions);
        vc.setId(idcounter++);
        vc.setName(name);
        vc.setProfile("0");
        voiceCommandsMapping.put(name,vc);
        voiceCommands.add(vc);
    }

    private VoiceCommand singleActionVoiceCommand(String name, String action){
        HashMap<String, KnxAction> knxActions = KnxActionFactory.getKNXActionsAsMap();
        return singleActionVoiceCommand(name, knxActions.get(action));
    }

    private VoiceCommand singleActionVoiceCommand(String name, KnxAction action){
        ArrayList<KnxAction> actionList = new ArrayList<KnxAction>();
        actionList.add(action);
        VoiceCommand vc = new VoiceCommand();
        vc.setActions(actionList);
        vc.setId(idcounter++);
        vc.setName(name);
        vc.setProfile("0");
        return vc;
    }

    public VoiceCommand getById(String id){
        for(VoiceCommand vc : voiceCommands)
            if(vc.getId().equals(Integer.valueOf(id)))
                return vc;
        Log.d("KNX - VCDAO", "getById(" + id + ") returned null");
        return null;
    }

    //public List<VoiceCommand> getVoiceCommands(){
    //Hier müsste dann der DB Kram laufen
    //select alle voiceCommands auf der entspr. Table
    //for(VoiceCommandMapping vc : result){
    //  populateVoiceCommand(vc);
    //}
    //}

    public Map<String, VoiceCommand> getVoiceCommandsMapping() {
        //Hier dann aus getVoiceCommands() eine Map machen.
        return voiceCommandsMapping;
    }

    //private void populateVoiceCommand(VoiceCommandMapping vc){
    //  List<String> ids = getActionIdsForVoiceCommand(vc.id);
    //  List<KnxAction> actions = new ArrayList<KnxAction>();
    //  for(String id : ids){
    //      actions.add(KnxActionDao.getKnxAction(id));
    //  }
    //  vc.actions = actions;
    //  return;
    //}

    //in KNXActionDao gibts public KnxAction getKnxAction(String id)
}
