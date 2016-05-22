package me.ewitte.todopath;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.ewitte.todopath.model.List;
import me.ewitte.todopath.model.Todo;

/**
 * Created by Allaire on 21.05.2016.
 */
public class TodosAdapter extends ArrayAdapter<Todo> {

    public TodosAdapter(Context context, ArrayList<Todo> todos) {
        super(context, 0, todos);
    }

    public TodosAdapter(Context context, int resource, ArrayList<Todo> todos) {
        super(context, resource, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Todo todo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }
        // Lookup view for data population
        if (todo != null) {
            TextView tTitle = (TextView) convertView.findViewById(R.id.task_title);
            // Populate the data into the template view using the data object
            if (tTitle != null) {
                tTitle.setText(todo.getName());
                if (todo.getStatus() == 1) {
                    tTitle.setPaintFlags(tTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tTitle.setPaintFlags(tTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
