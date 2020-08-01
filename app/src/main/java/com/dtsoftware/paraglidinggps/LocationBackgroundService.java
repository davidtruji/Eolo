package com.dtsoftware.paraglidinggps;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class LocationBackgroundService extends Service {


    // NotificationManager usado para mostrar la notificación permanente de la app
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private final int NOTIFICATION_ID = 0;
    private final String NOTIFICATION_CHANNEL_ID = "channelID";
    HandlerThread handlerThread;
    Looper looper;
    Handler handler;

    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(getString(R.string.debug_tag), "LocationRunnable Thread ID: "+Thread.currentThread().getId());
            handler.postDelayed(this,2000);
        }
    };


    //TODO: Separar ejecución del servicio a otro thread distinto al de la UI




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);

        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();
        handler = new Handler(looper);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getString(R.string.debug_tag), "Service Thread ID: "+Thread.currentThread().getId());
        //stopSelf();
        handler.post(locationRunnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION_ID);
        handler.removeCallbacks(locationRunnable);
        handlerThread.quit();
        // Tell the user we stopped.
        Log.i(getString(R.string.debug_tag), "Service Stopped");
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Channel", NotificationManager.IMPORTANCE_HIGH);
            mNM.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Background Location Service")
                .setOngoing(true)
                .setContentText("El servicio de localización en segundo plano está ejecutandose")
                .setSmallIcon(R.drawable.compass_on);

        mNM.notify(NOTIFICATION_ID, notification.build());
    }

}
