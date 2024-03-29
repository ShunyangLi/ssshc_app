package com.example.ssshc_app.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginUtils {
    public static String LOGIN_URL = "http://192.168.43.93/login";

    public static String LoginByPost(String phone_number,String passwd) {
        String msg = "ERROR";
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(LOGIN_URL).openConnection();

            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            //our post data
            String data = "password="+ URLEncoder.encode(passwd, "UTF-8")+
                    "&username="+ URLEncoder.encode(phone_number, "UTF-8");

            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();

            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                msg = new String(message.toByteArray());
                // maybe the the token
                msg =  get_json_data(msg);
            } else {
                msg = "ERROR";
            }


        }catch(Exception e){e.printStackTrace();}
        return msg;
    }

    public static String get_json_data(String json) {
        String res = "";
        try{
            JSONObject jObject = new JSONObject(json);
            res = jObject.getString("username");
        }catch (Exception e){e.printStackTrace();}
        return res;
    }
}
