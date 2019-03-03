package com.example.ise.watchout.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.network.Server;
import com.example.ise.watchout.setting.Setting;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.splash.Splash.deviceInfo;
import static com.example.ise.watchout.splash.Splash.userInfo;

public class MainActivity extends AppCompatActivity {
    final String link = new String("http://220.69.171.40");
    String sound, body, vibration;
    String lock, knock, man, noise;
    String serverResult;   //내용 전체 넣을거
    ArrayList<Ret> rDatas = new ArrayList<Ret>();

    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    RetAdapter adapter;

    ImageView setting, graph, delete;
    Boolean alaramSuccess, deleteSuccess;

    public static Thread crawlingThread;
    public static boolean isRun = true;

    Server server;
    boolean isAlarmInfosSuccess;
    int position;
    public static int checkAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = (ImageView) findViewById(R.id.setting);
        graph = (ImageView) findViewById(R.id.graph);
        delete = (ImageView) findViewById(R.id.deleteicon);

        //알림에서 사용
        FirebaseMessaging.getInstance().subscribeToTopic("alarm");
        FirebaseInstanceId.getInstance().getToken();

        server = ApplicationController.getInstance().getServer();
        alaramSuccess = false;
        deleteSuccess = false;
        isAlarmInfosSuccess = false;

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        rDatas = new ArrayList<Ret>();

        //삭제하기
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<DeleteResult> deletevaule = server.deleteValue(userInfo.userId);
                deletevaule.enqueue(new Callback<DeleteResult>() {
                    @Override
                    public void onResponse(Call<DeleteResult> call, Response<DeleteResult> response) {
                        if (response.isSuccessful()) {// 응답코드 200
                            DeleteResult deleteResult = response.body();
                            deleteSuccess = deleteResult.message.equals("alarm info delete success") ? true : false;
                        } else {
                        }
                        if (deleteSuccess) {
                            Toast.makeText(MainActivity.this, "정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DeleteResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("LoginTest", "요청메시지:" + call.toString());
                    }
                });
                rDatas.clear();
                adapter.notifyDataSetChanged();
            }
        });
        //설정으로가기
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //부모 자식 모두 같은 액티비티로 감
                Intent setting = new Intent(getApplicationContext(), Setting.class);
                startActivity(setting);

            }
        });
        //통계보러가기
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graphActivity = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(graphActivity);

            }
        });
        Call<RetResult> getRetResult = server.getRetResult(userInfo.userId);
        getRetResult.enqueue(new Callback<RetResult>() {
            @Override
            public void onResponse(Call<RetResult> call, Response<RetResult> response) {
                RetResult RetResult = response.body();
                if (response.isSuccessful()) {// 응답코드 200
                    isAlarmInfosSuccess = RetResult.message.equals("alarm info successful") ? true : false;
                }
                if (isAlarmInfosSuccess) {
                    rDatas.addAll(RetResult.ret);
                    Log.i("gg", "사이즈" + RetResult.ret.size());
                    adapter.notifyDataSetChanged();
                    isAlarmInfosSuccess = false;
                } else {
                    Toast.makeText(MainActivity.this, "알람정보 불러오기 오류", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RetResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                Log.i("gg", "요청메시지:" + call.toString());
            }
        });
        /**
         * 3. Adapter 생성 후 recyclerview에 지정
         */
        adapter = new RetAdapter(rDatas);
        recyclerView.setAdapter(adapter);


        crawlingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    try {
                        //   Jsoup.connect(link).timeout(30000).get();
                        Thread.sleep(1000);

                        //아두이노 서버 접근해서 값 받아오기
                        Connection.Response response = Jsoup.connect(link).method(Connection.Method.GET).execute();
                        Document googleDocument = response.parse();

                        Elements btnK = googleDocument.select("table tr td");

                        //초기화
                        lock = "";  knock = "";  man = "";  noise = "";  serverResult = "";

                        //값받아오기
                        body = btnK.first().text();
                        sound = btnK.get(1).text();
                        vibration = btnK.last().text();

                        if (!(body.equals("") && sound.equals("") && vibration.equals(""))) {
                            checkValue();
                        }

                    } catch (Exception e) {    }
                }
            }
        });
        crawlingThread.start(); // 쓰레드 시작
    }

    public void checkValue() {
        //도어락
        if (sound.equals("Sound check") && body.equals("Human detection") && vibration.equals("")) {
            lock = " 도어락 입력 시도 ";
            serverResult = lock;//값 서버로 보내주기, formatDate보내주기
            System.out.println(lock);

        }
        //두드림
        if (sound.equals("Sound check") && vibration.equals("Vibration check") && body.equals("")) {
            knock = " 두드림 ";
            serverResult = knock;
            System.out.println(knock);
        }
        //사람접근
        if (sound.equals("Sound check") && body.equals("Human detection") && vibration.equals("Vibration check")) {
            man = " 사람접근 ";
            serverResult = man;
            System.out.println(man);
        }

        if (serverResult.equals("")) {
            Toast.makeText(MainActivity.this, "없음", Toast.LENGTH_SHORT).show();
        } else {
            if (userInfo.type == 1) {
                Call<AlarmResult> getAlarmvalue = server.getAlarm(serverResult, deviceInfo.dId);
                getAlarmvalue.enqueue(new Callback<AlarmResult>() {
                    @Override
                    public void onResponse(Call<AlarmResult> call, Response<AlarmResult> response) {
                        if (response.isSuccessful()) {// 응답코드 200
                            AlarmResult AlarmResult = response.body();
                            alaramSuccess = AlarmResult.message.equals("sucess in send message") ? true : false;
                        }
                        if (alaramSuccess) {
                            //값바뀔때마다 업데이트
                            Call<RetResult> getRetResult = server.getRetResult(userInfo.userId);
                            getRetResult.enqueue(new Callback<RetResult>() {
                                @Override
                                public void onResponse(Call<RetResult> call, Response<RetResult> response) {
                                    RetResult RetResult = response.body();
                                    if (response.isSuccessful()) {// 응답코드 200
                                        isAlarmInfosSuccess = RetResult.message.equals("alarm info successful") ? true : false;
                                    }
                                    if (isAlarmInfosSuccess) {
                                        rDatas.clear();
                                        rDatas.addAll(RetResult.ret);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                        isAlarmInfosSuccess = false;
                                    } else {
                                        Toast.makeText(MainActivity.this, "알람정보 불러오기 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<RetResult> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                                    Log.i("gg", "요청메시지:" + call.toString());

                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<AlarmResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("LoginTest", "요청메시지:" + call.toString());
                    }
                });
            }       //본인일 때만 푸시를 한다.

            else {
                //값바뀔때마다 업데이트
                Call<RetResult> getRetResult = server.getRetResult(userInfo.userId);
                getRetResult.enqueue(new Callback<RetResult>() {
                    @Override
                    public void onResponse(Call<RetResult> call, Response<RetResult> response) {
                        RetResult RetResult = response.body();
                        if (response.isSuccessful()) {// 응답코드 200
                            isAlarmInfosSuccess = RetResult.message.equals("alarm info successful") ? true : false;
                        }
                        if (isAlarmInfosSuccess) {
                            rDatas.clear();
                            rDatas.addAll(RetResult.ret);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            isAlarmInfosSuccess = false;
                        } else {
                            Toast.makeText(MainActivity.this, "알람정보 불러오기 오류", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RetResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("gg", "요청메시지:" + call.toString());
                    }
                });
            }
        }
    }


    //Back 터치했을 때 불리는 함수
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //앱 종료에 대한 취소버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //앱 종료에 대한 확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.super.onBackPressed();
            }
        });

        builder.setMessage("앱을 종료하시겠습니까?");        //다이얼로그로 메세지 띄움
        AlertDialog dialog = builder.create();
        dialog.show();
    }


//    private View.OnClickListener recylerClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            //1.리사이클러뷰에 몇번째 항목을 클릭했는지 그 position을 가져오는 것.
//            position = recyclerView.getChildLayoutPosition(v);
//
//            String time = rDatas.get(position).time;
//            Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();
//
//        }
//    };
}

