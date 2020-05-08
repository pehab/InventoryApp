package de.phaberland.inventoryApp.frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.R;

import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ListProvider;

public class RemoveFromInventoryDialog extends DialogFragment {
    private final int m_itemId;
    private EditText m_text;
    private final MainScreen m_callback;

    /////////////////////
    // dialog creation //
    /////////////////////

    public RemoveFromInventoryDialog(MainScreen callback, int id) {
        m_itemId = id;
        m_callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_remove_amount);

        // create amount choosing
        int maxAmount = ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID).getAmountForId(m_itemId);
        DialogFragmentProvider.AmountControls controls = DialogFragmentProvider.createAmountChoosing(getActivity(), m_itemId, maxAmount);
        if(controls == null) {return builder.create();}
        m_text = controls.editText;
        builder.setView(controls.layout);

        // add buttons
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handlePositiveButton();
            }
        });

        return builder.create();
    }

    ///////////////////
    // eventhandling //
    ///////////////////

    private void handlePositiveButton() {
        ItemList list = ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID);
        Item item = ItemProvider.getInstance().getItemById(m_itemId);
        if(!list.hasItem(item)) {
            return;
        }

        list.remove(item ,getAmount());
        m_callback.update();
    }

    ///////////////////
    // setter/getter //
    ///////////////////

    private int getAmount() {
        return Integer.parseInt(m_text.getText().toString());
    }
}