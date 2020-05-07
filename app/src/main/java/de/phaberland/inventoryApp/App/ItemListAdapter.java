/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */
package de.phaberland.inventoryApp.App;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.R;

/**
 * ItemListAdapter is used to display and handle items
 * in a ListView.
 * It is using integers to map actual position in the List
 * to the id of the containing item, the name of the item
 * can be displayed.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class ItemListAdapter extends ArrayAdapter<Integer> {
    private List<Integer> m_ids;
    private int m_selectedItem;

    /**
     * Creates a new instance of ItemListAdapter.
     * Mostly it uses the standard constructor of ArrayAdapter
     * and additionally uses a list of ids, to map the position
     * in the list to the corresponding id of an item.
     * @param context the context the app is running in, used for ArrayAdapter
     * @param resource resources used by the app, used for ArrayAdapter
     * @param textViewResourceId textResource, used for ArrayAdapter
     * @param objects list of ids that need to be displayed
     * @see ArrayAdapter
     */
    public ItemListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Integer> objects) {
        super(context, resource, textViewResourceId, objects);

        m_ids = objects;
    }

    /**
     * Overrides the standard getView method of ArrayAdapter
     * to show the name of the item referenced by the id in
     * the displayed list. Without that override only the ids
     * would get displayed.
     * @param position position within the list used to get id
     * @param convertView not used
     * @param parent not used
     * @return a TextView containing the name of the item to display
     * @see ArrayAdapter
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Item item = ItemProvider.getInstance().getItemById(m_ids.get(position));
        TextView text = new TextView(getContext());
        text.setText(item.getM_name());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        if(position == m_selectedItem) {
           text.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
           text.setTextSize(TypedValue.COMPLEX_UNIT_PX, text.getTextSize() + 5);
        }
        return text;
    }

    /**
     * Overrides the getItem function from ArrayAdapter.
     * Basically only adds some checks to make sure the item is available
     * in the list.
     * @param position position within the listview
     * @return the result of ArrayAdapter.getItem
     * @see ArrayAdapter
     */
    @Override
    public Integer getItem(int position) {
        if(m_ids.isEmpty() || m_ids.size() <= position) {
            return -1;
        }
        return super.getItem(position);
    }

    /**
     * Setting item from the outside to be able to
     * mark selection and also select an item which was
     * added to the list.
     * @param pos position of selected item
     */
    public void setSelectedItem(int pos) {
        m_selectedItem = pos;
    }
}
