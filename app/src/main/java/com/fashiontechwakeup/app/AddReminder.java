package com.fashiontechwakeup.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddReminder extends AppCompatActivity {

    private int mHour, mMinute;
    private EditText reminder_input;
    private TimePicker timePicker;
    private Calendar calendar;
    private SharedPreferences prefs;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        calendar = Calendar.getInstance();

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        ID = prefs.getInt("IDDD", 1);

        reminder_input = (EditText) findViewById(R.id.reminder_input);
        reminder_input.setText("Reminders " + ID);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                calendar.setTimeInMillis(calendarView.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Log.i("CalendarView", String.valueOf(calendar.getTimeInMillis()));
            }
        });

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

                calendar.set(Calendar.MINUTE,   mMinute);
                calendar.set(Calendar.HOUR_OF_DAY, mHour);
                calendar.set(Calendar.SECOND,   0);

                Log.i("TimePicker", String.valueOf(calendar.getTimeInMillis()));
            }
        });

        Button enterButton = (Button)  findViewById(R.id.enterButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RemindersDatabase database = new RemindersDatabase(getApplicationContext());

                database.insertData(ID, reminder_input.getText().toString(), String.valueOf(calendar.getTimeInMillis()));

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent alertIntent = new Intent(getApplicationContext(), AlertReciever.class);
                alertIntent.putExtra(getString(R.string.COMMAND), "http://10.19.14.148/socket2On");
                alertIntent.putExtra("TITLE", reminder_input.getText().toString());

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        PendingIntent.getBroadcast(getApplicationContext(), ID, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                alertIntent.putExtra(getString(R.string.COMMAND), "http://10.19.14.148/socket2Off");

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000,
                        PendingIntent.getBroadcast(getApplicationContext(), 100, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                prefs.edit().putInt("IDDD", ID + 1).apply();
                database.close();
                finishAndRemoveTask();
            }
        });
    }
}