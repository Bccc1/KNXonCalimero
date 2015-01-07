package com.calimero.knx.knxvc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.calimero.knx.knxvc.core.KnxAction;

import java.util.ArrayList;
import java.util.List;


public class AddVoiceCommandActivity extends Activity {

    List<KnxAction> allKnxActions;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voice_command);

        listView = (ListView) findViewById(R.id.listView);
        allKnxActions = MainActivity.masterDao.getAllKnxAction();
        listView.setAdapter(new ArrayAdapter<KnxAction>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                allKnxActions));
        Button addBtn = (Button) findViewById(R.id.add_vc_btn);
        if(addBtn!=null) {
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createVC();
                }
            });
        }else{
            Log.d("AddVCActivity", "button not found");
        }
    }

    private void createVC(){
        VoiceCommand vc = new VoiceCommand();
        EditText editText = (EditText) findViewById(R.id.editText_vc);
        vc.setName(editText.getText().toString());
        Log.d("AddVCActivity","Name is "+vc.getName());
        ArrayList<KnxAction> knxActions = new ArrayList<>();

        SparseBooleanArray checked = listView.getCheckedItemPositions();
        int size = checked.size();
        for(int i=0;i< size; i++){
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value) {
                KnxAction ac = (KnxAction) listView.getItemAtPosition(key);
                knxActions.add(ac);
                Log.d("AddVCActivity","Added action: "+ac.getName());
            }
        }

        vc.setActions(knxActions);
        vc.setProfile("0");


        MainActivity.masterDao.saveVoiceCommand(vc);

        Log.d("AddVCActivity","Saved the vc");
        setResult(RESULT_OK);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_voice_command, menu);
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
}
