package com.project.bmail.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "BMAIL";

    private static final String FIRST_TIME_USER = "first";
    private static final String SIGNED_USER_NAME = "userName";
    private static final String SIGNED_USER_EMAIL = "userMail";

    private SharedPreferences pref; // Shared Preferences object
    private SharedPreferences.Editor editor;
    private static SessionManager mInstance;

    private SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null)
                    mInstance = new SessionManager(context.getApplicationContext());
            }
        }
        return mInstance;
    }

    public void setFirstTimeUser(boolean isFirstTime){
        editor.putBoolean(FIRST_TIME_USER,isFirstTime);
        editor.commit();
    }
    public boolean getFirstTimeUser() {
        return pref.getBoolean(FIRST_TIME_USER,true);
    }

    public void setSignedUserName(String userName){
        editor.putString(SIGNED_USER_NAME,userName);
        editor.commit();
    }
    public String getSignedUserName(){
        return pref.getString(SIGNED_USER_NAME,null);
    }

    public void setSignedUserEmail(String email){
        editor.putString(SIGNED_USER_EMAIL,email);
        editor.commit();
    }
    private String getSignedUserEmail(){
        return pref.getString(SIGNED_USER_EMAIL,null);
    }
}
