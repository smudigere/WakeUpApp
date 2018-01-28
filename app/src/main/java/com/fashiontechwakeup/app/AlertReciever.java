package com.fashiontechwakeup.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AlertReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(getClass().toString(), String.valueOf(System.currentTimeMillis()));

        String command = intent.getStringExtra(context.getString(R.string.COMMAND));

        new ConnectToArduino().execute(command);

        PendingIntent notificIntent = PendingIntent.getActivity(context, 100,
                new Intent(context, MainActivity.class).putExtra("TURN OFF", false), PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);      //Notification sound.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setContentTitle("Wake Up")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setSound(defaultSoundUri)
                .setContentIntent(notificIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private class ConnectToArduino extends AsyncTask<String, Void, Boolean> {

        private String API_RESULT;

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                API_RESULT = HttpConnection.httpConnection(params[0]);

                return true;
            } catch (Exception e)   {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}