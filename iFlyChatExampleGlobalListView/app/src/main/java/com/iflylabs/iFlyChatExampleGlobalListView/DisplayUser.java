package com.iflylabs.iflychatexamplegloballistview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.iflylabs.iFlyChatLibrary.iFlyChatRoster;
import com.iflylabs.iFlyChatLibrary.iFlyChatUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class DisplayUser extends Fragment {

    View display_user;
    ListView list;
    BroadcastReceiver receiver;
    private iFlyChatRoster roster;
    private List<iFlyChatUser> userList;
    private LinkedHashMap<String, iFlyChatUser> users;
    UserAdapter bindingData;
    private HashMap<String,String> chatSettings;

    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatSettings = (HashMap<String, String>) getArguments().getSerializable("chatSettings");

        display_user = inflater.inflate(R.layout.main, container, false);
        setHasOptionsMenu(true);

        mDrawableBuilder = TextDrawable.builder().round();

        list = (ListView) display_user.findViewById(R.id.user_list);

        // To receive Broadcast event(iFlyChat.onGlobalListUpdate) from iFlyChatLibrary.
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("iFlyChat.onGlobalListUpdate")) {

                    roster = intent.getParcelableExtra("globalList");
                    users = new LinkedHashMap<String, iFlyChatUser>();
                    users = roster.getUserList();
                    userList = new ArrayList<iFlyChatUser>(users.values());
                    bindData();
                }
            }
        };
        return display_user;
    };

    // Bind data of view with the UserAdapter class.
    public void bindData() {

        list = (ListView) display_user.findViewById(R.id.user_list);
        //Bind the data with listview
        bindingData = new UserAdapter(userList, getActivity(), mDrawableBuilder, chatSettings);
        //After binding
        list.setAdapter(bindingData);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Configure the search info and add any event listeners
        searchView.setId(R.id.searchviewuser);


        searchItem.setActionView(searchView);
        searchItem.setIcon(R.drawable.ic_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (bindingData != null) {

                    bindingData.resetData();
                    bindingData.getFilter().filter(newText.toString());
                }

                return false;
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver((receiver), new IntentFilter("iFlyChat.onGlobalListUpdate"));
    }


    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(receiver);
        super.onStop();
    }


}