package com.Gogo.Manga.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.Gogo.Manga.Activity.DetailActivity;
import com.Gogo.Manga.Activity.MainActivity;
import com.Gogo.Manga.Activity.SplashActivity;
import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.model.Bookmark;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG ="MyFirebaseMessagingServiceTAG";

    private ArrayList<Bookmark> bookmarks = new ArrayList<>( );

    @Override
    public void onNewToken(String s) {
        Log.i("getToken", s);
    }
    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // remoteMessage object contains all you need ;-)
//        String notificationTitle = remoteMessage.getNotification().getTitle();
//        String notificationContent = remoteMessage.getNotification().getBody();
        String notificationTitle = remoteMessage.getData().get("judul");
        String notificationContent = remoteMessage.getData().get("isi");
        String imageUrl = remoteMessage.getData().get("image");
        // lets create a notification with these data
        if (notificationTitle!=null) {
            Log.i("NotificationTest", notificationTitle);
            new  ParsePageTask(   ).execute( Constant.base_url+ "latest-releases/1");
         //   new  ParsePageTask(   ).execute("https://manganato.com/genre-all/1");
        }else {
            showNotification(remoteMessage.getNotification().getBody());
        }

    }

    class ParsePageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                Document mBlogDocument = Jsoup.connect(urls[0]).get();
                // Using Elements to get the Meta data

                Element link =mBlogDocument.getElementById("wrapper-inner");

                return link.toString();
            } catch (Exception ignored) {
            }

            return "";
        }

        protected void onPostExecute(String result) {
            try {
                Document  document = Jsoup.parse(result);
                Elements mElementDataSize2 = document.select( "div#content")
                        .select("div.dark-segment").select("ul.latest-updates").select("li.segment-poster-sm");

                Log.d(TAG, "onPostExecute: "+mElementDataSize2.toString());
                String tittle = mElementDataSize2.first().select("div.poster-subject").select("h2").text();
                String image =  Constant.base_url+mElementDataSize2.first().select("img").attr("data-src");
                String link = Constant.base_url+mElementDataSize2.first().select("div.poster-subject").select("h2").select("a").attr("href");

                String chap = mElementDataSize2.first().select("div.poster-subject").
                        select("ul.chapters").select("li")  .first()  .text();
                if (chekPref()==null || chekPref().equals("") || !chekPref().equals(link)) {
//                    for (int i = 0; i < bookmarks.size(); i++) {
//                        Log.d(TAG, "onPostExecute: "+bookmarks.get(i).getUrl());
//                        if (bookmarks.get(i).getUrl().equals(link)){
//                            createNotification(getString(R.string.app_name), tittle +" "+chap, fetchGambar(image) ,link);
//                            savePref(link);
//                            break;
//                        }
//                    }

                    createNotification(getString(R.string.app_name), tittle +" Chapter"+chap, fetchGambar(image) ,link);
                    savePref(link);
                }else {
                    Log.i("NotificationTest","UDAHHHH");
                }
            } catch (NullPointerException | IndexOutOfBoundsException n) {
                Log.d(TAG, n.getMessage());
                n.getMessage();
            }
        }
    }


    private void createNotification(String notificationTitle, String notificationContent, String imageUrl, String link){
        Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
        notificationIntent.putExtra("link", link);
        notificationIntent.putExtra("from", "Notif");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(DetailActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

        // Let's create a notification builder object
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getResources().getString(R.string.notification_channel_id));
        // Create a notificationManager object
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        // If android version is greater than 8.0 then create notification channel
        if (android.os.Build.VERSION.SDK_INT >=     android.os.Build.VERSION_CODES.O) {

            // Create a notification channel
            NotificationChannel notificationChannel = new NotificationChannel(getResources().getString(R.string.notification_channel_id),    getResources().getString(R.string.notification_channel_name),     NotificationManager.IMPORTANCE_DEFAULT);
            // Set properties to notification channel
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300});

            // Pass the notificationChannel object to notificationManager
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
// Set the notification parameters to the notification builder object
        builder.setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setSmallIcon(R.mipmap.logo)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
// Set the image for the notification
        if (imageUrl != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            builder.setStyle(
                                    new NotificationCompat.BigPictureStyle()
                                            .bigPicture(resource)
                                            .bigLargeIcon(null)
                            ).setLargeIcon(resource);


                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

//            Bitmap bitmap = getBitmapfromUrl( imageUrl);
//            builder.setStyle(
//                    new NotificationCompat.BigPictureStyle()
//                            .bigPicture(bitmap)
//                            .bigLargeIcon(null)
//            ).setLargeIcon(bitmap);
            notificationManager.notify(1, builder.build());
        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }

    public String fetchGambar(String html){
        try {

            Document document = Jsoup.parse(html);
            String image = document.select("img").attr("src").replace("w200-h113", "");
            Log.i("NotificationTest", image);
            return image;
        }catch (NullPointerException n) {
            n.getMessage();
            return "";
        }
    }

    public void savePref(String url){
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("link", String.valueOf(url));
        editor.commit();
    }

    private String chekPref(){
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        try {

            return  pref.getString("link",null);
        }catch (NullPointerException n){
            return "";
        }
    }


    public void showNotification(String message) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("from", "Notif");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

        String id = "main channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)  getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (notificationManager != null) {

                notificationManager.createNotificationChannel(notificationChannel);

            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), id);
        notificationBuilder.setSmallIcon(R.mipmap.logo);
        notificationBuilder.setContentTitle(getString(R.string.app_name));
        notificationBuilder.setContentText(message);
        notificationBuilder.setLights(Color.WHITE, 500, 5000);
        notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(1400, notificationBuilder.build());
    }
}
