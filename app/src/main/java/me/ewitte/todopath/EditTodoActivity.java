package me.ewitte.todopath;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.Contacts;
import android.view.View.OnClickListener;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import me.ewitte.todopath.model.CustomDateTimePicker;
import me.ewitte.todopath.model.Todo;

public class EditTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_TODO_ID = "me.ewitte.todopath.TODOID";
    public static final int REQUEST_PICK_CONTACT = 1;
    public static final int PERMISSION_READ_CONTACTS = 10;
    final static int RQS_1 = 1;
    private static final String LOG = "EditTodoActivity";

    private int todoID;
    private Spinner spinner;
    private EditText todoEditText;
    private Todo todo;
    private TextView contactName;
    private ImageButton contactImage;
    private TextView dateString;
    private ImageButton dateImage;

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
        dateString = (TextView) findViewById(R.id.textReminder);
        dateImage = (ImageButton) findViewById(R.id.buttonReminder);


        // Initialize and populate Spinner
        spinner = (Spinner) findViewById(R.id.priority_spinner);
        String[] options = {"High Priority", "Medium Priority", "Low Priority"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditTodoActivity.this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        if (extras != null) { // We are passing an existing Object
            todoTextView.setText(R.string.editTodo);
            todo = extras.getParcelable(Todo.TAG);
            todoID = extras.getInt(EXTRA_TODO_ID);
            todoEditText.setText(todo.getName());
            spinner.setSelection(todo.getPriority());
            if (todo.getContactName() != null) {
                contactName.setText(todo.getContactName());
                contactImage.setImageResource(R.drawable.account);
            }
            if (todo.getDate() != null) {
                dateString.setText(todo.getDate());
                dateImage.setImageResource(R.drawable.reminder);
                Log.e(LOG, todo.getDate());
            }
        } else {
            spinner.setSelection(1);
            todoTextView.setText(R.string.createTodo);
            todo = new Todo();
        }
    }


    private void setAlarm(String date){
        date += " 08:00:00";
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Log.e(LOG, date + " - " + timeInMilliseconds);
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(EditTodoActivity.this, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Todo.TAG, todo);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditTodoActivity.this, RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMilliseconds, pendingIntent);
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

    public void addDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EditTodoActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(true);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dateString.setText(date);
        todo.setDate(date);
        setAlarm(date);
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
