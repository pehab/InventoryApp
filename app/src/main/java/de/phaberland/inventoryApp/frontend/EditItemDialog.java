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

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.phaberland.inventoryApp.R;
import de.phaberland.inventoryApp.app.ItemListAdapter;
import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.interfaces.CreateItemDialogCallback;
import de.phaberland.inventoryApp.interfaces.YesNoCallback;

/**
 * EditItemDialog will be spawned when
 * edit button on settings is pressed.
 * There is an item selection and an
 * item editing component.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class EditItemDialog extends DialogFragment implements YesNoCallback, CreateItemDialogCallback {
    private final MainScreen m_callback;
    private DialogFragmentProvider.EditControls m_editControls;
    private DialogFragmentProvider.ItemControls m_itemControls;

    private int m_itemId;
    private CreateItemDialog m_createItemDlg;

    EditItemDialog(MainScreen callback) {
        m_callback = callback;
    }

    /////////////////////
    // dialog creation //
    /////////////////////

    /**
     * Creates a Dialog using DialogFragmentProvider
     * containing the item selection controlls and
     * item edit controls to create a new
     * item.
     * @param savedInstanceState not used here
     * @return a dialog to create an item
     * @see DialogFragmentProvider#createItemEdit(FragmentActivity)
     * @see DialogFragmentProvider#createItemSelection(FragmentActivity, CreateItemDialog)
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_items);

        m_createItemDlg = new CreateItemDialog(this);

        LinearLayout layout = new LinearLayout(m_callback);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(createItemSelection());

        m_editControls = DialogFragmentProvider.createItemEdit(m_callback);
        layout.addView(m_editControls.layout);

        // set up main layout
        builder.setView(layout);

        // add ok, button
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
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

    /**
     * Calls the item selection creation in DialogFragmentProvider.
     * Saves the resulting components and sets the OnClickListener
     * to the ListView. This OnClickListener, will refresh the
     * item edit depending on the selected item.
     *
     * @return a LinearLayout containing the Item selection
     * @see DialogFragmentProvider#createItemSelection(FragmentActivity, CreateItemDialog)
     */
    private LinearLayout createItemSelection() {
        m_itemControls = DialogFragmentProvider.createItemSelection(getActivity(),m_createItemDlg);

        m_itemControls.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position) == null) {
                    return;
                }
                m_itemId = (int)parent.getItemAtPosition(position);
                setItemEdit();
            }
        });
        return m_itemControls.layout;
    }

    ///////////////////
    // eventhandling //
    ///////////////////

    /**
     * updates the values of the selected item.
     */
    private void handlePositiveButton() {
        Item item = ItemProvider.getInstance().getItemById(m_itemId);

        String txt = m_editControls.name.getText().toString();
        if(!txt.isEmpty() && !txt.equals(item.getM_name())) {
            item.setM_name(txt);
        }

        Item.UNIT unit = Item.UNIT.values()[m_editControls.unit.getSelectedItemPosition()];
        if(!unit.equals(item.getM_unit())) {
            item.setM_unit(unit);
        }

        txt = m_editControls.def.getText().toString();
        if(!txt.isEmpty()) {
            int def = Integer.parseInt(txt);
            item.setM_defValue(def);
        }

        txt = m_editControls.crit.getText().toString();
        if(!txt.isEmpty()) {
            int crit = Integer.parseInt(txt);
            item.setM_critValue(crit);
        }

        if(m_callback != null) {
            m_callback.update();
        }
    }

    /**
     * gets the item specified and calls
     * setItemValues for it.
     * @see #setItemValues(Item)
     */
    @Override
    public void yesClicked() {
        Item.UNIT unit = Item.UNIT.values()[m_editControls.unit.getSelectedItemPosition()];
        String name = m_editControls.name.getText().toString();
        Item item = ItemProvider.getInstance().getItemById(ItemProvider.getInstance().findExistingItem(name, unit));

        setItemValues(item);
    }

    /**
     * updates the list of available items after
     * item creation and calls setItemEdit()
     * @param newItemId id of the new item
     * @see #setItemEdit() 
     */
    @Override
    public void update(int newItemId) {
        List<Integer> list = new ArrayList<>(ItemProvider.getInstance().getAllItems().keySet());
        ItemListAdapter adapter = new ItemListAdapter(m_callback,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        adapter.setSelectedItem(newItemId);
        m_itemControls.listView.setAdapter(adapter);
        m_itemId = newItemId;
        m_itemControls.listView.setSelection(newItemId);
        setItemEdit();
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
        String txt = m_editControls.def.getText().toString();
        int def = -1;
        if(!txt.isEmpty()) {
            def = Integer.parseInt(txt);
        }
        if(def != -1) {
            item.setM_defValue(def);
        }

        // crit value
        int crit = -1;
        txt = m_editControls.crit.getText().toString();
        if(!txt.isEmpty()) {
            crit = Integer.parseInt(txt);
        }

        if(crit != -1) {
            item.setM_critValue(crit);
        }
    }

    /**
     * updates the values in the item edit
     * component depending on the selected
     * item.
     */
    private void setItemEdit() {
        Item item = ItemProvider.getInstance().getItemById(m_itemId);
        if(m_editControls != null) {
            m_editControls.name.setText(item.getM_name());
            m_editControls.crit.setText(String.format(Locale.getDefault(), "%d",item.getM_critValue()));
            m_editControls.def.setText(String.format(Locale.getDefault(), "%d",item.getM_defValue()));
            m_editControls.unit.setSelection(item.getM_unit().ordinal());
        }
    }
}
