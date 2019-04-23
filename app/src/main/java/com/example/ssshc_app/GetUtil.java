package com.example.ssshc_app;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUtil {
    public static String LOGIN_URL = "http://3.104.253.200/getTime";

    public static String GetOrder(final Handler handler) {
        String msg = "";
        try{
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                // get the message
                msg = new String(message.toByteArray());

                Message newmsg = new Message();
                newmsg.obj = msg;
                handler.sendMessage(newmsg);

                msg = "SUCCESS";

            } else if (connection.getResponseCode() == 201) {
                msg = "TIME";
            } else {
                msg = "ERROR";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return msg;
    }
}
