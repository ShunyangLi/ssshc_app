package com.example.ssshc_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ssshc_app.Util.SharedPreferencesUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private String token;
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
                    token = LoginUtils.LoginByPost(phone,password);
                    System.out.println(token);
                };
            }.start();


            if (first_login) {
                sotre_username_password();
                first_login = false;
            }

            Intent intent=new Intent(LoginActivity.this, AfterLogin.class);
            startActivity(intent);

//            if (token.compareTo("ERROR") == 0) {
//                focusView.requestFocus();
//            } else {
//                // should return to a new page
//                Intent intent=new Intent(LoginActivity.this, AfterLogin.class);
//                startActivity(intent);
//            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.matches("[0-9]{10}");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



    private boolean firstLogin() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        boolean first = helper.getBoolean("first", true);
        if (first) {
            //创建一个ContentVa对象（自定义的）设置不是第一次登录，,并创建记住密码和自动登录是默认不选，创建账号和密码为空
            helper.putValues(new SharedPreferencesUtils.ContentValue("first", false),
                    new SharedPreferencesUtils.ContentValue("autoLogin", true),
                    new SharedPreferencesUtils.ContentValue("name", ""),
                    new SharedPreferencesUtils.ContentValue("password", ""));
            return true;
        }
        return false;
    }


    public void setTextNameAndPassword() {
        phone_number.setText("" + getLocalName());
        Password.setText("" + getLocalPassword());
    }


    public String getLocalName() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
        return helper.getString("name");
    }


    /**
     * 获得保存在本地的密码
     */
    public String getLocalPassword() {
        //获取SharedPreferences对象，使用自定义类的方法来获取对象
        SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");

        return helper.getString("password");
//       return password;   //解码一下

    }


    public void sotre_username_password() {

        new SharedPreferencesUtils.ContentValue("password", Password.getText().toString());
        new SharedPreferencesUtils.ContentValue("name", phone_number.getText().toString());

    }


}

