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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

import de.phaberland.inventoryApp.App.EventHandler;
import de.phaberland.inventoryApp.App.InventoryApp;
import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;
import de.phaberland.inventoryApp.Interfaces.EventCallback;
import de.phaberland.inventoryApp.R;

public class MainScreen extends AppCompatActivity implements EventCallback {
    private ScrollView m_list;
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
        m_app = new InventoryApp(this);
        m_app.init();

        // possibly load initial item list
        if(ItemProvider.getInstance().getAllItems().isEmpty()) {
            String[] initialItems = getResources().getStringArray(R.array.initial_items);
            for(String itemName: initialItems) {
                String unitIndex =  itemName.substring(0,1);
                itemName =  itemName.substring(1);
                Item.UNIT unit;
                switch (unitIndex) {
                    case "0": unit = Item.UNIT.GRAMM; break;
                    case "1": unit = Item.UNIT.MILILITER; break;
                    default: unit = Item.UNIT.PIECE; break;
                }
                ItemProvider.getInstance().addItem(itemName, unit, false);
            }
            ItemProvider.getInstance().sortItems();
        }

        // set up initial layout
        m_list = findViewById(R.id.mainTable);
        m_filter = "";

        Button activeButton = null;
        Button inactiveButton = null;
        if(m_app.getActiveList() == ItemList.INVENTORY_LIST_ID) {
            // activate inventory button
            activeButton = findViewById(R.id.inventoryButton);
            // deactivate shopping button
            inactiveButton = findViewById(R.id.shoppingButton);
        } else {//if(m_app.getActiveList() == ItemList.SHOPPING_LIST_ID) {
            // activate shopping button
            activeButton = findViewById(R.id.shoppingButton);
            // deactivate inventory button
            inactiveButton = findViewById(R.id.inventoryButton);
        }
        activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        activeButton.setActivated(true);

        inactiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorInactiv));
        inactiveButton.setActivated(false);

        // show initial list
        updateList();
    }

    void deinit() {
        m_app.deinit();
    }

    ///////////////////////////
    // Handle Button Presses //
    ///////////////////////////

    public void addButtonPressed(View view) {
        if(m_app.getActiveList() == ItemList.INVENTORY_LIST_ID) {
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
        m_app.importCsv();

        Toast.makeText(this.getApplicationContext(), R.string.toast_no_settings, Toast.LENGTH_SHORT).show();
    }

    public void inventoryButtonPressed(View view) {
        if(m_app.getActiveList() != 0) {
            // activate inventory button
            Button button = findViewById(R.id.inventoryButton);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            button.setActivated(false);
            // deactivate shopping button
            button = findViewById(R.id.shoppingButton);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorInactiv));
            button.setActivated(true);
            //change list mode and update the list
            m_app.setActiveList(ItemList.INVENTORY_LIST_ID);
            updateList();

            Toast.makeText(this.getApplicationContext(), (getString(R.string.toast_switch_inv)),Toast.LENGTH_SHORT).show();
        }
    }

    public void shoppingButtonPressed(View view) {
        if(m_app.getActiveList() != 1) {
            // activate shopping button
            Button button = findViewById(R.id.shoppingButton);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            button.setActivated(false);
            // deactivate inventory button
            button = findViewById(R.id.inventoryButton);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorInactiv));
            button.setActivated(true);
            m_app.setActiveList(ItemList.SHOPPING_LIST_ID);
            updateList();

            Toast.makeText(this.getApplicationContext(), (getString(R.string.toast_switch_shop)),Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////
    // List draws //
    ////////////////
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

    View addTextField(String name) {
        TableRow.LayoutParams colParams = new TableRow.LayoutParams();
        colParams.setMargins(0, 0, 1, 0);

        TextView textField= new TextView(this);
        textField.setText(name);
        textField.setGravity(Gravity.CENTER);
        textField.setLayoutParams(colParams);
        textField.setPadding(3, 3, 3, 3);
        addSwipeListener(textField);
        return textField;
    }

    View addButton(EventHandler.EventHandlerMode mode, int itemId, String txt) {
        TableRow.LayoutParams colParams = new TableRow.LayoutParams();
        colParams.setMargins(0, 0, 1, 0);

        Button button = new Button(this);
        button.setText(txt);
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(colParams);
        button.setPadding(3, 3, 3, 3);
        EventHandler.EventHandlerParams params = new EventHandler.EventHandlerParams();
        params.m_mode = mode;
        params.m_mainScreen = this;
        params.m_itemId = itemId;

        button.setOnClickListener(new EventHandler(params));
        addSwipeListener(button);
        return button;
    }

    /* updateList
     * this function will get the list, that should be displayed from the app
     * depending on the mode we are in it will show the filtered lists
     */
    private void updateList() {
        ItemList listToDisplay = ListProvider.getInstance().getFilteredList(m_app.getActiveList(), m_filter);
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

        for (HashMap.Entry<Item, Integer> entry : listToDisplay.getM_content().entrySet()) {
            TableRow tr = null;

            switch(m_app.getActiveList()) {
                case 0:
                    tr = createInventoryEntry( entry.getKey(),entry.getValue());
                    break;
                case 1:
                    tr = createShoppingEntry( entry.getKey());
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

        rowParams.setMargins(0, 0, 0, 1);

        tr.setLayoutParams(rowParams);

        if(item.getM_critValue() >= amount) {
            tr.setBackgroundColor(Color.RED);
        }

        // add name of item
        tr.addView(addTextField(item.getM_name() +
                " (" + item.getM_unit().toString() + ")"));

        // add amount of item
        tr.addView(addTextField(String.format(getResources().getConfiguration().locale,"%d", amount)));

        // add +/- button
        tr.addView(addButton(EventHandler.EventHandlerMode.INVENTORYLISTCLICK, item.getM_id(), getString(R.string.button_add_remove)));

        return tr;
    }

    /* createShoppingEntry
     * this function will create a view containing the item name, an ok button, which will
     * add the default amount to the inventory and a button to set a specific amount
     */
    private TableRow createShoppingEntry(Item item) {
        TableRow tr = new TableRow(this);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();

        rowParams.setMargins(0, 0, 0, 1);

        tr.setLayoutParams(rowParams);

        // add name of item
        tr.addView(addTextField(item.getM_name()));

        // add remove button
        tr.addView(addButton(EventHandler.EventHandlerMode.SHOPPINGLISTREMOVECLICK, item.getM_id(), getString(R.string.button_remove)));

        // add add button
        tr.addView(addButton(EventHandler.EventHandlerMode.SHOPPINGLISTADDCLICK, item.getM_id(), getString(R.string.button_add)));

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
        ListProvider.getInstance().getListById(m_app.getActiveList())
                .add(ItemProvider.getInstance().getItemById(m_addToInvDlg.getItemId()), m_addToInvDlg.getAmount());
        m_addToInvDlg.dismiss();
        updateList();
    }

    @Override
    public void readAddToShoppingDlgAndUpdate() {
        ListProvider.getInstance().getListById(m_app.getActiveList())
                .add(ItemProvider.getInstance().getItemById(m_addToShopDlg.getItemId()), 0);
        m_addToShopDlg.dismiss();
        updateList();
    }
}
