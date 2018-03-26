package edu.sjsu.anis.alphafitness.DataBase;

import android.provider.BaseColumns;

/**
 * Created by anisdhapa on 3/25/18.
 */

public final class RecordContract {


    public static final class Contracts implements BaseColumns
    {

        public static final String _ID = BaseColumns._ID;
        public static final String KEY_NAME = "name";
        public static final String KEY_GENDER = "gender";
        public static final String KEY_WEIGHT = "weight";
        public static final String KEY_DISTANCE = "distance";
        public static final String KEY_TIME = "time";
        public static final String KEY_NUM_OF_WORKOUTS = "workouts";
        public static final String KEY_CALORIES_BURNED = "calories_burned";

        public static final String TABLE_AVERAGE_WEEKLY = "Average_Weekly";
        public static final String TABLE_ALL_TIME = "All_Time";


        public static final String KEY_STEPS = "steps";
    }

}
