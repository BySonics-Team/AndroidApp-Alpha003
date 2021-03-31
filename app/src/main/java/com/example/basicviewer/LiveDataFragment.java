package com.example.basicviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.basicviewer.conn.RetrofitInterface;
import com.example.basicviewer.database.DataAllSensor;
import com.example.basicviewer.database.recordstatus;
import com.example.login_nodejs.R;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.example.basicviewer.conn.RetrofitInterface.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveDataFragment extends Fragment {
    //
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    String nameUser,idUser, idRompi;
    DataAllSensor dataAllSensor;
    private String lastPack_id = "000000", currPack_id = "000000";

    boolean firstFailure = false, drawData = false;
    Date currentTime;

    String valueSPO2 = "--", valueBPM="--";
    Handler handler;

    GraphView graphPPG,graphEKG, graphEMG, graphAcce, graphSuhu;
    TextView textSPO2, textBPM;
    Button stopBtn, startBtn, resetBtn;
    int i;
    private LineGraphSeries<DataPoint> seriesPPG, seriesEKG, seriesEMG, seriesAcceX, seriesAcceY, seriesAcceZ, seriesSuhu;
    private int lastX =0, packCount =0;
    private int indexData =0, samePackage = 0;
    private boolean record = false, graphStart = false;


    List<Integer[]> dataTrayPPG, dataTrayEKG, dataTrayEMG, dataTrayAcceX, dataTrayAcceY, dataTrayAcceZ, dataTraySuhu;
    Integer[] dataPPG, dataEKG, dataEMG, dataAcceX, dataAcceY, dataAcceZ, dataSuhu;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String ARG_SECTION_NUMBER = "section_number";
    View root;
    public LiveDataFragment() {
        // Required empty public constructor
    }

    public static LiveDataFragment newInstance(String param1, String param2) {
        LiveDataFragment fragment = new LiveDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        //preparing data Tray
        dataTrayPPG = new ArrayList<Integer[]>();
        dataTrayEKG = new ArrayList<Integer[]>();
        dataTrayEMG = new ArrayList<Integer[]>();
        dataTrayAcceX = new ArrayList<Integer[]>();
        dataTrayAcceY = new ArrayList<Integer[]>();
        dataTrayAcceZ = new ArrayList<Integer[]>();
        dataTraySuhu = new ArrayList<Integer[]>();


        currentTime = Calendar.getInstance().getTime();
        Log.i("Live Data", "onCreate: ");
        getStat();
        handler = new Handler(Looper.getMainLooper());
        //setText();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_live, container, false);
        //Grafik Set up
        //PPG
        graphPPG = (GraphView) root.findViewById(R.id.graphLivePPG);
        seriesPPG = new LineGraphSeries<DataPoint>();
        setGraph(graphPPG, seriesPPG, Color.BLUE);

        //EKG
        graphEKG = (GraphView) root.findViewById(R.id.graphLiveEKG);
        seriesEKG = new LineGraphSeries<DataPoint>();
        setGraph(graphEKG, seriesEKG, Color.MAGENTA);

        //EMG
        graphEMG = (GraphView) root.findViewById(R.id.graphLiveEMG);
        seriesEMG = new LineGraphSeries<DataPoint>();
        setGraph(graphEMG, seriesEMG, Color.RED);

        //Acce
        graphAcce = (GraphView) root.findViewById(R.id.graphLiveAcce);
        seriesAcceX = new LineGraphSeries<DataPoint>();
        setGraph(graphAcce, seriesAcceX, Color.YELLOW);

        seriesAcceY = new LineGraphSeries<DataPoint>();
        setGraph(graphAcce, seriesAcceY, Color.CYAN);

        seriesAcceZ = new LineGraphSeries<DataPoint>();
        setGraph(graphAcce, seriesAcceZ, Color.GREEN);

        //Suhu
        graphSuhu = (GraphView) root.findViewById(R.id.graphLiveSuhu);
        seriesSuhu = new LineGraphSeries<DataPoint>();
        setGraph(graphSuhu, seriesSuhu, Color.LTGRAY);
//        textView = root.findViewById(R.id.textFragment);


        startBtn = root.findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click Button", "onClick: Start");
                handleStartRecord();
            }
        });

        stopBtn = root.findViewById(R.id.btnStop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click Button", "onClick: Stop");
                handleEndRecord();
            }
        });

        resetBtn = root.findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click Button", "onClick: Reset");
                resetGraph();
            }
        });
        setLoggedInUser();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("TAG!", "onActivityCreated: here once" );

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // we add 100 new entries
//                onRecord();
//                // sleep to slow down the add of entries
//                try {
//                    Thread.sleep(80);
//                } catch (InterruptedException e) {
//                    // manage error ...
//                }
//            }
//        }).start();
//        //
    }

    private void onRecord() {
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while (record) {
                    drawData = true;
                    // sleep to slow down the add of entries
                    try {
                        if (i<packCount) {
                            final Integer[] dataPlotPPG = dataTrayPPG.get(i);
                            final Integer[] dataPlotEKG = dataTrayEKG.get(i);
                            final Integer[] dataPlotEMG = dataTrayEMG.get(i);
                            final Integer[] dataPlotAcceX = dataTrayAcceX.get(i);
                            final Integer[] dataPlotAcceY = dataTrayAcceY.get(i);
                            final Integer[] dataPlotAcceZ = dataTrayAcceZ.get(i);
                            final Integer[] dataPlotSuhu = dataTraySuhu.get(i);
                            indexData = 0;
                            Log.i("panjang Data", Integer.toString(dataPlotPPG.length));
                            for (int j = 0; j < 256; j++) {
                                Log.i("index J", Integer.toString(indexData));
                                Log.i("indexData", Integer.toString(indexData));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addEntry(dataPlotPPG, seriesPPG);
                                        addEntry(dataPlotEKG, seriesEKG);
                                        addEntry(dataPlotEMG, seriesEMG);
                                        addEntry(dataPlotAcceX, seriesAcceX);
                                        addEntry(dataPlotAcceY, seriesAcceY);
                                        addEntry(dataPlotAcceZ, seriesAcceZ);
                                        addEntry(dataPlotSuhu, seriesSuhu);
                                        lastX++;
                                        indexData++;
                                    }
                                });
                                Thread.sleep(40);
                            }
                            i++;
                        }
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
                drawData = false;
            }
        }).start();
    }

    private void addEntry(Integer[] data, LineGraphSeries<DataPoint> series) {
        double x;
        x = (double) lastX*0.04;
        try{
            if (x <= 2){
                series.appendData(new DataPoint(x,(double) data[indexData]), false, 500);
            }else{
                series.appendData(new DataPoint(x,(double)  data[indexData]), true, 500);
            }
        }catch (Exception e){
            Log.i("exception", "addEntry: oops");
        }
    }

    protected void getStat() {
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        //handleGetRecordStatus(); // ini diganti jadi kalau udha nemu pair atau deteksi koneksi lain
                        Log.i("getStat", Boolean.toString(record));
                        if (record){
                            if(samePackage <= 100){
                                dataAllSensor = handleGetRekonstruksiDialog();
                                Thread.sleep(3000);
                                try {
                                    if (parseTime(dataAllSensor.getCreatedAt())) {
                                        currPack_id = dataAllSensor.get_id();
                                        if (currPack_id.equals(lastPack_id) || currPack_id.equals("000000") || !(parseTime(dataAllSensor.getCreatedAt()))) {
                                            Log.i("IDComp Duplicate package", "Same");
                                            samePackage++;
                                        } else {
                                            samePackage = 0;
                                            //Thread.sleep(2000);
                                            addDataTray();
                                            //dataTray = new Integer[packCount][dataRekonstruksi.length];
                                            packCount++;
                                            Log.i("packCount:", Integer.toString(packCount));
                                            lastPack_id = currPack_id;
                                            if (!graphStart /*&& packCount==4*/) {
                                                graphStart = true;
                                                lastX = 0;
                                                i = 0;
                                                Log.i("graph start", "run: graph start");
                                                //setOnRecord(record);
                                                onRecord();
                                            }
                                        }
                                    }
                                }catch(Exception e){
                                    //manage error data kosong
                                }
                            }else{
                                handleEndRecord();
                                Thread.sleep(2000);

                                resetGraph();
                            }
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    private void handleStartRecord() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        HashMap<String, String> map = new HashMap<>();

        idRompi = getPairedDevice();
        map.put("id_rompi", idRompi);
        map.put("id_pasien", idUser);
        //Log.i("start", idRompi);
        //Log.i("start", idUser);


        Call<MessageResponse> call = retrofitInterface.startRecording(map);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code() == 200){
                    Log.i("PAIR", "success start");
                    MessageResponse result = response.body();
                    Toast.makeText(getActivity(),
                            result.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    record = true;
                }else if(response.code() == 400){
                    Log.i("PAIR", "fail start");
                    Gson gson = new Gson();
                    MessageResponse message =gson.fromJson(response.errorBody().charStream(),MessageResponse.class);
                    //Log.i("", message.getMessage());
                    Toast.makeText(getActivity(),
                            message.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                //responseMsg result = response.body();
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                //Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "There is a problem.\n Please check your internet connection.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleEndRecord() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        HashMap<String, String> map = new HashMap<>();

        idRompi = getPairedDevice();
        map.put("id_rompi", idRompi);
        map.put("id_pasien", idUser);

        Call<MessageResponse> call = retrofitInterface.endRecording(map);

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code() == 200){
                    Log.i("PAIR", "success end");
                    MessageResponse result = response.body();
                    Toast.makeText(getActivity(),
                            result.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    record = false;

                    //nanti auto nutup dan buka
                    //handleLoginDialog();
                }else if(response.code() == 400){
                    Log.i("PAIR", "fail start");
                    Gson gson = new Gson();
                    MessageResponse message =gson.fromJson(response.errorBody().charStream(),MessageResponse.class);
                    //Log.i("", message.getMessage());
                    Toast.makeText(getActivity(),
                            message.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                //Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "There is a problem.\n Please check your internet connection.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleGetRecordStatus() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Call<Collection<recordstatus>> call = retrofitInterface.getRecordStat(idRompi);

        call.enqueue(new Callback<Collection<recordstatus>>() {
            @Override
            public void onResponse(Call<Collection<recordstatus>> call, Response<Collection<recordstatus>> response) {
                if(firstFailure){
                    Toast.makeText(getActivity(),"Connection Resolved. Welcome Back!",Toast.LENGTH_SHORT).show();
                    firstFailure = false;
                }
                response.code();
                Collection<recordstatus> result = response.body();
                try{
                    recordstatus[] allrecordstatus = result.toArray(new recordstatus[result.size()]);
                    recordstatus stat = allrecordstatus[0];
                    //Log.i("response",);
                    record = stat.isRecordStat();
                    Log.d("recordStat", String.valueOf(record));
                    //statusLabel.setText(result.getStatusMessage());
                }catch(Exception e){
                    Log.i("connect Fail", "data null");
                }
            }
            @Override
            public void onFailure(Call<Collection<recordstatus>> call, Throwable t) {
                if (!firstFailure){
                    Log.i("connect Fail", t.getMessage());
                    Toast.makeText(getActivity(),"There is a problem.\nPlease check your internet connection or try again later",Toast.LENGTH_LONG).show();
                    firstFailure = true;
                }

            }
        });
    }

    private DataAllSensor handleGetRekonstruksiDialog() {
        Call<Collection<DataAllSensor>> call = retrofitInterface.getDataAll(idUser);

        call.enqueue(new Callback<Collection<DataAllSensor>>() {
            @Override
            public void onResponse(Call<Collection<DataAllSensor>> call, Response<Collection<DataAllSensor>> response) {
                response.code();
                try {
                    Collection<DataAllSensor> result = response.body();


                    DataAllSensor[] dataAllSensorAll = result.toArray(new DataAllSensor[result.size()]);
                    dataAllSensor = dataAllSensorAll[0];
                }catch (Exception e){

                }
                //dataRekonstruksi = dataAllSensor[0].getdataAllSensor().toArray(new Integer[dataAllSensor[0].getdataAllSensor().size()]);
            }
            @Override
            public void onFailure(Call<Collection<DataAllSensor>> call, Throwable t) {
                Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        return dataAllSensor;
    }

    public boolean parseTime(String date){
        //String date = "2020-11-11T04:12:39.205Z";
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formattedDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        boolean timevalid= false;

        try{
            Date timePacket = formattedDate.parse(date);
            Log.i("time packet",timePacket.toString());
            Log.i("time now", currentTime.toString());
            if (timePacket.after(currentTime)){
                Log.i("time valid", "yes");
                timevalid = true;
            }else{
                Log.i("time valid", "no");
                timevalid = false;
            }

        }catch (Exception e){

        }
        return timevalid;
    }

    public void setGraph(GraphView graph, LineGraphSeries<DataPoint> series, int colorGraph){
        series.setColor(colorGraph);
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(2);
        viewport.setScrollable(true);

        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");
        graph.getGridLabelRenderer().setLabelVerticalWidth(110);
        graph.getGridLabelRenderer().setGridColor(Color.RED);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().reloadStyles();
        graph.getGridLabelRenderer().setHorizontalAxisTitle("time (s)");

        //graph.setHorizontalAxisTitle("")
    }

    public void addDataTray(){
        //PPG
        dataPPG = dataAllSensor.getDataPPG().toArray(new Integer[dataAllSensor.getDataPPG().size()]);
        dataTrayPPG.add(dataPPG);
        //EKG
        dataEKG = dataAllSensor.getDataEKG().toArray(new Integer[dataAllSensor.getDataEKG().size()]);
        dataTrayEKG.add(dataEKG);
        //EMG
        dataEMG = dataAllSensor.getDataEMG().toArray(new Integer[dataAllSensor.getDataEMG().size()]);
        dataTrayEMG.add(dataEMG);
        //Acce X
        dataAcceX = dataAllSensor.getDataAccelerometer_X().toArray(new Integer[dataAllSensor.getDataAccelerometer_X().size()]);
        dataTrayAcceX.add(dataAcceX);
        //Acce Y
        dataAcceY = dataAllSensor.getDataAccelerometer_Y().toArray(new Integer[dataAllSensor.getDataAccelerometer_Y().size()]);
        dataTrayAcceY.add(dataAcceY);
        //Acce Z
        dataAcceZ = dataAllSensor.getDataAccelerometer_Z().toArray(new Integer[dataAllSensor.getDataAccelerometer_Z().size()]);
        dataTrayAcceZ.add(dataAcceZ);
        //Suhu
        dataSuhu = dataAllSensor.getDataSuhu().toArray(new Integer[dataAllSensor.getDataSuhu().size()]);
        dataTraySuhu.add(dataSuhu);
    }

    public void resetGraph(){
        if (drawData){
            Toast.makeText(getActivity(),"Please end record first and wait for the graph to stop.",Toast.LENGTH_SHORT).show();
        }else{
            //nanti jadi resetGraph();
            Log.i("reset graph", "run: restgraph");
            graphStart = false;
            samePackage = 0;
            packCount = 0;
            //dataPPG
            dataTrayPPG.removeAll(dataTrayPPG);
            graphPPG.removeAllSeries();
            seriesPPG = new LineGraphSeries<DataPoint>();
            setGraph(graphPPG, seriesPPG, Color.BLUE);

            //EKG
            dataTrayEKG.removeAll(dataTrayEKG);
            graphEKG.removeAllSeries();
            //EMG
            dataTrayEMG.removeAll(dataTrayEMG);
            graphEMG.removeAllSeries();
            //Acce_X
            dataTrayAcceX.removeAll(dataTrayAcceX);
            dataTrayAcceY.removeAll(dataTrayAcceY);
            dataTrayAcceZ.removeAll(dataTrayAcceZ);
            graphAcce.removeAllSeries();
            //EMG
            dataTraySuhu.removeAll(dataTraySuhu);
            graphSuhu.removeAllSeries();

            seriesEKG = new LineGraphSeries<DataPoint>();
            setGraph(graphEKG, seriesEKG, Color.MAGENTA);

            seriesEMG = new LineGraphSeries<DataPoint>();
            setGraph(graphEMG, seriesEMG, Color.RED);

            seriesAcceX = new LineGraphSeries<DataPoint>();
            setGraph(graphAcce, seriesAcceX, Color.YELLOW);

            seriesAcceY = new LineGraphSeries<DataPoint>();
            setGraph(graphAcce, seriesAcceY, Color.CYAN);

            seriesAcceZ = new LineGraphSeries<DataPoint>();
            setGraph(graphAcce, seriesAcceZ, Color.GREEN);

            seriesSuhu = new LineGraphSeries<DataPoint>();
            setGraph(graphSuhu, seriesSuhu, Color.LTGRAY);
        }

    }

    public void setLoggedInUser(){
        SharedPreferences sp1 = getActivity().getSharedPreferences("LoggedInUser", MODE_PRIVATE);
        nameUser = sp1.getString("id_pasien", null);
        idUser = sp1.getString("id_pasien", null);
        //idRompi = "001"; //sementara
        Log.i("NAME USER IMAGE", nameUser);
        Log.i("ID USER IMAGE", idUser);
    }

    private String getPairedDevice(){
        SharedPreferences spDevice =  getActivity().getSharedPreferences("PairedDevice", MODE_PRIVATE);
        return spDevice.getString("id_rompi", null);
    }

}