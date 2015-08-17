package com.iflylabs.iFlyChatExampleGlobalListView;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.iflylabs.iFlyChatLibrary.iFlyChatRoom;
import com.iflylabs.iFlyChatLibrary.iFlyChatRoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class DisplayRoom extends SherlockFragment {
    View display_room;
    ListView list;
    BroadcastReceiver receiver;
    iFlyChatRoster roster;
    List<iFlyChatRoom> roomList;

    private LinkedHashMap<String, iFlyChatRoom> rooms;
    RoomAdapter bindingData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        display_room = inflater.inflate(R.layout.main, container, false);

        setHasOptionsMenu(true);

        list = (ListView) display_room.findViewById(R.id.user_list);

        // To receive Broadcast event(iFlyChat.onGlobalListUpdate) from iFlyChatLibrary.
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("iFlyChat.onGlobalListUpdate")) {

                    roster = intent.getParcelableExtra("globalList");

                    rooms = new LinkedHashMap<String, iFlyChatRoom>();
                    rooms = roster.getRoomList();

                    roomList = new ArrayList<iFlyChatRoom>(rooms.values());
                    bindData();

                }
            }
        };

        return display_room;
    }

    ;

    // Bind data of view with the RoomAdapter class.
    public void bindData() {

            list = (ListView) display_room.findViewById(R.id.user_list);

            //Bind the data with listview
            bindingData = new RoomAdapter(roomList, getActivity());

            //After binding
            list.setAdapter(bindingData);


    }

    // To Search a room from the complete RoomList.
    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add("Search");

        com.actionbarsherlock.widget.SearchView sv = new com.actionbarsherlock.widget.SearchView(getSherlockActivity().getSupportActionBar().getThemedContext());
        sv.setId(R.id.searchviewuser);

        item.setActionView(sv);
        item.setIcon(R.drawable.ic_action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //...
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