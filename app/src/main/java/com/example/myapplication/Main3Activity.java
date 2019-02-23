package com.example.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.content.Intent;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.myapplication.MainActivity.convertStreamToString;

public class Main3Activity extends AppCompatActivity {
    Button button1;
    EditText tv;
    ListView listView1;
    private static String TAG_SCHEDULE = "raspisanie";
    private static String TAG_ID = "id";
    private static String TAG_PREDM = "urok";
    private static String TAG_TIME = "id_day";


    ArrayList<HashMap<String, String>> scheduleList;


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent intent = getIntent();
        tv = (EditText) findViewById(R.id.tv);
        String grp = intent.getStringExtra("grp");
        tv.setText(grp);
        scheduleList = new ArrayList<HashMap<String, String>>();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                ListAdapter adapter = new SimpleAdapter(Main3Activity.this, scheduleList, R.layout.activity_column, new String[]{TAG_TIME, TAG_PREDM}, new int[]{R.id.Coldayid, R.id.ColUrok});

                ListView lv = (ListView) findViewById(R.id.listView1);
                lv.setAdapter(adapter);
            }
        };
    }

    public String url1 = "https://domex666.000webhostapp.com/rasp4.php?id_group=";
    public void tester(View view) {
        updateHTTP();
    }
    public String getURL() {
         button1 = (Button) findViewById(R.id.button1);
        String uri = "";
        String id_group = tv.getText().toString();
        uri = url1 + id_group;
        Log.d("URL Request >>",uri);
        return uri;
    }
    protected void updateHTTP() {
        scheduleList.clear();
        Log.d("GO GO >>","START THIS PROGRAMM");

        Thread thread = new Thread(new Runnable(){


            @Override

            public void run() {
                try{
                    String newURL = getURL();
                    URL url = new URL(newURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    String data = convertStreamToString(stream);
                    Log.d("DATA >> ",data);
                    JSONObject dataJsonObj = new JSONObject(data);
                    JSONArray products = dataJsonObj.getJSONArray(TAG_SCHEDULE);

                    for(int i = 0; i < products.length();i++) {
                        JSONObject schedule = products.getJSONObject(i);
                        String id_sched = schedule.getString(TAG_ID);
                        String predm = schedule.getString(TAG_PREDM);
                        String time = schedule.getString(TAG_TIME);
                        HashMap<String, String> sched = new HashMap<String, String>();
                        sched.put(TAG_ID, id_sched);
                        sched.put(TAG_PREDM, predm);
                        sched.put(TAG_TIME,time);
                        scheduleList.add(sched);
                    }

                    Message msg = new Message();
                    msg.obj = scheduleList;
                    handler.sendMessage(msg);
                    Log.d("Request >>>>","SUCCESS");
                } catch (Exception e) {
                    Log.d("ERROR ctaciv>",e.getMessage());
                }
            }

        });

        thread.start();
    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
