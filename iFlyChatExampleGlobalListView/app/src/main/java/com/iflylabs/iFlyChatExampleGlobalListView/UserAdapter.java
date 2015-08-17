package com.iflylabs.iFlyChatExampleGlobalListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflylabs.iFlyChatLibrary.iFlyChatUser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iflylabs on 03/08/15.
 */

// Class to download user images from the avatarUrl of the user.
public class UserAdapter extends ArrayAdapter<iFlyChatUser> {

    Context context;
    private Filter userFilter;
    List<iFlyChatUser> userList, originalUsers;
//    UserHolder holder;

    public UserAdapter(List<iFlyChatUser> users, Context ctx) {

        super(ctx, R.layout.list_row);

        this.userList = users;
        this.context = ctx;
        this.originalUsers = users;
    }

    public void resetData() {
        userList = originalUsers;
    }

    public int getCount() {
        return userList.size();
    }


    @Override
    public iFlyChatUser getItem(int position) {
        return userList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return userList.get(position).hashCode();

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        UserHolder holder;
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

        holder.tvname.setText(userList.get(position).getName());

        // get AvatarUrl of the user and if it is null or empty show the default Url.
        String avatarUrl = userList.get(position).getAvatarUrl();
        if (avatarUrl.equals(null) || avatarUrl.equals("")) {
            avatarUrl = "//cdn.iflychat.com/mobile/images/default_avatar.png";
        }


        // Create an object for subclass of AsyncTask
        if (holder.tvstatusImage != null) {
            // check for url.
            if (cancelPotentialDownload(avatarUrl, holder.tvstatusImage)) {
                GetUsersTask task = new GetUsersTask(holder.tvstatusImage);
                DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                holder.tvstatusImage.setImageDrawable(downloadedDrawable);
                task.execute("http:" + avatarUrl);
            }
        }


        return v;
    }

    private static class UserHolder {
        TextView tvname;
        ImageView tvstatusImage;

    }

    @Override
    public Filter getFilter() {
        if (userFilter == null)
            userFilter = new PlanetFilter();
        System.out.println(userFilter);
        return userFilter;
    }

    // Filter to Search a user from the complete UserList.
    private class PlanetFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = originalUsers;
                results.count = originalUsers.size();
            } else {
                // We perform filtering operation
                List<iFlyChatUser> nuserlist = new ArrayList<iFlyChatUser>();

                for (iFlyChatUser p : userList) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nuserlist.add(p);
                }

                results.values = nuserlist;
                results.count = nuserlist.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0) {

                userList = (List<iFlyChatUser>) results.values;
                notifyDataSetInvalidated();

            } else {
                userList = (List<iFlyChatUser>) results.values;
                notifyDataSetChanged();
            }

        }

    }

    //AsyncTask to download the image from url asynchronously
    private class GetUsersTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> rowImageView;
        private String url;

        public GetUsersTask(ImageView imageView) {

            rowImageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                this.url = url;
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
                GetUsersTask getUsersTask = getGetUsersTask(imageView);
                // Change bitmap only if this process is still associated with it
                if (this == getUsersTask) {
                    if (imageView != null) {
                        if (result != null) {
                            Drawable drawable = new BitmapDrawable(result);
                            imageView.setImageDrawable(drawable);
                        } else {
                            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.default_avatar);
                            imageView.setImageDrawable(placeholder);
                        }
                    }

                }

            }

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

    // Create a week reference of the async task.
    static class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<GetUsersTask> getUsersTaskReference;


        public DownloadedDrawable(GetUsersTask getUsersTask) {

            getUsersTaskReference =
                    new WeakReference<GetUsersTask>(getUsersTask);
        }

        public GetUsersTask getGetUsersTask() {
            return getUsersTaskReference.get();
        }


    }

    // Check for validity of the URL.
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        GetUsersTask getUsersTask = getGetUsersTask(imageView);

        if (getUsersTask != null) {
            String bitmapUrl = getUsersTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                getUsersTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static GetUsersTask getGetUsersTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getGetUsersTask();
            }
        }
        return null;
    }
}
