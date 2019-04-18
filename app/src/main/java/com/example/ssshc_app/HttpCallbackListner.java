package com.example.ssshc_app;

import android.os.Handler;

public interface HttpCallbackListner {
    // make url keep live
    void onFinish(String respone, Handler handler);
    // check error
    void onError(Exception e);
}
