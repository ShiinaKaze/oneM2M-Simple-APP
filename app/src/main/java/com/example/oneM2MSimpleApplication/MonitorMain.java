package com.example.oneM2MSimpleApplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MonitorMain extends AppCompatActivity {

    EditText cseUri;
    Button Start, Stop, temperature_value, humidity_value;
    Boolean isStopThread = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Init control
        Start = findViewById(R.id.start);
        Stop = findViewById(R.id.stop);
        temperature_value = findViewById(R.id.button_temperature);
        humidity_value = findViewById(R.id.button_humidity);
        cseUri = findViewById(R.id.ipadress_text);
        //IP Address
        cseUri.setText("10.187.208.55");
    }
    //Button Start
    public void StartOnClick(View view) {
        try {
            startConnect();
            isStopThread = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Button Stop
    public void StopOnClick(View view) {
        isStopThread = true;
        System.out.println("HttpConnect stop");
    }
    //HttpConnect
    private void startConnect() {
        Map<Integer, String> hashMap = new HashMap();
        hashMap.put(1, "temperature");
        hashMap.put(2, "humidity");
        Map<Integer, Button> buttonTextMap = new HashMap<>();
        buttonTextMap.put(1, temperature_value);
        buttonTextMap.put(2, humidity_value);
        new Thread() {
            @Override
            public void run() {
                super.run();
                int n = 1;
                while (true) {
                    URL url = null;
                    try {
                        url = new URL("http://" + cseUri.getText() + ":8080/server/" + hashMap.get(n) + "/data/la");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        break;
                    }
                    try {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setConnectTimeout(3000);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setInstanceFollowRedirects(true);
                        conn.connect();
                        Thread.sleep(1000);
                        String con = getResponseBodyCon(resolveInputStream(conn));
                        loadButtonText(buttonTextMap.get(n), con);
                        System.out.println(hashMap.get(n) + ":" + con);
                    } catch (IOException | JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    n++;
                    if (n > 2) {
                        n = 1;
                    }
                    if (isStopThread) {
                        break;
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getResponseBodyCon(String body) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        return jsonObject.getJSONObject("m2m:cin").getString("con");

    }

    //Resolve Input Stream
    private String resolveInputStream(HttpURLConnection connection) throws IOException {
        InputStream in = connection.getInputStream();
        String responseBody;
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            baos.write((byte) result);
            result = bis.read();
        }
        responseBody = baos.toString();
        return responseBody;
    }

    public void loadButtonText(Button button, String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText(s);
            }
        });
    }
}