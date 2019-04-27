package com.example.ssshc_app;

import android.os.Handler;
import android.os.Message;

import com.example.ssshc_app.Util.HttpUtil;

public class HttpCallback implements HttpCallbackListner{
    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onFinish(String respone, Handler handler) {
        Message message = handler.obtainMessage();
        Message msg = new Message();
        msg.obj = respone;
        handler.sendMessage(msg);
        HttpUtil.sendHttpRequest("http://3.104.253.200/getTime",this,handler);
    }
}
