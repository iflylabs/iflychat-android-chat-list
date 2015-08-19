package com.iflylabs.iFlyChatExampleGlobalListView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.iflylabs.iFlyChatLibrary.iFlyChatConfig;
import com.iflylabs.iFlyChatLibrary.iFlyChatService;
import com.iflylabs.iFlyChatLibrary.iFlyChatUserAuthService;
import com.iflylabs.iFlyChatLibrary.iFlyChatUserSession;




public class MainActivity extends SherlockFragmentActivity {


    protected iFlyChatService service;
    private iFlyChatConfig config;
    private iFlyChatUserSession userSession;
    private iFlyChatUserAuthService authService;

    private String serverHost = "api.iflychat.com", sessionKey = "",
            authUrl = "http://your.website.com/auth-url";

    private Context mContext;
    int currentFragment;
    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    com.actionbarsherlock.app.ActionBar actionBar;
    com.actionbarsherlock.widget.SearchView searchview;
    com.actionbarsherlock.app.ActionBar.TabListener tabListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tablayout);

        userSession = new iFlyChatUserSession("username", "password");

        config = new iFlyChatConfig(serverHost, authUrl, true,
                userSession);

        config.setAutoReconnect(true);

        mContext = getApplicationContext();
        authService = new iFlyChatUserAuthService(config, userSession, mContext);

        sessionKey = userSession.getSessionKey();

        service = new iFlyChatService(userSession, config, authService, mContext);

        service.connectChat(sessionKey);

        currentFragment = 0;
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        Tab = (ViewPager) findViewById(R.id.pager);

        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar = getSupportActionBar();
                        actionBar.setSelectedNavigationItem(position);


                    }
                });

        Tab.setAdapter(TabAdapter);
        actionBar = getSupportActionBar();
        //	Enable Tabs on Action Bar
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);

        final com.actionbarsherlock.app.ActionBar.Tab users = getSupportActionBar().newTab();
        final com.actionbarsherlock.app.ActionBar.Tab rooms = getSupportActionBar().newTab();

        LayoutInflater inflater = getLayoutInflater();

        final View users_tab_view = inflater.inflate(R.layout.custom_tab, null);
        final View rooms_tab_view = inflater.inflate(R.layout.custom_tab_rooms, null);

        users.setCustomView(users_tab_view);
        rooms.setCustomView(rooms_tab_view);

        ((TextView) users_tab_view.findViewById(R.id.custom_tab_title)).setText("Users");
        ((TextView) rooms_tab_view.findViewById(R.id.custom_tab_title)).setText("Rooms");

        tabListener = new com.actionbarsherlock.app.ActionBar.TabListener() {

            @Override
            public void onTabSelected(com.actionbarsherlock.app.ActionBar.Tab tab,
                                      android.support.v4.app.FragmentTransaction ft) {
                Tab.setCurrentItem(tab.getPosition());

                searchview = (com.actionbarsherlock.widget.SearchView) findViewById(R.id.searchviewuser);
                if (searchview != null) {

                    searchview.setIconified(true);
                    searchview.setIconified(true);
                }
                searchview = (com.actionbarsherlock.widget.SearchView) findViewById(R.id.searchviewroom);
                if (searchview != null) {

                    searchview.setIconified(true);
                    searchview.setIconified(true);
                }

                //to set tab's icon
                if (tab.getPosition() == 0) {

                    (users_tab_view.findViewById(R.id.custom_tab_icon)).setBackgroundResource(R.drawable.selected_user);
                    (rooms_tab_view.findViewById(R.id.custom_tab_icon)).setBackgroundResource(R.drawable.group_unselected);
                    ((TextView) users_tab_view.findViewById(R.id.custom_tab_title)).setTextColor(Color.parseColor("#0000EE"));
                    ((TextView) rooms_tab_view.findViewById(R.id.custom_tab_title)).setTextColor(Color.parseColor("#CCCCCC"));
                    currentFragment = 0;

                } else {

                    (users_tab_view.findViewById(R.id.custom_tab_icon)).setBackgroundResource(R.drawable.unselected_user);
                    (rooms_tab_view.findViewById(R.id.custom_tab_icon)).setBackgroundResource(R.drawable.group_selected);
                    ((TextView) users_tab_view.findViewById(R.id.custom_tab_title)).setTextColor(Color.parseColor("#CCCCCC"));
                    ((TextView) rooms_tab_view.findViewById(R.id.custom_tab_title)).setTextColor(Color.parseColor("#0000EE"));
                    currentFragment = 1;
                }

            }

            @Override
            public void onTabUnselected(com.actionbarsherlock.app.ActionBar.Tab tab,
                                        android.support.v4.app.FragmentTransaction ft) {

                searchview = (com.actionbarsherlock.widget.SearchView) findViewById(R.id.searchviewuser);
                if (searchview != null) {

                    searchview.setIconified(true);
                    searchview.setIconified(true);
                }
                searchview = (com.actionbarsherlock.widget.SearchView) findViewById(R.id.searchviewroom);
                if (searchview != null) {

                    searchview.setIconified(true);
                    searchview.setIconified(true);
                }

// 	TODO Auto-generated method stub
            }

            @Override
            public void onTabReselected(
                    com.actionbarsherlock.app.ActionBar.Tab tab,
                    android.support.v4.app.FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }
        };

        getSupportActionBar().addTab(users.setTabListener(tabListener));
        getSupportActionBar().addTab(rooms.setTabListener(tabListener));

        getSupportActionBar().setDisplayOptions(com.actionbarsherlock.app.ActionBar.DISPLAY_SHOW_CUSTOM | com.actionbarsherlock.app.ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        getSupportActionBar().setCustomView(R.layout.abs_user_layout);

        getSupportActionBar().setIcon(R.drawable.chat_icon);
    }

    public com.actionbarsherlock.app.ActionBar.Tab createTab(int view, int titleView, String title) {
        com.actionbarsherlock.app.ActionBar.Tab tab = getSupportActionBar().newTab();

        tab.setCustomView(view);

        return tab;
    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
         super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();

    }


    @Override
    protected void onDestroy() {
        service.disconnectChat();
        super.onDestroy();

    }


}
