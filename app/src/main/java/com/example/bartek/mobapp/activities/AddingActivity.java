package com.example.bartek.mobapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bartek.mobapp.LocalDatabaseHelper;
import com.example.bartek.mobapp.R;

public class AddingActivity extends AppCompatActivity
{
    LocalDatabaseHelper myDb;

    Button bBackToMain;
    Button bCreateRow;
    EditText etCreateWare;
    EditText etCreateStore;
    EditText etCreatePrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        myDb = new LocalDatabaseHelper(this);

        bBackToMain = (Button) findViewById(R.id.bBackToMain);
        bCreateRow = (Button) findViewById(R.id.bCreateRow);

        etCreateWare = (EditText) findViewById(R.id.etCreateWare);
        etCreateStore = (EditText) findViewById(R.id.etCreateStore);
        etCreatePrice = (EditText) findViewById(R.id.etCreatePrice);

        
        bBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain(null, null);
            }
        });

        bCreateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWare();
            }
        });
    }

    private void addWare()
    {
        String wareName = etCreateWare.getText().toString();
        String wareStore = etCreateStore.getText().toString();
        String warePrice = etCreatePrice.getText().toString();

        if ( myDb.addData( wareName, wareStore, warePrice, 0 + "" ) )
            backToMain("info", "Dodano nową pozycję");

    }

    private void backToMain(String key, String message)
    {
        Intent intent = new Intent(AddingActivity.this, MainActivity.class);
        if(key != null && message != null)
            intent.putExtra(key, message);
        startActivity(intent);
        finish();
    }
}
