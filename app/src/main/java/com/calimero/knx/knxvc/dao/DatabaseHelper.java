package com.calimero.knx.knxvc.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sven Schilling on 10.12.2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version
    public static final int DB_VERSION = 1;

    //Database Name
    public static final String DB_NAME = "KNXVCDB";

    // Table Names
    public static final String TABLE_COMMAND = "Command";
    public static final String TABLE_ACTION = "Action";
    public static final String TABLE_COMMAND_ACTION = "Command_Action";
    public static final String TABLE_PROFILE = "Profile";

    // Common column names
    public static final String KEY_ID = "Id";

    // Command Table - Column names
    public static final String COL_COMMAND_TEXT = "Command";
    public static final String COL_COMMAND_PROFILE = "Profile";

    // Action Table - Column names
    public static final String COL_ACTION_NAME = "Name";
    public static final String COL_ACTION_GROUPADDRESS = "Groupaddress";
    public static final String COL_ACTION_DATA = "Data";

    // Command_Action Table - Column names
    public static final String COL_COMMAND_ID = "Command_Id";
    public static final String COL_ACTION_ID = "Action_Id";

    // Profile Table - Column names
    public static final String COL_PROFILE_NAME = "Name";

    // Table Create Statements
    // Command table create statement
    private static final String CREATE_TABLE_COMMAND = "CREATE TABLE "
            + TABLE_COMMAND + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_COMMAND_TEXT + " TEXT NOT NULL,"
            + COL_COMMAND_PROFILE + " INTEGER NOT NULL, "
            + "FOREIGN KEY("+COL_COMMAND_PROFILE+") REFERENCES "+TABLE_PROFILE+"("+COL_PROFILE_NAME+"));";

    // Action table create statement
    private static final String CREATE_TABLE_ACTION = "CREATE TABLE " + TABLE_ACTION
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_ACTION_NAME + " TEXT NOT NULL,"
            + COL_ACTION_GROUPADDRESS + " TEXT NOT NULL, "
            + COL_ACTION_DATA + " TEXT NOT NULL);";

    // Command_Action table create statement
    private static final String CREATE_TABLE_COMMAND_ACTION = "CREATE TABLE "
            + TABLE_COMMAND_ACTION + "("+ COL_COMMAND_ID + " INTEGER,"
            + COL_ACTION_ID + " INTEGER, PRIMARY KEY("+COL_COMMAND_ID+","+COL_ACTION_ID+"),"
            + "FOREIGN KEY("+COL_COMMAND_ID+") REFERENCES "+TABLE_COMMAND+"("+KEY_ID+"),"
            + "FOREIGN KEY("+COL_ACTION_ID+") REFERENCES "+TABLE_ACTION+"("+KEY_ID+"));";

    // Profile table create statement
    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_PROFILE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_PROFILE_NAME + " TEXT NOT NULL);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMAND+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMAND_ACTION+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE+ ";");

        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_COMMAND);
        db.execSQL(CREATE_TABLE_ACTION);
        db.execSQL(CREATE_TABLE_COMMAND_ACTION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMAND+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMAND_ACTION+ ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE+ ";");

        // create new tables
        onCreate(db);
    }
}
