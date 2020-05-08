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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.R;

import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ListProvider;

/**
 * RemoveFromInventoryDialog is a dialog which is
 * spawned when the remove button in the inventory table.
 * It is used to choose an amount that will be removed
 * from inventory.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class RemoveFromInventoryDialog extends DialogFragment {
    private final int m_itemId;
    private EditText m_text;
    private final MainScreen m_callback;

    /////////////////////
    // dialog creation //
    /////////////////////

    /**
     * Constructs an instance of the dialog,
     * setting the item id, that is is called for
     * and the callback.
     * @param callback a MainScreen instance used as callback
     * @param id id of the item selected.
     */
    public RemoveFromInventoryDialog(MainScreen callback, int id) {
        m_itemId = id;
        m_callback = callback;
    }

    /**
     * creates the Dialog and sets up everything.
     * The dialog will contain an amount choosing component,
     * and a positive button.
     * @param savedInstanceState Bundle not used in override
     * @return the set up Dialog
     */
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

    /**
     * the positive button was pressed.
     * the amount for the item in the inventory list is reduced
     * by the chosen amount.
     */
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
