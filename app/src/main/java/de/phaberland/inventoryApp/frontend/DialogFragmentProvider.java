/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.frontend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.phaberland.inventoryApp.R;
import de.phaberland.inventoryApp.app.ItemListAdapter;
import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.interfaces.YesNoCallback;

/**
 * DialogFragmentProvider is used in the frontend
 * to create certain parts of dialogs, which can
 * be reused by different dialogs.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class DialogFragmentProvider {
    private enum DialogPart {
        NAME,
        UNIT,
        DEFAULT,
        CRIT
    }

    //////////////
    // controls //
    //////////////

    /**
     * controls for amount choosing component
     *  - layout: the layout containing the fragment
     *  - editText: the EditText control containing the amount
     */
    static class AmountControls {
        EditText editText;
        LinearLayout layout;
    }

    /**
     * controls for item selection component
     *  - layout: the layout containing the fragment
     *  - listView: the control for the list of items
     *  - adapter: the ItemListAdapter containing the list of items
     */
    static class ItemControls {
        ListView listView;
        LinearLayout layout;
        ItemListAdapter adapter;
    }

    /**
     * controls for item editing
     *  - layout: the layout containing the fragment
     *  - name: EditText containing the name of the item
     *  - unit: Spinner containing the unit of the item
     *  - def: EditText containing the default shopping amount
     *  - crit: EditText containing the critical inventory amount
     */
    static class EditControls {
        LinearLayout layout;
        EditText name;
        Spinner unit;
        EditText def;
        EditText crit;
    }

    /////////////////////
    // complete dialog //
    /////////////////////

    /**
     * creates and shows a simple dialog displaying
     * the provided message, which can be answered
     * with yes and no.
     * On yes button clicked the callback will be called
     *
     * @param msg message to display on dialog
     * @param callback YesNoCallback to call, when yes is clicked
     * @param context Context from which it is called
     */
    public static void createSimpleYesNoDialog(String msg, final YesNoCallback callback, Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {//Yes button clicked
                    callback.yesClicked();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton(R.string.button_yes, dialogClickListener)
                .setNegativeButton(R.string.button_no, dialogClickListener).show();
    }

    //////////////////////////////
    // dialog fragment creation //
    //////////////////////////////

    /**
     * Creates a dialog fragment containing a Slider and a synced
     * EditText to be able to choose the amount of a specified item.
     *
     * @param activity the activity, which requested that fragment
     * @param itemId the item id for default selection
     * @param maxAmount maximal amount to choose in slider
     * @return the controls for the amount choosing fragment
     * @see AmountControls
     */
    static AmountControls createAmountChoosing(FragmentActivity activity, int itemId, int maxAmount) {
        final AmountControls controls = new AmountControls();

        controls.layout = new LinearLayout(activity);

        // Precondition, we have an item.
        if(ItemProvider.getInstance().getItemById(itemId) == null) {
            return null;
        }

        Item item = ItemProvider.getInstance().getItemById(itemId);

        controls.layout.setOrientation(LinearLayout.VERTICAL);

        final SeekBar seekBar = new SeekBar(activity);

        seekBar.setMax(maxAmount);
        int selection = item.getM_defValue();
        if(selection > maxAmount) {
            selection = maxAmount;
        }
        seekBar.setProgress(selection);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controls.editText.setText(String.format(Locale.getDefault(),"%d",seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        controls.layout.addView(seekBar);

        LinearLayout textArea = new LinearLayout(activity);
        textArea.setOrientation(LinearLayout.HORIZONTAL);
        textArea.setGravity(Gravity.CENTER);
        controls.editText = new EditText(activity);
        controls.editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        controls.editText.setText(String.format(Locale.getDefault(),"%d",seekBar.getProgress()));
        controls.editText.setGravity(Gravity.CENTER);
        controls.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String txt = s.toString();
                if(txt.isEmpty()) {
                    txt = "0";
                }
                seekBar.setProgress(Integer.parseInt(txt));
            }
        });
        textArea.addView(controls.editText);
        TextView text = new TextView(activity);
        text.setGravity(Gravity.CENTER);
        text.setText(String.format(Locale.getDefault(),"/%d",seekBar.getMax()));
        textArea.addView(text);
        controls.layout.addView(textArea);

        return controls;
    }

    /**
     * Creates a dialog fragment containing a list view to
     * select an item and a button to spawn a createItemDialog.
     *
     * @param activity the activity, which requested that fragment
     * @param createItemDialog an instance of CreateItemDialog to spawn if needed
     * @return the controls for the fragment
     * @see ItemControls
     */
    static ItemControls createItemSelection(final FragmentActivity activity, final CreateItemDialog createItemDialog) {
        final ItemControls controls = new ItemControls();
        controls.layout = new LinearLayout(activity);
        controls.layout.setOrientation(LinearLayout.HORIZONTAL);
        controls.layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout itemSelector = new LinearLayout(activity);
        itemSelector.setOrientation(LinearLayout.VERTICAL);
        itemSelector.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        // add filter
        EditText filter = new EditText(activity);
        filter.setHint(R.string.label_filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                List<Integer> list = new ArrayList<>(ItemProvider.getInstance().getAllItemsFiltered(s.toString()).keySet());
                if(controls.adapter != null) {
                    controls.adapter = new ItemListAdapter(activity,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    controls.listView.setAdapter(controls.adapter);
                }
            }
        });
        itemSelector.addView(filter);

        // item selector
        List<Integer> list = new ArrayList<>(ItemProvider.getInstance().getAllItems().keySet());
        controls.adapter = new ItemListAdapter(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);

        controls.listView = new ListView(activity);
        controls.listView.setAdapter(controls.adapter);
        controls.listView.setFastScrollEnabled(true);
        ViewGroup.MarginLayoutParams params;
        if(controls.adapter.getCount() > 5){
            View item = controls.adapter.getView(0, null, controls.listView);
            item.measure(0, 0);
            params = new ViewGroup.MarginLayoutParams(500, (int) (5.5 * item.getMeasuredHeight()));
        } else {
            params = new ViewGroup.MarginLayoutParams(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        params.setMargins(16,16,16,16);
        controls.listView.setLayoutParams(params);

        itemSelector.addView(controls.listView);
        controls.layout.addView(itemSelector);

        // create new Item button
        Button newItemButton = new Button(activity);
        ViewGroup.MarginLayoutParams buttonParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(16,16,16,16);
        newItemButton.setLayoutParams(buttonParams);
        newItemButton.setText(R.string.button_new);
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItemDialog.show(activity.getSupportFragmentManager(),activity.getString(R.string.tag_new_item_dlg));
            }
        });

        controls.layout.addView(newItemButton);
        return controls;
    }

    /**
     * Creates a dialog fragment containing the controls to
     * edit item attributes.
     *
     * @param activity the activity, which requested that fragment
     * @return the controls for the fragment
     * @see EditControls
     * @see #createDialogPart(DialogPart, FragmentActivity, EditControls)
     */
    static EditControls createItemEdit(final FragmentActivity activity){
        // create all necessary parts
        EditControls controls = new EditControls();
        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        for(DialogPart part : DialogPart.values()) {
            LinearLayout partLayout = createDialogPart(part, activity, controls);
            mainLayout.addView(partLayout);
        }
        controls.layout = mainLayout;
        return controls;
    }

    /////////////
    // helpers //
    /////////////

    /**
     * Creates a single set of Label and editable value.
     *
     * @param part part for which to create that the set
     * @param activity the activity, which requested that fragment
     * @param controls EditControls for the hosting component
     * @return A LinearLayout containing label and value
     */
    private static LinearLayout createDialogPart(DialogPart part, final FragmentActivity activity, EditControls controls) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16,16,16,16);
        layout.setLayoutParams(params);

        // initialize labels and values
        String textLabel = "";

        switch (part) {
            case NAME: textLabel = activity.getString(R.string.label_name); break;
            case UNIT: textLabel = activity.getString(R.string.label_unit); break;
            case DEFAULT: textLabel = activity.getString(R.string.label_default) + "" + activity.getString(R.string.label_optional); break;
            case CRIT: textLabel = activity.getString(R.string.label_crit) + "" + activity.getString(R.string.label_optional); break;
        }

        // add label
        TextView labelText = new TextView(activity);
        labelText.setText(textLabel);
        labelText.setGravity(Gravity.CENTER);
        layout.addView(labelText);

        // add value choosing part
        View valuePart = createValuePart(part, activity, controls);
        layout.addView(valuePart);

        return layout;
    }

    /**
     * Creates an editable Value choosing part depending on the
     * part of the dialog it can be EditText or spinner.
     *
     * @param part part for which to create that the value
     * @param activity the activity, which requested that fragment
     * @param controls EditControls for the hosting component
     * @return a View containing value controls for specified part
     */
    private static View createValuePart(DialogPart part, final FragmentActivity activity, EditControls controls) {
        LinearLayout valueView = new LinearLayout(activity);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16,16,16,16);
        valueView.setLayoutParams(params);
        valueView.setGravity(Gravity.END);
        switch (part) {
            case NAME: {
                controls.name = new EditText(activity);
                valueView.addView(controls.name);
                break;
            }
            case DEFAULT: {
                controls.def = new EditText(activity);
                controls.def.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueView.addView(controls.def);
                break;
            }
            case CRIT: {
                controls.crit = new EditText(activity);
                controls.crit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                valueView.addView(controls.crit);
                break;
            }
            case UNIT: {
                controls.unit = new Spinner(activity);
                ArrayAdapter<Item.UNIT> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_list_item_1, android.R.id.text1, Item.UNIT.values());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                controls.unit.setAdapter(adapter);
                valueView.addView(controls.unit);
                break;
            }
        }

        return valueView;
    }
}
