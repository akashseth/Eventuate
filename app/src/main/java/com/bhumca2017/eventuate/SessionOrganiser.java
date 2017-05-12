package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
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
    protected static final String KEY_drawer_flag_profile_input= "drawer_flag_profile_input";
    protected static final String KEY_drawer_flag_event_input = "drawer_flag_event_input";
    private static final String KEY_Organizer_NAME = "OrganizerName";
    private static final String KEY_JSON_STRING = "jsonString";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_BUDGET_LEFT = "budget_left";
    private static final String EventType = "EventType" ;
    private static final String EventDateDayOfMonth = "EventDateDayOfMonth";
    private static final String EventDateMonth = "EventDateMonth";
    private static final String EventDateYear = "EventDateYear";
    private static final String EventTimeFromHours = "EventTimeFromHours";
    private static final String EventTimeFromMinutes = "EventTimeFromMinutes";
    private static final String EventTimeToHours = "EventTimeToHours";
    private static final String  EventTimeToMinutes = "EventTimeToMinutes";
    private static final String KEY_Organizer_MobNo = "OrganizerMob";
    private static final String KEY_Organizer_Address = "OrganizerAddress";



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

    public void createNewRegisterSession(String jsonString){

        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            editor.putBoolean(IS_LOGIN,true);
            editor.putString(KEY_Organizer_Email,jsonObject.getString("EmailId"));
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

    public void saveEventDetails(String EventType,Integer EventDateDayOfMonth,Integer EventDateMonth,Integer EventDateYear,Integer EventTimeFromHours,
                            Integer EventTimeFromMinutes, Integer EventTimeToHours,Integer  EventTimeToMinutes){

        editor.putString(this.EventType,EventType);
        editor.putInt(this.EventDateDayOfMonth,EventDateDayOfMonth);
        editor.putInt(this.EventDateMonth,EventDateMonth);
        editor.putInt(this.EventDateYear,EventDateYear);
        editor.putInt(this.EventTimeFromHours,EventTimeFromHours);
        editor.putInt(this.EventTimeFromMinutes,EventTimeFromMinutes);
        editor.putInt(this.EventTimeToHours,EventTimeToHours);
        editor.putInt(this.EventTimeToMinutes,EventTimeToMinutes);
        editor.commit();
    }

    public  String getEventType(){

        return pref.getString(EventType,"");

    }
    public  Integer getEventDateDayOfMonth(){

        return pref.getInt(EventDateDayOfMonth,-1);
    }

    public  Integer getEventDateMonth(){

        return pref.getInt(EventDateMonth,-1);
    }
    public  Integer getEventDateYear(){

        return pref.getInt(EventDateYear,-1);
    }

    public  Integer getEventTimeFromHours(){

        return pref.getInt(EventTimeFromHours,-1);
    }

    public  Integer getEventTimeFromMinutes(){

        return pref.getInt(EventTimeFromMinutes,-1);
    }

    public  Integer getEventTimeToHours(){

        return pref.getInt(EventTimeToHours,-1);
    }

    public  Integer getEventTimeToMinutes(){

        return pref.getInt(EventTimeToMinutes,-1);
    }
    public void updateOrganiserName(String name){

        editor.putString(KEY_Organizer_NAME,name);
        editor.commit();
    }

    public void updateOrganiserMobNo(String mobNo){

        editor.putString(KEY_Organizer_MobNo,mobNo);
        editor.commit();
    }

    public void updateOrganiserAddress(String address){

        editor.putString(KEY_Organizer_Address,address);
        editor.commit();
    }

    public String getOrganizerMobNo(){

        return pref.getString(KEY_Organizer_MobNo,"");
    }

    public String getOrganizerAddress(){

        return pref.getString(KEY_Organizer_Address,"");
    }

    public void logout(){

        editor.clear();
        editor.commit();
    }
}
