package com.example.ise.watchout.Join;

import android.content.Intent;
import android.support.v4.content.IntentCompat;
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

public class JoinParentActivity extends AppCompatActivity {
    EditText joinParentId, joinParentPw, joinParentPwCheck, joinParentphone, stId;
    TextView joinParentIdCheck, joinParentstIdCheck;
    ImageView joinParentok;
    public Server server;
    boolean isJoinSuccess;
    boolean checksuccess, confirmsuccess;
    String userId, pwd, pwdCheck, phone, pId, deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_parent);

        server = ApplicationController.getInstance().getServer();
        checksuccess = false;
        confirmsuccess = false;

        joinParentId = (EditText) findViewById(R.id.joinID);
        joinParentPw = (EditText) findViewById(R.id.joinPW);
        joinParentPwCheck = (EditText) findViewById(R.id.joinPW_check);
        joinParentphone = (EditText) findViewById(R.id.joinPhone);
        stId = (EditText) findViewById(R.id.join_stuid);
        joinParentIdCheck = (TextView) findViewById(R.id.joinIdCheck);
        joinParentstIdCheck = (TextView) findViewById(R.id.stId);
        joinParentok = (ImageView) findViewById(R.id.joinParentok);

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        //아이디 중복확인
        joinParentIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = String.valueOf(joinParentId.getText());
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
                            Toast.makeText(JoinParentActivity.this, "사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(JoinParentActivity.this, "사용불가한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DupResult> call, Throwable t) {
                        Toast.makeText(JoinParentActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("LoginTest", "요청메시지:" + call.toString());
                    }
                });
            }
        });

        //학생아이디 체크
        joinParentstIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pId = String.valueOf(stId.getText());
                if (pId.equals("")) {
                    Toast.makeText(getApplicationContext(), "입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<ConfirmResult> getConfirmResult = server.getConfirmResult(pId);
                    getConfirmResult.enqueue(new Callback<ConfirmResult>() {
                        @Override
                        public void onResponse(Call<ConfirmResult> call, Response<ConfirmResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                Log.i("JoinTest", "요청메시지:" + call.toString() + " 응답메시지:" + response.toString());
                                ConfirmResult ConfirmResult = response.body();
                                confirmsuccess = ConfirmResult.message.equals("ID exists") ? true : false;

                            }
                            if(confirmsuccess){
                                Toast.makeText(JoinParentActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(JoinParentActivity.this, "학생 아이디가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ConfirmResult> call, Throwable t) {
                            Toast.makeText(JoinParentActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });

                }

            }
        });

        //회원가입 확인
        joinParentok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = String.valueOf(joinParentId.getText());
                pwd = String.valueOf(joinParentPw.getText());
                pwdCheck = String.valueOf(joinParentPwCheck.getText());
                phone = String.valueOf(joinParentphone.getText());
                pId = String.valueOf(stId.getText());

                int type = 2;
                if ((userId.equals("")) || (pwd.equals("")) || (pwdCheck.equals("")) || (phone.equals("")) || (pId.equals(""))) {
                    Toast.makeText(getApplicationContext(), "모든 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(pwdCheck)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하십시오.", Toast.LENGTH_SHORT).show();
                } else if (!checksuccess) {
                    Toast.makeText(JoinParentActivity.this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!confirmsuccess) {
                    Toast.makeText(JoinParentActivity.this, "학생 아이디 인증을 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)) {
                    Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {

                    Call<JoinResult> getJoinResult = server.getJoinResult(userId, pwd, phone, type, pId, deviceToken);
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
                                Toast.makeText(JoinParentActivity.this, "회원가입이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginActivity);
                            } else {
                                //edittext가 공백일때 경고하기
                                Toast.makeText(JoinParentActivity.this, "회원가입에 실패하였습니다. 아이디를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<JoinResult> call, Throwable t) {
                            Toast.makeText(JoinParentActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });

                }

            }
        });


    }
}
