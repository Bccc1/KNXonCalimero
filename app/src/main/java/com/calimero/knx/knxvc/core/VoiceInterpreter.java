package com.calimero.knx.knxvc.core;

import android.speech.tts.Voice;

import com.calimero.knx.knxvc.VoiceCommand;
import com.calimero.knx.knxvc.dao.MasterDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sven Schilling on 17.12.2014.
 */
public class VoiceInterpreter {

    private MasterDao masterDao;

    public VoiceInterpreter(MasterDao masterDao){
        this.masterDao=masterDao;
    }

    /**
     * Interpretes the results of the spoken text and returns a list of all voicecommands which matches the spoken text.
     * Therefore the results getting split by the keyword "und", so its possible to do several voice commands in one go.
     * @param commandList Result list of the spoken text
     * @return List of voicecommand-objects which matching the spoken text
     */
    public List<VoiceCommand> interpreteAll(List<String> commandList){
        List<VoiceCommand> resultVoiceCommandList = new ArrayList<>();
       if (masterDao!=null) {
           List<VoiceCommand> voiceCommandList = masterDao.getAllVoiceCommand();
           ArrayList<String> voiceCommandNameList = new ArrayList<String>();
           for (VoiceCommand command : voiceCommandList) {
               voiceCommandNameList.add(command.getName());
           }
           for (String command : commandList) {
               for (String voiceCommand : voiceCommandNameList) {
                   String[] splittedCommand = command.split("und");
                   if (splittedCommand.length == 1 && voiceCommand.toLowerCase().replaceAll(" ", "").equals(splittedCommand[0].toLowerCase().replaceAll(" ", ""))) {
                       if(!isCommandExisting(voiceCommand,resultVoiceCommandList)) {
                           resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(voiceCommand));
                       }
                   } 
                   else {
                       for (int i = 0; i < splittedCommand.length - 1; i++) {
                           String interpretedCommand = splittedCommand[i] + " " + splittedCommand[splittedCommand.length - 1];
                           if (voiceCommand.toLowerCase().replaceAll(" ","").equals(interpretedCommand.toLowerCase().replaceAll(" ", ""))) {
                               if(!isCommandExisting(voiceCommand,resultVoiceCommandList)) {
                                   resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(voiceCommand));
                               }
                           }
                           else{
                               String[] splittedLastCommand = splittedCommand[splittedCommand.length-1].split(" ");
                               interpretedCommand = splittedCommand[i] + " " + splittedLastCommand[splittedLastCommand.length - 1];
                               if (voiceCommand.toLowerCase().replaceAll(" ", "").equals(interpretedCommand.toLowerCase().replaceAll(" ", ""))) {
                                   if(!isCommandExisting(voiceCommand,resultVoiceCommandList)) {
                                       resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(voiceCommand));
                                   }
                               }
                           }
                           interpretedCommand = splittedCommand[i];
                           if (voiceCommand.toLowerCase().replaceAll(" ", "").equals(interpretedCommand.toLowerCase().replaceAll(" ", ""))) {
                               if(!isCommandExisting(voiceCommand,resultVoiceCommandList)) {
                                   resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(voiceCommand));
                               }
                           }
                           interpretedCommand = splittedCommand[i + 1];
                           if (voiceCommand.toLowerCase().replaceAll(" ", "").equals(interpretedCommand.toLowerCase().replaceAll(" ", ""))) {
                               if(!isCommandExisting(voiceCommand,resultVoiceCommandList)) {
                                   resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(voiceCommand));
                               }
                           }
                       }
                   }
               }
           }
       }
        return resultVoiceCommandList;
    }

    //Checks if there is a command in a list of voicecommand-objects which matches the interpreted command
    private Boolean isCommandExisting(String commandName, List<VoiceCommand> voiceCommandList) {
        Boolean isExisting = false;
        if (masterDao != null) {
            for (VoiceCommand voiceCommand : voiceCommandList) {
                if (commandName.equals(voiceCommand.getName())) {
                    isExisting = true;
                    break;
                }
            }
        }
        return isExisting;
    }
}
