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

//   public List<String> interprete(String command){
//       List<String> commandList = new ArrayList<String>();
//       String[] splittedCommand = command.split("und");
//       for(int i=0; i < splittedCommand.length - 1; i++){
//           commandList.add(splittedCommand[i]+ " "+ splittedCommand[splittedCommand.length-1]);
//           commandList.add(splittedCommand[i]);
//       }
//       return commandList;
//   }
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
                   } else {
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
