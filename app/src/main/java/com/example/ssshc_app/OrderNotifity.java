package com.example.ssshc_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssshc_app.Util.AcceptUtil;
import com.example.ssshc_app.Util.RefuseUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class OrderNotifity extends AppCompatActivity {

    private String phone_number = "";
    private String record_id = "";
    private String content = "";
    private String time = "";

    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Dialog mDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifity);
        mOffTextView = new TextView(this);
        init_data();
        set_dialog();

    }


    public void set_dialog() {
        mDialog = new AlertDialog.Builder(this)
                .setTitle("Order")//设置对话框的标题
                .setMessage(content)//设置对话框的内容
                .setView(mOffTextView)
                //设置对话框的按钮
                .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refuse_http(phone_number, record_id);
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
                                    Toast.makeText(OrderNotifity.this, "You get this order successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(OrderNotifity.this, "Sorry, you don't get this order", Toast.LENGTH_SHORT).show();
                                }
                            }
                        };
                        accpet_http(handler, phone_number, record_id);
                        dialog.dismiss();
                    }
                }).create();
        mDialog.show();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog.cancel();
                mDialog = null;
                Intent refresh =new Intent(OrderNotifity.this, Bookings.class);
                startActivity(refresh);
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
                }
                super.handleMessage(msg);
            }

        };

        //////倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = Integer.valueOf(time);
            public void run() {
                if (countTime > 0) {
                    countTime--;
                    // save the time into the file
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
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

    public void init_data() {
        try {
            this.phone_number = read("phone.txt");
            this.record_id = read("record.txt");
            this.content = read("notifity.txt");
            this.time = read("time.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
