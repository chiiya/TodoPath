package me.ewitte.todopath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.ewitte.todopath.model.List;

/**
 * Created by Allaire on 21.05.2016.
 */
public class ListsAdapter extends ArrayAdapter<List> {
    public ListsAdapter(Context context, ArrayList<List> lists) {
        super(context, 0, lists);
    }

    public ListsAdapter(Context context, int resource, ArrayList<List> lists) {
        super(context, resource, lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        List list = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }
        // Lookup view for data population
        if (list != null) {
            TextView lTitle = (TextView) convertView.findViewById(R.id.list_title);
            // Populate the data into the template view using the data object
            if (lTitle != null) {
                lTitle.setText(list.getTitle());
            }
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
