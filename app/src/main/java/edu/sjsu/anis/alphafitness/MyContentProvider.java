package edu.sjsu.anis.alphafitness;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import edu.sjsu.anis.alphafitness.DataBase.MyDataBase;
import edu.sjsu.anis.alphafitness.DataBase.RecordContract;

/**
 * Created by anisdhapa on 3/28/18.
 */

public class MyContentProvider extends ContentProvider {

    private MyDataBase dataBase;

    SQLiteDatabase db;

    private static final int USERS = 100;
    private static final int USER_ID = 101;


    private static final String AUTHORITY = "com.example.anis.alphafitness";
    private static final String BASE_PATH = "users";



    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, USERS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", USER_ID);
    }


    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/users";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/user";

    @Override
    public boolean onCreate() {
        dataBase = new MyDataBase(getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        queryBuilder.setTables(RecordContract.Contracts.TABLE_AVERAGE_WEEKLY);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case USERS:
                break;
            case USER_ID:
                queryBuilder.appendWhere(RecordContract.Contracts._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

       db = dataBase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, strings, s,
                strings1, null, null, s1);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {


        int type = uriMatcher.match(uri);
        db = dataBase.getWritableDatabase();

        long id ;
        switch (type) {
            case USERS:
                id = db.insert(RecordContract.Contracts.TABLE_AVERAGE_WEEKLY, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int type = uriMatcher.match(uri);
        db = dataBase.getWritableDatabase();
        int id ;
        switch (type) {
            case USERS:
                id = db.update(RecordContract.Contracts.TABLE_AVERAGE_WEEKLY,
                        contentValues,
                        s,
                        strings);
                break;
            case USER_ID:
                String lastPathSegment = uri.getLastPathSegment();
                if (TextUtils.isEmpty(s)) {
                    id = db.update(RecordContract.Contracts.TABLE_AVERAGE_WEEKLY,
                            contentValues,
                            RecordContract.Contracts._ID + "=" + lastPathSegment,
                            null);
                } else {
                    id = db.update(RecordContract.Contracts.TABLE_AVERAGE_WEEKLY,
                            contentValues,
                            RecordContract.Contracts._ID + "=" + lastPathSegment
                                    + " and "
                                    + s,
                                strings);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
//        getContext().getContentResolver().notifyChange(uri, null);
        return id;

    }
}
