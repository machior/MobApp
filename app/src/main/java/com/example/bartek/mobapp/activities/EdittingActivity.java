package com.example.bartek.mobapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartek.mobapp.LocalDatabaseHelper;
import com.example.bartek.mobapp.R;

public class EdittingActivity extends AppCompatActivity
{
    LocalDatabaseHelper myDb;

    Button bBackToMain;
    Button bCreateRow;
    Button bDelete;
    Button bAddUnit;
    Button bSubUnit;
    TextView etNameCreate;
    EditText etStoreNameCreate;
    EditText etPriceCreate;
    TextView etAmountCreate;
    TextView tvUnitIncr;
    private String wareName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editting);

        myDb = new LocalDatabaseHelper(this);

        bBackToMain = (Button) findViewById(R.id.bBackToMain);
        bDelete = (Button) findViewById(R.id.bDelete);
        bCreateRow = (Button) findViewById(R.id.bSubmitChanges);
        bAddUnit = (Button) findViewById(R.id.bAddUnit);
        bSubUnit = (Button) findViewById(R.id.bSubUnit);

        etNameCreate = (TextView) findViewById(R.id.etNameCreate);
        etStoreNameCreate = (EditText) findViewById(R.id.etStoreNameCreate);
        etPriceCreate = (EditText) findViewById(R.id.etPriceCreate);
        etAmountCreate = (TextView) findViewById(R.id.tvAmountCreate);
        tvUnitIncr = (TextView) findViewById(R.id.tvUnitIncr);

        fillGaps();

        bBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain(null, null);
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWare();
            }
        });
        bCreateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEdition();
            }
        });
        bAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubUnit(true);
            }
        });
        bSubUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubUnit(false);
            }
        });
    }

    private void addSubUnit(boolean oneMore){
        String amtStr = tvUnitIncr.getText().toString();
        int amt = Integer.parseInt(amtStr.substring(amtStr.indexOf("+") + 1));

        if(oneMore) amt += 1;
        else        amt -= 1;

        if(amt >= 0) tvUnitIncr.setText("+" + amt);
        else tvUnitIncr.setText("" + amt);
    }


    private void submitEdition()
    {
        if ( myDb.updateData(wareName,
                etNameCreate.getText().toString(),
                etStoreNameCreate.getText().toString(),
                etPriceCreate.getText().toString(),
                etAmountCreate.getText().toString(),
                getIncrease()) )
            backToMain("info", "Edytowano pozycję" + wareName);
        else
            Toast.makeText(EdittingActivity.this, "Nie udało się usunąć pozycji", Toast.LENGTH_SHORT).show();
    }

    private void deleteWare()
    {
        System.out.println("wareName = " + wareName);
        if ( myDb.deleteData(wareName) )
            backToMain("info", "Usunięto pozycję " + wareName);
        else
            Toast.makeText(EdittingActivity.this, "Nie udało się usunąć pozycji", Toast.LENGTH_SHORT).show();
    }

    private void backToMain(String key, String message)
    {
        Intent intent = new Intent(EdittingActivity.this, MainActivity.class);
        if(key != null && message != null)
            intent.putExtra(key, message);
        startActivity(intent);
        finish();
    }

    private String getIncrease(){
        Intent intent = getIntent();
        String ware = intent.getStringExtra("ware");

        int increase = 0;
        Cursor cursor = myDb.getCursor();
        while( cursor.moveToNext() )
            if( cursor.getString( cursor.getColumnIndex(myDb.GROCHERY_WARE_COL_3) ).equals(ware) ){
                increase = cursor.getInt( cursor.getColumnIndex(myDb.GROCHERY_INCREASE_COL_7) );
                break;
            }

        String amtStr = tvUnitIncr.getText().toString();
        int amt = Integer.parseInt(amtStr.substring(amtStr.indexOf("+") + 1));
        return Integer.toString( amt + increase );
    }

    private void fillGaps()
    {
        Intent intent = getIntent();
        String ware = intent.getStringExtra("ware");
        String store = intent.getStringExtra("store");
        float price = intent.getFloatExtra("price", -1);
        String amount = intent.getStringExtra("amount");

        etNameCreate.setText(ware);
        etStoreNameCreate.setText(store);
        etPriceCreate.setText(price + "");
        etAmountCreate.setText(amount);
        tvUnitIncr.setText("0");

        wareName = ware;
    }
}
