package de.phaberland.inventoryApp.Frontend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import de.phaberland.inventoryApp.App.ItemListAdapter;
import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemProvider;

import de.phaberland.inventoryApp.Interfaces.YesNoCallback;
import de.phaberland.inventoryApp.R;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class DialogFragmentProvider {
    static class AmountControls {
        EditText editText;
        LinearLayout layout;
    }

    static class ItemControls {
        ListView listView;
        LinearLayout layout;
        ItemListAdapter adapter;
    }

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
                controls.editText.setText(Integer.toString(seekBar.getProgress()));
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
        controls.editText.setText(Integer.toString(seekBar.getProgress()));
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
        text.setText("/" + seekBar.getMax());
        textArea.addView(text);
        controls.layout.addView(textArea);

        return controls;
    }

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
}
