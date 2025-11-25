package com.example.af;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {

        String nome = intent.getStringExtra("nome");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "meds")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Hora do remédio!")
                .setContentText("Tomar: " + nome)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int)(System.currentTimeMillis() % 10000), builder.build());

        Log.d("ALARM", "AlarmReceiver.onReceive() nome=" + nome + " intent=" + intent);

    }

}
