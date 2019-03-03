package com.example.ise.watchout.Main;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ise.watchout.R;
import com.example.ise.watchout.global.ApplicationController;
import com.example.ise.watchout.network.Server;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ise.watchout.splash.Splash.deviceInfo;

public class GraphActivity extends AppCompatActivity {
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Entry> lock = new ArrayList<>();
    ArrayList<Entry> man = new ArrayList<>();
    ArrayList<Entry> knock = new ArrayList<>();
    ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    LineChart lineChart;    //라인차트
    LineDataSet lockDataset, manDataset, knockDataset;    //라인차트모양?
    LineData lineData;
    Server server;
    Boolean isStatsSuccess;
    public static int monLock, monMan, monKnok;
    public static int tueLock, tueMan, tueKnok;
    public static int wedLock, wedMan, wedKnok;
    public static int thuLock, thuMan, thuKnok;
    public static int friLock, friMan, friKnok;
    public static int satLock, satMan, satKnok;
    public static int sunLock, sunMan, sunKnok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        server = ApplicationController.getInstance().getServer();
        lineChart = (LineChart) findViewById(R.id.chart);
        isStatsSuccess = false;

        // X축 라벨 추가
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");
        labels.add("일");

        Call<StatsResult> getStats = server.getStatsResult(deviceInfo.dId);
        getStats.enqueue(new Callback<StatsResult>() {
            @Override
            public void onResponse(Call<StatsResult> call, Response<StatsResult> response) {
                StatsResult StatsResult = response.body();
                if (response.isSuccessful()) {// 응답코드 200
                    isStatsSuccess = StatsResult.message.equals("success stats") ? true : false;
                }
                if (isStatsSuccess) {
                    //월요일
                    if (StatsResult.monday != null) {
                        for (int i = 0; i < StatsResult.monday.size(); i++) {
                            if (StatsResult.monday.get(i).content.toString().contains("도어락 입력 시도")) {
                                monLock = StatsResult.monday.get(i).count;
                            }
                            if (StatsResult.monday.get(i).content.toString().contains("두드림")) {
                                monMan = StatsResult.monday.get(i).count;
                            }
                            if (StatsResult.monday.get(i).content.toString().contains("사람접근")) {
                                monKnok = StatsResult.monday.get(i).count;
                            }
                        }
                    }
                    //화요일
                    if (StatsResult.tuesday != null) {
                        for (int i = 0; i < StatsResult.tuesday.size(); i++) {
                            if (StatsResult.tuesday.get(i).content.toString().contains("도어락 입력 시도")) {
                                tueLock = StatsResult.tuesday.get(i).count;
                            }
                            if (StatsResult.tuesday.get(i).content.toString().contains("두드림")) {
                                tueMan = StatsResult.tuesday.get(i).count;
                            }
                            if (StatsResult.tuesday.get(i).content.toString().contains("사람접근")) {
                                tueKnok = StatsResult.tuesday.get(i).count;
                            }
                        }
                    }
                    //수요일
                    if (StatsResult.wednesday != null) {
                        for (int i = 0; i < StatsResult.wednesday.size(); i++) {
                            if (StatsResult.wednesday.get(i).content.toString().contains("도어락 입력 시도")) {
                                wedLock = StatsResult.wednesday.get(i).count;
                            }
                            if (StatsResult.wednesday.get(i).content.toString().contains("두드림")) {
                                wedMan = StatsResult.wednesday.get(i).count;
                            }
                            if (StatsResult.wednesday.get(i).content.toString().contains("사람접근")) {
                                wedKnok = StatsResult.wednesday.get(i).count;
                            }
                        }
                    }
                    //목요일
                    if (StatsResult.thursday != null) {
                        for (int i = 0; i < StatsResult.thursday.size(); i++) {
                            if (StatsResult.thursday.get(i).content.toString().contains("도어락 입력 시도")) {
                                thuLock = StatsResult.thursday.get(i).count;
                            }
                            if (StatsResult.thursday.get(i).content.toString().contains("두드림")) {
                                thuMan = StatsResult.thursday.get(i).count;
                            }
                            if (StatsResult.thursday.get(i).content.toString().contains("사람접근")) {
                                thuKnok = StatsResult.thursday.get(i).count;
                            }
                        }
                    }
                    //금요일
                    if (StatsResult.friday != null) {
                        for (int i = 0; i < StatsResult.friday.size(); i++) {
                            if (StatsResult.friday.get(i).content.toString().contains("도어락 입력 시도")) {
                                friLock = StatsResult.friday.get(i).count;
                            }
                            if (StatsResult.friday.get(i).content.toString().contains("두드림")) {
                                friMan = StatsResult.friday.get(i).count;
                            }
                            if (StatsResult.friday.get(i).content.toString().contains("사람접근")) {
                                friKnok = StatsResult.friday.get(i).count;
                            }
                        }
                    }
                    //토요일
                    if (StatsResult.saturday != null) {
                        for (int i = 0; i < StatsResult.saturday.size(); i++) {
                            if (StatsResult.saturday.get(i).content.toString().contains("도어락 입력 시도")) {
                                satLock = StatsResult.saturday.get(i).count;
                            }
                            if (StatsResult.saturday.get(i).content.toString().contains("두드림")) {
                                satMan = StatsResult.saturday.get(i).count;
                            }
                            if (StatsResult.saturday.get(i).content.toString().contains("사람접근")) {
                                satKnok = StatsResult.saturday.get(i).count;
                            }
                        }
                    }
                    //일요일
                    if (StatsResult.sunday != null) {
                        for (int i = 0; i < StatsResult.sunday.size(); i++) {
                            if (StatsResult.sunday.get(i).content.toString().contains("도어락 입력 시도")) {
                                sunLock = StatsResult.sunday.get(i).count;
                            }
                            if (StatsResult.sunday.get(i).content.toString().contains("두드림")) {
                                sunMan = StatsResult.sunday.get(i).count;
                            }
                            if (StatsResult.sunday.get(i).content.toString().contains("사람접근")) {
                                sunKnok = StatsResult.sunday.get(i).count;
                            }
                        }
                    }
                    //그래프 그리기
                    getGraph();
                } else {
                    Toast.makeText(GraphActivity.this, "통계정보 불러오기 오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatsResult> call, Throwable t) {
                Toast.makeText(GraphActivity.this, "서비스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                Log.i("gg", "요청메시지:" + call.toString());
            }
        });
    }

    public void getGraph() {
        lock.clear();
        man.clear();
        knock.clear();

        //도어락데이터 추가
        lock.add(new Entry(monLock, 0));
        lock.add(new Entry(tueLock, 1));
        lock.add(new Entry(wedLock, 2));
        lock.add(new Entry(thuLock, 3));
        lock.add(new Entry(friLock, 4));
        lock.add(new Entry(satLock, 5));
        lock.add(new Entry(sunLock, 6));

        //사람접근데이터 추가
        man.add(new Entry(monMan, 0));
        man.add(new Entry(tueMan, 1));
        man.add(new Entry(wedMan, 2));
        man.add(new Entry(thuMan, 3));
        man.add(new Entry(friMan, 4));
        man.add(new Entry(satMan, 5));
        man.add(new Entry(sunMan, 6));

        //두드림데이터 추가
        knock.add(new Entry(monKnok, 0));
        knock.add(new Entry(tueKnok, 1));
        knock.add(new Entry(wedKnok, 2));
        knock.add(new Entry(thuKnok, 3));
        knock.add(new Entry(friKnok, 4));
        knock.add(new Entry(satKnok, 5));
        knock.add(new Entry(sunKnok, 6));


        //데이타셋 설정
        lockDataset = new LineDataSet(lock, "도어락");
        lockDataset.setDrawCircles(false);
        lockDataset.setColor(Color.WHITE);

        manDataset = new LineDataSet(man, "사람접근");
        manDataset.setDrawCircles(false);
        manDataset.setColor(Color.RED);

        knockDataset = new LineDataSet(knock, "두드림");
        knockDataset.setDrawCircles(false);
        knockDataset.setColor(Color.CYAN);

        lineDataSets.add(lockDataset);
        lineDataSets.add(manDataset);
        lineDataSets.add(knockDataset);

        //데이타 넣기
        lineData = new LineData(labels, lineDataSets);
        lineChart.setData(lineData); // set the data and list of lables into chart

        //기타 설정 ( X, Y축, Legend, MarkerView 설정 )
        MarkerView mv = new CustomMarkerView(this, R.layout.activity_custom_marker_view);
        //라인차트의 라인을 클릭했을때 떠서 그 위치의 값을 표시하는 창을 MarkerView라고 한다.

        lineChart.setMarkerView(mv);
        lineChart.setDrawMarkerViews(true);

        YAxis y = lineChart.getAxisLeft();
        y.setTextColor(Color.WHITE);

        XAxis x = lineChart.getXAxis();
        x.setTextColor(Color.WHITE);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);

        lineChart.animateXY(2000, 2000); //애니메이션 기능 활성화
        lineChart.invalidate();
    }
}