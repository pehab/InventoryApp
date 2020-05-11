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
import androidx.fragment.app.FragmentActivity;

import de.phaberland.inventoryApp.R;
import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.data.ListProvider;

/**
 * AddFromShoppingDialog is used to transfer items from
 * shopping list to inventory list.
 * It will have an amount choosing component, where the
 * amount that will be added to inventory can be selected.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class AddFromShoppingDialog extends DialogFragment {
    private final int m_itemId;
    private EditText m_text;
    private final MainScreen m_callback;

    /////////////////////
    // dialog creation //
    /////////////////////

    /**
     * Constructor setting up callback and item id
     * @param callback callback where update will be called
     * @param id id of the item, to add to inventory
     */
    public AddFromShoppingDialog(MainScreen callback, int id) {
        m_itemId = id;
        m_callback = callback;
    }

    /**
     * Creates the dialog using DialogFragmentProvider,
     * an amount choosing component will be added and
     * a positive button calling handlePositiveButton.
     * @param savedInstanceState not used here.
     * @return created dialog instance
     * @see DialogFragmentProvider#createAmountChoosing(FragmentActivity, int, int)
     * @see #handlePositiveButton()
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_add_amount);

        // create amount choosing
        int maxAmount = ItemProvider.getInstance().getItemById(m_itemId).getM_defValue() * 10;
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
     * Handles the positiv button press. The defined item will
     * be added with the chosen amount to the inventory list
     * and will be removed from the shopping list.
     * Afterwards update will be called on the callback.
     */
    private void handlePositiveButton() {
        ItemList list = ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID);
        list.add(ItemProvider.getInstance().getItemById(m_itemId),getAmount());

        list = ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID);
        if(list.hasItem(ItemProvider.getInstance().getItemById(m_itemId))) {
            list.remove(m_itemId);
        }

        m_callback.update();
    }

    ///////////////////
    // setter/getter //
    ///////////////////

    private int getAmount() {
        return Integer.parseInt(m_text.getText().toString());
    }
}
