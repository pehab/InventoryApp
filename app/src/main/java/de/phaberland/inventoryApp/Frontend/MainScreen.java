package de.phaberland.inventoryApp.Frontend;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import de.phaberland.inventoryApp.App.EventHandler;
import de.phaberland.inventoryApp.App.InventoryApp;
import de.phaberland.inventoryApp.Interfaces.EventCallback;
import de.phaberland.inventoryApp.R;

import java.util.HashMap;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

public class MainScreen extends AppCompatActivity implements EventCallback {
    private ScrollView m_list;
    private int m_listId;
    private InventoryApp m_app;
    private String m_filter;

    private AddToInventoryDialog m_addToInvDlg;
    private AddToShoppingDialog m_addToShopDlg;

    ////////////////////////
    // Activity Lifecycle //
    ////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // add swipe listener
        addSwipeListener(findViewById(R.id.mainLayout));

        // add filter change handler
        EditText filter = findViewById(R.id.filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                m_filter = s.toString();
                updateList();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected  void onStop() {
        super.onStop();
        deinit();
    }

    /////////////////
    // init/deinit //
    /////////////////

    private void init() {
        // initialize main app
        m_app = new InventoryApp(getApplicationContext());
        m_app.init();

        // set up initial layout
        m_list = findViewById(R.id.mainTable);
        m_listId = 0;
        m_filter = "";
        // activate inventory button
        Button button = findViewById(R.id.inventoryButton);
        button.setBackgroundColor(Color.CYAN);
        button.setActivated(false);
        // deactivate shopping button
        button = findViewById(R.id.shoppingButton);
        button.setBackgroundColor(Color.WHITE);
        button.setActivated(true);

        // show initial list
        updateList();
    }

    void deinit() {
        m_app.deinit();
    }

    ///////////////////////////
    // Handle Button Presses //
    ///////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipeListener(final View view) {
        if(view != null) {
            view.setOnTouchListener(new OnSwipeTouchListener(MainScreen.this) {
                public void onSwipeRight() {
                    inventoryButtonPressed(view);
                }
                public void onSwipeLeft() {
                    shoppingButtonPressed(view);
                }
            });
        }
    }

    public void addButtonPressed(View view) {
        if(m_listId == ItemList.INVENTORY_LIST_ID) {
            m_addToInvDlg = new AddToInventoryDialog(this);
            m_addToInvDlg.show(getSupportFragmentManager(), getString(R.string.tag_add_to_inv_dlg));
        } else {
            m_addToShopDlg = new AddToShoppingDialog(this);
            m_addToShopDlg.show(getSupportFragmentManager(), getString(R.string.tag_add_to_shop_dlg));
        }
    }

    /* button handling area Buttons in main screen are:
     *  - Settings: used to manage item/recipes list and hold the impressing
     *  - Inventory: activate inventory list
     *  - Shopping: activate shopping list
     */
    public void settingsButtonPressed(View view) {
        // Todo: create intent to switch to settings activity
        Toast.makeText(this.getApplicationContext(), R.string.toast_no_settings, Toast.LENGTH_SHORT).show();
    }

    public void inventoryButtonPressed(View view) {
        if(m_listId != 0) {
            // activate inventory button
            Button button = findViewById(R.id.inventoryButton);
            button.setBackgroundColor(Color.CYAN);
            button.setActivated(false);
            // deactivate shopping button
            button = findViewById(R.id.shoppingButton);
            button.setBackgroundColor(Color.WHITE);
            button.setActivated(true);
            //change list mode and update the list
            m_listId = ItemList.INVENTORY_LIST_ID;
            updateList();

            Toast.makeText(this.getApplicationContext(), (getString(R.string.toast_switch_inv)),Toast.LENGTH_SHORT).show();
        }
    }

    public void shoppingButtonPressed(View view) {
        if(m_listId != 1) {
            // activate shopping button
            Button button = findViewById(R.id.shoppingButton);
            button.setBackgroundColor(Color.CYAN);
            button.setActivated(false);
            // deactivate inventory button
            button = findViewById(R.id.inventoryButton);
            button.setBackgroundColor(Color.WHITE);
            button.setActivated(true);
            m_listId = ItemList.SHOPPING_LIST_ID;
            updateList();

            Toast.makeText(this.getApplicationContext(), (getString(R.string.toast_switch_shop)),Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////
    // List draws //
    ////////////////

    /* updateList
     * this function will get the list, that should be displayed from the app
     * depending on the mode we are in it will show the filtered lists
     */
    private void updateList() {
        ItemList listToDisplay = ListProvider.getInstance().getFilteredList(m_listId, m_filter);
        m_list.removeAllViews();

        addSwipeListener(m_list);

        if(listToDisplay == null || listToDisplay.getM_content().isEmpty()) {
            return;
        }

        TableLayout table = new TableLayout(this);

        TableLayout.LayoutParams lpTable = new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        table.setLayoutParams(lpTable);

        addSwipeListener(table);

        table.setStretchAllColumns(true);

        for (HashMap.Entry<Integer, Integer> entry : listToDisplay.getM_content().entrySet()) {
            TableRow tr = null;

            switch(m_listId) {
                case 0:
                    tr = createInventoryEntry( ItemProvider.getInstance().getItemById(entry.getKey()),entry.getValue());
                    break;
                case 1:
                    tr = createShoppingEntry( ItemProvider.getInstance().getItemById(entry.getKey()));
                    break;
                default:
                    // TODO: log an error
                    break;
            }
            table.addView(tr);
            addSwipeListener(tr);
        }
        m_list.addView(table);
    }

    /* createInventoryEntry
     * this function will create a view containing the item name, amount and a +/- button
     * to change the amount
     */
    private TableRow createInventoryEntry(Item item, int amount) {
        TableRow tr = new TableRow(this);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();
        TableRow.LayoutParams colParams = new TableRow.LayoutParams();

        rowParams.setMargins(0, 0, 0, 1);
        colParams.setMargins(0, 0, 1, 0);

        tr.setLayoutParams(rowParams);

        if(item.getM_critValue() > amount) {
            tr.setBackgroundColor(Color.RED);
        }

        // add name of item
        TextView textName= new TextView(this);
        textName.setText(item.getM_name());
        textName.setGravity(Gravity.CENTER);
        textName.setLayoutParams(colParams);
        textName.setPadding(3, 3, 3, 3);
        tr.addView(textName);

        // add amount of item
        TextView textAmount= new TextView(this);
        textAmount.setText(String.format(getResources().getConfiguration().locale,"%d", amount));
        textAmount.setGravity(Gravity.CENTER);
        textAmount.setLayoutParams(colParams);
        textAmount.setPadding(3, 3, 3, 3);
        tr.addView(textAmount);

        // add +/- button
        Button button = new Button(this);
        button.setText(R.string.button_add_remove);
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(colParams);
        button.setPadding(3, 3, 3, 3);
        EventHandler.EventHandlerParams params = new EventHandler.EventHandlerParams();
        params.m_mode = EventHandler.EventHandlerMode.INVENTORYLISTCLICK;
        params.m_callback = this;
        params.m_itemId = item.getM_id();
        params.m_activity = this;

        button.setOnClickListener(new EventHandler(params));
        tr.addView(button);

        return tr;
    }

    /* createShoppingEntry
     * this function will create a view containing the item name, an ok button, which will
     * add the default amount to the inventory and a button to set a specific amount
     */
    private TableRow createShoppingEntry(Item item) {
        TableRow tr = new TableRow(this);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();
        TableRow.LayoutParams colParams = new TableRow.LayoutParams();

        rowParams.setMargins(0, 0, 0, 1);
        colParams.setMargins(0, 0, 1, 0);

        tr.setLayoutParams(rowParams);

        // add name of item
        TextView textName= new TextView(this);
        textName.setText(item.getM_name());
        textName.setGravity(Gravity.CENTER);
        textName.setLayoutParams(colParams);
        textName.setPadding(3, 3, 3, 3);
        tr.addView(textName);

        // add remove button
        Button button = new Button(this);
        button.setText(R.string.button_remove);
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(colParams);
        button.setPadding(3, 3, 3, 3);
        EventHandler.EventHandlerParams params = new EventHandler.EventHandlerParams();
        params.m_mode = EventHandler.EventHandlerMode.SHOPPINGLISTREMOVECLICKED;
        params.m_callback = this;
        params.m_itemId = item.getM_id();
        params.m_activity = this;

        button.setOnClickListener(new EventHandler(params));
        tr.addView(button);

        // add add button
        Button button1 = new Button(this);
        button1.setText(R.string.button_add);
        button1.setGravity(Gravity.CENTER);
        button1.setLayoutParams(colParams);
        button1.setPadding(3, 3, 3, 3);
        EventHandler.EventHandlerParams params1 = new EventHandler.EventHandlerParams();
        params1.m_mode = EventHandler.EventHandlerMode.SHOPPINGLISTADDCLICK;
        params1.m_callback = this;
        params1.m_itemId = item.getM_id();
        params1.m_activity = this;

        button1.setOnClickListener(new EventHandler(params1));
        tr.addView(button1);

        return tr;
    }

    ////////////////////////////
    // EventCallback override //
    ////////////////////////////

    @Override
    public void update() {
        updateList();
    }

    @Override
    public void readAddToInvDlgAndUpdate() {
        m_app.addToList(m_listId, m_addToInvDlg.getItemId(), m_addToInvDlg.getAmount());
        m_addToInvDlg.dismiss();
        updateList();
    }

    @Override
    public void readAddToShoppingDlgAndUpdate() {
        m_app.addToList(m_listId, m_addToShopDlg.getItemId(), 0);
        m_addToShopDlg.dismiss();
        updateList();
    }
}
