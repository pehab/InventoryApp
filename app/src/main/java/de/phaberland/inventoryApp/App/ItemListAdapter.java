package de.phaberland.inventoryApp.App;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemProvider;

public class ItemListAdapter extends ArrayAdapter<Integer> implements SectionIndexer {
    private List<Integer> m_ids;
    private int m_selectedItem;

    public ItemListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Integer> objects) {
        super(context, resource, textViewResourceId, objects);

        m_ids = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Item item = ItemProvider.getInstance().getItemById(m_ids.get(position));
        TextView text = new TextView(getContext());
        text.setText(item.getM_name());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        if(position == m_selectedItem) {
           text.setBackgroundColor(Color.LTGRAY);
           text.setTextSize(TypedValue.COMPLEX_UNIT_PX, text.getTextSize() + 5);
        }
        return text;
    }

    @Override
    public Integer getItem(int position) {
        if(m_ids.isEmpty()) {
            return -1;
        }
        return super.getItem(position);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    public void setSelectedItem(int pos) {
        m_selectedItem = pos;
    }
}
