package com.example.fireapp;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the new token to your server
        sendRegistrationToServer(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        // sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
    }
/*
    private void sendNotification(String from, String body) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this.getAplicationConext(), from + ": " + body, Toast.LENGTH_LONG).show();
            }
        });
    } */


    private void sendRegistrationToServer(String token) {
        // Implement your logic to send the token to your server
    }
}