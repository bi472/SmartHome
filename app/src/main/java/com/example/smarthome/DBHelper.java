package com.example.smarthome;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    final String LOG_TAG = "myLogs";

    private final Context fContext;


    private static String DB_PATH; // полный путь к базе данных
    private static final int SCHEMA = 1; // версия базы данных


    private static final String DATABASE_NAME = "smart_home.db";
    public static final String TABLE_NAME_SWITCHES = "switches";
    public static final String TABLE_NAME_USERS = "users";
    public static final String TABLE_NAME_CAMERA = "camera";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        fContext = context;
        DB_PATH =context.getFilesDir().getPath() + DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w("TestBase", "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SWITCHES);
        onCreate(db);
    }

    void create_db(){

        File file = new File(DB_PATH);
        if (!file.exists()) {
            //получаем локальную бд как поток
            try(InputStream myInput = fContext.getAssets().open(DATABASE_NAME);
                // Открываем пустую бд
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
