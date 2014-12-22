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
        Set<VoiceCommand> resultVoiceCommandList = new HashSet<>();
       if (masterDao!=null) {
           List<VoiceCommand> voiceCommandList = masterDao.getAllVoiceCommand();
           for (String command : commandList) {
               String[] splittedCommand = command.split("und");
               if (splittedCommand.length == 1 && voiceCommandList.contains(splittedCommand[0])) {
                   resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(splittedCommand[0]));
               } else {
                   for (int i = 0; i < splittedCommand.length - 1; i++) {
                       String interpretedCommand= splittedCommand[i] + " " + splittedCommand[splittedCommand.length - 1];
                       if (voiceCommandList.contains(interpretedCommand)) {
                           resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(interpretedCommand));
                       }
                       interpretedCommand=splittedCommand[i];
                       if (voiceCommandList.contains(interpretedCommand)) {
                           resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(interpretedCommand));
                       }
                       interpretedCommand = splittedCommand[i + 1];
                       if (voiceCommandList.contains(interpretedCommand)) {
                           resultVoiceCommandList.add(masterDao.getVoiceCommandbyText(interpretedCommand));
                       }
                   }
               }

           }
       }
        return new ArrayList<>(resultVoiceCommandList);
    }
}
