package de.phaberland.inventoryApp.Frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;
import de.phaberland.inventoryApp.Interfaces.EventCallback;
import de.phaberland.inventoryApp.R;

public class AddFromShoppingDialog extends DialogFragment {
    private int m_itemId;
    private EditText m_text;
    private EventCallback m_callback;

    /////////////////////
    // dialog creation //
    /////////////////////

    public AddFromShoppingDialog(EventCallback callback, int id) {
        m_itemId = id;
        m_callback = callback;
    }

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

    private void handlePositiveButton() {
        ItemList list = ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID);
        list.add(ItemProvider.getInstance().getItemById(m_itemId),getAmount());

        list = ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID);
        if(list.hasItem(m_itemId)) {
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
