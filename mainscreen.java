package com.example.firesmart.firesmart;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class mainscreen extends AppCompatActivity  {

    TextView temptextview;
    TextView alarmval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        temptextview = (TextView) findViewById(R.id.tempvalue);
        alarmval  = (TextView) findViewById(R.id.alarmvalue);



        LinearLayout dd = (LinearLayout)findViewById(R.id.mainbox);
        LinearLayout ad = (LinearLayout) findViewById(R.id.chack);

        dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkforchanges();
            }
        });

        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changealarmstatus();
            }
        });





    }

    @Override
    protected  void onStart()
    {
        super.onStart();
        //Log.i("info","app is opened");
        checkforchanges();
    }
    private void PushNotification(Context context, Intent intent) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.firenotification)
                .setContentTitle("There is a fire")
                .setContentText("There is fire at home now ");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    protected void moveton (View v)
    {

               // Toast.makeText(this,"numbers clicked", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
                //PushNotification(getBaseContext(),i);

    }
    protected void checkforchanges()
    {
        // this is the function that request data from the server
        new sendrealdata(1,"{\"userkey\":\"wfwrgegttrhrthr\",\"requesttype\":\"temperature\"}").execute("life");
    }

    protected void changealarmstatus()
    {
        // this is the function that is used to cancel the alarm through the api
        new sendrealdata(2,"{\"userkey\":\"wfwrgegttrhrthr\",\"requesttype\":\"cancelalarm\",\"uservalue\":\"true\"}").execute("life");
    }



    class sendrealdata extends AsyncTask<String,Void, String> {

        int resulttype = 0;
        String jsonurl = "";

        public sendrealdata(int type,String datatosend)
        {
            this.resulttype = type;
            this.jsonurl = datatosend;
        }



        protected String doInBackground(String... urls) {
            Log.i("staus","its working");


            HttpURLConnection connection = null;


            String mainresponse= "";

            try {
                //Create connection
                URL url = null;
                if(this.resulttype==1) {
                    url  = new URL("https://firesmart.herokuapp.com/access/");
                }
                else if(this.resulttype==2)
                {
                    url  = new URL("https://firesmart.herokuapp.com/action/");
                }
                else
                {

                }
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(this.jsonurl.length()));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                wr.writeBytes(this.jsonurl);
                wr.close();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                //Log.i("advancedstaus",response.toString());

                //response.toString();
                mainresponse = response.toString();



            } catch (Exception e) {

                Log.i("error",e.toString());

            }

            return mainresponse;


        }

        @Override

        protected void onPostExecute(String result) {

            // this is the part that posts the result to views
            try
            {
                JSONObject json = new JSONObject(result);
                if(this.resulttype==1)
                {
                    temptextview.setText(json.get("temperature").toString());
                    alarmval.setText(json.get("alarmstatus").toString());
                }
                else if(this.resulttype==2)
                {
                    // because the user have succesfully switched off the alarm
                    if(json.get("value").toString().equals("11"))
                    {
                        Toast.makeText(mainscreen.this,
                                "Succesfylly cancel alarm",Toast.LENGTH_LONG).show();
                        alarmval.setText("off");
                    }
                    else
                    {
                        Toast.makeText(mainscreen.this,
                                "could not cancelalarm",Toast.LENGTH_LONG).show();
                        Log.i("yepaaaa",json.get("value").toString());
                    }

                }
                else
                {
                    Log.i("oops","damn nig");
                }


            }
            catch (Exception e)
            {
                //
                Log.i("error",e.toString());
            }


        }
    }



}

