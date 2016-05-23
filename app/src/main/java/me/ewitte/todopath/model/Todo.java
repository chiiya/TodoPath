package me.ewitte.todopath.model;

/**
 * Created by Allaire on 21.05.2016.
 */
public class Todo {

    public static final String TAG = List.class.getSimpleName();
    public static final String TABLE = "todos";
    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_LIST_ID = "list_id";
    public static final String KEY_PRIORITY = "priority";

    private long id;
    private String name;
    private String created_at;
    private int status;
    private long list_id;
    private int priority;

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

    public Todo(long id, String name, String created_at, int status, long list_id, int priority) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.status = status;
        this.list_id = list_id;
        this.priority = priority;
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

}
