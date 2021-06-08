package com.example.workmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyOneTimeWork extends Worker {

    private Handler handler;
    private Context context;
    public static boolean isStopped;
    private int progress = 10;

    public MyOneTimeWork(@NonNull Context context,
                         @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        handler = new Handler(Looper.getMainLooper());
        this.context = context;
        setProgressAsync(new Data.Builder().putInt("PROGRESS", 0).build());
        isStopped = false;
    }

    @NonNull @Override public Result doWork() {
        String inputData = getInputData().getString("key");
        handler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(context, "one time work inputData: " + inputData,
                        Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 1; i <= 5; i++) {
            if (isStopped) {
                break;
            }
            int data = i;
            progress = ((i * 100) / 100);

            handler.post(new Runnable() {
                @Override public void run() {
                    //ui task
                    Log.d("TAG", String.valueOf(data));
                    Toast.makeText(context, "One time work data: " + data, Toast.LENGTH_LONG).show();
                }
            });

            try {
                setProgressAsync(new Data.Builder().putInt("PROGRESS", progress).build());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //output data
        Data outputData = new Data.Builder().putString("output", "Some_Output_Data").build();
        return Result.success(outputData);
    }
}
