package com.example.bartek.mobapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "grocheryHelper.db";

    private static final String TABLE_GROCHERY = "GROCHERY";
    static final String GROCHERY_ID_COL_1 = "ID";
    static final String GROCHERY_NICK_COL_2 = "NICK";
    public static final String GROCHERY_WARE_COL_3 = "WARE";
    public static final String GROCHERY_STORE_COL_4 = "STORE";
    public static final String GROCHERY_PRICE_COL_5 = "PRICE";
    public static final String GROCHERY_AMOUNT_COL_6 = "AMOUNT";
    public static final String GROCHERY_INCREASE_COL_7 = "INCREASE";

    private static final String TABLE_CLIENT = "CLIENT";
    private static final String CLIENT_ID_COL_1 = "ID";
    private static final String CLIENT_NICK_COL_2 = "NICK";
    private static final String CLIENT_EMAIL_COL_3 = "EMAIL";
    private static final String CLIENT_PASSWORD_COL_4 = "PASSWORD";

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_CLIENT +" (" +
                CLIENT_ID_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CLIENT_NICK_COL_2 + " TEXT, " +
                CLIENT_EMAIL_COL_3 + " SURNAME TEXT, " +
                CLIENT_PASSWORD_COL_4 + " INTEGER)");
        db.execSQL("create table " + TABLE_GROCHERY +" (" +
                GROCHERY_ID_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GROCHERY_NICK_COL_2 + " TEXT, " +
                GROCHERY_WARE_COL_3 + " TEXT, " +
                GROCHERY_STORE_COL_4 + " TEXT, " +
                GROCHERY_PRICE_COL_5 + " REAL, " +
                GROCHERY_AMOUNT_COL_6 + " INTEGER, " +
                GROCHERY_INCREASE_COL_7 + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
//        onCreate(db);
    }

    public void dataFromServer(JSONObject jsonResponse) throws JSONException
    {
        deleteAllUserData();
        JSONArray rows = jsonResponse.names();

        for(int i=rows.length()-1; i>=0; --i)
        {
            if(!rows.getString(i).equals("success")){
                JSONObject jo = jsonResponse.getJSONObject(rows.getString(i));
                String ware = jo.getString("ware");
                String store = jo.getString("store");
                float price = (float) jo.getDouble("price");
                int amount = jo.getInt("amount");
                addData(ware, store, price+"", amount+"");
            }
        }
    }

    public boolean checkLoginLocally(String nick, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        String dbPassword;
        try {
            res = db.rawQuery("select * from " + TABLE_CLIENT + " WHERE " +
                    CLIENT_NICK_COL_2 + " = ?", new String[]{nick});
            if(res.getCount() < 1)
                return false;
            res.moveToFirst();
            dbPassword = res.getString(  res.getColumnIndex(CLIENT_PASSWORD_COL_4) );
        }catch (SQLException err){
            System.err.println(err.getMessage());
            return false;
        }
        res.close();
        db.close();

        try {
            String hashedPassword = AeSimpleSHA1.SHA1((password));
            System.out.println("dbPassword = " + dbPassword + ", hashedPassword = " + hashedPassword);
            return ( dbPassword.equals(hashedPassword) );
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.err.println("brak w bazie");
        return false;
    }

    public boolean addData(String ware, String store, String price, String amount)
    {
        boolean done = true;

        String SQL = "INSERT INTO " + TABLE_GROCHERY + " (" +
                GROCHERY_NICK_COL_2 + ", " +
                GROCHERY_WARE_COL_3 + ", " +
                GROCHERY_STORE_COL_4 + ", " +
                GROCHERY_PRICE_COL_5 + ", " +
                GROCHERY_AMOUNT_COL_6 + ") " +
                "VALUES ('" + User.user + "', '" + ware + "', '" + store + "', '" + price + "', '" + amount + "')";
        System.out.println("SQL = " + SQL);

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL(SQL);
        } catch (SQLException err) {
            System.err.println(err.getMessage());
            done = false;
        }
        return done;
    }

    public boolean unitEditData(String ware, int increase){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(GROCHERY_INCREASE_COL_7, increase);

        int count = db.update(TABLE_GROCHERY, contentValues, GROCHERY_NICK_COL_2+" = ? AND " + GROCHERY_WARE_COL_3+" = ? ",
                new String[] { User.user, ware });

        return count > 0;
    }

    public boolean updateData(String ware, String wareNewName, String store, String price, String amount, String increase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(GROCHERY_NICK_COL_2, User.user);
        contentValues.put(GROCHERY_WARE_COL_3, wareNewName);
        contentValues.put(GROCHERY_STORE_COL_4, store);
        contentValues.put(GROCHERY_PRICE_COL_5, price);
        contentValues.put(GROCHERY_AMOUNT_COL_6, amount);
        contentValues.put(GROCHERY_INCREASE_COL_7, increase);
        System.out.println("ware = " + ware + "\ndata -> " + User.user + " " + wareNewName + " " + store + " " + price + " " + amount + " " + increase);
        int count = db.update(TABLE_GROCHERY, contentValues, GROCHERY_NICK_COL_2+" = ? AND " + GROCHERY_WARE_COL_3+" = ? ",
                new String[] { User.user, ware });

        return count > 0;
    }

    public boolean deleteData (String ware) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_GROCHERY, GROCHERY_NICK_COL_2+" = ? AND " + GROCHERY_WARE_COL_3+" = ? ", new String[] {User.user, ware});
        System.err.println("teoretycznie po deletecie, count = " + count + ", nick = " + User.user + ", ware = " + ware);

        return (count > 0);
    }

    private boolean deleteAllUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_GROCHERY, GROCHERY_NICK_COL_2+" = ?", new String[] {User.user});

        return (count > 0);
    }

    public Cursor getCursor() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GROCHERY +
                " WHERE " + GROCHERY_NICK_COL_2 + " = '" + User.user +"'", null);
    }

    public void addLocalUser(String nick, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String hashedPassword = AeSimpleSHA1.SHA1(password);
            String SQL = "INSERT INTO " + TABLE_CLIENT + " (" +
                CLIENT_NICK_COL_2 + ", " +
                CLIENT_EMAIL_COL_3 + ", " +
                CLIENT_PASSWORD_COL_4 + ") " +
                "VALUES ('" + nick + "', '" + email + "', '" + hashedPassword + "')";

            db.execSQL(SQL);
        }catch (SQLException | NoSuchAlgorithmException | UnsupportedEncodingException err){
            System.err.println(err.getMessage());
        }
    }
}