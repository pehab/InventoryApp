/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.Interfaces;

/**
 * Classes using the EventHandler should also implement
 * the EventCallback interface.
 * This is used as callback from the dialogs spawned from within
 * the EventHandler, so on interaction with the lists in the mainscreen.
 * @see de.phaberland.inventoryApp.App.EventHandler
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public interface EventCallback {
    /**
     * update will be called by AddFromShoppingDialog and
     * RemoveFromInventoryDialog, once they are done and
     * everything is handled to update the inventory lists.
     * @see de.phaberland.inventoryApp.Frontend.AddFromShoppingDialog
     * @see de.phaberland.inventoryApp.Frontend.RemoveFromInventoryDialog
     */
    void update();

    /**
     * this will be called from the AddToInventoryDialog.
     * A class implementing this should read itemId and
     * Amount from the dialog and handle the update of values
     * @see de.phaberland.inventoryApp.Frontend.AddToInventoryDialog
     */
    void readAddToInvDlgAndUpdate();

    /**
     * this will be called from the AddToShoppingDialog.
     * A class implementing this should read itemId from
     * the dialog and handle the update
     * @see de.phaberland.inventoryApp.Frontend.AddToShoppingDialog
     */
    void readAddToShoppingDlgAndUpdate();
}
