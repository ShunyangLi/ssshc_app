package com.example.ssshc_app.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FetchBookingUtil {
    public static String LOGIN_URL = "http://192.168.43.93/getStatus";
    public static List<String> myList = new ArrayList<String>();

    public static List<String> GetOrder(String username) {
        String msg = "";
        try{
            myList = new ArrayList<String>();
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String data = "username="+ URLEncoder.encode(username, "UTF-8");
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
                msg = new String(message.toByteArray());

//                Message newmsg = new Message();
//                newmsg.obj = msg;
//                handler.sendMessage(newmsg);

                myList.add(get_record_id(msg));
                myList.add(get_booking(msg));

            } else {
                myList = new ArrayList<String>();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return myList;
    }


    public static String get_booking(String json) {
        String res = "";
        String final_res = "";

        try{
            JSONObject jObject = new JSONObject(json);
            res = jObject.getString("booking");

            try {
                JSONObject jObject1 = new JSONObject(res);

                final_res += "Adults: " + jObject1.getString("adults") + " Children: " + jObject1.getString("children") + "\n";
                final_res += "Name: " + jObject1.getString("customer_name") + "\n";
                final_res += "Date: " + jObject1.getString("date") + "\n";
                final_res += "Start location: " + jObject1.getString("start_location") + "\n";
                final_res += "Destination: " + jObject1.getString("destination") + "\n";
                final_res += "Price: $" + jObject1.getString("price") +"\n";

            }catch (Exception e) {e.printStackTrace();}

        }catch (Exception e){e.printStackTrace();}

        return final_res;
    }


    public static String get_record_id(String json) {
        String res = "";
        try {
            JSONObject jObject = new JSONObject(json);
            res = jObject.getString("record_id");
        }catch (Exception e) {e.printStackTrace();}
        return res;
    }




}
