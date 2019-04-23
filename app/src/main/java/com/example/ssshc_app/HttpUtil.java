package com.example.ssshc_app;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListner listener, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                int code = 0;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    code = connection.getResponseCode();

                    InputStream in = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder respone = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null){
                        respone.append(line);
                    }
                    reader.close();
                    in.close();

                    if(listener != null){
                        listener.onFinish(respone.toString(),handler);
                    }
                }catch (Exception e){
                    if(listener != null){
                        System.out.println(code);
                        e.printStackTrace();
                        listener.onError(e);

                    }

                }finally {
                    if( connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}