package com.calimero.knx.knxoncalimero;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1234;
    private ListView resultList;
    Button speakButton;
    TextView tv;
    ImageView lightImage;
    Boolean lightIsOn = false;
    final String LIGHT_IS_ON_PARAM = "lightIsOn";

    /* Hier kommen dann als Key das Sprachkommando und als Value eine Liste von auszuführenden Befehlen rein.
     */
    Map<String, List<KNXAction>> voiceCommandsMapping = new HashMap<String, List<KNXAction>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speakButton = (Button) findViewById(R.id.speakButton);

        resultList = (ListView) findViewById(R.id.list);

        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speakButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Recognizer Not Found",
                    Toast.LENGTH_SHORT).show();
        }

        speakButton.setOnClickListener(new OnClickListener() {
            	   @Override
            	   public void onClick(View v) {
                	    startVoiceRecognitionActivity();
                	   }
            	  });
        lightImage = (ImageView) findViewById(R.id.imageLight);

        tv = (TextView)findViewById(R.id.tvText);

        //Hier ist erstmal ein hardgecodetes Mapping von Sprachbefehl zu KNXBefehl
        HashMap<String, KNXAction> knxActions = KNXActionFactory.getKNXActionsAsMap();

        voiceCommandsMapping.put("an", singleActionList("Licht anschalten"));
        voiceCommandsMapping.put("aus",singleActionList("Licht ausschalten"));
        voiceCommandsMapping.put("Licht an", singleActionList("Licht anschalten"));
        voiceCommandsMapping.put("Licht aus",singleActionList("Licht ausschalten"));
        voiceCommandsMapping.put("Licht dimmen",singleActionList("Licht dimmen"));
        voiceCommandsMapping.put("Jalousie hoch",singleActionList("Jalousien hochfahren"));
        voiceCommandsMapping.put("Jalousie runter",singleActionList("Jalousien herunterfahren"));
        voiceCommandsMapping.put("Kamin an",singleActionList("Kamin entfachen"));
        voiceCommandsMapping.put("Kamin aus",singleActionList("Kamin löschen"));

        //TODO Noch romantischer gestalten ;)
        ArrayList<KNXAction> actionListRomantisch = new ArrayList<KNXAction>();
        actionListRomantisch.add(knxActions.get("Licht dimmen"));
        actionListRomantisch.add(knxActions.get("Kamin entfachen"));
        voiceCommandsMapping.put("romantisch",actionListRomantisch);

    }

    private ArrayList<KNXAction> singleActionList(String action){
        HashMap<String, KNXAction> knxActions = KNXActionFactory.getKNXActionsAsMap();
        return singleActionList(knxActions.get(action));
    }

    private ArrayList<KNXAction> singleActionList(KNXAction action){
        ArrayList<KNXAction> actionList = new ArrayList<KNXAction>();
        actionList.add(action);
        return actionList;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(LIGHT_IS_ON_PARAM)){
            lightIsOn = savedInstanceState.getBoolean(LIGHT_IS_ON_PARAM);
        }else{
            lightIsOn = false;
        }
        updateGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "AndroidBite Voice Recognition...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            resultList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches));
//            if(matches.contains("an")){
//                lightOn();
//            }else if(matches.contains("aus")) {
//                lightOff();
//            }

            for(String match : matches){
                if(voiceCommandsMapping.containsKey(match)){
                    executeKNXActions(voiceCommandsMapping.get(match));
                    break;
                }

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void executeKNXActions(List<KNXAction> actions){
        StringBuilder sb = new StringBuilder();
        sb.append("Executing:\n");
        for(KNXAction action : actions){
            if(action.name.equals("Licht anschalten"))
                lightOn();
            if(action.name.equals("Licht ausschalten"))
                lightOff();
            sb.append(action.name).append(" - ").append(action.gruppenadresse).append(" - ").append(action.daten).append("\n");
        }
        Toast.makeText(getApplicationContext(), sb.toString(),
                Toast.LENGTH_LONG).show();
    }

    private void lightOn(){
        lightIsOn = true;
        updateGui();
    }
    private void lightOff(){
        lightIsOn = false;
        updateGui();
    }

    private void updateGui(){
        if(lightIsOn){
            lightImage.setImageResource(R.drawable.light_bulb_on);
            tv.setText("Licht an");
        }else{
            lightImage.setImageResource(R.drawable.light_bulb_off);
            tv.setText("Licht aus");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LIGHT_IS_ON_PARAM, lightIsOn);
    }
}
