package com.example.basicviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.basicviewer.conn.RetrofitInterface;
import com.example.basicviewer.database.DataAllSensor;
import com.example.basicviewer.database.recordstatus;
import com.example.basicviewer.ui.main.CustomViewPager;
import com.example.login_nodejs.R;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basicviewer.ui.main.SectionsPagerAdapter;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.basicviewer.conn.RetrofitInterface.BASE_URL;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    public RetrofitInterface retrofitInterface;
    DataAllSensor dataAllSensor;


    TextView textSPO2, textBPM, textUser;
    String valueSPO2 = "--", valueBPM = "--", nameUser, idUser;
    boolean readData= false;
    boolean deviceReady = false;
    boolean deviceFound = false;


    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //
        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        textSPO2 = (TextView) findViewById(R.id.spo2);
        textBPM = (TextView) findViewById(R.id.bpm);
        textUser = (TextView) findViewById(R.id.nameUser);
        readData = true;

        //get Nama user dari preference
        SharedPreferences sp1 = this.getSharedPreferences("LoggedInUser", MODE_PRIVATE);
        nameUser =sp1.getString("name", null);
        idUser = sp1.getString("id_pasien", null);
        Log.i("NAMA USER", nameUser);
        textUser.setText("Welcome " + nameUser + " !");

        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                handleLogoutDialog();
            }
        });

        findViewById(R.id.pairMenuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                handlePairDialog();
            }
        });

        //getData(); /*TEST CONNECTION?* : minta data terus meenrus/

    }

    public void setText(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textSPO2.setText(valueSPO2);
                textBPM.setText(valueBPM);
                // Stuff that updates the UI

            }
        });
    }
    private void getData() {
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while (readData) {
                    handleGetRekonstruksiDialog();
                    //vLog.i("readData MAIN", Integer.toString(i++));
                    setText();
                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    private void handleGetRekonstruksiDialog() {

        Call<Collection<DataAllSensor>> call = retrofitInterface.getDataAll(idUser);

        call.enqueue(new Callback<Collection<DataAllSensor>>() {
            @Override
            public void onResponse(Call<Collection<DataAllSensor>> call, Response<Collection<DataAllSensor>> response) {
                response.code();
                Collection<DataAllSensor> result = response.body();

                try {
                DataAllSensor[] dataAllSensorAll = result.toArray(new DataAllSensor[result.size()]);
                dataAllSensor = dataAllSensorAll[0];
                    valueSPO2 = Integer.toString(dataAllSensor.getDataSPO2());
                    Log.i("SPO2-MAIN", valueSPO2);
                    valueBPM = Integer.toString(dataAllSensor.getDataBPM());
                    Log.i("BPM-MAIN", valueBPM);
                    //textSPO2.setText(Integer.toString(dataAllSensor.getDataSPO2()));
                }catch (Exception e){
                    //Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Collection<DataAllSensor>> call, Throwable t) {
                //Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"There is a problem.\nPlease check your internet connection or try again later",Toast.LENGTH_LONG).show();

            }
        });
    }

    public RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }

    private void setLoggedInUser(String name, String id){
        SharedPreferences sp=getSharedPreferences("LoggedInUser", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("name",name );
        Ed.putString("id_pasien",id);
        Ed.commit();
    }

    private void setPairedDevice(String id){
        SharedPreferences spDevice=getSharedPreferences("PairedDevice", MODE_PRIVATE);
        SharedPreferences.Editor Ed=spDevice.edit();
        Ed.putString("id_rompi",id);
        Ed.commit();
    }

    private String getPairedDevice(){
        SharedPreferences spDevice = this.getSharedPreferences("PairedDevice", MODE_PRIVATE);
        return spDevice.getString("id_rompi", null);
    }

    private void moveToNewActivity () {
        Intent intent =  new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void handleLogoutDialog(){
        setLoggedInUser(null,null );
        setPairedDevice(null);
        moveToNewActivity();
    }

    private void handlePairDialog(){
        final View pairView = getLayoutInflater().inflate(R.layout.pair_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog pairDialog = builder.setView(pairView).show();

        Button pairBtn = pairView.findViewById(R.id.pairButton);
        Button disconnectBtn = pairView.findViewById(R.id.disconnectButton);
        final EditText sensorIDEdit = pairView.findViewById(R.id.sensorIDEdit);

        if(getPairedDevice()!= null){
            sensorIDEdit.setText(getPairedDevice());
        }
        pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String idRompi = sensorIDEdit.getText().toString();
                            handlePostPairSensor(idRompi);
                            Thread.sleep(3000);
                            if(deviceFound) {
                                int tryCount = 0;
                                while(tryCount<=5 && !deviceReady){
                                    Log.i("PAIR", "device found mau get");
                                    handleGetRompiStatus(idRompi);
                                    Thread.sleep(3000);
                                    tryCount++;
                                }
                            }
                            Log.i("PAIR", "keluar get");
                            Log.i("PAIR", Boolean.toString(deviceReady));
                            if (deviceReady) {
                                //set Id rompi di device preference
                                setPairedDevice(idRompi);
                                pairDialog.dismiss();
                                Toast.makeText(MainActivity.this,
                                        "Device Connected",
                                        Toast.LENGTH_LONG).show();
                                Log.i("PAIR", "device ready ");
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Rompi not ready. Try again with another ID",
                                        Toast.LENGTH_LONG).show();
                                Log.i("PAIR", "device not ready");
                            }
                        }catch (Exception e){
                            //what to do?
                        }
                    }
                }).start();
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idRompi = getPairedDevice();
                handleDisconnectSensor(idRompi);
            }
        });
    }

    private void handlePostPairSensor(final String idSensor) {
        HashMap<String, String> map = new HashMap<>();

        map.put("id_rompi", idSensor);
        map.put("id_pasien", idUser);

        Call<Integer> call = (Call<Integer>) retrofitInterface.pairDevice(map);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer result = response.body();
                if (result == 1) {
                    try{
                        //kalau device ketemu
                        Toast.makeText(MainActivity.this,
                                "Device Found. Waiting for Response",
                                Toast.LENGTH_SHORT).show();
                        //terus nunggu response balik
                        deviceFound = true;
                        Log.i("PAIR", "Device found");
                    }catch (Exception e){
                        //
                    }
                    //kalau sukses lag baru setpair
                }else {
                    Toast.makeText(MainActivity.this,
                            "Rompi not Found.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private void handleDisconnectSensor(final String idSensor) {

        Call<MessageResponse> call = (Call<MessageResponse>) retrofitInterface.getDisconnectRompi(idSensor, idUser);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code() == 200){
                    MessageResponse result = response.body();
                    Toast.makeText(MainActivity.this,
                            result.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    setPairedDevice(null);
                }else if(response.code() == 400){
                    Gson gson = new Gson();
                    MessageResponse message = gson.fromJson(response.errorBody().charStream(),MessageResponse.class);
                    Toast.makeText(MainActivity.this,
                            message.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleGetRompiStatus(final String idRompi) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Call<String> call = retrofitInterface.getRompiStat(idRompi);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    wait(2500);
                }catch (Exception e){
                    //
                }
                String result = response.body();
                if (result == "true") {
                    //kalau device udah kasih response
                    //rompistat true
                    Toast.makeText(MainActivity.this,
                            "Pairing Successfull",
                            Toast.LENGTH_SHORT).show();
                    deviceReady = true;
                    Log.i("PAIR", "Device Ready");
                }else {
                    Toast.makeText(MainActivity.this,
                            "Rompi not ready. Try again with another ID",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}