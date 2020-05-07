package de.phaberland.inventoryApp.Frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Interfaces.CreateItemDialogCallback;

import de.phaberland.inventoryApp.Interfaces.YesNoCallback;
import de.phaberland.inventoryApp.R;

public class CreateItemDialog extends DialogFragment implements YesNoCallback {
    private enum DialogPart {
        NAME,
        UNIT,
        DEFAULT,
        CRIT
    }

    private CreateItemDialogCallback m_callback;
    private EditText m_Name;
    private Spinner m_Unit;
    private EditText m_Default;
    private EditText m_Crit;

    /////////////////////
    // dialog creation //
    /////////////////////

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_new_item);

        // set up main layout
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(mainLayout);

        // create all necessary parts
        for(DialogPart part : DialogPart.values()) {
            LinearLayout partLayout = createDialogPart(part);
            mainLayout.addView(partLayout);
        }

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

    private LinearLayout createDialogPart(DialogPart part) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16,16,16,16);
        layout.setLayoutParams(params);

        // initialize labels and values
        String textLabel = "";

        switch (part) {
            case NAME: textLabel = getString(R.string.label_name); break;
            case UNIT: textLabel = getString(R.string.label_unit); break;
            case DEFAULT: textLabel = getString(R.string.label_default) + "" + getString(R.string.label_optional); break;
            case CRIT: textLabel = getString(R.string.label_crit) + "" + getString(R.string.label_optional); break;
        }

        // add label
        TextView labelText = new TextView(getContext());
        labelText.setText(textLabel);
        labelText.setGravity(Gravity.CENTER);
        layout.addView(labelText);

        // add value choosing part
        View valuePart = createValuePart(part);
        layout.addView(valuePart);

        return layout;
    }

    private View createValuePart(DialogPart part) {
        LinearLayout valueView = new LinearLayout(getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16,16,16,16);
        valueView.setLayoutParams(params);
        valueView.setGravity(Gravity.END);
        switch (part) {
            case NAME: {
                m_Name = new EditText(getContext());
                valueView.addView(m_Name);
                break;
            }
            case DEFAULT: {
                m_Default = new EditText(getContext());
                m_Default.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueView.addView(m_Default);
                break;
            }
            case CRIT: {
                m_Crit = new EditText(getContext());
                m_Crit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueView.addView(m_Crit);
                break;
            }
            case UNIT: {
                m_Unit = new Spinner(getContext());
                ArrayAdapter<Item.UNIT> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, Item.UNIT.values());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                m_Unit.setAdapter(adapter);
                valueView.addView(m_Unit);
                break;
            }
        }

        return valueView;
    }

    ///////////////////
    // eventhandling //
    ///////////////////

    private void handlePositiveButton() {
        Item.UNIT unit = Item.UNIT.values()[m_Unit.getSelectedItemPosition()];
        String name = m_Name.getText().toString();
        String txt = m_Default.getText().toString();
        int def = -1;
        if(!txt.isEmpty()) {
            def = Integer.parseInt(txt);
        }
        int crit = -1;
        txt = m_Crit.getText().toString();
        if(!txt.isEmpty()) {
            crit = Integer.parseInt(txt);
        }
        int m_existingItemId = ItemProvider.getInstance().findExistingItem(name, unit);
        Item item;
        if(m_existingItemId == -1) {
            m_existingItemId = ItemProvider.getInstance().addItem(name, unit);
            item = ItemProvider.getInstance().getItemById(m_existingItemId);
            if(def != -1) {
                item.setM_defValue(def);
            }
            if(crit != -1) {
                item.setM_critValue(crit);
            }
        } else {
            item = ItemProvider.getInstance().getItemById(m_existingItemId);

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

    @Override
    public void yesClicked() {
        Item.UNIT unit = Item.UNIT.values()[m_Unit.getSelectedItemPosition()];
        String name = m_Name.getText().toString();
        Item item = ItemProvider.getInstance().getItemById(ItemProvider.getInstance().findExistingItem(name, unit));
        String txt = m_Default.getText().toString();
        int def = -1;
        if(!txt.isEmpty()) {
            def = Integer.parseInt(txt);
        }
        int crit = -1;
        txt = m_Crit.getText().toString();
        if(!txt.isEmpty()) {
            crit = Integer.parseInt(txt);
        }

        if(crit != -1) {
            item.setM_critValue(crit);
        }
        if(def != -1) {
            item.setM_defValue(def);
        }
    }

    ///////////////////
    // setter/getter //
    ///////////////////

    void setCallback(CreateItemDialogCallback callback) {
        m_callback = callback;
    }
}
