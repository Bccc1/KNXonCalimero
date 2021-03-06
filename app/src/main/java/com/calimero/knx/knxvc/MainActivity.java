package com.calimero.knx.knxvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.calimero.knx.knxvc.core.KnxAction;
import com.calimero.knx.knxvc.core.KnxActionFactory;
import com.calimero.knx.knxvc.core.Profile;
import com.calimero.knx.knxvc.core.VoiceInterpreter;
import com.calimero.knx.knxvc.dao.MasterDao;
import com.calimero.knx.knxvc.dao.VoiceCommandDao;
import com.calimero.knx.knxvc.xml.XmlKnxActionFactory;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class MainActivity extends Activity implements VoiceControlFragment.OnVoiceControlInteractionListener, VoiceCommandFragment.OnVoiceCommandInteractionListener, VoiceCommandListFragment.Callbacks{

    public static final String PROJECTING_FILE = "/sdcard/windows/BstSharedFolder/projectingdemo.xml";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static MasterDao masterDao;

    public static final int REQUEST_CODE_ADD_VC = 90;

    VoiceCommandFragment voiceCommandFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        masterDao = new MasterDao(getApplicationContext());
        try {
            masterDao.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void importXml() {

        Log.d("init XML","loading from XML file");
        XmlPullParserFactory xmlFactoryObject = null;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();

            File projectingConfig = new File(PROJECTING_FILE);

            if (projectingConfig.exists()) {
                FileInputStream stream = new FileInputStream(projectingConfig);
                myparser.setInput(stream, null);

                XmlKnxActionFactory factory = new XmlKnxActionFactory(myparser);
                factory.writeToDatabase(this);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadDummyXml() throws IOException {

        boolean targetExists = false;
        long targetSize = 0l;
        File targetFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        targetExists = targetFolder.exists();
        targetSize = targetFolder.length();

        String targetFile = targetFolder.getAbsolutePath() + "/projectingdemo.xml";
        targetExists = new File(targetFile).exists();
        targetSize = new File(targetFile).length();

        int sourceId = getResources().getIdentifier("raw/projectingdemo",
                "raw", getPackageName());

        InputStream inputStream = getResources().openRawResource(sourceId);
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        // IOUtils is a class from Apache Commons IO
        // It writes an InputStream to an OutputStream
        IOUtils.copy(inputStream, fileOutputStream);
        fileOutputStream.close();

        targetExists = new File(targetFile).exists();
        targetSize = new File(targetFile).length();
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

        switch(id){
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_add_voice_command:
                openNewVoiceCommand();
                return true;
            case R.id.action_loadTestData:
                loadTestDateIntoDB();
                return true;
            case R.id.action_importXmlData:
                importXml();
                return true;
            case R.id.action_downloadDummyXml:
                try {
                    downloadDummyXml();
                    Toast.makeText(this,
                            "Leere XML-Projektierung befindet sich im Downloads-Verzeichnis.",
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,
                            "Bitte beachten: Dateierzeugung ist nur erfolgreich, "
                             + "wenn KEINE USB-Verbindung besteht.",
                            Toast.LENGTH_LONG).show();
                }
                catch (IOException e) {
                    Toast.makeText(this,
                            "Download der leeren XML-Projektierung schlug fehl.",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_drop_db:
                dropDatabase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    private void dropDatabase(){
        masterDao.deleteAllCommands();
        masterDao.deleteAllActions();
        voiceCommandFragment.refresh();
    }

    private void openNewVoiceCommand(){
        Intent newVoiceCommandIntent = new Intent(this, AddVoiceCommandActivity.class);
        //newVoiceCommandIntent.putExtra();
        startActivityForResult(newVoiceCommandIntent, REQUEST_CODE_ADD_VC);
    }

    private void loadTestDateIntoDB(){
        for(KnxAction ac: KnxActionFactory.getKNXActionsAsList()) {
            masterDao.saveKnxAction(ac);
        }

        Profile profile = new Profile();
        profile.setId(0);
        profile.setName("Default");
        //Testdaten laden
        VoiceCommandDao vcdao = VoiceCommandDao.getInstance();
        vcdao.createVoiceCommandMapping();
        for(VoiceCommand vc : vcdao.getVoiceCommands()){
            vc.setProfile(String.valueOf(profile.getId()));
            masterDao.saveVoiceCommand(vc);
        }

        voiceCommandFragment.refresh();
    }

    @Override
    public void onVoiceControlInteraction(Uri uri) {

    }

    @Override
    public void onVoiceCommandInteraction(Uri uri) {

    }

    @Override
    public void onItemSelected(String id) {
        boolean mTwoPane = true; //Woher soll diese Stelle das wissen? Das ist doch scheiße -.-
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(VoiceCommandDetailFragment.ARG_ITEM_ID, id);
            VoiceCommandDetailFragment fragment = new VoiceCommandDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.voicecommand_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, VoiceCommandDetailActivity.class);
            detailIntent.putExtra(VoiceCommandDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    voiceCommandFragment = VoiceCommandFragment.newInstance(position + 1 + "", "mapping in here pls",masterDao);
                    return voiceCommandFragment;
                case 1:
                    return VoiceControlFragment.newInstance("a","b");
                default:
                    return PlaceholderFragment.newInstance(position + 1,"");
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_VC && resultCode == Activity.RESULT_OK) {
            Log.d("Main Activity","AddVCActivity Intended successfully closed");
            //TODO Aktualisiere VoiceCommandFragment bzw die Listview.
            voiceCommandFragment.refresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_TEXT = "section_text";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String sectionText) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_TEXT, sectionText);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

                TextView textView = (TextView) rootView.findViewById(R.id.section_text);
                if(textView != null){
                    Bundle args = this.getArguments();
                    String sectionText = args.getString(ARG_SECTION_TEXT);
                    if(sectionText!=null) {
                        textView.setText(sectionText);
                    }else{
                        Log.d("KNX - Main","sectionText is null");
                    }
                }
                else{
                    Log.d("KNX - Main","textView is null");
                }
            return rootView;
        }
    }

}
