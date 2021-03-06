package com.calimero.knx.knxvc.dao;

import android.util.Log;

import com.calimero.knx.knxvc.MainActivity;
import com.calimero.knx.knxvc.VoiceCommand;
import com.calimero.knx.knxvc.core.KnxAction;
import com.calimero.knx.knxvc.core.KnxActionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David on 29.11.2014.
 *
 * Sollte ursprünglich nur verwendet werden für die Testdaten und während die Datenbank noch nicht stand.
 * Jetzt wird es an einigen Stellen immer noch verwendet, weshalb die Klasse erweitert wurde.
 * Ist ziemliches Flickwerk und sollte langfristig komplett entfernt werden.
 * An den meisten Stellen könnte direkt auf die Datenbank über das MasterDao zugegriffen werden.
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


    /**
     * klassisches Singleton Pattern.
     * @return eine Instanz des VoiceCommandDao
     */
    public static VoiceCommandDao getInstance(){
        if(voiceCommandDao == null) {
            voiceCommandDao = new VoiceCommandDao();
            voiceCommandDao.loadDataFromDb();
        }
        return voiceCommandDao;
    }

    private VoiceCommandDao() {
    }

    /**
     * aktualisiert die Daten des Dao Caches mit denen aus der Datenbank.
     */
    public void loadDataFromDb(){
        voiceCommands = MainActivity.masterDao.getAllVoiceCommand();
        voiceCommands.clear();
        for(VoiceCommand vc : voiceCommands){
            voiceCommandsMapping.put(vc.getName(),vc);
        }
    }

    /**
     * Erzeugt Testdaten
     */
    public void createVoiceCommandMapping(){
        voiceCommands = new ArrayList<VoiceCommand>();
        voiceCommandsMapping = new HashMap<String, VoiceCommand>();

        HashMap<String, KnxAction> knxActions = KnxActionFactory.convertKnxActionListToMap(MainActivity.masterDao.getAllKnxAction());

        //Hier ist erstmal ein hardgecodetes Mapping von Sprachbefehl zu KNXBefehl
        addSingleActionVoiceCommand("an","Licht Oben Ein");
        addSingleActionVoiceCommand("aus", "Licht Oben Aus");
        addSingleActionVoiceCommand("Licht an", "Licht Mitte/Unten Ein");
        addSingleActionVoiceCommand("Licht aus", "Licht Mitte/Unten Aus");

        addSingleActionVoiceCommand("Jalousie hoch", "Jalousie-Langezeitfahren hoch");
        addSingleActionVoiceCommand("Jalousie runter", "Jalousie-Langezeitfahren runter");

        //TODO Noch romantischer gestalten ;)
        ArrayList<KnxAction> actionListRomantisch = new ArrayList<KnxAction>();
        actionListRomantisch.add(knxActions.get("Licht Mitte/Unten Aus"));
        actionListRomantisch.add(knxActions.get("Jalousie-Langezeitfahren runter"));
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
        HashMap<String, KnxAction> knxActions = KnxActionFactory.convertKnxActionListToMap(MainActivity.masterDao.getAllKnxAction());
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
