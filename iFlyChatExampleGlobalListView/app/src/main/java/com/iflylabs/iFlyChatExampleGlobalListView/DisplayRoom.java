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

import com.iflylabs.iFlyChatLibrary.iFlyChatRoom;
import com.iflylabs.iFlyChatLibrary.iFlyChatRoster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DisplayRoom extends Fragment {
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
    };

    // Bind data of view with the RoomAdapter class.
    public void bindData() {

        list = (ListView) display_room.findViewById(R.id.user_list);

        //Bind the data with listview
        bindingData = new RoomAdapter(roomList, getActivity());

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
        searchView.setId(R.id.searchviewroom);

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