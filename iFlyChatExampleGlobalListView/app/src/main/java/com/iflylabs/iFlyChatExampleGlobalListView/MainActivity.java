package com.iflylabs.iflychatexamplegloballistview;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.iflylabs.iFlyChatLibrary.iFlyChatConfig;
import com.iflylabs.iFlyChatLibrary.iFlyChatService;
import com.iflylabs.iFlyChatLibrary.iFlyChatUserAuthService;
import com.iflylabs.iFlyChatLibrary.iFlyChatUserSession;
import com.iflylabs.iFlyChatLibrary.util.iFlyChatUtilities;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    protected iFlyChatService service;
    protected iFlyChatConfig config;
    private iFlyChatUserSession userSession;
    private iFlyChatUserAuthService authService;

    private String serverHost = "", sessionKey = "",
            authUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //iFlyChat Library objects
        iFlyChatUtilities.setiFlyChatContext(getApplicationContext());
        iFlyChatUtilities.setIsDebug(true);

        userSession = new iFlyChatUserSession("", "");

        config = new iFlyChatConfig(serverHost, authUrl,
                false);
        config.setAutoReconnect(true);

        authService = new iFlyChatUserAuthService(config, userSession);

        sessionKey = userSession.getSessionKey();
        config.getIflychatSettings(sessionKey);

        HashMap<String,String> chatSettings = config.getChatSettings();

        service = new iFlyChatService(userSession, config, authService);

        service.connectChat(sessionKey);

        //To set the screen UI

        toolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, chatSettings);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(6);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));

        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
// two tabs are set here

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("USERS");
        tabOne.setTypeface(null, Typeface.BOLD);
        tabOne.setTextSize(14);
        tabOne.setTextColor(getResources().getColorStateList(R.color.selector));
        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabOne.setSelected(true);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("ROOMS");
        tabTwo.setTypeface(null, Typeface.BOLD);
        tabTwo.setTextSize(14);
        tabTwo.setTextColor(getResources().getColorStateList(R.color.selector));
        tabLayout.getTabAt(1).setCustomView(tabTwo);


    }

    private void setupViewPager(ViewPager viewPager, HashMap<String,String> chatSettings) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable("chatSettings", chatSettings);

// set Fragmentclass Arguments
        DisplayUser userObj = new DisplayUser();
        userObj.setArguments(bundle);
        adapter.addFrag(userObj, "Users");
        adapter.addFrag(new DisplayRoom(), "Rooms");

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

}