package com.example.workmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnStartOneTimeWork, btnStopOneTimeWork, btnGetOneTimeWorkUpdates,
            btnStartPeriodicWork, btnGetPeriodicWorkUpdates, btnStopPeriodicWork,
            btnStartForegroundWork, btnGetForegroundWorkUpdates, btnStopForegroundWork;
    TextView txtOneTimeWorkResult, txtPeriodicWorkResult, txtForegroundWorkResult;
    private OneTimeWorkRequest oneTimeWorkRequest;
    private PeriodicWorkRequest periodicWorkRequest;
    private WorkRequest foregroundWorkRequest;
    private Context context;
    private Constraints constraints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartOneTimeWork = findViewById(R.id.btnStartOneTimeWork);
        btnGetOneTimeWorkUpdates = findViewById(R.id.btnGetOneTimeWorkUpdates);
        btnStopOneTimeWork = findViewById(R.id.btnStopOneTimeWork);
        btnStartPeriodicWork = findViewById(R.id.btnStartPeriodicWork);
        btnGetPeriodicWorkUpdates = findViewById(R.id.btnGetPeriodicWorkUpdates);
        btnStopPeriodicWork = findViewById(R.id.btnStopPeriodicWork);
        btnStartForegroundWork = findViewById(R.id.btnStartForegroundWork);
        btnGetForegroundWorkUpdates = findViewById(R.id.btnGetForegroundWorkUpdates);
        btnStopForegroundWork = findViewById(R.id.btnStopForegroundWork);

        txtOneTimeWorkResult = findViewById(R.id.txtOneTimeWorkResult);
        txtPeriodicWorkResult = findViewById(R.id.txtPeriodicWorkResult);
        txtForegroundWorkResult = findViewById(R.id.txtForegroundWorkResult);

        this.context = this;

        btnStartOneTimeWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d("TAG", "Starting the work!");
                Toast.makeText(context, "Starting one time work!", Toast.LENGTH_SHORT).show();

                Data data = new Data.Builder().putString("key", "IT wala").build();

                constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
//                .setRequiresCharging(false)
//                .setRequiresStorageNotLow(true)
//                .setRequiresBatteryNotLow(true)

                oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyOneTimeWork.class)
                        .addTag("onetimework" + System.currentTimeMillis())
                        .setConstraints(constraints)
                        .setInputData(data)
                        .setInitialDelay(1, TimeUnit.SECONDS)
                        .setBackoffCriteria(BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS)
                        .build();
                WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
            }
        });

        btnStopOneTimeWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Operation operation =
                        WorkManager.getInstance(context).cancelWorkById(oneTimeWorkRequest.getId());
                MyOneTimeWork.isStopped = true;
                Log.d("TAG", "Stopping one time work!");
                Toast.makeText(context, "Stopping one time work!" + operation.getState(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnGetOneTimeWorkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    WorkInfo workInfo =
                            WorkManager.getInstance(context).getWorkInfoById(oneTimeWorkRequest.getId())
                                    .get();

                    Log.d("TAG", workInfo.toString());
                    String updates =
                            "\nState: " + workInfo.getState() + "\nProgress: " + workInfo.getProgress()
                                    + "\nOutputData: " + workInfo.getOutputData();
                    txtOneTimeWorkResult.setText("One time work updates: " + updates);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnStartPeriodicWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d("TAG", "Starting periodic work!");
                Toast.makeText(context, "Starting periodic work!", Toast.LENGTH_SHORT).show();

                Data data = new Data.Builder().putString("key", "IT wala periodic work").build();

                periodicWorkRequest = new PeriodicWorkRequest.Builder(MyPeriodicWork.class, 15,
                        TimeUnit.MINUTES)
                        .addTag("periodicwork" + System.currentTimeMillis())
                        .setBackoffCriteria(BackoffPolicy.LINEAR,
                                PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build();

                WorkManager.getInstance(context).enqueue(periodicWorkRequest);
            }
        });

        btnGetPeriodicWorkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    WorkInfo workInfo =
                            WorkManager.getInstance(context).getWorkInfoById(periodicWorkRequest.getId())
                                    .get();

                    Log.d("TAG", workInfo.toString());
                    String updates =
                            "\nState: " + workInfo.getState() + "\nProgress: " + workInfo.getProgress()
                                    + "\nOutputData: " + workInfo.getOutputData();
                    txtPeriodicWorkResult.setText("Periodic work updates: " + updates);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnStopPeriodicWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Operation operation =
                        WorkManager.getInstance(context).cancelWorkById(periodicWorkRequest.getId());
                MyPeriodicWork.isStopped = true;
                Log.d("TAG", "Stopping periodic work!");
                Toast.makeText(context, "Stopping periodic work!" + operation.getState(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnStartForegroundWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(context, "Starting foreground work!", Toast.LENGTH_SHORT).show();
                foregroundWorkRequest = new OneTimeWorkRequest.Builder(MyForegroundWork.class)
                        .addTag("foregroundwork" + System.currentTimeMillis())
                        .setBackoffCriteria(BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS)
                        .build();
                WorkManager.getInstance(context).enqueue(foregroundWorkRequest);
            }
        });

        btnStopForegroundWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MyForegroundWork.isStopped = true;
                Operation operation =
                        WorkManager.getInstance(context).cancelWorkById(foregroundWorkRequest.getId());
                Log.d("TAG", "Stopping foreground time work!");
                Toast.makeText(context, "Stopping foreground work!" + operation.getState(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnGetForegroundWorkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                WorkInfo workInfo = null;
                try {
                    workInfo =
                            WorkManager.getInstance(context).getWorkInfoById(foregroundWorkRequest.getId()).get();
                    Log.d("TAG", workInfo.toString());
                    String updates =
                            "\nState: " + workInfo.getState() + "\nProgress: " + workInfo.getProgress()
                                    + "\nOutputData: " + workInfo.getOutputData();
                    txtForegroundWorkResult.setText("Periodic work updates: " + updates);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}