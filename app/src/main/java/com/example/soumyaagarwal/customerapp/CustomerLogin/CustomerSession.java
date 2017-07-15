package com.example.soumyaagarwal.customerapp.CustomerLogin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SoumyaAgarwal on 7/11/2017.
 */

public class CustomerSession {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int mode=0;
    String prefname="SESSION";
    private String is_loggedin = "is_loggedin";
    private String username = "username";

    public CustomerSession(Context context)
    {
        this._context=context;
        pref = _context.getSharedPreferences(prefname,mode);
        editor = pref.edit();
    }

    public void create_oldusersession(String username_get)
    {
        editor.putBoolean(is_loggedin,true);
        editor.putString(username,username_get);
        editor.putString("designation","coordinator");
        editor.commit();
    }

    public Boolean isolduser()
    {
        return pref.getBoolean(is_loggedin,false);
    }

    public String getUsername()
    {
        return pref.getString(username,"");
    }

    public void clearoldusersession()
    {
        editor.clear();
        editor.commit();
    }
}

