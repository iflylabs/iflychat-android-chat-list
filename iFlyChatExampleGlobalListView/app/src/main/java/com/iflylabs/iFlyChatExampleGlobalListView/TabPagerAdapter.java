package com.iflylabs.iFlyChatExampleGlobalListView;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;
//Adapter to show users and rooms tabs data.
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
    super(fm);
    // TODO Auto-generated constructor stub
  }
  @Override
  public SherlockFragment getItem(int i) {
    switch (i) {
        case 0:
            //Fragement for Users Tab
            return new DisplayUser();
        case 1:
           //Fragment for Rooms Tab
            return new DisplayRoom();
        
        }
    return null;
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return 2; //No. of Tabs
  }
  

    }