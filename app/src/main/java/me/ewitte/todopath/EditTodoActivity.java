package me.ewitte.todopath;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.Contacts;
import me.ewitte.todopath.model.Todo;

public class EditTodoActivity extends AppCompatActivity {

    public static final String EXTRA_TODO_ID = "me.ewitte.todopath.TODOID";
    public static final int REQUEST_PICK_CONTACT = 1;
    public static final int PERMISSION_READ_CONTACTS = 10;

    private int todoID;
    private Spinner spinner;
    private EditText todoEditText;
    private Todo todo;
    private TextView contactName;
    private ImageButton contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        todoEditText = (EditText) findViewById(R.id.et_todo);
        TextView todoTextView = (TextView) findViewById(R.id.tv_todo);
        contactName = (TextView) findViewById(R.id.contactName);
        contactImage = (ImageButton) findViewById(R.id.buttonAddContact);

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
            contactName.setText(todo.getContactName());
            contactImage.setImageResource(R.drawable.account);
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

    public void addContact(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACTS);
        } else {
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                    startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);

                } else {
                    Toast.makeText(EditTodoActivity.this, "Can't assign contact without required Permissions.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String id = contactUri.getLastPathSegment();
                String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
                String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id };
                Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                if (nameCur.moveToFirst()) {
                    String name = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    contactName.setText(name);
                    contactImage.setImageResource(R.drawable.account);
                    todo.setContactUri(contactUri.toString());
                    todo.setContactName(name);
                }
                nameCur.close();
            }
        }
    }
}
