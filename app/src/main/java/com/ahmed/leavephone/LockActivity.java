package com.ahmed.leavephone;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class LockActivity extends Activity {

    static final long AWAY_MS = 5 * 60 * 1000L;

    long offAt = 0;
    final Handler handler = new Handler(Looper.getMainLooper());
    final Runnable finisher = new Runnable() {
        @Override
        public void run() {
            done();
        }
    };
    BroadcastReceiver rec;

    void done() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.cancel(1);
        finishAndRemoveTask();
    }

    void checkAway() {
        if (offAt != 0 && SystemClock.elapsedRealtime() - offAt >= AWAY_MS) {
            done();
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TextView tv = new TextView(this);
        tv.setText(getSharedPreferences("cfg", MODE_PRIVATE).getString("msg", "سيب الموبايل"));
        tv.setTextSize(44);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.BLACK);
        setContentView(tv);

        rec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                if (Intent.ACTION_SCREEN_OFF.equals(i.getAction())) {
                    offAt = SystemClock.elapsedRealtime();
                    handler.removeCallbacks(finisher);
                    handler.postDelayed(finisher, AWAY_MS);
                } else if (Intent.ACTION_SCREEN_ON.equals(i.getAction())) {
                    checkAway();
                    if (!isFinishing()) {
                        handler.removeCallbacks(finisher);
                        offAt = 0;
                    }
                }
            }
        };

        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_SCREEN_OFF);
        f.addAction(Intent.ACTION_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(rec, f, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(rec, f);
        }
    }

    @Override
    public void onBackPressed() {
        // مفيش رجوع
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(finisher);
        try {
            unregisterReceiver(rec);
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
