package com.calimero.knx.knxvc;

import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.calimero.knx.knxvc.core.KnxAction;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a single VoiceCommand detail screen.
 * This fragment is either contained in a {@link VoiceCommandListActivity}
 * in two-pane mode (on tablets) or a {@link VoiceCommandDetailActivity}
 * on handsets.
 */
public class VoiceCommandDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private VoiceCommand mItem;

    /** The List View */
    ListView actionListView;

    List<KnxAction> knxActionList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VoiceCommandDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MainActivity.masterDao.getVoiceCommand(Integer.valueOf(getArguments().getString(ARG_ITEM_ID)));
            //mItem = VoiceCommandDao.getInstance().getById(getArguments().getString(ARG_ITEM_ID));
            knxActionList = MainActivity.masterDao.getAllKnxAction();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voicecommand_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ArrayList<KnxAction> tempActions = new ArrayList<KnxAction>(mItem.actions);
            for(KnxAction action : knxActionList){
                if(!tempActions.contains(action))
                    tempActions.add(action);
            }

            ((TextView) rootView.findViewById(R.id.voicecommand_detail)).setText(mItem.name);
            actionListView = (ListView) rootView.findViewById(R.id.actionListView);
            ArrayAdapter arrayAdapter = new ArrayAdapter<KnxAction>(
                    getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    tempActions);
            actionListView.setAdapter(arrayAdapter);
            int pos = 0;
            for(KnxAction knxAction : mItem.actions){
                actionListView.setItemChecked(pos,true);
                pos++;
            }

            actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listViewToVoiceCommand();
                    saveVoiceCommand();
                }
            });
        }
        return rootView;
    }

    /** iterates through the listview and updates the VoiceCommand mItem */
    private void listViewToVoiceCommand(){
        List<KnxAction> actions = new ArrayList<KnxAction>();
        SparseBooleanArray checkedItemPositions = actionListView.getCheckedItemPositions();
        for(int i = 0; i<checkedItemPositions.size(); i++){
            if(checkedItemPositions.valueAt(i)){
                int key = checkedItemPositions.keyAt(i);
                actions.add((KnxAction) actionListView.getAdapter().getItem(key));
            }
        }
        mItem.setActions(actions);
    }

    private void saveVoiceCommand(){
        MainActivity.masterDao.saveVoiceCommand(mItem);
    }
}
