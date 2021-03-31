package com.example.basicviewer.conn;

import com.example.basicviewer.LoginResult;
import com.example.basicviewer.MessageResponse;
import com.example.basicviewer.RegisterStatus;
import com.example.basicviewer.database.*;

import java.util.Collection;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface RetrofitInterface {
    //public String BASE_URL = "https://bysonics-alpha001-tester.herokuapp.com/";
    //public String BASE_URL = "https://bysonicsalpha0002.herokuapp.com/"; //personal
    public String BASE_URL = "https://bysonicsalpha0003.herokuapp.com/"; //personal TEST
    //public String BASE_URL = "https://bysonics-alpha002-v1.herokuapp.com/"; //team
    //public String BASE_URL = "https://bysonics-alpha001.herokuapp.com/";

//    @GET("/dataPPG/Lastest")
//    Call<Collection<DataPPG>> getDataPPG();
//
//    @GET("/dataEKG/Lastest")
//    Call<Collection<DataEKG>> getDataEKG();
//
//    @GET("/dataEMG/Lastest")
//    Call<Collection<DataEMG>> getDataEMG();
//
//    @GET("/dataAccelerometer/Lastest")
//    Call<Collection<DataAccelerometer>> getDataAcce();
//
//    @GET("/dataSuhu/Lastest")
//    Call<Collection<DataSuhu>> getDataSuhu();



    //Recording status
    @POST("/recording/start")
    Call<MessageResponse> startRecording(@Body HashMap<String, String> map);

    @POST("/recording/end")
    Call<MessageResponse> endRecording(@Body HashMap<String, String> map);

    @GET("recording/recordStat/ID")
    Call<Collection<recordstatus>> getRecordStat(@Query("rompiID") String id_rompi);

    @POST("/user/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    //void karena saat setelah signup no feedback
    @POST("/user/signup")
    Call<RegisterStatus> executeSignup(@Body HashMap<String, String> map);

    @GET("/dataImage/Lastest/ID")
    Call<Collection<DataImage>> getDataImage(@Query("pasienID") String id_pasien);

    @GET("/dataAllSensor/Lastest/ID")
    Call<Collection<DataAllSensor>> getDataAll(@Query("pasienID") String id_pasien);

    @POST("/sensor/pair")
    Call<Integer> pairDevice(@Body HashMap<String, String> map);

    @GET("/sensor/readStat/ID")
    Call<String> getRompiStat(@Query("rompiID") String id_rompi);

    @GET("/sensor/disconnect/ID")
    Call<MessageResponse> getDisconnectRompi(@Query("rompiID") String id_rompi,
                                    @Query("pasienID") String id_pasien);

    //get Last record
    @GET("/dataAllSensor/LastRecord/ID")
    Call<Collection<DataAllSensor>> getLastRecord(@Query("pasienID") String id_pasien);

    @GET("/dataAllSensor/Record/ID")
    Call<Collection<DataAllSensor>> getRecord(@Query("pasienID") String id_pasien,
                                                  @Query("recordIndex") String indexRecord);

}
