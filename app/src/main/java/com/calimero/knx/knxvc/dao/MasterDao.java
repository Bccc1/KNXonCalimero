package com.calimero.knx.knxvc.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.Voice;
import android.util.Log;

import com.calimero.knx.knxvc.VoiceCommand;
import com.calimero.knx.knxvc.core.KnxAction;
import com.calimero.knx.knxvc.core.Profile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sven Schilling on 10.12.2014.
 */
public class MasterDao {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private final String[] COLUMNS_ACTION = {
            DatabaseHelper.KEY_ID,
            DatabaseHelper.COL_ACTION_NAME,
            DatabaseHelper.COL_ACTION_DATA,
            DatabaseHelper.COL_ACTION_GROUPADDRESS
    };

    public MasterDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        Boolean test = database.isOpen();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveKnxAction(KnxAction knxaction) {
        if(knxaction.getId() != null){
            KnxAction knxactionVergleich = getKnxAction(knxaction.getId());
            if (knxactionVergleich==null){
                insertKnxAction(knxaction);
            }
            else{
                updateKnxAction(knxaction);
            }
        }
        else{
            insertKnxAction(knxaction);
        }
    }

    private void insertKnxAction(KnxAction knxaction){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ACTION_NAME, knxaction.getName());
        values.put(DatabaseHelper.COL_ACTION_DATA, knxaction.getData());
        values.put(DatabaseHelper.COL_ACTION_GROUPADDRESS, knxaction.getGroupAddress());
        database.insert(DatabaseHelper.TABLE_ACTION,null, values);
    }

    private void updateKnxAction(KnxAction knxaction){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ACTION_NAME, knxaction.getName());
        values.put(DatabaseHelper.COL_ACTION_DATA, knxaction.getData());
        values.put(DatabaseHelper.COL_ACTION_GROUPADDRESS, knxaction.getGroupAddress());
        database.update(DatabaseHelper.TABLE_ACTION,values,DatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(knxaction.getId())});

    }

    public void saveVoiceCommand(VoiceCommand voicecommand) {
        if(voicecommand.getId() != null){
            VoiceCommand voicecommandVergleich = getVoiceCommand(voicecommand.getId());
            if (voicecommandVergleich==null){
                insertVoiceCommand(voicecommand);
            }
            else{
                updateVoiceCommand(voicecommand);
            }
        }
        else{
            insertVoiceCommand(voicecommand);
        }
    }

    private void insertCommandAction(VoiceCommand voicecommand){
        ContentValues values = new ContentValues();
        for(KnxAction action : voicecommand.getActions()){
            values.put(DatabaseHelper.COL_COMMAND_ID,String.valueOf(voicecommand.getId()));
            values.put(DatabaseHelper.COL_ACTION_ID,String.valueOf(action.getId()));
        }
        database.insert(DatabaseHelper.TABLE_COMMAND_ACTION,null, values);
    }

    private void updateCommandAction(VoiceCommand voicecommand){
        ContentValues values = new ContentValues();
        for(KnxAction action : voicecommand.getActions()){
            values.put(String.valueOf(voicecommand.getId()),String.valueOf(action.getId()));
        }
        database.delete(DatabaseHelper.TABLE_COMMAND_ACTION,DatabaseHelper.COL_COMMAND_ID + " = ?",
                new String[]{String.valueOf(voicecommand.getId())});
        database.insert(DatabaseHelper.TABLE_COMMAND_ACTION,null, values);
    }

    private void insertVoiceCommand(VoiceCommand voicecommand){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_COMMAND_TEXT, voicecommand.getName());
        values.put(DatabaseHelper.COL_COMMAND_PROFILE, voicecommand.getProfile());
        long id = database.insert(DatabaseHelper.TABLE_COMMAND, null, values);
        voicecommand.setId((int)id);
        for(KnxAction action : voicecommand.getActions()) {
            saveKnxAction(action);
        }
        insertCommandAction(voicecommand);
    }

    private void updateVoiceCommand(VoiceCommand voicecommand){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_COMMAND_TEXT, voicecommand.getName());
        values.put(DatabaseHelper.COL_COMMAND_PROFILE, voicecommand.getProfile());
        database.update(DatabaseHelper.TABLE_COMMAND,values,DatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(voicecommand.getId())});
        updateCommandAction(voicecommand);
    }

    public List<KnxAction> getAllKnxAction() {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.KEY_ID + ", " +
                        DatabaseHelper.COL_ACTION_NAME + ", " +
                        DatabaseHelper.COL_ACTION_DATA + ", " +
                        DatabaseHelper.COL_ACTION_GROUPADDRESS + " FROM " +
                        DatabaseHelper.TABLE_ACTION, new String[]{});
        cursor.moveToFirst();
        List<KnxAction> knxaction = new ArrayList<KnxAction>();
        while (!cursor.isAfterLast()) {
            knxaction.add(populateKnxAction(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return knxaction;
    }

    public List<KnxAction> getAllKnxActionFromCommand(int commandId) {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.COL_COMMAND_ID + ", " +
                        DatabaseHelper.COL_ACTION_ID +
                        " FROM " + DatabaseHelper.TABLE_COMMAND_ACTION +
                        " WHERE " + DatabaseHelper.COL_COMMAND_ID + " = ?", new String[]{String.valueOf(commandId)});
        cursor.moveToFirst();
        List<KnxAction> knxaction = new ArrayList<KnxAction>();
        while (!cursor.isAfterLast()) {
            knxaction.add(populateKnxAction(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return knxaction;
    }


    public KnxAction getKnxAction(int actionId) {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.KEY_ID + ", " +
                        DatabaseHelper.COL_ACTION_NAME + ", " +
                        DatabaseHelper.COL_ACTION_DATA + ", " +
                        DatabaseHelper.COL_ACTION_GROUPADDRESS + " FROM " + DatabaseHelper.TABLE_ACTION +
                        " WHERE " + DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(actionId)});
        cursor.moveToFirst();
        KnxAction knxaction = null;
        if (!cursor.isAfterLast()) {
            knxaction = populateKnxAction(cursor);
        }
        cursor.close();
        return knxaction;
    }

    public List<VoiceCommand> getAllVoiceCommand() {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.KEY_ID + ", " +
                        DatabaseHelper.COL_COMMAND_TEXT + ", " +
                        DatabaseHelper.COL_COMMAND_PROFILE + " FROM " +
                        DatabaseHelper.TABLE_COMMAND+";", new String[]{});
        cursor.moveToFirst();
        List<VoiceCommand> voicecommand = new ArrayList<VoiceCommand>();
        while (!cursor.isAfterLast()) {
            voicecommand.add(populateVoiceCommand(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return voicecommand;
    }

    public VoiceCommand getVoiceCommand(int commandId) {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.KEY_ID + ", " +
                        DatabaseHelper.COL_COMMAND_TEXT + ", " +
                        DatabaseHelper.COL_COMMAND_PROFILE + " FROM " + DatabaseHelper.TABLE_COMMAND +
                        " WHERE " + DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(commandId)});
        cursor.moveToFirst();
        VoiceCommand voicecommand = null;
        if (!cursor.isAfterLast()) {
            voicecommand = populateVoiceCommand(cursor);
        }
        cursor.close();
        return voicecommand;
    }

    public void deleteProfile(int id){
        database.delete(DatabaseHelper.TABLE_PROFILE, DatabaseHelper.KEY_ID + "=" + id, null);
    }

    public void deleteVoiceCommand(int id){
        database.delete(DatabaseHelper.TABLE_COMMAND_ACTION, DatabaseHelper.COL_COMMAND_ID + "=" + id, null);
        database.delete(DatabaseHelper.TABLE_COMMAND, DatabaseHelper.KEY_ID + "=" + id, null);
    }

    public void deleteAction(int id){
        database.delete(DatabaseHelper.TABLE_COMMAND_ACTION, DatabaseHelper.COL_ACTION_ID + "=" + id, null);
        database.delete(DatabaseHelper.TABLE_ACTION, DatabaseHelper.KEY_ID + "=" + id, null);
    }

    public void deleteAllProfiles(){
        database.delete(DatabaseHelper.TABLE_PROFILE, null, null);
    }

    public void deleteAllActions(){
        database.delete(DatabaseHelper.TABLE_COMMAND_ACTION, null, null);
        database.delete(DatabaseHelper.TABLE_ACTION, null, null);
    }

    public void deleteAllCommands(){
        database.delete(DatabaseHelper.TABLE_COMMAND_ACTION, null, null);
        database.delete(DatabaseHelper.TABLE_COMMAND, null, null);
    }

    public void insertProfile(Profile profile){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PROFILE_NAME, profile.getName());
        database.insert(DatabaseHelper.TABLE_PROFILE, null, values);
    }

    public Profile getProfile(int profileId) {
        Cursor cursor = database.rawQuery(
                "SELECT " + DatabaseHelper.KEY_ID + ", " +
                        DatabaseHelper.COL_PROFILE_NAME + " FROM " + DatabaseHelper.TABLE_PROFILE +
                        " WHERE " + DatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(profileId)});
        cursor.moveToFirst();
        Profile profile = null;
        if (!cursor.isAfterLast()) {
            profile = populateProfile(cursor);
        }
        cursor.close();
        return profile;
    }

    private KnxAction populateKnxAction(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_ACTION_NAME);
        int dataIndex = cursor.getColumnIndex(DatabaseHelper.COL_ACTION_DATA);
        int groupaddressIndex = cursor.getColumnIndex(DatabaseHelper.COL_ACTION_GROUPADDRESS);

        KnxAction knxaction = new KnxAction();
        knxaction.setId(cursor.getInt(idIndex));
        knxaction.setName(cursor.getString(nameIndex));
        knxaction.setData(cursor.getString(dataIndex));
        knxaction.setGroupAddress(cursor.getString(groupaddressIndex));

        return knxaction;
    }

    private VoiceCommand populateVoiceCommand(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_COMMAND_TEXT);
        int profileIndex = cursor.getColumnIndex(DatabaseHelper.COL_COMMAND_PROFILE);

        VoiceCommand voicecommand = new VoiceCommand();
        voicecommand.setId(cursor.getInt(idIndex));
        voicecommand.setName(cursor.getString(nameIndex));
        voicecommand.setProfile(cursor.getString(profileIndex));
        List<KnxAction> knxactionList = getAllKnxActionFromCommand(cursor.getInt(idIndex));
        voicecommand.setActions(knxactionList);

        return voicecommand;
    }

    private Profile populateProfile(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.KEY_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_PROFILE_NAME);

        Profile profile = new Profile();
        profile.setId(cursor.getInt(idIndex));
        profile.setName(cursor.getString(nameIndex));

        return profile;
    }
}
