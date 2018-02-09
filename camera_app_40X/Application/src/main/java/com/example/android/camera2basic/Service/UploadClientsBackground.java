package com.example.android.camera2basic.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * Created by root on 2/5/18.
 */

public class UploadClientsBackground extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
//
//    // Log
//    public static final String TAG = "UploadImagesJob::";
//
//    // Class variables
//    boolean isWorking = false;
//    boolean jobCancelled = false;
//
//    @Override
//    public void onCreate(){
//        Log.i(TAG, "Service created");
//    }
//
//    @Override
//    public void onDestroy(){
//        Log.i(TAG, "Service stopped");
//    }
//
//    // Called by the Android system when it's time to run the job
//    @Override
//    public boolean onStartJob(JobParameters jobParameters) {
//        Log.d(TAG, "Upload images job started!");
//        isWorking = true;
//        // We need 'jobParameters' so we can call 'jobFinished'
//        /** Services do NOT run on a separate thread. Thus, we have to create
//         * another worker thread to not overload the main thread since uploading
//         * files requires a lot of resources. */
//        startWorkOnNewThread(jobParameters);
//        return isWorking;
//    }
//
//    private void startWorkOnNewThread(final JobParameters jobParameters) {
//        new Thread(new Runnable() {
//            public void run() {
//                doWork(jobParameters);
//            }
//        }).start();
//    }
//
//    private void doWork(JobParameters jobParameters) {
//        // 10 seconds of 'working' (1000*10ms)
//        for (int i = 0; i < 1000; i++) {
//            // If the job has been cancelled, stop working; the job will be rescheduled.
//            if (jobCancelled)
//                return;
//            try {
//                Thread.sleep(10);
//            } catch (Exception e) { }
//        }
//        Log.d(TAG, "Job finished!");
//        isWorking = false;
//        boolean needsReschedule = true;
//        jobFinished(jobParameters, needsReschedule);
//    }
//
//    // Called if the job was cancelled before being finished
//    @Override
//    public boolean onStopJob(JobParameters jobParameters) {
//        Log.d(TAG, "Job cancelled before being completed.");
//        jobCancelled = true;
//        boolean needsReschedule = isWorking;
//        jobFinished(jobParameters, needsReschedule);
//        return needsReschedule;
//    }

}
