package com.example.bartek.mobapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SynchroActivity extends AppCompatActivity
{
    LocalDatabaseHelper myDb;

    EditText etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchro);

        myDb = new LocalDatabaseHelper(this);

        Button backToMain = (Button) findViewById(R.id.bBackToMain);
        Button bSyncData = (Button) findViewById(R.id.bSyncData);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });
        bSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronise();
            }
        });
    }

    private void synchronise()
    {
        System.out.println("synchronise data");

        Cursor res = myDb.getCursor();

        Map<String, String> params = new HashMap<>();
        int i=0;

        params.put("nick", User.user);
        params.put("password", etConfirmPassword.getText().toString());

        while ( res.moveToNext() )
        {
            String wareString = res.getString(res.getColumnIndex(LocalDatabaseHelper.GROCHERY_WARE_COL_3));
            String storeString = res.getString(res.getColumnIndex(LocalDatabaseHelper.GROCHERY_STORE_COL_4));
            float priceFloat = res.getFloat(res.getColumnIndex(LocalDatabaseHelper.GROCHERY_PRICE_COL_5));
//            int amountInt = res.getInt(res.getColumnIndex(LocalDatabaseHelper.GROCHERY_AMOUNT_COL_6));
            int increaseInt = res.getInt(res.getColumnIndex(LocalDatabaseHelper.GROCHERY_INCREASE_COL_7));

            params.put(i++ + "", wareString);
            params.put(i++ + "", storeString);
            params.put(i++ + "", priceFloat + "");
//            params.put(i++ + "", amountInt + "");
            params.put(i++ + "", increaseInt + "");
        }
        res.close();

        System.out.println("params = " + params.toString());

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                System.out.println("edit respose = " + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success){
                        myDb.dataFromServer(jsonResponse);
                        goToMain("Udało się zsynchronizować dane");
                    } else
                        synchFailed("Nie udało się zsynchronizować danych");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        ConnectionRequest pullRequest = ConnectionRequest.connectionAction(ConnectionRequest.CONNECT_ACTION.PULL, params, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SynchroActivity.this);
        queue.add(pullRequest);
    }

    private void goToMain(String message){
        Intent intent = new Intent(SynchroActivity.this, MainActivity.class);
        intent.putExtra("info", message);
        startActivity(intent);
        finish();
    }

    private void backToMain() {
        Intent intent = new Intent(SynchroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void synchFailed(String message){
        Toast.makeText(SynchroActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
