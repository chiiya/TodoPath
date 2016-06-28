package me.ewitte.todopath.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Allaire on 21.05.2016.
 */
public class Todo implements Parcelable{

    public static final String TAG = Todo.class.getSimpleName();
    public static final String TABLE = "todos";
    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_LIST_ID = "list_id";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_CONTACT_URI = "contact_uri";
    public static final String KEY_CONTACT_NAME = "contact_name";
    public static final String KEY_REMINDER = "reminder";


    public static final int PRIORITY_HIGH = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_LOW = 2;

    private long id;
    private String name;
    private String created_at;
    private int status;
    private long list_id;
    private int priority;
    private String contactUri;
    private String contactName;
    private String reminder;

    public Todo() {
    }

    public Todo(String name, long list_id, int priority) {
        this.name = name;
        this.list_id = list_id;
        this.priority = priority;
    }

    public Todo(String name, int status, long list_id, int priority) {
        this.name = name;
        this.status = status;
        this.list_id = list_id;
        this.priority = priority;
    }

    public Todo(long id, String name, String created_at, int status, long list_id, int priority, String contactUri, String
                contactName) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.status = status;
        this.list_id = list_id;
        this.priority = priority;
        this.contactUri = contactUri;
        this.contactName = contactName;
        this.reminder = reminder;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getList_id() {
        return list_id;
    }

    public void setList_id(long list_id) {
        this.list_id = list_id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getContactUri() {
        return contactUri;
    }

    public void setContactUri(String contactUri) {
        this.contactUri = contactUri;
    }

    public String getContactName() { return contactName; }

    public void setContactName(String contactName) { this.contactName = contactName; }

    public String reminder() {
        return this.reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    protected Todo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        created_at = in.readString();
        status = in.readInt();
        list_id = in.readLong();
        priority = in.readInt();
        contactUri = in.readString();
        contactName = in.readString();
        reminder = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(created_at);
        dest.writeInt(status);
        dest.writeLong(list_id);
        dest.writeInt(priority);
        dest.writeString(contactUri);
        dest.writeString(contactName);
        dest.writeString(reminder);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };


    public String getReminder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}


