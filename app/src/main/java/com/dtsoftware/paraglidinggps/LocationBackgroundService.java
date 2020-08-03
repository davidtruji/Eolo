package com.dtsoftware.paraglidinggps;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;



public class LocationBackgroundService extends Service {


    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int NOTIFICATION_ID = 0;


    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(getString(R.string.debug_tag), "LocationRunnable Thread ID: " + Thread.currentThread().getId());





            // Subproceso que obtendrá la localización (¿Será necesario en el MapBox SDK? o ya usará subprocesos él solo)






        }
    };



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);



        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getString(R.string.debug_tag), "Service Thread ID: "+Thread.currentThread().getId());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION_ID);
        // Tell the user we stopped.
        Log.i(getString(R.string.debug_tag), "Service Stopped");
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {

        String NOTIFICATION_CHANNEL_ID = "channelID";
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Channel", NotificationManager.IMPORTANCE_HIGH);
            mNM.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Background Location Service")
                .setOngoing(true)
                .setContentText("El servicio de localización en segundo plano está ejecutandose")
                .setSmallIcon(R.drawable.compass_on);

        mNM.notify(NOTIFICATION_ID, notification.build());
    }


}
