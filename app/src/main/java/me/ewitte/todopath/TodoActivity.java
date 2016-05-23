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

import me.ewitte.todopath.db.TaskDBHelper;
import me.ewitte.todopath.model.List;
import me.ewitte.todopath.model.Todo;

public class TodoActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_TITLE = "me.ewitte.todopath.LISTTITLE";
    public static final String EXTRA_LIST_ID = "me.ewitte.todopath.LISTID";

    private TaskDBHelper db;
    private ListView tdvListView;
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

        tdvListView = (ListView) findViewById(R.id.tdv_list);
        registerForContextMenu(tdvListView);
        tdvListView.setVerticalScrollBarEnabled(false);
        tdvListView.setDivider(null);

        tdvListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Todo todo = todosAdapter.getItem(position);
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

        // Get the custom dialog view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.todo_dialog, null);
        final EditText todoEditText = (EditText)dialogView.findViewById(R.id.edit_todo);
        spinner = (Spinner)dialogView.findViewById(R.id.priority_spinner);

        // Populate Spinner with Options via Adapter
        String[] options = {"High Priority", "Medium Priority", "Low Priority"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TodoActivity.this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a new Todo")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(todoEditText.getText());
                        int priority = spinner.getSelectedItemPosition();
                        Todo todo = new Todo(name, listID, priority);
                        todo.setId(db.createTodo(todo));
                        todos.add(todo);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        updateUI();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.tdv_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        final Todo itemSelected = todosAdapter.getItem(info.position);
        switch(item.getItemId()) {
            case R.id.edit:
                // Get the custom dialog view
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.todo_dialog, null);
                final EditText todoEditText = (EditText)dialogView.findViewById(R.id.edit_todo);
                todoEditText.setText(itemSelected.getName());
                spinner = (Spinner)dialogView.findViewById(R.id.priority_spinner);

                // Populate Spinner with Options via Adapter
                String[] options = {"High Priority", "Medium Priority", "Low Priority"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(TodoActivity.this, android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(itemSelected.getPriority());

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Edit Todo")
                        .setView(dialogView)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = String.valueOf(todoEditText.getText());
                                int priority = spinner.getSelectedItemPosition();
                                itemSelected.setName(name);
                                itemSelected.setPriority(priority);
                                db.updateTodo(itemSelected);
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                updateUI();
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
            todosAdapter = new TodosAdapter(this,
                    R.layout.item_todo,
                    todos);
            tdvListView.setAdapter(todosAdapter);
        } else {
            todosAdapter.notifyDataSetChanged();
        }

    }

}
