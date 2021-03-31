package com.example.basicviewer;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.basicviewer.conn.RetrofitInterface;
import com.example.basicviewer.database.DataAllSensor;
import com.example.login_nodejs.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.example.basicviewer.conn.RetrofitInterface.BASE_URL;
//import  com.example.basicviewer.conn.LoggedInUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LastDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LastDataFragment extends Fragment {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    DataAllSensor dataAllSensor;

    GraphView graphPPG,graphEKG, graphEMG, graphAcce, graphSuhu;
    private int lastX =0, packCount =0, indexData = 0;
    Integer[] dataPPG, dataEKG, dataEMG, dataAcceX, dataAcceY, dataAcceZ, dataSuhu;
    List<Integer[]> dataTrayPPG, dataTrayEKG, dataTrayEMG, dataTrayAcceX, dataTrayAcceY, dataTrayAcceZ, dataTraySuhu;

    private LineGraphSeries<DataPoint> seriesPPG, seriesEKG, seriesEMG, seriesAcceX, seriesAcceY, seriesAcceZ, seriesSuhu;

    String nameUser,idUser;
    int i;
    private static final String ARG_SECTION_NUMBER = "section_number";
    View root;

    //ini buat handle no record
    int currIndexRecord, maxIndexRecord;
    public LastDataFragment() {
        // Required empty public constructor
    }

    public static LastDataFragment newInstance(String param1, String param2) {
        LastDataFragment fragment = new LastDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_last, container, false);
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
        graphSuhu.getViewport().setMinY(20);

        setLoggedInUser();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        resetGraph();
        handleGetRekonstruksiDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton fab = root.findViewById(R.id.refreshButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGraph();
                handleGetRekonstruksiDialog();
            }
        });
        Button prevButtton = root.findViewById(R.id.prevButton);
        prevButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currIndexRecord!=1){
                    currIndexRecord--;
                    resetGraph();
                    handleGetSpecificRekonstruksiDialog();
                    setRecordNumber();
                }
                else{
                    Toast.makeText(getActivity(),"You Have Reached First Record",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button nextButtton = root.findViewById(R.id.nextButton);
        nextButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currIndexRecord!=maxIndexRecord){
                    currIndexRecord++;
                    resetGraph();
                    handleGetSpecificRekonstruksiDialog();
                    setRecordNumber();
                }
                else{
                    Toast.makeText(getActivity(),"You Have Reached Last Record",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void resetGraph(){
            //nanti jadi resetGraph();
            Log.i("reset graph", "run: restgraph");
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
    //get last
    private void handleGetRekonstruksiDialog() {

        //Call<Collection<DataAllSensor>> call = retrofitInterface.getDataAll(idUser);
        Call<Collection<DataAllSensor>> call = retrofitInterface.getLastRecord(idUser);

        call.enqueue(new Callback<Collection<DataAllSensor>>() {
            @Override
            public void onResponse(Call<Collection<DataAllSensor>> call, Response<Collection<DataAllSensor>> response) {
                response.code();
                Collection<DataAllSensor> result = response.body();

                try {
                    DataAllSensor[] dataAllSensorAll = result.toArray(new DataAllSensor[result.size()]);
                    maxIndexRecord = dataAllSensorAll[0].getIndex_Record();
                    currIndexRecord = maxIndexRecord;
                    setRecordNumber();
                    packCount = 0;
                    Log.i("Record plot", "record len: "+Integer.toString(dataAllSensorAll.length));
                    for (int k=0; k<dataAllSensorAll.length; k++){
                        dataAllSensor = dataAllSensorAll[k];
                        addDataTray(); /*Problem here?!!*/
                        Log.i("Record plot", "here: "+Integer.toString(k));
                        //add pack count
                        //plotData(k);
                        packCount++;
                        Log.i("Record plot", "here2: "+ dataAllSensor.get_id());
                    }
                    Log.i("Record plot", "pack count: "+ Integer.toString(packCount));
                    //startGraph();
                    //plotData();
                    plotDataTray();
                }catch (Exception e){
                    Toast.makeText(getActivity(),"No Data",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Collection<DataAllSensor>> call, Throwable t) {
                //Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),"There is a problem.\nPlease check your internet connection or try again later",Toast.LENGTH_LONG).show();

            }
        });
        //return dataAllSensor;
    }
    //get specific
    private void handleGetSpecificRekonstruksiDialog() {

        //Call<Collection<DataAllSensor>> call = retrofitInterface.getDataAll(idUser);
        Call<Collection<DataAllSensor>> call = retrofitInterface.getRecord(idUser, Integer.toString(currIndexRecord));

        call.enqueue(new Callback<Collection<DataAllSensor>>() {
            @Override
            public void onResponse(Call<Collection<DataAllSensor>> call, Response<Collection<DataAllSensor>> response) {
                response.code();
                Collection<DataAllSensor> result = response.body();

                try {
                    DataAllSensor[] dataAllSensorAll = result.toArray(new DataAllSensor[result.size()]);
                    packCount = 0;
                    Log.i("Record plot", "record len: "+Integer.toString(dataAllSensorAll.length));
                    for (int k=0; k<dataAllSensorAll.length; k++){
                        dataAllSensor = dataAllSensorAll[k];
                        addDataTray(); /*Problem here?!!*/
                        Log.i("Record plot", "here: "+Integer.toString(k));
                        //add pack count
                        //plotData(k);
                        packCount++;
                        Log.i("Record plot", "here2: "+ dataAllSensor.get_id());
                    }
                    Log.i("Record plot", "pack count: "+ Integer.toString(packCount));
                    //startGraph();
                    //plotData();
                    plotDataTray();
                }catch (Exception e){
                    Toast.makeText(getActivity(),"No Data",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Collection<DataAllSensor>> call, Throwable t) {
                //Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),"There is a problem.\nPlease check your internet connection or try again later",Toast.LENGTH_LONG).show();

            }
        });
        //return dataAllSensor;
    }

    //set reset graph dari live
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
    //new plot record
    //add data
    public void addDataTray(){
        Log.i("record", "addDataTray: ");
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

    public void plotDataTray(){
        Log.i("Record plot", " data tray plot start");
        lastX = 0;
        for (int indexPack =0; indexPack<packCount; indexPack++) {
            Log.i("Record plot", " data tray plot masuk indexPack " + Integer.toString(indexPack));
            Integer[] dataPlotPPG = dataTrayPPG.get(indexPack);
            Integer[] dataPlotEKG = dataTrayEKG.get(indexPack);
            Integer[] dataPlotEMG = dataTrayEMG.get(indexPack);
            Integer[] dataPlotAcceX = dataTrayAcceX.get(indexPack);
            Integer[] dataPlotAcceY = dataTrayAcceY.get(indexPack);
            Integer[] dataPlotAcceZ = dataTrayAcceZ.get(indexPack);
            Integer[] dataPlotSuhu = dataTraySuhu.get(indexPack);
            indexData = 0;
            Log.i("record panjang Data", Arrays.toString(dataPlotPPG));
            for (int j = 0; j < 256; j++) {
                Log.i("record index J", Integer.toString(indexData));
                Log.i("record indexData", Integer.toString(indexData));
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
        }
    }

    private void addEntry(Integer[] data, LineGraphSeries<DataPoint> series) {
        double x;
        x = (double) lastX*0.04;
        try{
            series.appendData(new DataPoint(x,(double) data[indexData]), true, 10000);
        }catch (Exception e){
            Log.i("exception", "addEntry: oops");
        }
    }
    //User
    public void setLoggedInUser(){
        SharedPreferences sp1 = getActivity().getSharedPreferences("LoggedInUser", MODE_PRIVATE);
        nameUser = sp1.getString("id_pasien", null);
        idUser = sp1.getString("id_pasien", null);
        Log.i("NAME USER IMAGE", nameUser);
        Log.i("ID USER IMAGE", idUser);
    }
    private void setRecordNumber(){
        TextView currIndex = (TextView) root.findViewById(R.id.currRecordNumber);
        TextView totalIndex = (TextView) root.findViewById(R.id.totalRecordNumber);
        currIndex.setText(Integer.toString(currIndexRecord));
        totalIndex.setText("/"+Integer.toString(maxIndexRecord));
    }
}