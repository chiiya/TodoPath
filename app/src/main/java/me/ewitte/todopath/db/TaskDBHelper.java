package me.ewitte.todopath.db;

/**
 * Created by vicakatherine on 5/4/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import me.ewitte.todopath.model.List;
import me.ewitte.todopath.model.Todo;

public class TaskDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "me.ewitte.todopath.db";
    private static final String TAG = TaskDBHelper.class.getSimpleName();
    private static final String LOG = "TaskDBHelper";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + List.TABLE + " ( " +
                        List.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        List.KEY_TITLE + " TEXT," +
                        List.KEY_CREATED_AT + " DATETIME" +
                        " );"
        );
        db.execSQL(
                "CREATE TABLE " + Todo.TABLE + " ( " +
                        Todo.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Todo.KEY_NAME + " TEXT," +
                        Todo.KEY_STATUS + " INTEGER," +
                        Todo.KEY_CREATED_AT + " DATETIME," +
                        Todo.KEY_LIST_ID + " INTEGER," +
                        Todo.KEY_PRIORITY + " INTEGER," +
                        "FOREIGN KEY(" + Todo.KEY_LIST_ID + ") REFERENCES " + List.TABLE + "(" + List.KEY_ID + ") ON DELETE CASCADE" +
                        " );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        db.execSQL("DROP TABLE IF EXISTS " + List.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Todo.TABLE);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        } else {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    /**
     * Inserts a new List of Todos into the database
     * @param list List Object to be inserted
     * @return The ID of the inserted row
     */
    public long createList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(List.KEY_TITLE, list.getTitle());
        values.put(List.KEY_CREATED_AT, getDateTime());

        long id = db.insert(List.TABLE, null, values);
        db.close();
        return id;
    }


    /**
     * Retrieves a single row from the lists table
     * @param list_id ID of the List to be selected
     * @return List Object
     */
    public List getList(long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + List.TABLE + " WHERE " + List.KEY_ID + " = " + list_id;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        List list = new List(c.getInt(c.getColumnIndex(List.KEY_ID)), c.getString(c.getColumnIndex(List.KEY_TITLE)),
                c.getString(c.getColumnIndex(List.KEY_CREATED_AT)));

        c.close();
        db.close();
        return list;
    }


    /**
     * Retrieves all lists from the database
     * @return ArrayList of all lists
     */
    public ArrayList<List> getAllLists() {
        ArrayList<List> lists = new ArrayList<List>();
        String selectQuery = "SELECT * FROM " + List.TABLE;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // Looping through all rows
        if (c.moveToFirst()) {
            do {
                List list = new List(c.getInt(c.getColumnIndex(List.KEY_ID)), c.getString(c.getColumnIndex(List.KEY_TITLE)),
                        c.getString(c.getColumnIndex(List.KEY_CREATED_AT)));

                lists.add(list);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return lists;
    }

    /**
     * Updates the database record of a single List object
     * @param list Object to be updated
     * @return Number of rows affected
     */
    public int updateList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(List.KEY_TITLE, list.getTitle());

        int rows = db.update(List.TABLE, values, List.KEY_ID + " = ?", new String[] { String.valueOf(list.getId()) });
        db.close();
        return rows;
    }


    /**
     * Deletes a single List object from the database
     * @param list Object to be deleted
     */
    public void deleteList(List list) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(List.TABLE, List.KEY_ID + " = ?", new String[] { String.valueOf(list.getId()) });
        db.close();
    }


    /**
     * Inserts a new Todo object into the database
     * @param todo Object to be inserted
     * @return ID of the inserted row
     */
    public long createTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Todo.KEY_NAME, todo.getName());
        values.put(Todo.KEY_STATUS, 0);
        values.put(Todo.KEY_LIST_ID, todo.getList_id());
        values.put(Todo.KEY_CREATED_AT, getDateTime());
        values.put(Todo.KEY_PRIORITY, todo.getPriority());

        long id = db.insert(Todo.TABLE, null, values);
        db.close();
        return id;
    }


    /**
     * Retrieves a single Todo Object from the database
     * @param todo_id ID of the row to be selected
     * @return Todo Object built from selcted row
     */
    public Todo getTodo(long todo_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + Todo.TABLE + " WHERE " + Todo.KEY_ID + " = " + todo_id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Todo todo = new Todo(c.getInt(c.getColumnIndex(Todo.KEY_ID)), c.getString(c.getColumnIndex(Todo.KEY_NAME)),
                c.getString(c.getColumnIndex(Todo.KEY_CREATED_AT)), c.getInt(c.getColumnIndex(Todo.KEY_STATUS)),
                c.getInt(c.getColumnIndex(Todo.KEY_LIST_ID)), c.getInt(c.getColumnIndex(Todo.KEY_PRIORITY)));

        c.close();
        db.close();
        return todo;
    }


    /**
     * Retrieves all Todos associated with a given list from the database
     * @return ArrayList of all Todos
     */
    public ArrayList<Todo> getAllTodosFromList(long list_id) {
        ArrayList<Todo> todos = new ArrayList<Todo>();
        String selectQuery = "SELECT * FROM " + Todo.TABLE + " WHERE " + Todo.KEY_LIST_ID + " = " + list_id;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // Looping through all rows
        if (c.moveToFirst()) {
            do {
                Todo todo = new Todo(c.getInt(c.getColumnIndex(Todo.KEY_ID)), c.getString(c.getColumnIndex(Todo.KEY_NAME)),
                        c.getString(c.getColumnIndex(Todo.KEY_CREATED_AT)), c.getInt(c.getColumnIndex(Todo.KEY_STATUS)),
                        c.getInt(c.getColumnIndex(Todo.KEY_LIST_ID)), c.getInt(c.getColumnIndex(Todo.KEY_PRIORITY)));
                todos.add(todo);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return todos;
    }


    /**
     * Updates the database record of a single Todo object
     * @param todo Object to be updated
     * @return Number of rows affected
     */
    public int updateTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Todo.KEY_NAME, todo.getName());
        values.put(Todo.KEY_STATUS, todo.getStatus());
        values.put(Todo.KEY_PRIORITY, todo.getPriority());

        int rows = db.update(Todo.TABLE, values, Todo.KEY_ID + " = ?", new String[] { String.valueOf(todo.getId()) });
        db.close();
        return rows;
    }

    /**
     * Deletes a single Todo object from the database
     * @param todo Object to be deleted
     */
    public void deleteTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Todo.TABLE, Todo.KEY_ID + " = ?", new String[] { String.valueOf(todo.getId()) });
        db.close();
    }


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
