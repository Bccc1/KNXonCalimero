package com.calimero.knx.knxoncalimero;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoiceControlFragment.OnVoiceControlInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoiceControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceControlFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private static final int REQUEST_CODE = 1234;
    private ListView resultList;
    Button speakButton;
    TextView tv;
    ImageView lightImage;
    Boolean lightIsOn = false;
    final String LIGHT_IS_ON_PARAM = "lightIsOn";

    KnxAdapter knxAdapter;

    VoiceCommandDao vcDao = VoiceCommandDao.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnVoiceControlInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VoiceControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoiceControlFragment newInstance(String param1, String param2) {
        Log.d("VCFragment","newInstance got called");
        VoiceControlFragment fragment = new VoiceControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public VoiceControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        knxAdapter = new KnxAdapter();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            resultList.setAdapter(new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_list_item_1, matches));

            for(String match : matches){
                if(vcDao.voiceCommandsMapping.containsKey(match)){
                    executeKNXActions(vcDao.voiceCommandsMapping.get(match).actions);
                    break;
                }

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void executeKNXActions(List<KnxAction> actions){
        StringBuilder sb = new StringBuilder();
        sb.append("Executing:\n");
        for(KnxAction action : actions){
            sb.append(action.name).append(" - ").append(action.gruppenadresse).append(" - ").append(action.daten).append("\n");
        }
        Toast.makeText(getActivity().getApplicationContext(), sb.toString(),
                Toast.LENGTH_LONG).show();

        knxAdapter.executeKnxActions(actions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_control, container, false);
        speakButton = (Button) view.findViewById(R.id.speakButton);
        resultList = (ListView) view.findViewById(R.id.vcResultListView);
        tv = (TextView) getActivity().findViewById(R.id.tvText);

        // Disable button if no recognition service is present
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speakButton.setEnabled(false);
            Toast.makeText(getActivity().getApplicationContext(), "Recognizer Not Found",
                    Toast.LENGTH_SHORT).show();
        }

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onVoiceControlInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnVoiceControlInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnVoiceControlInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnVoiceControlInteractionListener {
        // TODO: Update argument type and name
        public void onVoiceControlInteraction(Uri uri);
    }

}
