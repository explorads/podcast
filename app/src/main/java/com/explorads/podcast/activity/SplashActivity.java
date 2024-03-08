package com.explorads.podcast.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.explorads.podcast.error.CrashReportWriter;
import com.explorads.podcast.storage.database.PodDBAdapter;
import com.google.android.gms.ads.MobileAds;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Shows the AntennaPod logo while waiting for the main activity to start.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(() -> false); // Keep splash screen active


        MobileAds.initialize(this);

        Completable.create(subscriber -> {
                    // Trigger schema updates
                    PodDBAdapter.getInstance().open();
                    PodDBAdapter.getInstance().close();
                    subscriber.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        overridePendingTransition(0, 0);
//                        finish();


                            MobileAds.initialize(this, initializationStatus -> {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            });

                        }, error -> {
                        error.printStackTrace();
                        CrashReportWriter.write(error);
                        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        finish();

//                            MobileAds.initialize(this, initializationStatus -> {
//                                error.printStackTrace();
//                                CrashReportWriter.write(error);
//                                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                                finish();
//                            });

                        });
    }
}
