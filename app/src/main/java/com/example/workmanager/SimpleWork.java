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

public class SimpleWork extends Worker {

    private ExecutorService executorService;
    private Handler handler;
    private Context context;
    public static boolean isStopped;
    private int progress = 10;

    public SimpleWork(@NonNull Context context,
                      @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        this.context = context;
        setProgressAsync(new Data.Builder().putInt("PROGRESS", 0).build());
    }

    @NonNull @Override public Result doWork() {
        String inputData = getInputData().getString("key");
        handler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(context, "inputData: " + inputData, Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 1; i <= 100; i++) {
            if (isStopped) {
                break;
            }
            int data = i;
            progress = ((i * 100) / 100);

            handler.post(new Runnable() {
                @Override public void run() {
                    //ui task
                    Log.d("TAG", String.valueOf(data));
                    Toast.makeText(context, "Data: " + data, Toast.LENGTH_LONG).show();
                }
            });

            try {
                setProgressAsync(new Data.Builder().putInt("PROGRESS", progress).build());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }
}
