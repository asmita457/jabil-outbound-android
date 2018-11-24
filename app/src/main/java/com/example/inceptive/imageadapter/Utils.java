package com.example.inceptive.imageadapter;

import android.content.Context;
import android.content.SharedPreferences;

class Utils {

    public static final String PrefName = "Jabil_Outbound_App";

    public static String getStringPreferences(Context con, String key)
    {
        SharedPreferences sharedPreferences = con.getSharedPreferences(PrefName, 0);
        return sharedPreferences.getString(key, "");
    }
}
