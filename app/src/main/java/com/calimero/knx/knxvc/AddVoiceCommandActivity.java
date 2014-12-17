package com.calimero.knx.knxvc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class AddVoiceCommandActivity extends Activity {

    List<VoiceCommand> allVoiceCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voice_command);

        ListView listView = (ListView) findViewById(R.id.listView);
        allVoiceCommand = MainActivity.masterDao.getAllVoiceCommand();
        listView.setAdapter(new ArrayAdapter<VoiceCommand>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                allVoiceCommand));
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
