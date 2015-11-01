package com.shmuelrosansky.contentprovidertutorial.models;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.shmuelrosansky.contentprovidertutorial.dataUtils.SqlHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by User on 10/24/2015.
 */
public class TodoItem implements Comparable<TodoItem>,Parcelable {

    private int id = -1;
    private String text;
    private boolean completed = false;
    private long createdTimestamp;

    private TodoItem(){

    }

    public TodoItem(String text, long createdTimestamp){
        this.text = text;
        this.createdTimestamp = createdTimestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public int getId(){
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof TodoItem){
            return ((TodoItem) o).id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        if(id != -1) contentValues.put(SqlHelper.COLUMN_ID, id);
        contentValues.put(SqlHelper.COLUMN_TIMESTAMP, createdTimestamp);
        contentValues.put(SqlHelper.COLUMN_TEXT, text);
        contentValues.put(SqlHelper.COLUMN_COMPLETED, completed ? 1 : 0);
        return contentValues;
    }

    public static TodoItem buildFromCursor(Cursor cursor){
        int idColumn = cursor.getColumnIndex(SqlHelper.COLUMN_ID);
        int timestampColumn = cursor.getColumnIndex(SqlHelper.COLUMN_TIMESTAMP);
        int textColumn = cursor.getColumnIndex(SqlHelper.COLUMN_TEXT);
        int completedColumn = cursor.getColumnIndex(SqlHelper.COLUMN_COMPLETED);

        if(idColumn == -1 || timestampColumn == -1 || textColumn == -1 || completedColumn == -1){
            throw new RuntimeException("Database must have all the columns for the todo item!");
        }

        TodoItem todoItem = new TodoItem();
        todoItem.id = cursor.getInt(idColumn);
        todoItem.createdTimestamp = cursor.getLong(timestampColumn);
        todoItem.text = cursor.getString(textColumn);
        todoItem.completed = cursor.getInt(completedColumn) == 1;

        return todoItem;
    }

    @Override
    public int compareTo(TodoItem another) {
        if(this.getId() > another.getId()){
            return 1;
        }else if(this.getId() < another.getId()){
            return -1;
        }else{
            return 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeByte(completed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdTimestamp);

    }

    protected TodoItem(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.completed = in.readByte() != 0;
        this.createdTimestamp = in.readLong();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
