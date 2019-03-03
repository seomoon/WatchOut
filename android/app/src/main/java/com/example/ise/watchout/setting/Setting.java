package com.example.ise.watchout.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ise.watchout.Main.MainActivity;
import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.login.LoginActivity;
import com.example.ise.watchout.network.Server;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.Main.MainActivity.crawlingThread;
import static com.example.ise.watchout.splash.Splash.deviceInfo;
import static com.example.ise.watchout.splash.Splash.userInfo;


public class Setting extends AppCompatActivity {

    public static Switch alarm, power;
    ImageView chagnePw, logout, userOutBtn;
    RelativeLayout powerLayout;
    Activity activity;
    public static String isAlarmOn = "0";  //push 알람 받을지 말지 스위치 값 저장, 처음에는 off를 기본값으로 사용

    public Server server;
    Boolean deleteCheck, alarmSuccess, PowerSucceess;
    public static int alarmCheck, powerCheck;

    @Override
    protected void onRestart() {
        super.onRestart();
        //알람,전원 값에 따라서 스위치 변경하기
        if (deviceInfo.connect == -1 || powerCheck == -1) {
            power.setChecked(false);
        } else {
            power.setChecked(true);
        }
        if (userInfo.push == -1 || alarmCheck == -1) {
            alarm.setChecked(false);
        } else {
            alarm.setChecked(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        server = ApplicationController.getInstance().getServer();

        alarm = (Switch) findViewById(R.id.childAlarm);
        power = (Switch) findViewById(R.id.childPower);
        chagnePw = (ImageView) findViewById(R.id.childChangePw);
        logout = (ImageView) findViewById(R.id.childLogout);

        powerLayout = (RelativeLayout) findViewById(R.id.powerView);

        //부모로 로그인 한 경우 power 아이콘과 스위치 숨김
        if (userInfo.type == 2) { //유저의 타입이 부모인 경우
            powerLayout.setVisibility(View.INVISIBLE);
        }

        userOutBtn = (ImageView) findViewById(R.id.userOut);
        activity = this;
        deleteCheck = false;
        alarmSuccess = false;
        chagnePw.setOnClickListener(clickListener);
        logout.setOnClickListener(clickListener);
        userOutBtn.setOnClickListener(clickListener);

        //알람,전원 값에 따라서 스위치 변경하기
        if (deviceInfo.connect == -1 || powerCheck == -1) {
            power.setChecked(false);
        } else {
            power.setChecked(true);
        }
        if (userInfo.push == -1 || alarmCheck == -1) {
            alarm.setChecked(false);
        } else {
            alarm.setChecked(true);
        }

        onOff();

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.childChangePw:                        //비밀번호 변경
                    Intent changePw = new Intent(getApplicationContext(), ChangePass.class);
                    startActivity(changePw);
                    break;

                case R.id.childLogout:                          //로그아웃
                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = auto.edit();
                    //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                    editor.clear();
                    editor.commit();
                    //다시 로그인하기기
                    Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    break;

                case R.id.userOut:                              //회원탈퇴
                    View dialogView = (View) View.inflate(Setting.this, R.layout.userout_dialog, null);
                    final AlertDialog dlg = new AlertDialog.Builder(activity).create();
                    final EditText inputId = (EditText) dialogView.findViewById(R.id.useroutId);
                    ImageView ok = (ImageView) dialogView.findViewById(R.id.useroutOk);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String id = inputId.getText().toString();
                            if (id.equals("")) {
                                Toast.makeText(Setting.this, "모든 칸을 입력하세요", Toast.LENGTH_SHORT).show();
                            } else {
                                if(userInfo.userId.equals(id)){
                                    Call<DeleteUserResult> delete = server.deleteUser(id);
                                    delete.enqueue(new Callback<DeleteUserResult>() {
                                        @Override
                                        public void onResponse(Call<DeleteUserResult> call, Response<DeleteUserResult> response) {
                                            if (response.isSuccessful()) {// 응답코드 200
                                                DeleteUserResult CheckPhoneResult = response.body();
                                                deleteCheck = CheckPhoneResult.message.equals("user withdraw success") ? true : false;
                                            }
                                            if (deleteCheck) {
                                                Toast.makeText(Setting.this, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                                dlg.dismiss();

                                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = auto.edit();
                                                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                                                editor.clear();
                                                editor.commit();
                                                //다시 로그인하기기
                                                Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                                                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(logout);
                                                //PhoneCheckSuccess = false;
                                            } else {
                                                Toast.makeText(Setting.this, "탈퇴 실패 다시 입력해주세요 ", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DeleteUserResult> call, Throwable t) {
                                            Toast.makeText(Setting.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                            Log.i("LoginTest", "요청메시지:" + call.toString());
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(Setting.this, "자신의 아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                    dlg.setIcon(R.drawable.setting_out_icon);
                    dlg.setView(dialogView);
                    dlg.show();
                    break;
            }
        }
    };

    public void onOff() {
        //알람OnOff값 저장
        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Call<AlarmOnoffResult> alarmonoff = server.Alarmonoff(userInfo.userId);
                    alarmonoff.enqueue(new Callback<AlarmOnoffResult>() {
                        @Override
                        public void onResponse(Call<AlarmOnoffResult> call, Response<AlarmOnoffResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                AlarmOnoffResult AlarmOnoffResult = response.body();
                                alarmSuccess = AlarmOnoffResult.message.equals("push alarm update ok") ? true : false;
                                alarmCheck = AlarmOnoffResult.pushInfo;
                                Log.i("check", "check:" + alarmCheck);
                            }
                            if (alarmSuccess) {
                                Toast.makeText(Setting.this, "알람이 켜졌습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Setting.this, "실패.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<AlarmOnoffResult> call, Throwable t) {
                            Toast.makeText(Setting.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });
                } else {
                    Call<AlarmOnoffResult> alarmonoff = server.Alarmonoff(userInfo.userId);
                    alarmonoff.enqueue(new Callback<AlarmOnoffResult>() {
                        @Override
                        public void onResponse(Call<AlarmOnoffResult> call, Response<AlarmOnoffResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                AlarmOnoffResult AlarmOnoffResult = response.body();
                                alarmSuccess = AlarmOnoffResult.message.equals("push alarm update ok") ? true : false;
                                alarmCheck = AlarmOnoffResult.pushInfo;
                                Log.i("check", "check:" + alarmCheck);
                            }
                            if (alarmSuccess) {
                                Toast.makeText(Setting.this, "알람이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Setting.this, "실패.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<AlarmOnoffResult> call, Throwable t) {
                            Toast.makeText(Setting.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });
                }
                Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                Main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(Main);

            }
        });

        //파워OnOff값 저장
        power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.isRun = true;  //쓰레드 돌리기
                    Call<PowerOnoffResult> poweronoff = server.Poweronoff(userInfo.userId);
                    poweronoff.enqueue(new Callback<PowerOnoffResult>() {
                        @Override
                        public void onResponse(Call<PowerOnoffResult> call, Response<PowerOnoffResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                PowerOnoffResult PowerOnoffResult = response.body();
                                PowerSucceess = PowerOnoffResult.message.equals("device connection update ok") ? true : false;
                                powerCheck = PowerOnoffResult.connectInfo;
                                Log.i("powerCheck", "powerCheck:" + powerCheck);
                            }
                            if (PowerSucceess) {
                                Toast.makeText(Setting.this, "전원이 켜졌습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Setting.this, "실패.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PowerOnoffResult> call, Throwable t) {
                            Toast.makeText(Setting.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });

                    // powerCheck = isChecked;
                } else {
                    MainActivity.isRun = false;
                    Call<PowerOnoffResult> poweronoff = server.Poweronoff(userInfo.userId);
                    poweronoff.enqueue(new Callback<PowerOnoffResult>() {
                        @Override
                        public void onResponse(Call<PowerOnoffResult> call, Response<PowerOnoffResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                PowerOnoffResult PowerOnoffResult = response.body();
                                PowerSucceess = PowerOnoffResult.message.equals("device connection update ok") ? true : false;
                                powerCheck = PowerOnoffResult.connectInfo;
                                Log.i("powerCheck", "powerCheck:" + powerCheck);
                            }
                            if (PowerSucceess) {
                                Toast.makeText(Setting.this, "전원이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Setting.this, "실패.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PowerOnoffResult> call, Throwable t) {
                            Toast.makeText(Setting.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }

                    });
                    //   powerCheck = isChecked;
                }
                //메인으로 넘김
                Intent Main = new Intent(getApplicationContext(), MainActivity.class);
                Main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(Main);
            }
        });

    }
}
