package com.example.ise.watchout.network;

import com.example.ise.watchout.Join.ConfirmResult;
import com.example.ise.watchout.Join.DupResult;
import com.example.ise.watchout.Join.JoinResult;
import com.example.ise.watchout.Main.AlarmResult;
import com.example.ise.watchout.Main.DeleteResult;
import com.example.ise.watchout.Main.RetResult;
import com.example.ise.watchout.Main.StatsResult;
import com.example.ise.watchout.login.ChangePw;
import com.example.ise.watchout.login.ChangePwResult;
import com.example.ise.watchout.login.CheckPhoneResult;
import com.example.ise.watchout.login.FindIdResult;
import com.example.ise.watchout.login.LoginResult;
import com.example.ise.watchout.setting.AlarmOnoffResult;
import com.example.ise.watchout.setting.DeleteUserResult;
import com.example.ise.watchout.setting.PowerOnoffResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by user on 2017-08-13.
 */

public interface Server {

    //로그인
    @FormUrlEncoded
    @POST("/members/login")
    Call<LoginResult> getLoginResult(@Field("userId") String userId, @Field("pwd") String pwd, @Field("deviceToken") String deviceToken);

    //회원가입
    @FormUrlEncoded
    @POST("/members/join")
    Call<JoinResult> getJoinResult(@Field("userId") String userId, @Field("pwd") String pwd, @Field("phone") String phone, @Field("type") int type,
                                   @Field("pId") String pId, @Field("deviceToken") String deviceToken);

    //아이디 중복확인
    @GET("/members/dup/{userId}")
    Call<DupResult> getDuplicationResult(@Path("userId") String userId);

    //대표자 아이디 확인
    @GET("/members/confirm/{userId}")
    Call<ConfirmResult> getConfirmResult(@Path("userId") String userId);

    //아이디 찾기
    @GET("/members/id/{phone}")
    Call<FindIdResult> getFindIdResult(@Path("phone") String phone);

    //핸드폰번호인증
    @GET("/members/phone/{phone}")
    Call<CheckPhoneResult> getCheckPhoneResult(@Path("phone") String phone);

    //비밀번호 변경-로그인엑티비티
    @PUT("/members/pwd/{phone}")
    Call<ChangePwResult> updatePw(@Path("phone") String phone,@Body ChangePw pwd);

    //회원탈퇴
    @DELETE("/mypage/withdraw/{userId}")
    Call<DeleteUserResult> deleteUser(@Path("userId") String userId);

    //알람받기여부설정
    @PUT("/mypage/alarm/{userId}")
    Call<AlarmOnoffResult> Alarmonoff(@Path("userId") String userId);

    //전원원설정
    @PUT("/mypage/device/{userId}")
    Call<PowerOnoffResult> Poweronoff(@Path("userId") String userId);

    //아두이노값 푸시
    @FormUrlEncoded
    @POST("/push")
    Call<AlarmResult> getAlarm(@Field("content") String content,@Field("dId") String dId);

    //알람정보삭제
    @DELETE("/infos/{userId}")
    Call<DeleteResult> deleteValue(@Path("userId") String userId);

    //알람정보조회
    @GET("/infos/{userId}")
    Call<RetResult> getRetResult(@Path("userId") String userId);

    //기기등록
    @FormUrlEncoded
    @POST("/members/register")
    Call<JoinResult> getRegisterResult(@Field("userId") String userId, @Field("dId") String dId);

    //통계
    @GET("/stats/{dId}")
    Call<StatsResult> getStatsResult(@Path("dId") String dId);


}
