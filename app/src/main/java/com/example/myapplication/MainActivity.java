package com.example.myapplication;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

    // *** Это теги для дальнейшей обработки JSON ***
    private static String TAG_SCHEDULE = "raspisanie";
    private static String TAG_ID = "id";
    private static String TAG_PREDM = "urok";
    private static String TAG_TIME = "id_day";


    // *** Определяем ассоциативный массив для адаптера ***
    ArrayList<HashMap<String, String>> scheduleList;

    // *** Определяем handler для дальнейшей работы адаптера ***
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleList = new ArrayList<HashMap<String, String>>();

        // *** Активируем наш handler, что происходит уже после выполнения метода MainActivity->updater(); ***
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //Заносим новые значения в ListView
                ListAdapter adapter = new SimpleAdapter(MainActivity.this, scheduleList, R.layout.activity_column, new String[]{TAG_TIME, TAG_PREDM}, new int[]{R.id.Coldayid, R.id.ColUrok});

                ListView lv = (ListView) findViewById(R.id.listView1);
                lv.setAdapter(adapter);
            }
        };
    }

    // *** URL где у меня находится JSON текст с данными о парах ***
    public String url1 = "https://domex666.000webhostapp.com/rasp4.php?id_group=";

    // *** tester - это метод который привязан к событию нажатия кнопки "Просмотреть расписание" ***
    public void tester(View view) {
        updateHTTP();
    }

    // *** Здесь мы строим url: записываем наши входные параметры, дату и группу, чье расписание мы будем смотреть ***
    public String getURL() {
        String uri = "";
        EditText et = (EditText) findViewById(R.id.editvg);
        String id_group = et.getText().toString();
        uri = url1 + id_group;
        Log.d("URL Request >>",uri);
        return uri;
    }

    // *** Метод показывает пользователю страницу ввода группы и даты ***
    public void viewFirst(View view) {
        //Все элементы которые нужно будет показать
        EditText et = (EditText) findViewById(R.id.editvg);
        EditText et1 = (EditText) findViewById(R.id.editdp);
        TextView tv = (TextView) findViewById(R.id.textdp);
        TextView tv1 = (TextView) findViewById(R.id.textvg);
        TextView tv2 = (TextView) findViewById(R.id.title);
        Button bt = (Button) findViewById(R.id.button1);

        //Все элементы которые скрываем
        ListView lv1 = (ListView) findViewById(R.id.listView1);
        Button bt1 = (Button) findViewById(R.id.button2);

        //Показываем форму
        et.setVisibility(View.VISIBLE);
        et1.setVisibility(View.VISIBLE);
        tv1.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);

        //Закрываем рассписание
        lv1.setVisibility(View.GONE);
        bt1.setVisibility(View.GONE);
    }

    // *** Показываем расписание пользователю ***
    public void hideFirst() {

        //Все элементы которые нужно будет скрыть
        EditText et = (EditText) findViewById(R.id.editvg);
        EditText et1 = (EditText) findViewById(R.id.editdp);
        TextView tv = (TextView) findViewById(R.id.textdp);
        TextView tv1 = (TextView) findViewById(R.id.textvg);
        TextView tv2 = (TextView) findViewById(R.id.title);
        Button bt = (Button) findViewById(R.id.button1);

        //Все элементы которые показываем
        ListView lv1 = (ListView) findViewById(R.id.listView1);
        Button bt1 = (Button) findViewById(R.id.button2);

        //Очищаем экран
        et.setVisibility(View.GONE);
        et1.setVisibility(View.GONE);
        tv1.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        bt.setVisibility(View.GONE);

        //Показываем рассписание
        lv1.setVisibility(View.VISIBLE);
        bt1.setVisibility(View.VISIBLE);

    }

    // *** И вот она долгожданная функция для работы с JSON, данная функция обновляет
    // 	данные приходящие с сервера, по нажатию кнопки ***
    protected void updateHTTP() {
        // *** Очищаем наш ArrayList, чтобы не добавить расписание, а вывести новое ***
        scheduleList.clear();

        // *** Выводим строку в лог, для проверки, что работа началась ***
        Log.d("GO GO >>","START THIS PROGRAMM");

        // *** Скрываем элементы "первого" экрана и эмулируем второй, показывая элементы "второго" экрана ***
        hideFirst();


        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    // *** Определяем строку с нашим URL ***
                    String newURL = getURL();

                    // *** Определяем переменную типа URL для работы с HttpURLConnection ***
                    URL url = new URL(newURL);

                    // *** Определяем переменную conn ***
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // *** Запускаем соединение с сервером ***
                    conn.connect();

                    // *** Определяем переменную stream, чтобы в будущем влезть в поток ***
                    InputStream stream = conn.getInputStream();

                    // *** И определяем переменную data в которую заносим все, что нам вернул сервер в виде строки ***
                    String data = convertStreamToString(stream);

                    // *** Выводим в logcat, что нам вернул сервер ***
                    Log.d("DATA >> ",data);

                    // *** Определяем объект типа JSONObject и достаем данные из JSON строки вернувшейся с сервера ***
                    JSONObject dataJsonObj = new JSONObject(data);

                    // *** Достаем именно наше расписание ***
                    JSONArray products = dataJsonObj.getJSONArray(TAG_SCHEDULE);

                    // Определяем цикл, который пройдет по всем элементам которые нам пришли из JSON строки
                    for(int i = 0; i < products.length();i++) {
                        //schedule временный объект
                        JSONObject schedule = products.getJSONObject(i);

                        //Достаем все данные о рассписании
                        String id_sched = schedule.getString(TAG_ID);
                        String predm = schedule.getString(TAG_PREDM);
                        String time = schedule.getString(TAG_TIME);

                        // *** Определяем временную переменную для одной пары ***
                        HashMap<String, String> sched = new HashMap<String, String>();

                        // *** Заносим данные в временное хранилище)) ***
                        sched.put(TAG_ID, id_sched);
                        sched.put(TAG_PREDM, predm);
                        sched.put(TAG_TIME,time);

                        // *** Добавляем данные о паре в наш ассоциативный массив ***
                        scheduleList.add(sched);
                       // SimpleAdapter sAdap;
                       // sAdap = new SimpleAdapter(MainActivity.this, scheduleList, R.layout.activity_column, new String[]{id_sched, time, predm}, new int[]{R.id.ColGroupid, R.id.Coldayid, R.id.ColUrok});

                       // listView.setAdapter(sAdap);
                    }



                    // *** Создаем объект типа Message, для работы нашего хандла ***
                    Message msg = new Message();

                    // *** Заносим наш ассоциативный массив в свойство объекта msg типа Message ***
                    msg.obj = scheduleList;

                    // *** Запускаем наш хандл, для вывода данных запуска нашего адаптера ***
                    handler.sendMessage(msg);




                    // *** Выводим в лог, что все прошло успешно ***
                    Log.d("Request >>>>","SUCCESS");
                } catch (Exception e) {

                    // *** Если у нас есть какая-то ошибка, выводим её
                    Log.d("ERROR ctaciv>",e.getMessage());
                }
            }

        });

        // *** Поехали, запускаем всю работу ***
        thread.start();
    }

    // *** Метод для конвертации потока в строку ***
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


}

