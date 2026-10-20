package com.ahmed.leavephone;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getSharedPreferences("cfg", MODE_PRIVATE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 60, 40, 40);

        TextView title = new TextView(this);
        title.setText("اكتب الرسالة واختار الوقت");
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        final EditText msg = new EditText(this);
        msg.setText(sp.getString("msg", "سيب الموبايل"));
        msg.setGravity(Gravity.CENTER);
        root.addView(msg);

        final TimePicker tp = new TimePicker(this);
        tp.setHour(sp.getInt("h", 22));
        tp.setMinute(sp.getInt("m", 0));
        root.addView(tp);

        Button save = new Button(this);
        save.setText("حفظ وتفعيل");
        root.addView(save);

        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        setContentView(sv);

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit()
                        .putString("msg", msg.getText().toString())
                        .putInt("h", tp.getHour())
                        .putInt("m", tp.getMinute())
                        .apply();
                AlarmReceiver.schedule(MainActivity.this);
                Toast.makeText(MainActivity.this, "تم التفعيل", Toast.LENGTH_LONG).show();

                if (Build.VERSION.SDK_INT >= 34) {
                    NotificationManager nm = getSystemService(NotificationManager.class);
                    if (!nm.canUseFullScreenIntent()) {
                        startActivity(new Intent(
                                Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                                Uri.parse("package:" + getPackageName())));
                    }
                }
            }
        });
    }
}
