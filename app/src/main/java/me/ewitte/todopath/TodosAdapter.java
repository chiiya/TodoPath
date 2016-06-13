package me.ewitte.todopath;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.ewitte.todopath.model.Todo;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Allaire on 21.05.2016.
 */
public class TodosAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ArrayList<Todo> todos;
    private Context context;

    public TodosAdapter(Context context, ArrayList<Todo> todos) {
        this.todos = todos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Object getItem(int position) {
        return todos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return todos.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder;

        // Get the data item for this position
        Todo todo = (Todo) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_todo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.todoTextView.setText(todo.getName());

        // Check status and strikethrough if task is done
        if (todo.getStatus() == 1) {
            holder.todoTextView.setPaintFlags(holder.todoTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.todoTextView.setPaintFlags(holder.todoTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Show Contact ImageButton if a contact is assigned to the Todo
        if (todo.getContactUri() != null && !todo.getContactUri().isEmpty()) {
            holder.todoContactImage.setVisibility(View.VISIBLE);
            holder.todoContactName.setText(todo.getContactName());
            holder.todoContactName.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        HeaderViewHolder holder;

        // Get the data item for this position
        Todo todo = (Todo) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_header, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        switch(todo.getPriority()) {
            case Todo.PRIORITY_HIGH:
                holder.headerTextView.setText(R.string.high_priority);
                break;
            case Todo.PRIORITY_MEDIUM:
                holder.headerTextView.setText(R.string.medium_priority);
                break;
            case Todo.PRIORITY_LOW:
                holder.headerTextView.setText(R.string.low_priority);
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return (long) todos.get(position).getPriority();
    }

    private class HeaderViewHolder {
        private TextView headerTextView;

        public HeaderViewHolder(View v) {
            headerTextView = (TextView) v.findViewById(R.id.task_header);
        }
    }

    private class ViewHolder {
        private TextView todoTextView;
        private ImageButton todoContactImage;
        private TextView todoContactName;

        public ViewHolder(View v) {
            todoTextView = (TextView) v.findViewById(R.id.task_title);
            todoContactImage = (ImageButton) v.findViewById(R.id.buttonContact);
            todoContactName = (TextView) v.findViewById(R.id.tvListContactName);
        }
    }

}
