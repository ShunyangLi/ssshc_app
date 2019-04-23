package com.example.ssshc_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class AfterLogin extends AppCompatActivity {

    private TextView show_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_api);

        show_text = (TextView) findViewById(R.id.show);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String respone = (String)msg.obj;
                show_text.setText(respone);
                Log.d("veve", "handleMessage: "+respone);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GetUtil.GetOrder(handler);
            }
        }, 0 , 1000);



//        Handler handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                String respone = (String)msg.obj;
//                show_text.setText(respone);
//                Log.d("veve", "handleMessage: "+respone);
//            }
//        };
//
//        HttpUtil.sendHttpRequest("http://3.104.253.200/getTime",new HttpCallback(),handler);
    }
}
