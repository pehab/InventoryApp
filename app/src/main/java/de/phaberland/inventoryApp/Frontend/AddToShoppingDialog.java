package de.phaberland.inventoryApp.Frontend;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

import de.phaberland.inventoryApp.App.ItemListAdapter;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Interfaces.CreateItemDialogCallback;
import de.phaberland.inventoryApp.R;


public class AddToShoppingDialog extends DialogFragment implements CreateItemDialogCallback {
    private int m_itemId = 0;
    private MainScreen m_callback;

    private LinearLayout m_ItemLayout;
    private ListView m_itemList;
    private CreateItemDialog m_createItemDlg;

    AddToShoppingDialog(MainScreen callback) {
        m_callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_add_to_shopping);

        // initialize itemcreation
        m_createItemDlg = new CreateItemDialog(this);

        // create item selection
        createItemSelection();
        builder.setView(m_ItemLayout);

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

    private void createItemSelection() {
        final DialogFragmentProvider.ItemControls controls = DialogFragmentProvider.createItemSelection(getActivity(),m_createItemDlg);

        m_ItemLayout = controls.layout;
        m_itemList = controls.listView;

        m_itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position) == null) {
                    return;
                }
                m_itemId = position;
                controls.adapter.setSelectedItem(m_itemId);
                controls.adapter.notifyDataSetChanged();
            }
        });
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

    @Override
    public Activity getHostingActivity() {
        return m_callback;
    }
}
