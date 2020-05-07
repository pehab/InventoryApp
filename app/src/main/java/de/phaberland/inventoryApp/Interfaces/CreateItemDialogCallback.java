/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */
package de.phaberland.inventoryApp.Interfaces;

/**
 * CreateItemDialogCallback interface must implement the update method
 * Classes that want to be able to handle the DialogCallbacks.
 * This is used for example by the createItemDialog.
 * @see de.phaberland.inventoryApp.Frontend.CreateItemDialog
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public interface CreateItemDialogCallback {
    /**
     * Will be called by after a new Item was created,
     * providing the id of the newly created item.
     * @param newItemId id of the new item
     */
    public void update(int newItemId);
}
