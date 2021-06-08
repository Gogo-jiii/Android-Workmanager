package com.example.workmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyPeriodicWork extends Worker {

    private long timeMillis;
    private static int counter = 0;
    private Handler handler;
    private Context context;
    public static boolean isStopped;

    public MyPeriodicWork(@NonNull Context context,
                          @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        setProgressAsync(new Data.Builder().putString("PROGRESS", "Time: "+ 0L+"_"+"Counter: "+0).build());
        isStopped = false;
    }

    @NonNull @Override public Result doWork() {
        //get current milliseconds
        timeMillis = System.currentTimeMillis();
        counter++;

        setProgressAsync(new Data.Builder().putString("PROGRESS",
                "Time: "+ timeMillis+"_"+"Counter: "+counter).build());

        handler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(context, "Time: " + timeMillis + "_and_" + "Counter: " + counter,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return Result.success();
    }
}
