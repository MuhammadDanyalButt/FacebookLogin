package com.example.dani.facebooktesting;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton login_button;
    TextView txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializecontrol();
        loginwithfacebook();


    }

    private void loginwithfacebook() {

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                txtview.setText(loginResult.toString());
     //xtview.setText("Login Successfully\n"+loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                txtview.setText("Login Cancel\n");

            }

            @Override
            public void onError(FacebookException error) {
                txtview.setText("Login Error\n"+error.getMessage());

            }
        });
    }

    private void initializecontrol() {

        callbackManager = CallbackManager.Factory.create();
        login_button = (LoginButton)findViewById(R.id.login_button);
        txtview=(TextView)findViewById(R.id.txtview);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
