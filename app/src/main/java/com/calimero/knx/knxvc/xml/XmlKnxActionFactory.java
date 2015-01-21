package com.calimero.knx.knxvc.xml;

import android.content.Context;
import android.util.Log;

import com.calimero.knx.knxvc.core.KnxAction;
import com.calimero.knx.knxvc.dao.MasterDao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates ActionFactory instances from a XML file.
 * Created by Jonas on 21.01.2015.
 */
public class XmlKnxActionFactory {

    private final XmlPullParser content;
    private List<KnxAction> actions;

    public XmlKnxActionFactory(XmlPullParser content) throws XmlPullParserException, IOException {

        this.content = content;
        readData();
    }

    private void readData() throws XmlPullParserException, IOException {

        actions = new ArrayList<>();

        int event = content.getEventType();
        while (event != XmlPullParser.END_DOCUMENT)
        {
            String tagName=content.getName();
            tagName = (tagName == null) ? "" : tagName;

            switch (tagName) {
                case "Action":

                    if (content.getEventType() == XmlPullParser.START_TAG) {

                        // we are now within an "Action" tag. Extract attribute values!
                        String id = content.getAttributeValue(null, "id");
                        String name = content.getAttributeValue(null, "name");
                        String data = content.getAttributeValue(null, "data");
                        String groupAddress = content.getAttributeValue(null, "groupAddress");

                        KnxAction action = new KnxAction(name);
                        action.setId(Integer.parseInt(id));
                        action.setData(data);
                        action.setGroupAddress(groupAddress);

                        Log.d("getKNXActionsAsList", "created Action: " + action.toString());
                        actions.add(action);
                    }



                default:
                    break;
            }
            event = content.next();
        }
    }

    public List<KnxAction> getKNXActionsAsList() throws XmlPullParserException, IOException {

        return new ArrayList<>(actions);
    }

    public void writeToDatabase(Context context) {

        MasterDao masterDao = new MasterDao(context);
        try {

            masterDao.open();
            for(KnxAction ac: actions) {
                masterDao.saveKnxAction(ac);
            }
        } catch (SQLException e) {

            Log.d("writeToDatabase", "Failed to write KnxActions to database: " + e.getMessage());
        } finally {

            masterDao.close();
        }
    }
}
