package com.assignment.fitsync;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {

    public boolean moved = false;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }

    }






    private static final String TAG = "Background Service";



    public void onCreate() {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        return START_STICKY;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Sync done", Toast.LENGTH_SHORT).show();
    }



}

