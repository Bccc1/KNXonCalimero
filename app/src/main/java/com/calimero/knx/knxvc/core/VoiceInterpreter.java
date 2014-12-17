package com.calimero.knx.knxvc.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sven Schilling on 17.12.2014.
 */
public class VoiceInterpreter {

   public List<String> interprete(String command){
       List<String> commandList = new ArrayList<String>();
       String[] splittedCommand = command.split("und");
       for(int i=0; i < splittedCommand.length - 1; i++){
           commandList.add(splittedCommand[i]+ " "+ splittedCommand[splittedCommand.length-1]);
           commandList.add(splittedCommand[i]);
       }
       return commandList;
   }
    public List<String> interpreteAll(List<String> commandList){
        Set<String> extendedCommandList = new HashSet<>();
       for(String command : commandList) {
           String[] splittedCommand = command.split("und");
           if(splittedCommand.length==1){
             extendedCommandList.add(splittedCommand[0]);
           }
           else {
               for (int i = 0; i < splittedCommand.length - 1; i++) {
                   extendedCommandList.add(splittedCommand[i] + " " + splittedCommand[splittedCommand.length - 1]);
                   extendedCommandList.add(splittedCommand[i]);
                   extendedCommandList.add(splittedCommand[i+1]);
               }
           }

       }
        return new ArrayList<>(extendedCommandList);
    }
}
