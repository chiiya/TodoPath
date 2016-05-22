package me.ewitte.todopath.db;

/**
 * Created by vicakatherine on 5/4/16.
 */

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "me.ewitte.todopath.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "lists";
        public static final String COLUMN_NAME_TITLE = "title";
    }
}
