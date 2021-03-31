package com.example.basicviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.example.basicviewer.conn.RetrofitInterface;
import com.example.login_nodejs.R;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import static com.example.basicviewer.conn.RetrofitInterface.BASE_URL;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    //private String BASE_URL = "https://bysonics-alpha001.herokuapp.com";

    //response register
    String message;
    boolean loginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                handleLoginDialog();
            }
        });

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                handleSignupDialog();
            }
        });

        findViewById(R.id.override).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                handleOverrideDialog();
            }
        });
    }

    private void handleLoginDialog() {
        View view = getLayoutInflater().inflate(R.layout.login_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        //builder.setView(view).setBackgroundColor(Color.TRANSPARENT).show();
        //view.setBackgroundColor(#00000000);
        //view.setBackgroundDrawable(null);
        builder.setView(view).show();

        Button loginBtn = view.findViewById(R.id.login);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();

                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<LoginResult> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        if (response.code() == 200){

                            LoginResult result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            setLoggedInUser(result.getName(), result.getId_pasien());
                            String message = "Welcome " + result.getName() +" !";
                            Toast.makeText(LoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();
                            loginSuccess = true;
                            moveToNewActivity();
                        }else if(response.code() == 400) {
                            Gson gson = new Gson();
                            RegisterStatus message =gson.fromJson(response.errorBody().charStream(),RegisterStatus.class);
                            //Log.i("", message.getMessage());
                            Toast.makeText(LoginActivity.this,
                                    message.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            loginSuccess = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void handleSignupDialog() {
        final View view = getLayoutInflater().inflate(R.layout.signup_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog signupDialog = builder.setView(view).show();

        Button signupBtn = view.findViewById(R.id.signup);
        final EditText nameEdit = view.findViewById(R.id.nameEdit);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();

                map.put("name", nameEdit.getText().toString());
                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<RegisterStatus> call = (Call<RegisterStatus>) retrofitInterface.executeSignup(map);

                call.enqueue(new Callback<RegisterStatus>() {
                    @Override
                    public void onResponse(Call<RegisterStatus> call, Response<RegisterStatus> response) {

                        //RegisterStatus body = response.peekBody(Long.MAX_VALUE);
                        if (response.code() == 200){
                            RegisterStatus result = response.body();
                            Toast.makeText(LoginActivity.this,
                                    result.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            signupDialog.dismiss();
                            handleLoginDialog();
                            //
                            //nanti auto nutup dan buka
                            //handleLoginDialog();
                        }else if(response.code() == 400){
                            Gson gson = new Gson();
                            RegisterStatus message =gson.fromJson(response.errorBody().charStream(),RegisterStatus.class);
                            //Log.i("", message.getMessage());
                            Toast.makeText(LoginActivity.this,
                                    message.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterStatus> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });

            }
        });
    }

    private void moveToNewActivity () {

        Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void handleOverrideDialog(){
        String message = "Welcome Admin BySonics!";
        setLoggedInUser("Admin BySonics", "60484c4924d3682334ca5c3c");
        Toast.makeText(LoginActivity.this,
                message,
                Toast.LENGTH_LONG).show();
        moveToNewActivity();
    }

    private void setLoggedInUser(String name, String id){
        SharedPreferences sp=getSharedPreferences("LoggedInUser", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("name",name );
        Ed.putString("id_pasien",id);
        Ed.commit();
    }
}