package com.pushbunny;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONObject;

import com.onesignal.OSDeviceState;
import com.onesignal.OSNotification;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;
import com.pushbunny.Database.DatabaseHelper;
import com.pushbunny.Models.NotificationModel;

import java.math.BigInteger;
import java.util.Date;

@SuppressWarnings("unused")
public class NotificationServiceExtension implements OSRemoteNotificationReceivedHandler {

    DatabaseHelper db;

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();

        // Example of modifying the notification's accent color
        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            db = new DatabaseHelper(context);

            // Sets the accent color to Green on Android 5+ devices.
            // Accent color controls icon and action buttons on Android 5+. Accent color does not change app title on Android 10+
            builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            // Sets the notification Title to Red
            Spannable spannableTitle = new SpannableString(notification.getTitle());
            spannableTitle.setSpan(new ForegroundColorSpan(Color.BLACK),0,notification.getTitle().length(),0);
            builder.setContentTitle(spannableTitle);
            // Sets the notification Body to Blue
            Log.d("dxdiag : ", spannableTitle.toString());
            Spannable spannableBody = new SpannableString(notification.getBody());

            Log.d("dxdiag : ", spannableBody.toString());
            spannableBody.setSpan(new ForegroundColorSpan(Color.GRAY),0,notification.getBody().length(),0);
            builder.setContentText(spannableBody);

            String image = notification.getBigPicture() == null ? "" : notification.getBigPicture();

            String otherData = notification.getAdditionalData() == null
                    || notification.getAdditionalData().equals("{}")
                    || notification.getAdditionalData().equals("")
                    ? "" : notification.getAdditionalData().toString();

            Log.d("dxdiag : ","image data : " + image);
            Log.d("dxdiag : ","other data : " + otherData);

            boolean inserted = db.insertData(spannableTitle.toString(), spannableBody.toString(), new Date().getTime()+"", image, otherData);
            if (inserted = true) {
                Log.d("dxdiag : ", "Data Inserted Successfully");

            } else {
                Log.d("dxdiag : ", "Data Insertion Failed");
            }
            //Force remove push from Notification Center after 30 seconds
            builder.setTimeoutAfter(3000000);
            return builder;
        });
        JSONObject data = notification.getAdditionalData();
        Log.d("dxdiag", "Received Notification Data: " + data);

        // If complete isn't call within a time period of 25 seconds, OneSignal internal logic will show the original notification
        // To omit displaying a notification, pass `null` to complete()
        notificationReceivedEvent.complete(mutableNotification);
    }
}
