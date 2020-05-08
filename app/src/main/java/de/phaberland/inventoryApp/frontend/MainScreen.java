/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.frontend;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Locale;

import de.phaberland.inventoryApp.app.EventHandler;
import de.phaberland.inventoryApp.app.InventoryApp;
import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.data.ListProvider;
import de.phaberland.inventoryApp.R;

/**
 * MainScreen is the main Frontend class.
 * It handles initialization of the backend,
 * creates the main layout and provides its
 * functionality, calling the needed dialogs.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class MainScreen extends AppCompatActivity implements View.OnClickListener {
    private ScrollView m_list;
    private InventoryApp m_app;
    private String m_filter;

    private AddToInventoryDialog m_addToInvDlg;
    private AddToShoppingDialog m_addToShopDlg;

    ////////////////////////
    // Activity Lifecycle //
    ////////////////////////

    /**
     * onCreate overrides the main creation method of Activity.
     * It loads the main layout, adds Swipe listener to it sets
     * the TextChangeListener for the List filter.
     * @param savedInstanceState an instance of a Bundle, not needed here
     * @see OnSwipeTouchListener
     */
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

    /**
     * onStart is called when the activity get shown
     * on display. This calls its parent function and init
     * @see #init()
     */
    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    /**
     * onStop gets called, when the activity get hidden,
     * like turning of the screen, or putting it in the
     * background. This calls its parent function and deinit
     * @see #deinit()
     */
    @Override
    protected  void onStop() {
        super.onStop();
        deinit();
    }

    /////////////////
    // init/deinit //
    /////////////////

    /**
     * initializes members of the MainScreen. Calls
     * init from InventoryApp and sets up button
     * states and layouts.
     * If no items are specified yet it will also
     * load some predefined items.
     * @see InventoryApp#init()
     * @see #setUpInitialButtons()
     * @see #prepareInitialItems()
     */
    private void init() {
        // initialize main app
        m_app = new InventoryApp(this);
        m_app.init();

        // possibly load initial item list
        if(ItemProvider.getInstance().getAllItems().isEmpty()) {
            prepareInitialItems();
        }

        // initialize members
        m_list = findViewById(R.id.mainTable);
        m_filter = "";

        // set this as onClickListeners
        Button button = findViewById(R.id.addItemButton);
        button.setOnClickListener(this);
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);

        //set up initial buttons
        setUpInitialButtons();

        // show initial list
        updateList();
    }

    /**
     * reads from the InventoryApp, which button currently
     * is active and sets inventory and shopping buttons
     * accordingly.
     */
    private void setUpInitialButtons() {
        Button activeButton;
        Button inactiveButton;
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
        activeButton.setOnClickListener(this);

        inactiveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorInactiv));
        inactiveButton.setActivated(false);
        inactiveButton.setOnClickListener(this);
    }

    /**
     * creates some initial items for the item list,
     * which are read from R.array.initial_items resource.
     */
    private void prepareInitialItems() {
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

    /**
     * calls deinit from InventoryApp
     * @see InventoryApp#deinit()
     */
    private void deinit() {
        m_app.deinit();
    }

    ///////////////////////////
    // Handle Button Presses //
    ///////////////////////////

    /**
     * handles when the add button was pressed.
     * Depending on the currently active list
     * either AddToInventoryDialog or AddToShoppingDialog
     * will be called.
     * @see AddToInventoryDialog
     * @see AddToShoppingDialog
     */
    private void addButtonPressed() {
        if(m_app.getActiveList() == ItemList.INVENTORY_LIST_ID) {
            m_addToInvDlg = new AddToInventoryDialog(this);
            m_addToInvDlg.show(getSupportFragmentManager(), getString(R.string.tag_add_to_inv_dlg));
        } else {
            m_addToShopDlg = new AddToShoppingDialog(this);
            m_addToShopDlg.show(getSupportFragmentManager(), getString(R.string.tag_add_to_shop_dlg));
        }
    }

    /**
     * handles a press of the Settings button.
     * This will call a SettingsDialog.
     */
    private void settingsButtonPressed() {
        // Todo: create SettingsDialog
        m_app.importCsv();

        Toast.makeText(this.getApplicationContext(), R.string.toast_no_settings, Toast.LENGTH_SHORT).show();
    }

    /**
     * handles a press of the inventory button.
     * If the current list is not Inventory,
     * the marking of the buttons will get switched
     * and the active list will be set to Inventory
     */
    private void inventoryButtonPressed() {
        if(m_app.getActiveList() != ItemList.INVENTORY_LIST_ID) {
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

    /**
     * handles a press of the shopping button.
     * If the current list is not Shopping,
     * the marking of the buttons will get switched
     * and the active list will be set to Shopping
     */
    private void shoppingButtonPressed() {
        if(m_app.getActiveList() != ItemList.SHOPPING_LIST_ID) {
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

    /**
     * will add an isntance of OnSwipeListener to
     * the provided view.
     * @param view the view to which to add the listener
     */
    private void addSwipeListener(final View view) {
        if(view != null) {
            view.setOnTouchListener(new OnSwipeTouchListener(MainScreen.this) {
                public void onSwipeRight() {
                    inventoryButtonPressed();
                }
                public void onSwipeLeft() {
                    shoppingButtonPressed();
                }
            });
        }
    }

    /**
     * create a TextView with given name.
     * Sets layout and swipeListener.
     * @param name name to display in the textfield
     * @return the created textfield
     */
    private View createTextField(String name) {
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

    /**
     * Creates a button with the given text
     * adding the Eventhandler to the button
     * and setting the layout.
     * @param mode Eventhandler mode to run in
     * @param itemId id of the item related to the button
     * @param txt text to display on the button
     * @return the created button
     * @see EventHandler
     */
    private View createButton(EventHandler.EventHandlerMode mode, int itemId, String txt) {
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

    /**
     * gets the list of items to display filtered
     * for m_filter from the ListProvider and calls the
     * function to set up the table.
     * @see #createTable(ItemList)
     */
    private void updateList() {
        ItemList listToDisplay = ListProvider.getInstance().getFilteredList(m_app.getActiveList(), m_filter);
        m_list.removeAllViews();

        addSwipeListener(m_list);

        if(listToDisplay == null || listToDisplay.getM_content().isEmpty()) {
            return;
        }

        createTable(listToDisplay);
    }

    /**
     * creates a table displaying the provided list.
     * depending on the active list, for each entry
     * createInventoryEntry or createShoppingEntry
     * will be called.
     * @param listToDisplay the list to show in the table
     * @see #createInventoryEntry(Item, int) 
     * @see #createShoppingEntry(Item)
     */
    private void createTable(ItemList listToDisplay) {
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
                    break;
            }
            table.addView(tr);
            addSwipeListener(tr);
        }
        m_list.addView(table);
    }

    /**
     * creates a TableRow containing Name of an item,
     * the current amount of the item in the inventory
     * list and button to be able to remove some amount
     * from the inventory.
     * @param item the item to display in the table entry
     * @param amount amount of the item available in inventory
     * @return a TableRow containing the entry information
     * @see #createButton(EventHandler.EventHandlerMode, int, String) 
     * @see #createTextField(String)
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
        tr.addView(createTextField(item.getM_name() + " (" + item.getM_unit().toString() + ")"));
        // add amount of item
        tr.addView(createTextField(String.format(Locale.getDefault(),"%d", amount)));
        // add +/- button
        tr.addView(createButton(EventHandler.EventHandlerMode.INVENTORYLISTCLICK, item.getM_id(), getString(R.string.button_add_remove)));

        getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        return tr;
    }

    /**
     * creates a TableRow containing Name of an item,
     * a button to remove the item from the shopping list
     * and a button to add some amount to the inventory.
     * @param item the item to display in the table entry
     * @return a TableRow containing the entry information
     * @see #createButton(EventHandler.EventHandlerMode, int, String)
     * @see #createTextField(String)
     */
    private TableRow createShoppingEntry(Item item) {
        TableRow tr = new TableRow(this);

        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams();
        rowParams.setMargins(0, 0, 0, 1);
        tr.setLayoutParams(rowParams);

        // add name of item
        tr.addView(createTextField(item.getM_name()));
        // add remove button
        tr.addView(createButton(EventHandler.EventHandlerMode.SHOPPINGLISTREMOVECLICK, item.getM_id(), getString(R.string.button_remove)));
        // add add button
        tr.addView(createButton(EventHandler.EventHandlerMode.SHOPPINGLISTADDCLICK, item.getM_id(), getString(R.string.button_add)));

        return tr;
    }

    /////////////////////////////////////
    // Eventhandler callback functions //
    /////////////////////////////////////

    /**
     * public function to call updateList
     * @see #updateList()
     */
    public void update() {
        updateList();
    }

    /**
     * add the information aquired from AddToInventoryDialog
     * to the ListProvider and triggers an update of the list
     * @see AddToInventoryDialog
     * @see #updateList()
     */
    public void readAddToInvDlgAndUpdate() {
        ListProvider.getInstance().getListById(m_app.getActiveList())
                .add(ItemProvider.getInstance().getItemById(m_addToInvDlg.getItemId()), m_addToInvDlg.getAmount());
        m_addToInvDlg.dismiss();
        updateList();
    }

    /**
     * add the information aquired from AddToShoppingDialog
     * to the ListProvider and triggers an update of the list
     * @see AddToShoppingDialog
     * @see #updateList()
     */
    public void readAddToShoppingDlgAndUpdate() {
        ListProvider.getInstance().getListById(m_app.getActiveList())
                .add(ItemProvider.getInstance().getItemById(m_addToShopDlg.getItemId()), 0);
        m_addToShopDlg.dismiss();
        updateList();
    }

    //////////////////////////////
    // OnClickListener Override //
    //////////////////////////////

    /**
     * depending on the id of the button clicked
     * calls the corresponding click handling function
     * @param v the button that was clicked
     * @see #addButtonPressed()
     * @see #inventoryButtonPressed()
     * @see #shoppingButtonPressed()
     * @see #settingsButtonPressed()
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addItemButton: addButtonPressed(); break;
            case R.id.inventoryButton: inventoryButtonPressed(); break;
            case R.id.shoppingButton: shoppingButtonPressed(); break;
            case R.id.settingsButton: settingsButtonPressed(); break;
        }
    }
}
