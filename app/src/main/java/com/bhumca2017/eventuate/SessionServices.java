package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by toaka on 12-05-2017.
 */

public class SessionServices{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "EventuateServices";

    private static final String KEY_USER_ID = "userID";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_SERVICES_OPTED = "servicesOpted";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_MOB_NO = "mobileNo";
    private static final String KEY_Address = "address";
    private static final String KEY_EMAIL_ID = "emailId";


   public SessionServices(Context context) {

        pref = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();

   }

   public void createLoginSession(int userId,String emailId){

       editor.putInt(KEY_USER_ID,userId);
       editor.putBoolean(KEY_IS_LOGGED_IN,true);
       editor.putString(KEY_EMAIL_ID,emailId);
       editor.commit();
   }

   public Boolean isLoggedIn(){

       return pref.getBoolean(KEY_IS_LOGGED_IN,false);
   }

   public void logout(){

       editor.clear();
       editor.commit();
   }
   public int getUserId(){

       return pref.getInt(KEY_USER_ID,-1);
   }
   public void updateProfile(Set<String> set,String fullName, String mobNo, String address){

       editor.putStringSet(KEY_SERVICES_OPTED,set);
       editor.putString(KEY_FULL_NAME,fullName);
       editor.putString(KEY_MOB_NO,mobNo);
       editor.putString(KEY_Address,address);
       editor.commit();
   }

   public Set<String> getServicesOpted(){

       return pref.getStringSet(KEY_SERVICES_OPTED,new HashSet<String>());

   }

   public String getFullName(){

       return pref.getString(KEY_FULL_NAME,"");
   }

    public String getMobNo(){

        return pref.getString(KEY_MOB_NO,"");
    }

    public String getAddress(){

        return pref.getString(KEY_Address,"");
    }

    public String getEmailId(){

        return pref.getString(KEY_EMAIL_ID,"");
    }

}

