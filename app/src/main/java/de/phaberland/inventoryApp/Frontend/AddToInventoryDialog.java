package de.phaberland.inventoryApp.Frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.phaberland.inventoryApp.App.ItemListAdapter;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Interfaces.EventCallback;
import de.phaberland.inventoryApp.R;

import java.util.ArrayList;
import java.util.List;

import de.phaberland.inventoryApp.Interfaces.DialogCallback;


public class AddToInventoryDialog extends DialogFragment implements DialogCallback {
    private int m_itemId = 0;
    private int m_amount;
    private EventCallback m_callback;

    private LinearLayout m_mainLayout;

    private LinearLayout m_AmountLayout;
    private EditText m_text;

    private LinearLayout m_ItemLayout;
    private ListView m_itemList;
    private CreateItemDialog m_createItemDlg;

    AddToInventoryDialog(EventCallback callback) {
        m_callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_add_to_inventory);

        // set up main layout
        m_mainLayout = new LinearLayout(getActivity());
        m_mainLayout.setOrientation(LinearLayout.VERTICAL);
        m_mainLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        // initialize itemcreation
        m_createItemDlg = new CreateItemDialog();
        m_createItemDlg.setCallback(this);

        // create item selection
        createItemSelection();
        m_mainLayout.addView(m_ItemLayout);

        // create initial amount
        createAmountChoosing();
        m_mainLayout.addView(m_AmountLayout);

        builder.setView(m_mainLayout);

        // set buttonss
        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                m_amount = Integer.parseInt(m_text.getText().toString());
                if(m_callback != null) {
                    m_callback.readAddToInvDlgAndUpdate();
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
                m_itemId = (int)parent.getItemAtPosition(position);
                controls.adapter.setSelectedItem(position);
                controls.adapter.notifyDataSetChanged();
                m_mainLayout.removeView(m_AmountLayout);
                createAmountChoosing();
                m_mainLayout.addView(m_AmountLayout);
            }
        });
    }

    private void createAmountChoosing() {
        int maxAmount = ItemProvider.getInstance().getItemById(m_itemId).getM_defValue() * 10;
        DialogFragmentProvider.AmountControls controls = DialogFragmentProvider.createAmountChoosing(getActivity(), m_itemId, maxAmount);
        if(controls == null) {
            return;
        }
        m_AmountLayout = controls.layout;
        m_text = controls.editText;
    }

    /////////////
    // getters //
    /////////////
    int getItemId() {
        return m_itemId;
    }

    int getAmount() {
        return m_amount;
    }

    ////////////////////////
    // Interface Override //
    ////////////////////////

    @Override
    public void update(int newItemId) {
        List<Integer> list = new ArrayList<>(ItemProvider.getInstance().getAllItems().keySet());
        ItemListAdapter adapter = new ItemListAdapter(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        adapter.setSelectedItem(newItemId);
        m_itemList.setAdapter(adapter);
        m_itemId = newItemId;
        m_itemList.setSelection(newItemId);
        m_mainLayout.removeView(m_AmountLayout);
        createAmountChoosing();
        m_mainLayout.addView(m_AmountLayout);
    }
}
