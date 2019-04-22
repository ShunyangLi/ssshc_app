package com.example.ssshc_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private String token;
    // UI references.
    private EditText phone_number;
    private EditText Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        // Set up the login form.
        phone_number = (EditText) findViewById(R.id.phone);

        Password = (EditText) findViewById(R.id.password);
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


        // TODO this part is get information
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


}

