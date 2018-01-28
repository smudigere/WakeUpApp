package com.fashiontechwakeup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener{

    private TextView alarmTime;
    private SharedPreferences prefs;
    private List<List<String>> reminders;

    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        Button on = (Button) findViewById(R.id.on);
        Button off = (Button) findViewById(R.id.off);
        Button setAlarm = (Button) findViewById(R.id.set_alarm);
        Button addReminder = (Button) findViewById(R.id.addReminder);
        addReminder.setOnClickListener(this);
        setAlarm.setOnClickListener(this);
        on.setOnClickListener(this);
        off.setOnClickListener(this);

        alarmTime = (TextView) findViewById(R.id.alarm_time);
        alarmTime.setText(prefs.getString("Alarm Time", ""));

        try {

            RemindersDatabase database = new RemindersDatabase(getApplicationContext());
            reminders = database.getEvents();

            for (int i = 0; i < reminders.size(); i++)  {
                long expiry = Long.parseLong(reminders.get(i).get(2));

                if (expiry < System.currentTimeMillis())
                    database.deleteEntry(Integer.parseInt(reminders.get(i).get(0)));
            }

            reminders = database.getEvents();

            database.close();

            ListView listView = (ListView) findViewById(R.id.listView);
            adapter = new ListViewAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(this);

        } catch (Exception e)   {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        alarmTime.setText(prefs.getString("Alarm Time", ""));

        if (!String.valueOf(getIntent().getExtras()).equals("null")) {
            if (getIntent().getExtras().containsKey("TURN OFF")) { //Runs when the user clicks on a Notification.

                getIntent().removeExtra("TURN OFF");    //Required to avoid opening the coupon upon resuming.

                new ConnectToArduino().execute("http://10.19.14.148/socket2Off");
            }
        }

        try {

            RemindersDatabase database = new RemindersDatabase(getApplicationContext());
            reminders = database.getEvents();

            database.close();

            ListView listView = (ListView) findViewById(R.id.listView);
            adapter = new ListViewAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(this);

        } catch (Exception e)   {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())   {

            case R.id.off:
                new ConnectToArduino().execute("http://10.19.14.148/socket2Off");
                break;
            case R.id.on:
                new ConnectToArduino().execute("http://10.19.14.148/socket2On");
                break;
            case R.id.set_alarm:
                startActivity(new Intent(getApplicationContext(), SetAlarm.class));
                break;
            case R.id.addReminder:
                startActivity(new Intent(getApplicationContext(), AddReminder.class));
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}


    private class ConnectToArduino extends AsyncTask<String, Void, Boolean> {

        private String API_RESULT;

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                API_RESULT = HttpConnection.httpConnection(params[0]);

                return true;
            } catch (Exception e)   {
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }


    /**
     * A adapter class to populate the listview.
     */
    private class ListViewAdapter extends BaseAdapter {

        /**
         * How many items are in the data set represented by this Adapter.
         * @return   Count of items.
         */
        @Override
        public int getCount() {
            try {
                return reminders.size();
            } catch (Exception e)   {
                return 0;
            }
        }

        /**
         * Get the data item associated with the specified position in the data set.
         * @param position    Position of the item whose data we want within the adapter's data set.
         * @return  The data at the specified position.
         */
        @Override
        public Object getItem(  int position) {
            return position;
        }

        /**
         * Get the row id associated with the specified position in the list.
         * @param position   The position of the item within the adapter's data set whose row id we want.
         * @return    The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either create a
         * View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...)
         * will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a
         * root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view we want.
         * @param convertView       The old view to reuse, if possible. Note: You should check that
         *                      this view is non-null and of an appropriate type before using.
         *                      If it is not possible to convert this view to display the correct data,
         *                      this method can create a new view. Heterogeneous lists can specify their number of
         *                      view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
         * @param parent      The parent that this view will eventually be attached to
         * @return    A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)    {
                LayoutInflater inflater = (LayoutInflater)  getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview, parent, false);
            }

            try {

                TextView remindersText = (TextView) convertView.findViewById(R.id.reminderText);
                remindersText.setText(reminders.get(position).get(1));

                TextView expiryText = (TextView) convertView.findViewById(R.id.expiryText);

                Date date = new Date(Long.parseLong(reminders.get(position).get(2)));
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                expiryText.append(format.format(date));

            } catch (Exception e)   {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}