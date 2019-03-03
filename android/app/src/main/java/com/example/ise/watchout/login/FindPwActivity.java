package com.example.ise.watchout.login;

import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.network.Server;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPwActivity extends AppCompatActivity {

    EditText findPhone, findNew, findNewCheck;
    ImageView findPhoneCheck, findPwCheck;
    public Server server;
    public String phone, newPw, newPwCheck;
    boolean PhoneCheckSuccess,ChangePwSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        server = ApplicationController.getInstance().getServer();

        findPhone = (EditText) findViewById(R.id.changepw_phone);
        findNew = (EditText) findViewById(R.id.changepw_new);
        findNewCheck = (EditText) findViewById(R.id.changepw_newCheck);
        findPhoneCheck = (ImageView) findViewById(R.id.changepw_phoneCheck);
        findPwCheck = (ImageView) findViewById(R.id.changepw_ok);
        ChangePwSuccess=false;

        //핸드폰 인증버튼
        findPhoneCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = String.valueOf(findPhone.getText());
                if (!Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", phone)) {
                    Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<CheckPhoneResult> getCheckPhoneResult = server.getCheckPhoneResult(phone);
                    getCheckPhoneResult.enqueue(new Callback<CheckPhoneResult>() {
                        @Override
                        public void onResponse(Call<CheckPhoneResult> call, Response<CheckPhoneResult> response) {
                            if (response.isSuccessful()) {// 응답코드 200
                                CheckPhoneResult CheckPhoneResult = response.body();
                                PhoneCheckSuccess = CheckPhoneResult.message.equals("This member exists. Please change your password") ? true : false;
                                //message=  FindIdResult.message;
                            }
                            if (PhoneCheckSuccess) {
                                Toast.makeText(FindPwActivity.this, "인증 되었습니다", Toast.LENGTH_SHORT).show();
                                //PhoneCheckSuccess = false;
                            } else {
                                Toast.makeText(FindPwActivity.this, "다시확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<CheckPhoneResult> call, Throwable t) {
                            Toast.makeText(FindPwActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                            Log.i("LoginTest", "요청메시지:" + call.toString());
                        }
                    });
                }
            }
        });

        //확인버튼
        findPwCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPw = String.valueOf(findNew.getText());
                newPwCheck = String.valueOf(findNewCheck.getText());
                if(findPhone.getText().toString().equals("")|| findNew.getText().toString().equals("")||findNewCheck.getText().toString().equals("")){
                    Toast.makeText(FindPwActivity.this, "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if (!newPw.equals(newPwCheck)) {
                    Toast.makeText(FindPwActivity.this, "새 비밀번호을 확인해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    if (PhoneCheckSuccess) {
                        ChangePw chpw = new ChangePw(newPw);    //바꾼비밀번호 넣을 곳
                        Call<ChangePwResult> changePw = server.updatePw(phone,chpw);
                        changePw.enqueue(new Callback<ChangePwResult>() {
                            @Override
                            public void onResponse(Call<ChangePwResult> call, Response<ChangePwResult> response) {
                                if (response.isSuccessful()) {// 응답코드 200
                                    ChangePwResult ChangePwResult = response.body();
                                    ChangePwSuccess = ChangePwResult.message.equals("password change successful") ? true : false;
                                    //message=  FindIdResult.message;
                                }
                                if (ChangePwSuccess) {
                                    Toast.makeText(FindPwActivity.this, "비밀번호 변경 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                    loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginActivity);
                                    //PhoneCheckSuccess = false;
                                } else {
                                    Toast.makeText(FindPwActivity.this, "다시확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ChangePwResult> call, Throwable t) {
                                Toast.makeText(FindPwActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                Log.i("LoginTest", "요청메시지:" + call.toString());
                            }
                        });

                    } else {
                        Toast.makeText(FindPwActivity.this, "핸드폰 번호 인증해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
