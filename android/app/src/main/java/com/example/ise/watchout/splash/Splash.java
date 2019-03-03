package com.example.ise.watchout.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ise.watchout.Main.MainActivity;
import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.login.DeviceInfo;
import com.example.ise.watchout.login.LoginActivity;
import com.example.ise.watchout.login.LoginResult;
import com.example.ise.watchout.login.UserInfo;
import com.example.ise.watchout.network.Server;
import com.example.ise.watchout.setting.Setting;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.Main.MainActivity.crawlingThread;

public class Splash extends AppCompatActivity {
    public Server server;
    public static UserInfo userInfo;
    public static DeviceInfo deviceInfo;
    boolean loginSuccess;
    public static LoginResult LoginResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        server = ApplicationController.getInstance().getServer();
        userInfo = new UserInfo();
        deviceInfo= new DeviceInfo();

        //자동로그인
        LoginActivity.auto = getSharedPreferences("auto", 0);
        LoginActivity.autoEditor = LoginActivity.auto.edit();


        final Intent mainIntent = new Intent(this, LoginActivity.class);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                Thread splashThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (LoginActivity.auto.getBoolean("autoLogin", Boolean.parseBoolean(""))) {
                            Call<LoginResult> getLoginResult = server.getLoginResult(LoginActivity.auto.getString("id", ""), LoginActivity.auto.getString("pw", ""), LoginActivity.auto.getString("deviceToken", ""));
                            getLoginResult.enqueue(new Callback<LoginResult>() {
                                @Override
                                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                    if (response.isSuccessful()) {// 응답코드 200
                                        LoginResult = response.body();
                                        userInfo.userId = LoginResult.userInfo.userId;
                                        userInfo.pwd = LoginResult.userInfo.pwd;
                                        userInfo.phone = LoginResult.userInfo.phone;
                                        userInfo.type = LoginResult.userInfo.type;
                                        userInfo.push = LoginResult.userInfo.push;
                                        userInfo.deviceToken = LoginResult.userInfo.deviceToken;

                                        deviceInfo.dId = LoginResult.deviceInfo.dId;
                                        deviceInfo.userId = LoginResult.deviceInfo.userId;
                                        deviceInfo.connect = LoginResult.deviceInfo.connect;

                                        loginSuccess = LoginResult.message.equals("login is successful") ? true : false;

                                        Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(Main);
                                    }
                                }
                                @Override
                                public void onFailure(Call<LoginResult> call, Throwable t) {

                                }
                            });
                        }
                    }
                });
                splashThread.start();

                startActivity(mainIntent);
                finish();
            }
        }, 1500);

    }
}
