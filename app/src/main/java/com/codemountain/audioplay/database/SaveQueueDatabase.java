package com.codemountain.audioplay.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codemountain.audioplay.interfaces.DefaultColumn;
import com.codemountain.audioplay.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SaveQueueDatabase extends SQLiteOpenHelper {

    private String tableName;
    private SQLiteDatabase sqLiteDatabase;

    public SaveQueueDatabase(Context context, String name) {
        super(context, name, null, Constants.DbVersion);
        this.tableName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.DefaultColumn(tableName));

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL("DROP TABLE IF NOT EXISTS " + tableName);
        onCreate(sqLiteDatabase);
    }

    public void addQueueName(String queueName) {
        sqLiteDatabase = getWritableDatabase();
        try {
            storeQueueName(sqLiteDatabase, queueName);
        } finally {
            sqLiteDatabase.close();
        }
    }

    private void storeQueueName(SQLiteDatabase sqLiteDatabase, String name) {
        ContentValues values = new ContentValues();
        values.put(DefaultColumn.QueueName, name);
        sqLiteDatabase.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void removeAll() {
        sqLiteDatabase = getReadableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(tableName, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public List<String> readAll() {
        List<String> QueueList = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(tableName, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int colRead = cursor.getColumnIndex(DefaultColumn.QueueName);
                do {
                    String gotName = cursor.getString(colRead);
                    QueueList.add(gotName);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } finally {
            sqLiteDatabase.close();
        }
        return QueueList;
    }

    public boolean isExist(String queueName) {
        boolean result = false;
        sqLiteDatabase = getReadableDatabase();
        try {
            Cursor cursor = sqLiteDatabase.query(getTableName(), null, DefaultColumn.QueueName + "= ?", new String[]{queueName}, null, null, null, "1");
            if (cursor != null && cursor.moveToNext()) {
                result = true;
            }
            if (cursor != null) {
                cursor.close();
            }
        } finally {
            sqLiteDatabase.close();
        }
        return result;
    }

    public void deleteQueueName(String queueName) {
        sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.delete(getTableName(), DefaultColumn.QueueName + "= ?", new String[]{queueName});
        } finally {
            sqLiteDatabase.close();
        }
    }

    public String getTableName() {
        return tableName;
    }
}
