/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */
package de.phaberland.inventoryApp.app;

import android.content.Context;
import android.view.View;

import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.data.ListProvider;
import de.phaberland.inventoryApp.frontend.AddFromShoppingDialog;
import de.phaberland.inventoryApp.frontend.DialogFragmentProvider;
import de.phaberland.inventoryApp.frontend.MainScreen;
import de.phaberland.inventoryApp.frontend.RemoveFromInventoryDialog;
import de.phaberland.inventoryApp.interfaces.YesNoCallback;
import de.phaberland.inventoryApp.R;

/**
 * Eventhandler is to handle button clicks within lists.
 * depending on the mode different functions will be used to initialize the correct dialogs to spawn
 * and set them up correctly.
 * EventHandlerParams will be used to transfer the necessary information to the EventHandlerInstance
 * which will than be registered as onClick Listeners for the buttons.
 * @see android.view.View.OnClickListener
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class EventHandler implements View.OnClickListener, YesNoCallback {

    /**
     * Eventhandler Modes enum is used to decide where and which button exactly was pressed.
     *  - +/- button in inventory will have the INVENTORYLISTCLICKED mode
     *  - remove button in shopping list will have the SHOPPINGLISTREMOVECLICKED mode
     *  - add button in shopping list will have the SHOPPINGLISTADDCLICK mode
     */
    public enum EventHandlerMode {
        INVENTORYLISTCLICK,
        SHOPPINGLISTREMOVECLICK,
        SHOPPINGLISTADDCLICK,
    }

    /**
     * EventHandlerParams are used pass information to the EventHandler
     *  - EventHandlerMode mode in which the EventHandler is running
     *  - int itemId is the id of the item in the list, that was clicked
     *  - MainScreen we also need an instance of the MainScreen, which should
     *  implement EventCallback, so updates can be triggered and AppCompatActivity
     *  to be able to use contexts and compatibility functions
     * @see EventHandlerMode
     * @see MainScreen
     */
    public static class EventHandlerParams {
        public EventHandlerMode m_mode;
        public int m_itemId;
        public MainScreen m_mainScreen;
    }

    private final EventHandlerParams m_params;

    /**
     * constructs an instance of EventHandler which can than be used as
     * OnClickListener in programmatically created buttons
     * @param params an instance of EventHandler params holding information about the Button
     *
     * @see EventHandlerParams
     */
    public EventHandler(EventHandlerParams params) {
        m_params = params;
    }

    //////////////////////////////
    // OnClickListener override //
    //////////////////////////////

    /**
     * Checks the mode of the EventHandler and calls the appropriate function
     * to process the click further.
     *
     * @param v the view on which the onClick happened,currently not need
     *
     * @see EventHandlerMode
     */
    @Override
    public void onClick(View v) {
        switch (m_params.m_mode) {
            case INVENTORYLISTCLICK: onInventoryListButtonClicked(); break;
            case SHOPPINGLISTREMOVECLICK: onShoppingButtonRemoveClicked(); break;
            case SHOPPINGLISTADDCLICK: onShoppingButtonAddClicked(); break;
        }
    }

    ////////////////////////////
    // Yes/No Dialog Callback //
    ////////////////////////////

    /**
     * Handles positive button press of SimpleYesNoDialog. 
     * This is called when an item gets removed from the shopping list.
     * So after pressing yes the item will get removed from the shopping 
     * list and the mainScreen need to be updated
     * @see DialogFragmentProvider#createSimpleYesNoDialog(String, YesNoCallback, Context) 
     */
    @Override
    public void yesClicked() {
        ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID).remove(m_params.m_itemId);
        if(m_params.m_mainScreen != null) {
            m_params.m_mainScreen.update();
        }
    }

    ///////////////////////////////////
    // ACTUAL BUTTON PRESS FUNCTIONS //
    ///////////////////////////////////

    /**
     * Gets called, when the +/- button in the inventory gets clicked.
     * The function will check if an item exists with the id specified
     * in the EventHandlerParams and spawn a RemoveFromInventoryDialog
     * when the item is existing.
     * @see RemoveFromInventoryDialog
     */
    private void onInventoryListButtonClicked() {
        Item item = ItemProvider.getInstance().getItemById(m_params.m_itemId);
        if(item == null) {
            return;
        }
        RemoveFromInventoryDialog dlg = new RemoveFromInventoryDialog(m_params.m_mainScreen, m_params.m_itemId);
        dlg.show(m_params.m_mainScreen.getSupportFragmentManager(), m_params.m_mainScreen.getString(R.string.tag_remove_item_dlg));
    }

    /**
     * Gets called, when the Remove button in the ShoppingList is clicked.
     * The function will check if an item exists with the id specified
     * in the EventHandlerParams and spawn a SimpleYesNo Dialog, making sure
     * the user really wants to remove the item.
     * @see #yesClicked()
     */
    private void onShoppingButtonRemoveClicked() {
        Item item = ItemProvider.getInstance().getItemById(m_params.m_itemId);
        if(item == null) {
            return;
        }
        DialogFragmentProvider.createSimpleYesNoDialog(
                m_params.m_mainScreen.getString(R.string.msg_are_your_sure),
                this,
                m_params.m_mainScreen
        );

    }

    /**
     * Gets called, when the +/- button in the inventory gets clicked.
     * The function will check if an item exists with the id specified
     * in the EventHandlerParams and spawn a AddFromShoppingDialog
     * when the item is existing.
     * @see AddFromShoppingDialog
     */
    private void onShoppingButtonAddClicked() {
        Item item = ItemProvider.getInstance().getItemById(m_params.m_itemId);
        if(item == null) {
            return;
        }
        AddFromShoppingDialog dlg = new AddFromShoppingDialog(m_params.m_mainScreen, m_params.m_itemId);
        dlg.show(m_params.m_mainScreen.getSupportFragmentManager(), m_params.m_mainScreen.getString(R.string.tag_add_item_dlg));
    }
}
