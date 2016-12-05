package com.example.bartek.mobapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartek.mobapp.LocalDatabaseHelper;
import com.example.bartek.mobapp.R;
import com.example.bartek.mobapp.User;

public class MainActivity extends AppCompatActivity
{

    Button bSynchro;
    Button bLogout;
    TableLayout tTable;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null)
            if (extras.containsKey("info"))
                Toast.makeText(this, intent.getStringExtra("info"), Toast.LENGTH_LONG).show();

        bSynchro = (Button) findViewById(R.id.bSynch);
        bLogout = (Button) findViewById(R.id.bBackToMain);
        tTable = (TableLayout) findViewById(R.id.table);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(AddingActivity.class);
            }
        });
        bSynchro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToActivity(SynchroActivity.class);
            }
        });
        viewAllData();
    }

    private void goToActivity(Class activity)
    {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
        finish();
    }

    private void viewAllData()
    {
        LocalDatabaseHelper myDb = new LocalDatabaseHelper(this);
        Cursor res = myDb.getCursor();

        while (res.moveToNext())
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            String wareString = res.getString(res.getColumnIndex(myDb.GROCHERY_WARE_COL_3));
            String storeString = res.getString(res.getColumnIndex(myDb.GROCHERY_STORE_COL_4));
            float priceFloat = res.getFloat(res.getColumnIndex(myDb.GROCHERY_PRICE_COL_5));
            int amountInt = res.getInt(res.getColumnIndex(myDb.GROCHERY_AMOUNT_COL_6));
            int increase = res.getInt(res.getColumnIndex(myDb.GROCHERY_INCREASE_COL_7));

            TextView ware = createTextView( wareString );
            TextView store = createTextView( storeString );
            TextView price = createTextView( priceFloat + "" );
            String increaseString;
            if(increase >= 0)
                increaseString = "+" + increase;
            else
                increaseString = "" + increase;
            TextView amount = createTextView(amountInt + " ( " + increaseString + " )");

            tr.addView(ware);
            tr.addView(store);
            tr.addView(price);
            tr.addView(amount);

            tr.setOnClickListener(new EditButtonHandler(wareString, storeString, priceFloat, amount.getText().toString()));

            tTable.addView(tr);
        }
        res.close();
        myDb.close();
    }

    private TextView createTextView(String name)
    {
        System.out.println("////////////////// name = " + name);
        TextView amountView = new TextView(this);
        amountView.setText(name);
        amountView.setTextSize(20);
        amountView.setTextColor(getResources().getColor(R.color.regularText));
        amountView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        amountView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return amountView;
    }

    public void logout(){
        User.user = "";
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("info", "Zostałeś wylogowany");
        startActivity(intent);
        finish();
    }

    private class EditButtonHandler implements View.OnClickListener
    {
        String ware;
        String store;
        float price;
        String amount;

        EditButtonHandler(String ware, String store, float price, String amount){
            this.ware = ware;
            this.store = store;
            this.price = price;
            this.amount = amount;
        }

        @Override
        public void onClick(View view) {
            System.out.println("kliknąłeś" + ware + ", " + store + ", " + price + ", " + amount);

            Intent intent = new Intent(MainActivity.this, EdittingActivity.class);
            intent.putExtra("ware", ware);
            intent.putExtra("store", store);
            intent.putExtra("price", price);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        }
    }
}
