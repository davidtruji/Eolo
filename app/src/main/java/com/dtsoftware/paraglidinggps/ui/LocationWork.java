package com.dtsoftware.paraglidinggps.ui;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dtsoftware.paraglidinggps.R;

public class LocationWork extends Worker {
    public LocationWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(getApplicationContext().getString(R.string.debug_tag),"LocationWorker Thread ID: "+Thread.currentThread().getId());
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(getApplicationContext().getString(R.string.debug_tag),"LocationWorker se detuvo");

    }
}
