package com.alquerias.textoyfuente;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override

    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Looper.prepare();
        Handler handler = new Handler();
        handler.post(() -> {
            Toast.makeText(getBaseContext(), remoteMessage.getNotification().getTitle(), Toast.LENGTH_LONG).show();
        });
        Looper.loop();
    }
}

