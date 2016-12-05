package com.example.bartek.mobapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.bartek.mobapp.LocalDatabaseHelper;
import com.example.bartek.mobapp.R;
import com.example.bartek.mobapp.requests.ConnectionRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    LocalDatabaseHelper myDb;

    private EditText etNick;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordRe;
    private Button bBackToLogin;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myDb = new LocalDatabaseHelper(this);

        etNick = (EditText) findViewById(R.id.etNick);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etPasswordRe = (EditText) findViewById(R.id.etPasswordRe);
        bBackToLogin = (Button) findViewById(R.id.bBackToLogin);
        bRegister = (Button) findViewById(R.id.bRegister);

        backToLogin();
        register();
    }

    private void register()
    {
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_LONG)
                if(!isOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Brak połączenia z internetem")
                            .setNegativeButton("Powrót", null)
                            .create()
                            .show();
                    return;
                }

                final String nick = etNick.getText().toString();
                final String email = etEmail.getText().toString();
                System.out.println("onClickListener username = " + email);
                final String password = etPassword.getText().toString();
                final String passwordRe = etPasswordRe.getText().toString();

                if(password.equals(passwordRe)) {

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println("response RA = " + response);
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    System.out.println("sukces, wchodzimy do bazy danych");
                                    myDb.addLocalUser(nick, email, password);
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    intent.putExtra("info", "Udało się zarejestrować użytkownika");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("Register Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Map<String, String> params = new HashMap<>();
                    params.put("nick", nick);
                    params.put("email", email);
                    params.put("password", password);
                    ConnectionRequest registerRequest = ConnectionRequest.connectionAction(ConnectionRequest.CONNECT_ACTION.REGISTRATION, params, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Podane hasła nie są identyczne")
                            .setNegativeButton("Powrót", null)
                            .create()
                            .show();
                }
            }
        });
    }

    private void backToLogin() {
        bBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                return;
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
