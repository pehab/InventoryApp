/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import de.phaberland.inventoryApp.app.ItemListAdapter;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.interfaces.CreateItemDialogCallback;
import de.phaberland.inventoryApp.R;

/**
 * AddToShoppingDialog is used to add items to the shopping list.
 * An item selection will be added, to specify the item that
 * should be added.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class AddToShoppingDialog extends DialogFragment implements CreateItemDialogCallback {
    private int m_itemId = 0;
    private final MainScreen m_callback;

    private ListView m_itemList;
    private CreateItemDialog m_createItemDlg;

    AddToShoppingDialog(MainScreen callback) {
        m_callback = callback;
    }

    /**
     * Creates a main layout and adds an item selection
     * component to it.
     *
     * @param savedInstanceState parameter not used here
     * @return returns the created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_add_to_shopping);

        // initialize itemcreation
        m_createItemDlg = new CreateItemDialog(this);

        // create item selection
        builder.setView(createItemSelection());

        // set buttonss
        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(m_callback != null) {
                    m_callback.readAddToShoppingDlgAndUpdate();
                }
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    //////////////////////
    // Creation helpers //
    //////////////////////

    /**
     * Calls the item selection creation in DialogFragmentProvider.
     * Saves the resulting components and sets the OnClickListener
     * to the ListView.
     *
     * @return a LinearLayout containing the Item selection
     * @see DialogFragmentProvider#createItemSelection(FragmentActivity, CreateItemDialog)
     */
    private LinearLayout createItemSelection() {
        final DialogFragmentProvider.ItemControls controls = DialogFragmentProvider.createItemSelection(getActivity(),m_createItemDlg);

        m_itemList = controls.listView;

        m_itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position) == null) {
                    return;
                }
                m_itemId = (int)parent.getItemAtPosition(position);
                controls.adapter.setSelectedItem(position);
                controls.adapter.notifyDataSetChanged();
            }
        });
        return controls.layout;
    }

    /////////////
    // getters //
    /////////////

    int getItemId() {
        return m_itemId;
    }

    ////////////////////////
    // Interface Override //
    ////////////////////////

    /**
     *Updates the listView with the possibly changed list
     * and sets the item in the parameter as selected.
     * @param newItemId id of the new item
     */
    @Override
    public void update(int newItemId) {
        List<Integer> list = new ArrayList<>(ItemProvider.getInstance().getAllItems().keySet());
        ItemListAdapter adapter = new ItemListAdapter(m_callback,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        adapter.setSelectedItem(newItemId);
        m_itemList.setAdapter(adapter);
        m_itemId = newItemId;
        m_itemList.setSelection(newItemId);
    }

}
