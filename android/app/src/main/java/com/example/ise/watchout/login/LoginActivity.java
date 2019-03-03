package com.example.ise.watchout.login;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ise.watchout.Join.JoinChildActivity;
import com.example.ise.watchout.Join.JoinParentActivity;
import com.example.ise.watchout.Main.MainActivity;
import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.network.Server;
import com.example.ise.watchout.splash.Splash;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.splash.Splash.deviceInfo;
import static com.example.ise.watchout.splash.Splash.userInfo;

public class LoginActivity extends AppCompatActivity {
    EditText id, pw, idFindPhone;
    ImageView loginBtn, joinBtn, idFind, pwFind, check, ok;
    CheckBox autoCheckBtn;
    public Activity activity;
    View viewId;
    public static SharedPreferences auto;                   //자동로그인 기능에 활용
    public static SharedPreferences.Editor autoEditor;          //자동로그인 기능에 활용
    boolean loginChecked;
    String findid, phone;
    public Server server;
    String userId, pwd, deviceToken;
    boolean loginSuccess, isFindIdSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        server = ApplicationController.getInstance().getServer();
        loginSuccess = false;
        isFindIdSuccess = false;

        //자동로그인 세팅
        auto = getSharedPreferences("auto", 0);
        autoEditor = auto.edit();

        id = (EditText) findViewById(R.id.editID);
        pw = (EditText) findViewById(R.id.editPW);


        loginBtn = (ImageView) findViewById(R.id.loginBtn);
        joinBtn = (ImageView) findViewById(R.id.joinBtn);
        idFind = (ImageView) findViewById(R.id.idFindBtn);
        pwFind = (ImageView) findViewById(R.id.pwFindBtn);

        autoCheckBtn = (CheckBox) findViewById(R.id.autoCheck);

        //리스너 연결
        loginBtn.setOnClickListener(clickListener);
        joinBtn.setOnClickListener(clickListener);
        idFind.setOnClickListener(clickListener);
        pwFind.setOnClickListener(clickListener);

        activity = this;

        //set CheckBoxListener
        autoCheckBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //자동로그인이 체크되어 있으면 true
                if (isChecked) {
                    loginChecked = true;
                }
                //아니면 Preference에 저장된 모든 값 삭제
                else {
                    //if unChecked, remove All
                    loginChecked = false;
                    autoEditor.putBoolean("autoLogin", false);
                    autoEditor.clear();
                    autoEditor.commit();
                }
            }
        });
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.joinBtn:                      //회원가입 버튼
                    final String[] select = new String[]{"본인", "가족"};
                    // 다이얼로그 바디
                    AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
                    // 다이얼로그 메세지
                    dlg.setTitle("선택하세요.");
                    dlg.setItems(select, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent joinChild = new Intent(getApplicationContext(), JoinChildActivity.class);
                                    startActivity(joinChild);
                                    break;
                                case 1:
                                    Intent joinParent = new Intent(getApplicationContext(), JoinParentActivity.class);
                                    startActivity(joinParent);
                                    break;
                            }
                        }
                    });
                    dlg.show();
                    break;
                case R.id.loginBtn:                         //로그인버튼

                    userId = String.valueOf(id.getText());
                    pwd = String.valueOf(pw.getText());
                    deviceToken = FirebaseInstanceId.getInstance().getToken();

                    if (id.getText().toString().equals("") || pw.getText().toString().equals("")) {
                        //id,pw칸이 비어있을 때
                        Toast.makeText(getApplicationContext(), "입력하시오", Toast.LENGTH_SHORT).show();
                    } else {
                        if (loginChecked == true) { //자동로그인한다고 체크 함
                            autoEditor.putString("id", id.getText().toString().trim());    //ID에 id값을 넣는다.
                            autoEditor.putString("pw", pw.getText().toString().trim());  //PW에 pass값을 넣는다.
                            autoEditor.putString("deviceToken", deviceToken);  //deviceToken에 deviceToken 넣는다.
                            autoEditor.putBoolean("autoLogin", true);
                            autoEditor.commit();
                        }

                        Call<LoginResult> getLoginResult = server.getLoginResult(userId, pwd, deviceToken);
                        getLoginResult.enqueue(new Callback<LoginResult>() {
                            @Override
                            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                if (response.isSuccessful()) {// 응답코드 200
                                    LoginResult LoginResult = response.body();
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
                                }
                                if (loginSuccess) {
                                    //파워끄기
                                    if (deviceInfo.connect == -1) {
                                        MainActivity.isRun = false;
                                    }
                                    Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);


                                } else {
                                    Toast.makeText(LoginActivity.this, "아이디/비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResult> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                Log.i("LoginTest", "요청메시지:" + call.toString());
                            }
                        });
                    }


                    break;
                case R.id.idFindBtn:                        //아이디 찾는 다이얼로그
                    final AlertDialog iddlg = new AlertDialog.Builder(activity).create();

                    viewId = (View) View.inflate(LoginActivity.this, R.layout.activity_iddlg, null);
                    check = (ImageView) viewId.findViewById(R.id.pw_find_ok);
                    ok = (ImageView) viewId.findViewById(R.id.ok);                          //다이얼로그 확이버튼
                    idFindPhone = (EditText) viewId.findViewById(R.id.editphone);        //아이디 찾을 때 핸드폰번호

                    check.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String message;
                            phone = String.valueOf(idFindPhone.getText());
                            if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)) {
                                Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Call<FindIdResult> getFindIdResult = server.getFindIdResult(phone);
                                getFindIdResult.enqueue(new Callback<FindIdResult>() {
                                    @Override
                                    public void onResponse(Call<FindIdResult> call, Response<FindIdResult> response) {
                                        if (response.isSuccessful()) {// 응답코드 200
                                            FindIdResult FindIdResult = response.body();

                                            findid = FindIdResult.result.userId;
                                            isFindIdSuccess = FindIdResult.message.equals("ID exists") ? true : false;
                                            //message=  FindIdResult.message;
                                        }
                                        if (isFindIdSuccess) {
                                            Toast.makeText(LoginActivity.this, "ID : " + findid, Toast.LENGTH_SHORT).show();
                                            isFindIdSuccess = false;

                                        } else {
                                            Toast.makeText(LoginActivity.this, "등록되지 않은 번호입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FindIdResult> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                        Log.i("LoginTest", "요청메시지:" + call.toString());
                                    }
                                });
                            }


                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            iddlg.dismiss();
                        }
                    });
                    iddlg.setView(viewId);
                    iddlg.show();
                    break;

                case R.id.pwFindBtn:                    //비밀번호 찾는 화면
                    Intent pwFindIntent = new Intent(getApplicationContext(), FindPwActivity.class);
                    startActivity(pwFindIntent);
                    break;


            }
        }
    };
}
