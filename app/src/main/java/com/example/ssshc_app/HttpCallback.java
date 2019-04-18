package com.example.ssshc_app;

import android.os.Handler;
import android.os.Message;

public class HttpCallback implements HttpCallbackListner{
    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onFinish(String respone, Handler handler) {
        Message message = handler.obtainMessage();
        //在这里将 respone 放入 hander 中
        Message msg = new Message();
        msg.obj = respone;
        handler.sendMessage(msg);
        HttpUtil.sendHttpRequest("http://3.104.253.200/getTime",this,handler);
    }
}
