package com.example.workmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
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

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button btnStartWork, btnStopWork, btnGetWorkUpdates;
    TextView txtResult;
    private WorkRequest workRequest;
    private Context context;
    private Constraints constraints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartWork = findViewById(R.id.btnStartWork);
        btnGetWorkUpdates = findViewById(R.id.btnGetWorkUpdates);
        btnStopWork = findViewById(R.id.btnStopWork);
        txtResult = findViewById(R.id.txtResult);

        this.context = this;

        btnStartWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d("TAG", "Starting the work!");
                Toast.makeText(context, "Starting the work!", Toast.LENGTH_SHORT).show();

                Data data = new Data.Builder().putString("key", "IT wala").build();

                constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                workRequest = new OneTimeWorkRequest.Builder(SimpleWork.class)
                        .addTag("firstWork")
                        .setConstraints(constraints)
                        .setInputData(data)
                        .setInitialDelay(1, TimeUnit.SECONDS)
                        .setBackoffCriteria(BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.SECONDS)
                        .build();
                WorkManager.getInstance(context).enqueue(workRequest);
            }
        });

        btnStopWork.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Operation operation =
                        WorkManager.getInstance(context).cancelWorkById(workRequest.getId());
                SimpleWork.isStopped = true;
                Log.d("TAG", "Stopping the work!");
                Toast.makeText(context, "Stopping the work!", Toast.LENGTH_SHORT).show();
            }
        });

        btnGetWorkUpdates.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    WorkInfo workInfo =
                            WorkManager.getInstance(context).getWorkInfoById(workRequest.getId())
                                    .get();

                    Log.d("TAG", workInfo.toString());
                    String updates = workInfo.toString();
                    txtResult.setText("Updates: " + updates);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}