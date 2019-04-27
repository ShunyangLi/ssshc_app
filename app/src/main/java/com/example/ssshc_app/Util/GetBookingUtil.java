package com.example.ssshc_app.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GetBookingUtil {

    public static String LOGIN_URL = "http://192.168.43.93/getBookings";
    public static List<String> myList = new ArrayList<String>();

    public static List<String> GetBooking(String username) {
        String msg = "";
        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String data = "username=" + URLEncoder.encode(username, "UTF-8");
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

                myList = get_booking(msg);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myList;
    }


    public static List<String> get_booking(String json) {
        List<String> myList = new ArrayList<String>();
        try{
            JSONObject jsonObject1 = new JSONObject(json);
            JSONArray jsonArray = jsonObject1.getJSONArray("bookings");

            for (int i = 0; i < jsonArray.length(); i++) {
                String final_res = "";
                JSONObject jObject1 = (JSONObject) jsonArray.get(i);
                final_res += "Adults: " + jObject1.getString("adults") + " Children: " + jObject1.getString("children") + "\n";
                final_res += "Name: " + jObject1.getString("customer_name") + "\n";
                final_res += "Date: " + jObject1.getString("date") + "\n";
                final_res += "Start location: " + jObject1.getString("start_location") + "\n";
                final_res += "Destination: " + jObject1.getString("destination") + "\n\n\n\n";


                myList.add(final_res);
            }

        }catch (Exception e){e.printStackTrace();}

        return myList;
    }
}
