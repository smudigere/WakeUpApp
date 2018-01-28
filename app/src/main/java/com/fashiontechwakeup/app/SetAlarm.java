package com.fashiontechwakeup.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetAlarm extends AppCompatActivity {

    private int mHour, mMinute;
    private SharedPreferences prefs;

    private final static long DAY_IN_MILLI = 86400000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mHour = timePicker.getHour();
                    mMinute = timePicker.getMinute();
                } else {
                    mHour = timePicker.getCurrentHour();
                    mMinute = timePicker.getCurrentMinute();
                }
            }
        });

        Button doneButton = (Button) findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent alertIntent = new Intent(getApplicationContext(), AlertReciever.class);
                try {

                    alertIntent.putExtra(getString(R.string.COMMAND), "http://10.19.14.148/socket2On");
                    alertIntent.putExtra("TITLE", "WAKE UP");

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MINUTE,   mMinute);
                    calendar.set(Calendar.HOUR_OF_DAY, mHour);
                    calendar.set(Calendar.SECOND,   0);

                    long sys_time = calendar.getTimeInMillis();

                    if (calendar.getTimeInMillis() < System.currentTimeMillis())
                        sys_time += DAY_IN_MILLI;

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, sys_time,
                            PendingIntent.getBroadcast(getApplicationContext(), 1000, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                    alertIntent.putExtra(getString(R.string.COMMAND), "http://10.19.14.148/socket2Off");

                    alarmManager.set(AlarmManager.RTC_WAKEUP, sys_time + 30000,
                            PendingIntent.getBroadcast(getApplicationContext(), 2000, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                    prefs.edit().putString("Alarm Time", mHour + " : " + mMinute).apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                finishAndRemoveTask();
            }
        });
    }
}
