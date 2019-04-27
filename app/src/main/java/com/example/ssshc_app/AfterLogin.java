package com.example.ssshc_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ssshc_app.Util.AcceptUtil;
import com.example.ssshc_app.Util.GetBookingUtil;
import com.example.ssshc_app.Util.GetUtil;
import com.example.ssshc_app.Util.RefuseUtil;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AfterLogin extends AppCompatActivity {

    private String phone_number = "";
    private String record_id = "";
    private List<String> my_booking;

    private TextView show_text;
    private String res = "";

    // set dialog
    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Dialog mDialog;
    private NotificationManager mNManager;
    private Notification notify1;
    Bitmap LargeBitmap = null;
    private static final int NOTIFYID_1 = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_api);

        show_text = (TextView) findViewById(R.id.show);
        mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        set_phone();
        mOffTextView = new TextView(this);
        init_bookings();
        start_fetch_orders();

    }

    public void start_fetch_orders() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> myres = GetUtil.GetOrder(phone_number);
                if (myres.size() != 0) {
                    set_record(myres.get(0));
                    // set dialog
                    set_notification();
                    set_dialog(myres.get(1));
                }
            }
        }, 0 , 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void set_notification() {
        Intent it = new Intent(AfterLogin.this, OrderNotifity.class);
        PendingIntent pit = PendingIntent.getActivity(AfterLogin.this, 0, it, 0);

        //设置图片,通知标题,发送时间,提示方式等属性
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setContentTitle("Orders")
                .setContentText("You got a new order")
                .setSubText("Please have a check")
                .setTicker("You receive ssshc orders")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ncb_old_logo)
                .setLargeIcon(LargeBitmap)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pit);
        notify1 = mBuilder.build();
        mNManager.notify(NOTIFYID_1, notify1);
    }


    public void init_bookings(){
        new Thread() {
            @Override
            public void run() {
                my_booking = GetBookingUtil.GetBooking(phone_number);
                if (my_booking.size() != 0) {
                    set_view_booking();
                }
            }
        }.start();
    }


    public void set_view_booking() {
        String res = "";

        for (int i = 0; i < my_booking.size(); i ++) {
            res += my_booking.get(i) +"\n";
        }

        show_text.setText(res);
    }

    public void set_dialog(String content) {
        try {
            save("notifity.txt", content);
        } catch (Exception e) {e.printStackTrace();}
        Looper.prepare();
        mDialog = new AlertDialog.Builder(this)
                .setTitle("Order")//设置对话框的标题
                .setMessage(content)//设置对话框的内容
                .setView(mOffTextView)
                //设置对话框的按钮
                .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refuse_http(phone_number, record_id);
                        mNManager.cancel(NOTIFYID_1);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 200) {
                                    Toast.makeText(AfterLogin.this, "You get this order successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AfterLogin.this, "Sorry, you don't get this order", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        accpet_http(handler, phone_number, record_id);
                        mNManager.cancel(NOTIFYID_1);
                        dialog.dismiss();
                    }
                }).create();
        mDialog.show();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog.cancel();
                mDialog = null;
                Refresh();
            }
        });



        mOffHandler = new Handler() {
            public void handleMessage(Message msg) {

                if (msg.what > 0) {
                    ////动态显示倒计时
                    mOffTextView.setText("After："+msg.what +" gonna auto cancel !");

                } else {
                    ////倒计时结束自动关闭
                    if(mDialog!=null){
                        mDialog.dismiss();
                    }
                    mOffTime.cancel();
                    refuse_http(phone_number, record_id);
                    mNManager.cancel(NOTIFYID_1);
                }
                super.handleMessage(msg);
            }

        };

        //////倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 60;
            public void run() {
                if (countTime > 0) {
                    countTime--;
                    // save the time into the file
                    try {
                        save("time.txt", String.valueOf(countTime));
                    } catch (Exception e) {e.printStackTrace();}
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);

        Looper.loop();

    }


    public void refuse_http(final String username, final String record_id) {
        new Thread() {
            @Override
            public void run() {
                RefuseUtil.RefuseOrder(username, record_id);
            }
        }.start();
    }


    public void accpet_http(final Handler handler, final String username, final String record_id) {
        new Thread() {
            @Override
            public void run() {
                int code = AcceptUtil.AcceptOrder(username, record_id);
                Message msg = new Message();
                msg.what = code;
                handler.sendMessage(msg);

            }
        }.start();

    }


    public void set_phone() {
        try {
            this.phone_number = read("phone.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
       try get the record_id
     */
    public void set_record(String record_id) {
        try {
            save("record.txt", record_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.record_id = record_id;
    }




    public String read(String filename) throws IOException {
        //打开文件输入流
        FileInputStream input = getApplicationContext().openFileInput(filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        input.close();
        return sb.toString();
    }


    public void save(String filename, String filecontent) throws Exception {
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
        FileOutputStream output = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
        output.write(filecontent.getBytes());  //将String字符串以字节流的形式写入到输出流中
        output.close();         //关闭输出流
    }

    public void Refresh() {
//        Intent refresh =new Intent(this, AfterLogin.class);
//        startActivity(refresh);

        finish();
        startActivity(getIntent());
    }

}
