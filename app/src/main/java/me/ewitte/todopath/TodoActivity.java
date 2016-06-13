package me.ewitte.todopath;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.ewitte.todopath.db.TaskDBHelper;
import me.ewitte.todopath.model.List;
import me.ewitte.todopath.model.Todo;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TodoActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_TITLE = "me.ewitte.todopath.LISTTITLE";
    public static final String EXTRA_LIST_ID = "me.ewitte.todopath.LISTID";
    static final int EDIT_TODO = 0;
    static final int CREATE_TODO = 1;

    private TaskDBHelper db;
    private StickyListHeadersListView tdvListView;
    private TodosAdapter todosAdapter;
    private ArrayList<Todo> todos;
    private Spinner spinner;
    private long listID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new TaskDBHelper(this);

        // Get the extra data from intent call
        Intent intent = getIntent();
        String listTitle = intent.getStringExtra(EXTRA_LIST_TITLE);
        listID = intent.getLongExtra(EXTRA_LIST_ID, 0);

        // Set title of TextView to the list currently active
        TextView tdv_title = (TextView) findViewById(R.id.tdv_list_title);
        tdv_title.setText(listTitle);

        todos = db.getAllTodosFromList(listID);

        tdvListView = (StickyListHeadersListView) findViewById(R.id.tdv_list);
        registerForContextMenu(tdvListView);
        tdvListView.setVerticalScrollBarEnabled(false);
        tdvListView.setDivider(null);

        tdvListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Todo todo = (Todo) todosAdapter.getItem(position);
                if (todo.getStatus() == 0) {
                    todo.setStatus(1);
                } else {
                    todo.setStatus(0);
                }
                db.updateTodo(todo);
                updateUI();
            }
        });

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickButtonNewTodo(View view) {
        Intent intent = new Intent(TodoActivity.this, EditTodoActivity.class);
        startActivityForResult(intent, CREATE_TODO);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((View)v.getParent()).getId()==R.id.tdv_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        final Todo itemSelected = (Todo) todosAdapter.getItem(position);
        switch(item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(TodoActivity.this, EditTodoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Todo.TAG, itemSelected);
                bundle.putInt(EditTodoActivity.EXTRA_TODO_ID, position);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_TODO);
                return true;
            case R.id.delete:
                db.deleteTodo(itemSelected);
                todos.remove(itemSelected);
                updateUI();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateUI() {
        if (todosAdapter == null) {
            todosAdapter = new TodosAdapter(this, todos);
            tdvListView.setAdapter(todosAdapter);
        } else {
            Collections.sort(todos, new Comparator<Todo>() {
                @Override
                public int compare(Todo t1, Todo t2) {
                    return t1.getPriority() - t2.getPriority();
                }
            });
            todosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case EDIT_TODO:
                    Bundle extras = data.getExtras();

                    if (extras != null) {
                        Todo todo = extras.getParcelable(Todo.TAG);
                        int todoID = extras.getInt(EditTodoActivity.EXTRA_TODO_ID);
                        todos.set(todoID, todo);
                        db.updateTodo(todo);
                        updateUI();
                    }
                    break;

                case CREATE_TODO:
                    Bundle extras2 = data.getExtras();

                    if (extras2 != null) {
                        Todo todo = extras2.getParcelable(Todo.TAG);
                        todo.setList_id(listID);
                        todo.setId(db.createTodo(todo));
                        todos.add(todo);
                        updateUI();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
