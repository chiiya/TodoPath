package me.ewitte.todopath;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.Toolbar;

import java.util.ArrayList;

import me.ewitte.todopath.db.TaskContract;
import me.ewitte.todopath.db.TaskDBHelper;
import me.ewitte.todopath.model.List;

public class MainActivity extends AppCompatActivity {

    private ListView todoLists;
    private ImageButton newList;
    private ArrayList<List> lists;
    private ListsAdapter listsAdapter;
    private TaskDBHelper db;
    private Toolbar toolbar;
    private TextView emptyMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        emptyMessage = (TextView) findViewById(R.id.emptyMessage);

        db = new TaskDBHelper(this);

        lists = db.getAllLists();

        todoLists = (ListView) findViewById(R.id.list_lists);
        registerForContextMenu(todoLists);

        todoLists.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent intent = new Intent(MainActivity.this, TodoActivity.class);
                intent.putExtra(TodoActivity.EXTRA_LIST_TITLE, listsAdapter.getItem(position).getTitle());
                intent.putExtra(TodoActivity.EXTRA_LIST_ID, listsAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });
        todoLists.setVerticalScrollBarEnabled(false);

        newList =(ImageButton)findViewById(R.id.list_new);

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

    public void clickButtonNewList(View view) {
        final EditText listEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.id.command)
                .setView(listEditText)
                .setPositiveButton(R.id.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = String.valueOf(listEditText.getText());
                        List list = new List(title);
                        list.setId(db.createList(list));
                        lists.add(list);
                        emptyMessage.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton(R.id.cancel, null)
                .create();
        dialog.show();
        updateUI();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_lists) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position;
        final List itemSelected = listsAdapter.getItem(info.position);
        switch(item.getItemId()) {
            case R.id.edit:
                final EditText listEditText = new EditText(this);
                listEditText.setText(itemSelected.getTitle());
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.id.edit)
                        .setView(listEditText)
                        .setPositiveButton(R.id.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = String.valueOf(listEditText.getText());
                                itemSelected.setTitle(title);
                                db.updateList(itemSelected);
                                updateUI();
                            }
                        })
                        .setNegativeButton(R.id.cancel, null)
                        .create();
                dialog.show();
                updateUI();
                return true;
            case R.id.delete:
                db.deleteList(itemSelected);
                lists.remove(itemSelected);
                updateUI();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateUI() {
        if (listsAdapter == null) {
            listsAdapter = new ListsAdapter(this,
                    R.layout.item_list,
                    lists);
            todoLists.setAdapter(listsAdapter);
        } else {
            listsAdapter.notifyDataSetChanged();
        }

        if (lists.size() > 0) {
            emptyMessage.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

}
