package com.example.ise.watchout.global;

import android.app.Application;

import com.example.ise.watchout.network.Server;
import com.example.ise.watchout.security.SecurityDataSet;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 2017-08-13.
 */
public class ApplicationController extends Application {

    private static final String URL = SecurityDataSet.ServerUrl;
    private static ApplicationController instance;
    private static final String AppVersion = "0.1"; //버전정보

    private Server server;

    public ApplicationController() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = new ApplicationController();
        }
        instance = this;


        //Server Build
        buildServer();
    }

    /**
     * Getter & Setter
     */
    public static ApplicationController getInstance() {
        return instance;
    }

    public String getVersion() {
        return AppVersion;
    }

    public Server getServer() {
        return server;
    }

    /**
     * methods
     */

    private void buildServer() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        server = retrofit.create(Server.class);
    }
}
