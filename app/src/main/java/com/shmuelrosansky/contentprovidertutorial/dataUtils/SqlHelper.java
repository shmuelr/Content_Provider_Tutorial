package com.shmuelrosansky.contentprovidertutorial.dataUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 10/24/2015.
 */
public class SqlHelper extends SQLiteOpenHelper{

    private static final String TAG = SqlHelper.class.getSimpleName();

    public static final String TABLE_TODO_ITEMS = "todo_items";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_COMPLETED = "completed";

    private static final String DATABASE_NAME = "todo_items.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "CREATE TABLE "+TABLE_TODO_ITEMS
                    +" ("+COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +COLUMN_TIMESTAMP+" INTEGER NOT NULL, "
                    +COLUMN_TEXT+" TEXT NOT NULL, "
                    +COLUMN_COMPLETED+" INTEGER" +
                    ");";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB created");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "DB upgrading. Dropping old table...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_ITEMS);
        onCreate(db);
    }
}
