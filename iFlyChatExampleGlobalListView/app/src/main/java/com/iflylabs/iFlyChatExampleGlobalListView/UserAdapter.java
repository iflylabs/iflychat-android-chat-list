package com.iflylabs.iflychatexamplegloballistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by iflylabs on 03/08/15.
 */

// Class to download user images from the avatarUrl of the user.
public class UserAdapter extends ArrayAdapter<iFlyChatUser> {

    Context context;
    private Filter userFilter;
    List<iFlyChatUser> userList, originalUsers;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    int i=0;
    private HashMap<String ,String>chatSettings;
    private boolean defaultUserImageFlag =false;





//    UserHolder holder;

    public UserAdapter(List<iFlyChatUser> users, Context ctx, TextDrawable.IBuilder mDrawableBuilder, HashMap<String, String> chatSettings) {

        super(ctx, R.layout.list_row);

        this.userList = users;
        this.context = ctx;
        this.mDrawableBuilder = mDrawableBuilder;
        this.originalUsers = users;
        this.chatSettings = chatSettings;
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

            holder = new UserHolder(v);
            v.setTag(holder);

        } else
            holder = (UserHolder) v.getTag();


        holder.tvname.setText(userList.get(position).getName());

        setChatImage(holder, userList.get(position).getId(), userList.get(position).getName(), userList.get(position).getAvatarUrl());

        return v;
    }

    /**
     * Method to set gmail like circular images in view
     * @param holder
     * @param id user id
     * @param userName
     * @param avatarUrl
     */
    private void setChatImage(UserHolder holder, String id, String userName, String avatarUrl){

        String upToNCharacters = id.substring(0, Math.min(id.length(), 2));
        //User without Prefix
        if(!upToNCharacters.equals("0-")){

            if(!avatarUrl.equals(null) && !avatarUrl.equals("")) {

                if (avatarUrl.contains("default_avatar") || avatarUrl.contains("gravatar")) {

                    if (defaultUserImageFlag == true) {

                        holder.userImage2.setVisibility(View.VISIBLE);
                        holder.tvstatusImage.setVisibility(View.GONE);
                        Drawable placeholder;

                        if (holder.number == 0)
                            placeholder = ContextCompat.getDrawable(context, R.drawable.male_user);
                        else
                            placeholder = ContextCompat.getDrawable(context, R.drawable.female_user);

                        holder.userImage2.setImageDrawable(placeholder);
                        holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));

                    } else {
                        char firstLetter = userName.charAt(0);
                        String name = Character.toString(firstLetter);
                        if(name.matches("[a-zA-Z]+")) {

                            holder.userImage2.setVisibility(View.GONE);
                            holder.tvstatusImage.setVisibility(View.VISIBLE);
                            char upperCaseLetter = Character.toUpperCase(userName.charAt(0));
                            TextDrawable drawable = mDrawableBuilder.build(String.valueOf(upperCaseLetter), mColorGenerator.getColor(userName));
                            holder.tvstatusImage.setImageDrawable(drawable);
                            holder.view.setBackgroundColor(Color.TRANSPARENT);
                        }
                        //Name Contains number or special character
                        else {

                            holder.userImage2.setVisibility(View.VISIBLE);
                            holder.tvstatusImage.setVisibility(View.GONE);
                            Drawable placeholder;

                            if(holder.number==0)
                                placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                            else
                                placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                            holder.userImage2.setImageDrawable(placeholder);
                            holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));

                        }

                    }
                } else {

                    if (holder.userImage2 != null) {
                        holder.tvstatusImage.setVisibility(View.GONE);
                        holder.userImage2.setVisibility(View.VISIBLE);

                        // check for url.
                        if (cancelPotentialDownload(avatarUrl, holder.userImage2)) {
                            GetUsersTask task = new GetUsersTask(holder.userImage2);
                            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                            holder.userImage2.setImageDrawable(downloadedDrawable);
                            task.execute("http:" + avatarUrl);

                        }
                    }
                }
            }
            else if(defaultUserImageFlag==true) {

                holder.userImage2.setVisibility(View.VISIBLE);
                holder.tvstatusImage.setVisibility(View.GONE);
                Drawable placeholder;

                if(holder.number==0)
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                else
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                holder.userImage2.setImageDrawable(placeholder);
                holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));
            }
            else{
                char firstLetter = userName.charAt(0);
                String name = Character.toString(firstLetter);
                if(name.matches("[a-zA-Z]+")) {

                    holder.userImage2.setVisibility(View.GONE);
                    holder.tvstatusImage.setVisibility(View.VISIBLE);
                    char upperCaseLetter = Character.toUpperCase(userName.charAt(0));
                    TextDrawable drawable = mDrawableBuilder.build(String.valueOf(upperCaseLetter), mColorGenerator.getColor(userName));
                    holder.tvstatusImage.setImageDrawable(drawable);
                    holder.view.setBackgroundColor(Color.TRANSPARENT);
                }
                //Name Contains number or special character
                else {

                    holder.userImage2.setVisibility(View.VISIBLE);
                    holder.tvstatusImage.setVisibility(View.GONE);
                    Drawable placeholder;

                    if(holder.number==0)
                        placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                    else
                        placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                    holder.userImage2.setImageDrawable(placeholder);
                    holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));

                }
            }

        }
        else{
            if(defaultUserImageFlag==true){

                if (holder.userImage2 != null) {
                    holder.tvstatusImage.setVisibility(View.GONE);
                    holder.userImage2.setVisibility(View.VISIBLE);

                    // check for url.
                    if (cancelPotentialDownload(avatarUrl, holder.userImage2)) {
                        GetUsersTask task = new GetUsersTask(holder.userImage2);
                        DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                        holder.userImage2.setImageDrawable(downloadedDrawable);
                        task.execute("http:" + avatarUrl);

                    }
                }
            }
            else{

                String value  = chatSettings.get("guestPrefix");
                String name = userName.replaceFirst(value,"");

                if(name.matches("[a-zA-Z]+")) {

                    holder.userImage2.setVisibility(View.GONE);
                    holder.tvstatusImage.setVisibility(View.VISIBLE);
                    char upperCaseLetter = Character.toUpperCase(name.charAt(0));

                    TextDrawable drawable = mDrawableBuilder.build(String.valueOf(upperCaseLetter), mColorGenerator.getColor(name));
                    holder.tvstatusImage.setImageDrawable(drawable);
                    holder.view.setBackgroundColor(Color.TRANSPARENT);
                }
                else{

                    holder.userImage2.setVisibility(View.VISIBLE);
                    holder.tvstatusImage.setVisibility(View.GONE);
                    Drawable placeholder;

                    if(holder.number==0)
                        placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                    else
                        placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                    holder.userImage2.setImageDrawable(placeholder);
                    holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));
                }
            }
        }

    }


    private void updateCheckedState(UserHolder holder, String id, String userName) {

        String upToNCharacters = id.substring(0, Math.min(id.length(), 2));

        //User with Prefix
        if(upToNCharacters.equals("0-")){
            String value  = (String)chatSettings.get("guestPrefix");
            String name = userName.replaceFirst(value,"");

            if(name.matches("[a-zA-Z]+")){
                holder.userImage2.setVisibility(View.GONE);
                holder.tvstatusImage.setVisibility(View.VISIBLE);
            char upperCaseLetter = Character.toUpperCase(name.charAt(0));

            TextDrawable drawable = mDrawableBuilder.build(String.valueOf(upperCaseLetter), mColorGenerator.getColor(name));
            holder.tvstatusImage.setImageDrawable(drawable);
            holder.view.setBackgroundColor(Color.TRANSPARENT);
            }
            ////Name Contains number or special character
            else{
                holder.userImage2.setVisibility(View.VISIBLE);
                holder.tvstatusImage.setVisibility(View.GONE);
                Drawable placeholder;
                if(holder.number==0)
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                else
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                holder.userImage2.setImageDrawable(placeholder);
                holder.userImage2.setBorderColor(mColorGenerator.getColor(name));

            }
        }
        //Normal User
        else {
                // Name contains letters
            if(userName.matches("[a-zA-Z]+")) {
                holder.userImage2.setVisibility(View.GONE);
                holder.tvstatusImage.setVisibility(View.VISIBLE);
                char upperCaseLetter = Character.toUpperCase(userName.charAt(0));
                TextDrawable drawable = mDrawableBuilder.build(String.valueOf(upperCaseLetter), mColorGenerator.getColor(userName));
                holder.tvstatusImage.setImageDrawable(drawable);
                holder.view.setBackgroundColor(Color.TRANSPARENT);
            }
            //Name Contains number or special character
            else {

                holder.userImage2.setVisibility(View.VISIBLE);
                holder.tvstatusImage.setVisibility(View.GONE);
                Drawable placeholder;

                if(holder.number==0)
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.male_user);
                else
                    placeholder =   ContextCompat.getDrawable(context, R.drawable.female_user);

                holder.userImage2.setImageDrawable(placeholder);
                holder.userImage2.setBorderColor(mColorGenerator.getColor(userName));

            }
        }

    }



    private static class UserHolder {
        private TextView tvname;
        private ImageView tvstatusImage;
        private CircularImageView userImage2;

        private View view;
        private int number;


        private UserHolder(View view) {

            this.view = view;
            tvname = (TextView) view.findViewById(R.id.user_name); //  name
            tvstatusImage = (ImageView) view.findViewById(R.id.user_image); // thumb image
            userImage2 = (CircularImageView) view.findViewById(R.id.user_image2);
            Random ran = new Random();
            int number = ran.nextInt(2);
            this.number = number;
        }
    }

    @Override
    public Filter getFilter() {
        if (userFilter == null)
            userFilter = new PlanetFilter();

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
