package com.example.soumyaagarwal.customerapp.tablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.soumyaagarwal.customerapp.Task.TaskHome;
import com.example.soumyaagarwal.customerapp.chat.ChatHome;

public class pager extends FragmentStatePagerAdapter
{
    int tabCount;

    public pager(FragmentManager fm, int tabCount)
    {
        super(fm);
        this.tabCount=tabCount;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                //task tab
                TaskHome a = new TaskHome();
                return a;
            case 1:
                //chat tab
                ChatHome b = new ChatHome();
                return b;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return tabCount;
    }

}