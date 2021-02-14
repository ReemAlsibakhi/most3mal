package com.reemsib.mst3jl.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.reemsib.mst3jl.R;
import com.reemsib.mst3jl.activity.AdvertDetailActivity;
import com.reemsib.mst3jl.activity.chat.ChatRoomActivity;
import com.reemsib.mst3jl.model.PushNotification;
import com.reemsib.mst3jl.setting.PreferencesManager;
import com.reemsib.mst3jl.utils.Constants;
import org.jetbrains.annotations.NotNull;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private PreferencesManager manager;
    private static int count = 0;
    private int reviewsCurrentCount;
    private int chatsCurrentCount;
    PushNotification pushNot;
    JsonParser parser;
    Gson gson;
    JsonElement mJson;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       // Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "Msg received From: " + remoteMessage.getFrom());

        manager=new PreferencesManager(getApplicationContext());
         gson = new Gson();
        parser = new JsonParser();
        reviewsCurrentCount=manager.getRevsCount();
        chatsCurrentCount=manager.getChatsCount();
        Log.e("chat",chatsCurrentCount+"");
        Log.e("review",reviewsCurrentCount+"");
        // Check if message contains a data payload.
        //Here notification is recieved from server
       if (remoteMessage.getData().size() > 0 ) {
            Log.e("Message_payload",remoteMessage.getData().toString());
            mJson = parser.parse(remoteMessage.getData().get("moreData").toString());
           pushNot = gson.fromJson(mJson, PushNotification.class);
           showNotification(pushNot);
        }
       if (remoteMessage.getNotification()!=null){
              mJson = parser.parse(remoteMessage.getData().get("notification").toString());
             pushNot = gson.fromJson(mJson, PushNotification.class);
             showNotification(pushNot);
       }
        Intent pushNotification = new Intent("notify");
        pushNotification.putExtra("chat id",Integer.valueOf(pushNot.getTarget_id()));
        pushNotification.putExtra("type",Integer.valueOf(pushNot.getMsgType()));
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
    private void showNotification(PushNotification pushDat) {
        PendingIntent pendingIntent = null;

        Intent intentChat = new Intent(this, ChatRoomActivity.class);
        intentChat.putExtra(Constants.CHAT_ID,Integer.valueOf(pushDat.getTarget_id()));

        Intent intentReview = new Intent(this, AdvertDetailActivity.class);
        intentReview.putExtra(Constants.ADVERT_ID,Integer.valueOf(pushDat.getTarget_id()));

        if (pushNot.getMsgType().equals("2")){
            reviewsCurrentCount++;
            Log.e("service_rev",reviewsCurrentCount+"");
            manager.setRevsCount(reviewsCurrentCount);
            pendingIntent = PendingIntent.getActivity(this, 0 , intentReview, PendingIntent.FLAG_ONE_SHOT);

        }else if (pushNot.getMsgType().equals("3")) {
            chatsCurrentCount++;
            manager.setChatsCount(chatsCurrentCount);
            pendingIntent = PendingIntent.getActivity(this, 0 , intentChat, PendingIntent.FLAG_ONE_SHOT);
            Log.e("service_chat",chatsCurrentCount+"");
        }

        System.out.println("msgType + targetId"+pushDat.getMsgType()+" و "+pushDat.getTarget_id());

        intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentReview.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(pushDat.getTitle())
                .setContentText(pushDat.getBody())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "com.myApp";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(channelId);
        }
        notificationManager.notify(count, builder.build());
        count++;
    }

    @Override
    public void onNewToken(@NotNull String token) {
        Log.e(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        sendRegistrationToServer(token);
        Log.e("fcm token:",token);
    }

    private void sendRegistrationToServer(String token) {
       new PreferencesManager(getApplicationContext()).setFcmToken(token);
        Log.e("fcm token:",token);

    }



}
