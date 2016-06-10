package me.ewitte.todopath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import me.ewitte.todopath.db.TaskDBHelper;
import me.ewitte.todopath.model.Todo;

public class EditTodoActivity extends AppCompatActivity {

    public static final String EXTRA_TODO_TEXT = "me.ewitte.todopath.TODOTEXT";
    public static final String EXTRA_TODO_PRIORITY = "me.ewitte.todopath.TODOPRIORITY";
    public static final String EXTRA_TODO_ID = "me.ewitte.todopath.TODOID";

    private int todoID;
    private Spinner spinner;
    private EditText todoEditText;
    private Todo todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        todoEditText = (EditText) findViewById(R.id.et_todo);
        TextView todoTextView = (TextView) findViewById(R.id.tv_todo);

        // Initialize and populate Spinner
        spinner = (Spinner) findViewById(R.id.priority_spinner);
        String[] options = {"High Priority", "Medium Priority", "Low Priority"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTodoActivity.this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if ( extras != null ) { // We are passing an existing Object
            todoTextView.setText(R.string.editTodo);
            todo = extras.getParcelable(Todo.TAG);
            todoID = extras.getInt(EXTRA_TODO_ID);
            todoEditText.setText(todo.getName());
            spinner.setSelection(todo.getPriority());
        } else {
            spinner.setSelection(1);
            todoTextView.setText(R.string.createTodo);
            todo = new Todo();
        }
    }

    public void saveTodo(View view){
        todo.setName(String.valueOf(todoEditText.getText()));
        todo.setPriority(spinner.getSelectedItemPosition());

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Todo.TAG, todo);

        if (todoID != 0) {
            bundle.putInt(EXTRA_TODO_ID, todoID);
        }

        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }
}
