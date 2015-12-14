package com.iflylabs.iflychatexamplegloballistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.iflylabs.iFlyChatLibrary.iFlyChatRoom;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by iflylabs on 03/08/15.
 */
public class RoomAdapter extends ArrayAdapter<iFlyChatRoom> {

    Context context;
    private Filter roomFilter;
    List<iFlyChatRoom> roomList, originalRooms;
    RoomHolder holder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    public RoomAdapter(List<iFlyChatRoom> rooms, Context ctx) {

        super(ctx, R.layout.list_row);

        this.roomList = rooms;
        this.context = ctx;
        this.originalRooms = rooms;

    }

    public void resetData() {
        roomList = originalRooms;
    }

    public int getCount() {
        return roomList.size();
    }


    @Override
    public iFlyChatRoom getItem(int position) {
        return roomList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return roomList.get(position).hashCode();

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;


        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_row, null);
            // Now we can fill the layout with the right values

            holder = new RoomHolder(v);
            v.setTag(holder);


        } else
            holder = (RoomHolder) v.getTag();

        String roomName = roomList.get(position).getName();
        holder.tvname.setText(roomName);

        holder.tvstatusImage.setVisibility(View.GONE);
        holder.userImage2.setVisibility(View.VISIBLE);

        Drawable placeHolder = ContextCompat.getDrawable(context, R.drawable.home);
        holder.userImage2.setImageDrawable(placeHolder);
        holder.userImage2.setBorderColor(mColorGenerator.getColor(roomName));


        return v;
    }

    private static class RoomHolder {
        private TextView tvname;
        private ImageView tvstatusImage;
        private CircularImageView userImage2;
        private View view;


        private RoomHolder(View view) {
            this.view = view;
            tvname = (TextView) view.findViewById(R.id.user_name); //  name
            tvstatusImage = (ImageView) view.findViewById(R.id.user_image); // thumb image
            userImage2 = (CircularImageView) view.findViewById(R.id.user_image2);
        }

    }

    @Override
    public Filter getFilter() {
        if (roomFilter == null)
            roomFilter = new PlanetFilter();
        return roomFilter;
    }


    private class PlanetFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = originalRooms;
                results.count = originalRooms.size();
            } else {
                // We perform filtering operation
                List<iFlyChatRoom> nroomlist = new ArrayList<iFlyChatRoom>();

                for (iFlyChatRoom p : roomList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nroomlist.add(p);
                }

                results.values = nroomlist;
                results.count = nroomlist.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {

                roomList = (List<iFlyChatRoom>) results.values;
                notifyDataSetInvalidated();

            } else {
                roomList = (List<iFlyChatRoom>) results.values;
                notifyDataSetChanged();
            }

        }

    }



}
