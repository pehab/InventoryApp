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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.interfaces.CreateItemDialogCallback;

import de.phaberland.inventoryApp.interfaces.YesNoCallback;
import de.phaberland.inventoryApp.R;

/**
 * CreateItemDialog will be
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class CreateItemDialog extends DialogFragment implements YesNoCallback {
    private final CreateItemDialogCallback m_callback;
    private DialogFragmentProvider.EditControls m_controls;

    CreateItemDialog(CreateItemDialogCallback callback) {
        m_callback = callback;
    }

    /////////////////////
    // dialog creation //
    /////////////////////

    /**
     * Creates a Dialog using DialogFragmentProvider
     * containing item edit controls to create a new
     * item.
     * @param savedInstanceState not used here
     * @return a dialog to create an item
     * @see DialogFragmentProvider#createItemEdit(FragmentActivity)
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_new_item);

        // set up main layout
        m_controls = DialogFragmentProvider.createItemEdit(getActivity());
        builder.setView(m_controls.layout);

        // add ok, button
        builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handlePositiveButton();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { }
        });

        // return created dialog
        return builder.create();
    }

    ///////////////////
    // eventhandling //
    ///////////////////

    /**
     * checks if the item specified already exists.
     * If the item specified by name and unit is already
     * defined the user will be asked if the values should be
     * updated, if not the item will be created with the
     * specified values.
     */
    private void handlePositiveButton() {
        Item.UNIT unit = Item.UNIT.values()[m_controls.unit.getSelectedItemPosition()];
        String name = m_controls.name.getText().toString();

        int m_existingItemId = ItemProvider.getInstance().findExistingItem(name, unit);
        Item item;
        if(m_existingItemId == -1) {
            m_existingItemId = ItemProvider.getInstance().addItem(name, unit);
            item = ItemProvider.getInstance().getItemById(m_existingItemId);
            setItemValues(item);
        } else {
            item = ItemProvider.getInstance().getItemById(m_existingItemId);

            String txt = m_controls.def.getText().toString();
            int def = -1;
            if(!txt.isEmpty()) {
                def = Integer.parseInt(txt);
            }
            int crit = -1;
            txt = m_controls.crit.getText().toString();
            if(!txt.isEmpty()) {
                crit = Integer.parseInt(txt);
            }

            if((crit != -1 || def != -1) &&
                    (item.getM_critValue() != crit || item.getM_defValue() != def)) {
                // update values?
               DialogFragmentProvider.createSimpleYesNoDialog(
                       getString(R.string.msg_replace_values),
                       this,
                       getContext());
            }
        }
        if(m_callback != null) {
            m_callback.update(item.getM_id());
        }
    }

    /**
     * gets the item specified and calls
     * setItemValues for it.
     * @see #setItemValues(Item)
     */
    @Override
    public void yesClicked() {
        Item.UNIT unit = Item.UNIT.values()[m_controls.unit.getSelectedItemPosition()];
        String name = m_controls.name.getText().toString();
        Item item = ItemProvider.getInstance().getItemById(ItemProvider.getInstance().findExistingItem(name, unit));

        setItemValues(item);
    }

    ////////////
    // helper //
    ////////////

    /**
     * sets crit and default values specified in the
     * dialog to the given item.
     * @param item the item to set values to.
     */
    private void setItemValues(Item item) {
        // default value
        String txt = m_controls.def.getText().toString();
        int def = -1;
        if(!txt.isEmpty()) {
            def = Integer.parseInt(txt);
        }
        if(def != -1) {
            item.setM_defValue(def);
        }

        // crit value
        int crit = -1;
        txt = m_controls.crit.getText().toString();
        if(!txt.isEmpty()) {
            crit = Integer.parseInt(txt);
        }

        if(crit != -1) {
            item.setM_critValue(crit);
        }
    }
}
