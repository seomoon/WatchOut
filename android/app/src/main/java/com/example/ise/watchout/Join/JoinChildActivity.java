package com.example.ise.watchout.Join;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.login.LoginActivity;
import com.example.ise.watchout.network.Server;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.push.MyFirebaseInstanceIDService.token;

public class JoinChildActivity extends AppCompatActivity {
    EditText joinChildId, joinChildPw, joinChildPwCheck, joinChildPhone;
    ImageView joinChildbtn;
    Activity activity;
    View device;
    ImageView deviceok;
    EditText deviceId;
    TextView devicecheck, joinChildIdCheck;

    public Server server;
    boolean isJoinSuccess;
    boolean checksuccess, deviceSuccess;
    String userId, pwd, pwdCheck, phone, deviceToken, dId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        checksuccess = false;
        deviceSuccess = false;
        server = ApplicationController.getInstance().getServer();

        joinChildId = (EditText) findViewById(R.id.joinID);
        joinChildPw = (EditText) findViewById(R.id.joinPW);
        joinChildPwCheck = (EditText) findViewById(R.id.joinPW_check);
        joinChildPhone = (EditText) findViewById(R.id.joinPhone);
        joinChildbtn = (ImageView) findViewById(R.id.joinChildok);
        joinChildIdCheck = (TextView) findViewById(R.id.idCheck);

        activity = this;


        //아이디 중복체크하는 곳
        joinChildIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = String.valueOf(joinChildId.getText());
                //Toast.makeText(JoinParentActivity.this, "아이디 중복확인", Toast.LENGTH_SHORT).show();
                Call<DupResult> getDuplicationResult = server.getDuplicationResult(userId);
                getDuplicationResult.enqueue(new Callback<DupResult>() {
                    @Override
                    public void onResponse(Call<DupResult> call, Response<DupResult> response) {
                        if (response.isSuccessful()) {// 응답코드 200
                            DupResult DupResult = response.body();
                            checksuccess = DupResult.message.equals("available ID") ? true : false;
                        }
                        if (checksuccess) {
                            Toast.makeText(JoinChildActivity.this, "사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(JoinChildActivity.this, "사용불가한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DupResult> call, Throwable t) {
                        Toast.makeText(JoinChildActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("LoginTest", "요청메시지:" + call.toString());
                    }
                });

            }
        });

        //확인 눌렀을 때 회원가입되고 기기아이디 등록 다이얼로그 뜸 -> 확인하면 로그인창으로 넘어감
        joinChildbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = String.valueOf(joinChildId.getText());
                pwd = String.valueOf(joinChildPw.getText());
                pwdCheck = String.valueOf(joinChildPwCheck.getText());
                phone = String.valueOf(joinChildPhone.getText());
                /***
                 * 여기 토큰~~~밑에!!!
                 *
                 */
                deviceToken = FirebaseInstanceId.getInstance().getToken();
                System.out.print("@@@@@@@"+deviceToken);

                //학생 회원가입은 타입 1
                int type = 1;

                if ((userId.equals("")) || (pwd.equals("")) || (pwdCheck.equals("")) || (phone.equals(""))) {
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(pwdCheck)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하십시오.", Toast.LENGTH_SHORT).show();
                } else if (!checksuccess) {
                    Toast.makeText(JoinChildActivity.this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)) {
                    Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<JoinResult> getJoinResult = server.getJoinResult(userId, pwd, phone, type, null, deviceToken);
                    getJoinResult.enqueue(new Callback<JoinResult>() {
                        @Override
                        public void onResponse(Call<JoinResult> call, Response<JoinResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                Log.i("JoinTest", "요청메시지:" + call.toString() + " 응답메시지:" + response.toString());
                                JoinResult JoinResult = response.body();
                                isJoinSuccess = JoinResult.message.equals("sucess in join") ? true : false;

                            }
                            if (isJoinSuccess) {
                                //회원가입이 성공했을 때. 튜토리얼 화면으로 이동.
                                Toast.makeText(JoinChildActivity.this, "회원가입이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                //다이얼로그 뜸

                                final AlertDialog devicedlg = new AlertDialog.Builder(activity).create();

                                device = (View) View.inflate(JoinChildActivity.this, R.layout.activity_deviceid, null);
                                devicecheck = (TextView) device.findViewById(R.id.devicecheck);
                                deviceok = (ImageView) device.findViewById(R.id.deviceok);
                                deviceId = (EditText) device.findViewById(R.id.joinDevice);

                                devicecheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dId = String.valueOf(deviceId.getText());
                                        Call<JoinResult> getRegisterResult = server.getRegisterResult(userId, dId);
                                        getRegisterResult.enqueue(new Callback<JoinResult>() {
                                            @Override
                                            public void onResponse(Call<JoinResult> call, Response<JoinResult> response) {
                                                if (response.isSuccessful()) {// 응답코드 200
                                                    JoinResult JoinResult = response.body();
                                                    deviceSuccess = JoinResult.message.equals("sucess in register") ? true : false;
                                                }
                                                if (deviceSuccess) {
                                                    Toast.makeText(JoinChildActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(JoinChildActivity.this, "존재하지 않는 기기 아이디/ 이미 사용중인 기기입니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<JoinResult> call, Throwable t) {
                                                Toast.makeText(JoinChildActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                                Log.i("LoginTest", "요청메시지:" + call.toString());
                                            }
                                        });


                                    }
                                });


                                deviceok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        devicedlg.dismiss();
                                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(loginActivity);

                                    }
                                });
                                devicedlg.setView(device);
                                devicedlg.show();

                                //다이얼로그 끝

                            } else {
                                Toast.makeText(JoinChildActivity.this, "회원가입에 실패하였습니다. 아이디를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<JoinResult> call, Throwable t) {
                            Toast.makeText(JoinChildActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });

                }


            }
        });


    }
}
