package com.example.ssshc_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssshc_app.Util.LoginUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private String result = "";
    // UI references.
    private EditText phone_number;
    private EditText Password;
    private boolean first_login = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        // Set up the login form.
        phone_number = (EditText) findViewById(R.id.phone);
        Password = (EditText) findViewById(R.id.password);

        initData();

        Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    private void initData() {
        if (!firstLogin()) {
            setTextNameAndPassword();
            attemptLogin();
        } else {
            first_login = true;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        phone_number.setError(null);
        Password.setError(null);

        // Store values at the time of the login attempt.
        final String phone = phone_number.getText().toString();
        final String password = Password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            Password.setError(getString(R.string.error_invalid_password));
            focusView = Password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            phone_number.setError(getString(R.string.error_field_required));
            focusView = phone_number;
            cancel = true;
        } else if (!isEmailValid(phone)) {
            phone_number.setError(getString(R.string.error_invalid_email));
            focusView = phone_number;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            new Thread() {
                public void run() {
                    result = LoginUtils.LoginByPost(phone,password);
                    if (result.compareTo("ERROR") == 0) {
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        return;
                    } else {
                        new_Activity();
                    }
                    if (first_login) {
                        store_username_password();
                        first_login = false;
                    }
                };
            }.start();
        }
    }

    public void new_Activity() {
        Intent intent=new Intent(LoginActivity.this, Bookings.class);
        startActivity(intent);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return email.matches("[0-9]{10}");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        if (getLocalName().compareTo("ERROR") == 0) {
            return true;
        } else {
            return false;
        }
    }


    public void setTextNameAndPassword() {
        phone_number.setText(getLocalName());
        Password.setText(getLocalPassword());
    }


    public String getLocalName() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        try {
            return read("phone.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR";
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        try {
            return read("password.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ERROR";

    }


    public void store_username_password() {
        // try to store the phone number into file
        try{
            save("password.txt", Password.getText().toString());
            save("phone.txt", phone_number.getText().toString());
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


    public void save(String filename, String filecontent) throws Exception {
        FileOutputStream output = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
        output.write(filecontent.getBytes());
        output.close();
    }


}

