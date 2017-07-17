package com.example.soumyaagarwal.customerapp.CustomerLogin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.soumyaagarwal.customerapp.Model.NameAndStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.soumyaagarwal.customerapp.CustomerApp.DBREF;

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

    public void create_oldusersession(final String username_get)
    {

        DatabaseReference dbOnlineStatus = DBREF.child("Users").child("Usersessions").child(username_get).getRef();
        dbOnlineStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NameAndStatus nameAndStatus = dataSnapshot.getValue(NameAndStatus.class);
                    editor.putString(is_loggedin,"true");
                    editor.putString(username,username_get);
                    editor.putString("name",nameAndStatus.getName());
                    editor.commit();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

