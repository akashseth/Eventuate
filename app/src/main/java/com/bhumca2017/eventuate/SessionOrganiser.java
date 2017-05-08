package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by toaka on 02-05-2017.
 */

public class SessionOrganiser {

    SharedPreferences pref;
    Editor editor;
    private Context mContext;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "EventuateOrganiser";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_Organizer_Email = "OrganizerEmail";
    private static final String KEY_drawer_flag_profile_input= "drawer_flag_profile_input";
    private static final String KEY_drawer_flag_event_input = "drawer_flag_event_input";
    private static final String KEY_Organizer_NAME = "OrganizerName";
    private static final String KEY_JSON_STRING = "jsonString";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_BUDGET_LEFT = "budget_left";


    public SessionOrganiser (Context context) {

        mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String jsonString) {

        // parsing the json string
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String OrganizerEmail = jsonObject.getString("EmailId");
            Boolean drawer_flag_profile_input=jsonObject.getBoolean("OrganizerProfileInput");
            Boolean drawer_flag_event_input=jsonObject.getBoolean("OrganizerEventInput");
            String OrganizerName = jsonObject.getString("OrganizerName");

            editor.putBoolean(IS_LOGIN,true);
            editor.putString(KEY_JSON_STRING,jsonString);

            editor.putString(KEY_Organizer_Email,OrganizerEmail);
            editor.putBoolean(KEY_drawer_flag_profile_input,drawer_flag_profile_input);
            editor.putBoolean(KEY_drawer_flag_event_input,drawer_flag_event_input);
            editor.putString(KEY_Organizer_NAME,OrganizerName);
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getOrganiserEmail(){

        return pref.getString(KEY_Organizer_Email,"");
    }
    public String getOrganiserName(){

        return pref.getString(KEY_Organizer_NAME,"");
    }
    public Boolean getDrawerFlagProfileInput() {


        return pref.getBoolean(KEY_drawer_flag_profile_input,false);
    }
    public Boolean getDrawerFlagEvenInput() {


        return pref.getBoolean(KEY_drawer_flag_event_input,false);
    }

    public Boolean isLoggedIn(){

        return pref.getBoolean(IS_LOGIN,false);
    }

    public String getJSonString(){

        return pref.getString(KEY_JSON_STRING,"");
    }
    public void updateBudget(Integer budget){

        editor.putInt(KEY_BUDGET,budget);
        editor.commit();
    }

    public void updateBudgetLeft(Integer budgetLeft){

        editor.putInt(KEY_BUDGET_LEFT,budgetLeft);
        editor.commit();
    }

    public int getBudget(){

        return pref.getInt(KEY_BUDGET,-1);
    }

    public int getBudgetLeft(){

        return pref.getInt(KEY_BUDGET_LEFT,-1);
    }
}
