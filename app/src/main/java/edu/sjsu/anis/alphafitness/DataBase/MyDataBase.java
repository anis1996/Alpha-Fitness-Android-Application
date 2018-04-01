package edu.sjsu.anis.alphafitness.DataBase;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static edu.sjsu.anis.alphafitness.DataBase.RecordContract.Contracts;

/**
 * Created by anisdhapa on 3/25/18.
 */

public class MyDataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AlphaFitness.db";
    public static final int DATABASE_VERSION = 1;

    public MyDataBase(Context context) {
      super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String CREATE_AVERAGE_WEEKLY_TABLE = "CREATE TABLE " + Contracts.TABLE_AVERAGE_WEEKLY + "("
                + Contracts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contracts.KEY_NAME + " TEXT,"
                + Contracts.KEY_GENDER + " TEXT,"
                + Contracts.KEY_WEIGHT + " INT,"
                + Contracts.KEY_DISTANCE + " FLOAT,"
                + Contracts.KEY_TIME + " TEXT,"
                + Contracts.KEY_NUM_OF_WORKOUTS + " INTEGER,"
                + Contracts.KEY_CALORIES_BURNED + " FLOAT"
                + ")";

        sqLiteDatabase.execSQL(CREATE_AVERAGE_WEEKLY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contracts.TABLE_AVERAGE_WEEKLY);

        onCreate(sqLiteDatabase);

    }
}
