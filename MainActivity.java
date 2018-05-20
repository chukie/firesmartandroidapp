package com.example.firesmart.firesmart;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submit = (Button) findViewById(R.id.submitid);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaterequest();
            }
        });

        //validaterequest();
    }

    protected void validaterequest()
    {
        TextView adressid = (TextView) findViewById(R.id.adressid);
        TextView stateid = (TextView) findViewById(R.id.stateid);
        TextView cityid = (TextView) findViewById(R.id.cityid);
        TextView zipid = (TextView) findViewById(R.id.zipid);

        if(adressid.length()==0 || stateid.length()==0 || cityid.length()==0 || zipid.length()==0)
        {
            Toast.makeText(this,"one of the fields are empty please fill all fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(this,"success", Toast.LENGTH_SHORT).show();

            // this code heres forms the insert data that is about to sent to the server
            String datatpass = String.format("{\"userkey\":\"wfwrgegttrhrthr\",\"requesttype\":\"address\",\"useraddress\":{\"line\":\"%s\",\"city\":\"%s\",\"userstate\":\"%s\",\"zipcode\":\"%s\"}}",adressid.getText(),cityid.getText(),stateid.getText(),zipid.getText());
            Log.i("stringifo",datatpass);
            new sendrealdata(datatpass).execute("life");

        }


    }



    class sendrealdata extends AsyncTask<String,Void, String> {

        String jsonurl = "";

        public sendrealdata(String datatosend)
        {
            this.jsonurl = datatosend;
        }



        protected String doInBackground(String... urls) {
            Log.i("staus","its working");


            HttpURLConnection connection = null;


            String mainresponse= "";

            try {
                //Create connection
                URL url = null;
                    url  = new URL("https://firesmart.herokuapp.com/insert/");
                connection = (HttpURLConnection) url.openConnection();
                //connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(this.jsonurl.length()));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setRequestProperty("User-Agent", "Firesmart app");
                connection.setRequestProperty("Accept-Language", "UTF-8");
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


                    // because the user have succesfully switched off the alarm
                    if(json.get("value").toString().equals("11"))
                    {
                        Toast.makeText(MainActivity.this,
                                "Succesfylly changed address",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,
                                "There was a problem updating your address , make sure it is a valid address",Toast.LENGTH_LONG).show();
                    }


            }
            catch (Exception e)
            {
                //
                Log.i("error",e.toString());
                Log.i("error",result);
            }


        }
    }




}
