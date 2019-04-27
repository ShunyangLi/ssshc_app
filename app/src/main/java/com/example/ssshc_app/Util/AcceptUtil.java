package com.example.ssshc_app.Util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AcceptUtil {
    public static String LOGIN_URL = "http://192.168.43.93/accept";
    public static List<String> myList = new ArrayList<String>();

    public static int AcceptOrder(String username, String record_id) {

        int code = 404;
        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String data = "record_id="+ URLEncoder.encode(record_id, "UTF-8")+
                    "&username="+ URLEncoder.encode(username, "UTF-8");


            OutputStream out = connection.getOutputStream();
            out.write(data.getBytes());
            out.flush();

            // that means i got the data
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
                code = 200;

            } else {
                code = 404;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return code;
    }
}
