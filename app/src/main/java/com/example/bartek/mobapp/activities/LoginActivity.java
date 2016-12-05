package com.example.bartek.mobapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.bartek.mobapp.LocalDatabaseHelper;
import com.example.bartek.mobapp.R;
import com.example.bartek.mobapp.User;
import com.example.bartek.mobapp.requests.ConnectionRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    LocalDatabaseHelper myDb;
    ConnectionRequest connectionRequest;

    private EditText etUsername;
    private EditText etPassword;
    private Button bLogin;
    private TextView tvRegisterLink;
    private CheckBox cbOnlineLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
            if (extras.containsKey("info"))
                Toast.makeText(this, intent.getStringExtra("info"), Toast.LENGTH_LONG).show();


        myDb = new LocalDatabaseHelper(this);

        etUsername = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etConfirmPassword);
        tvRegisterLink = (TextView) findViewById(R.id.tvRegisterHere);
        bLogin = (Button) findViewById(R.id.bLogin);
        cbOnlineLogin = (CheckBox) findViewById(R.id.cbOnlineLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegister();
            }
        });
    }


    private void login()
    {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        if( myDb.checkLoginLocally( username, password ) ) {
            User.user = etUsername.getText().toString();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("info", "Zalogowano z użyciem wewnętrznej bazy");
            startActivity(intent);
            finish();
        }
        else if( cbOnlineLogin.isChecked() ){
            checkLoginOnline();
        }
        else
            loginFailed("Nie istniejesz w lokalnej bazie");

    }

    private void checkLoginOnline()
    {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("response LA = " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        String email = jsonResponse.getString("email");
                        myDb.addLocalUser(username, email, password);

                        User.user = etUsername.getText().toString();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("info", "Importowano dane użytkownika z serwera");
                        startActivity(intent);
                        finish();
                    } else {
                        loginFailed("Nie istniejesz w bazie serwera");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("nick", username);
        params.put("password", password);
        ConnectionRequest loginRequest = ConnectionRequest.connectionAction(ConnectionRequest.CONNECT_ACTION.LOGIN, params, responseListener);
//        ConnectionRequest registerRequest = new ConnectionRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }


    private void loginFailed(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void gotoRegister()
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
