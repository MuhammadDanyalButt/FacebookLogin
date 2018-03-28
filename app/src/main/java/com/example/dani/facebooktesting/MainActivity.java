package com.example.dani.facebooktesting;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    LoginButton login_button;
    TextView txtview;
    PrefUtil prefUtil = new PrefUtil(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        login_button = (LoginButton) findViewById(R.id.login_button);
        initializecontrol();
        loginwithfacebook();
        login_button.setReadPermissions(Arrays.asList(
                "public_profile", "email"));

        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        String accessToken = loginResult.getAccessToken().getToken();

                        // save accessToken to SharedPreference
                        prefUtil.saveAccessToken(accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject jsonObject,
                                                            GraphResponse response) {



                                        try {
                                            if( jsonObject != null){
                                                Log.e("Facebook Data",jsonObject.toString());
                                                String userData="Gender: ";
                                                userData+=jsonObject.get("gender")+"\n";
                                                userData+="First Name : "+jsonObject.get("first_name")+"\n";
                                                userData+="Last Name : "+jsonObject.get("last_name")+"\n";
                                                userData+="Email : "+jsonObject.get("email")+"\n";
                                                txtview.setText(userData);

                                            }else{
                                                txtview.setText("Facebook Data is null");
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();

                                            txtview.setText("Error while parsing data.");
                                        }

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }


                    @Override
                    public void onCancel () {
                            Log.d("Cancelled", "Login attempt cancelled.");
                    }

                    @Override
                    public void onError (FacebookException e){
                        e.printStackTrace();
                        Log.d("Failed attempt", "Login attempt failed.");
                        deleteAccessToken();
                    }
                }
        );

    }
    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));


            prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"),
                    object.getString("gender"), profile_pic.toString());
            txtview.setText((CharSequence) object);

        } catch (Exception e) {
            Log.d("Bundle Exception", "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }

    private void loginwithfacebook() {

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    prefUtil.clearToken();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }
}
