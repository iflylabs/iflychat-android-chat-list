package com.iflylabs.iFlyChatExampleGlobalListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflylabs.iFlyChatLibrary.iFlyChatRoom;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iflylabs on 06/08/15.
 */


/**
 * Created by iflylabs on 03/08/15.
 */
public class RoomAdapter extends ArrayAdapter<iFlyChatRoom> {

    Context context;
    private Filter roomFilter;
    List<iFlyChatRoom> roomList, originalRooms;
    UserHolder holder;

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

            holder = new UserHolder();

            holder.tvname = (TextView) v.findViewById(R.id.user_name); //  name
            holder.tvstatusImage = (ImageView) v.findViewById(R.id.user_image); // thumb image


            v.setTag(holder);


        } else
            holder = (UserHolder) v.getTag();

        holder.tvname.setText(roomList.get(position).getName());

        String avatarUrl = roomList.get(position).getAvatarUrl();
        if (avatarUrl.equals(null) || avatarUrl.equals("")) {
            avatarUrl = "//cdn.iflychat.com/mobile/images/default_room.png";

        }

        ImageView imageView = holder.tvstatusImage;
        // Create an object for subclass of AsyncTask
        GetRoomsTask task = new GetRoomsTask(imageView);
        DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
        holder.tvstatusImage.setImageDrawable(downloadedDrawable);
        task.execute("http:" + avatarUrl);

        return v;
    }

    private static class UserHolder {
        TextView tvname;
        ImageView tvstatusImage;

    }

    @Override
    public Filter getFilter() {
        if (roomFilter == null)
            roomFilter = new PlanetFilter();
        System.out.println(roomFilter);
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


    private class GetRoomsTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private WeakReference<ImageView> rowImageView;

        public GetRoomsTask(ImageView imageView) {

            rowImageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {

            if (isCancelled()) {
                result = null;
            }
            if (rowImageView != null) {
                ImageView imageView = rowImageView.get();
                GetRoomsTask getXMLTask = getGetRoomsTask(imageView);
                // Change bitmap only if this process is still associated with it
                if (this == getXMLTask) {
                    if (imageView != null) {
                        if (result != null) {
                            Drawable drawable = new BitmapDrawable(result);
                            imageView.setImageDrawable(drawable);
                        } else {
                            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.default_room);
                            imageView.setImageDrawable(placeholder);
                        }
                    }

                }

            }

            rowImageView.get().setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    static class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<GetRoomsTask> getRoomsTaskReference;

        public DownloadedDrawable(GetRoomsTask getRoomsTask) {

            getRoomsTaskReference =
                    new WeakReference<GetRoomsTask>(getRoomsTask);
        }

        public GetRoomsTask getGetRoomsTask() {
            return getRoomsTaskReference.get();
        }


    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        GetRoomsTask getRoomsTask = getGetRoomsTask(imageView);

        if (getRoomsTask != null) {
            String bitmapUrl = getRoomsTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                getRoomsTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static GetRoomsTask getGetRoomsTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getGetRoomsTask();
            }
        }
        return null;
    }
}
