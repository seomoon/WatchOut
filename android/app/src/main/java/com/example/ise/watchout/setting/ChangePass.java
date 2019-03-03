package com.example.ise.watchout.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ise.watchout.Main.MainActivity;
import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.login.ChangePw;
import com.example.ise.watchout.login.ChangePwResult;
import com.example.ise.watchout.login.FindPwActivity;
import com.example.ise.watchout.login.LoginActivity;
import com.example.ise.watchout.network.Server;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.splash.Splash.userInfo;


public class ChangePass extends AppCompatActivity {
    EditText nowPw, newPw, checkNewPw;
    ImageView changeOk;
    Boolean ChangePwSuccess;
    public Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        server = ApplicationController.getInstance().getServer();


        nowPw = (EditText) findViewById(R.id.nowpw);
        newPw = (EditText) findViewById(R.id.newpw);
        checkNewPw = (EditText) findViewById(R.id.checknewpw);
        changeOk = (ImageView) findViewById(R.id.setchangeok);

        ChangePwSuccess = false;


        changeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버로 비번 변경하고
                String nowpw = nowPw.getText().toString();
                String changenewpw = newPw.getText().toString();
                String changenewpwcheck = checkNewPw.getText().toString();

                if (nowpw.equals("") || changenewpw.equals("") || changenewpwcheck.equals("")) {
                    Toast.makeText(ChangePass.this, "모든 칸을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (userInfo.pwd.equals(nowpw)) {
                        if (changenewpw.equals(changenewpwcheck)) {
                            ChangePw chpw = new ChangePw(changenewpw);    //바꾼비밀번호 넣을 곳
                            Call<ChangePwResult> changePw = server.updatePw(userInfo.phone, chpw);
                            changePw.enqueue(new Callback<ChangePwResult>() {
                                @Override
                                public void onResponse(Call<ChangePwResult> call, Response<ChangePwResult> response) {
                                    if (response.isSuccessful()) {// 응답코드 200
                                        ChangePwResult ChangePwResult = response.body();
                                        ChangePwSuccess = ChangePwResult.message.equals("password change successful") ? true : false;
                                        //message=  FindIdResult.message;
                                    }
                                    if (ChangePwSuccess) {
                                        Toast.makeText(ChangePass.this, "비밀번호 변경 성공", Toast.LENGTH_SHORT).show();
                                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(loginActivity);
                                        //PhoneCheckSuccess = false;
                                    } else {
                                        Toast.makeText(ChangePass.this, "실패.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ChangePwResult> call, Throwable t) {
                                    Toast.makeText(ChangePass.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                    Log.i("LoginTest", "요청메시지:" + call.toString());
                                }
                            });

                            //로그아웃하고
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = auto.edit();
                            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                            editor.clear();
                            editor.commit();
                            //다시 로그인하기기
                            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                        } else {
                            Toast.makeText(ChangePass.this, "새 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePass.this, "현재 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });
    }
}
