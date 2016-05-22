package me.ewitte.todopath.model;

/**
 * Created by Allaire on 20.05.2016.
 */
public class List {

    public static final String TAG = List.class.getSimpleName();
    public static final String TABLE = "lists";
    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CREATED_AT = "created_at";

    private int id;
    private String title;
    private String created_at;

    public List() {
    }

    public List(String title) {
        this.title = title;
    }

    public List(String title, String created_at) {
        this.title = title;
        this.created_at = created_at;
    }

    public List(int id, String title, String created_at) {
        this.id = id;
        this.title = title;
        this.created_at = created_at;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

