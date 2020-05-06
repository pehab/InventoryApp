/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.Data;

import java.util.HashMap;

/**
 * ItemList is the List containing Items and their respective amount within the list.
 * Every List has an id, by which it can be identified.
 * There are two prefined list ids:
 *  - 0 always is the id of the inventory list (INVENTORY_LIST_ID)
 *  - 1 always is the id of the shopping list (SHOPPING_LIST_ID)
 * the contents of the lists are saved as a HashMap with the item as key and amount as value
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 * @see Item
 */
public class ItemList {
    public static final int INVENTORY_LIST_ID = 0;
    public static final int SHOPPING_LIST_ID = 1;

    private int id;
    private HashMap<Item, Integer> m_content;

    /**
     * Constructs an instance of an ItemList.
     * This will set the id provided from the ListProvider or
     * -1 if bTemp is true and initialize the content with an empty map.
     * Temporary lists will not be registered with the ListProvider.
     * @param bTemp true if the ItemList instance is not only temporary
     */
    ItemList(boolean bTemp) {
        if(bTemp) {
            id = -1;
        } else {
            id = ListProvider.getInstance().getNextId();
        }

        m_content = new HashMap<>();
    }

    /**
     * Constuctor for ItemList creation without temporary argument, defaulting to false.
     * @see #ItemList(boolean)
     */
    ItemList() {
        this(false);
    }

    /**
     * Adds an item with the specified amount to the list.
     * If the item already exists in the list, the current amount
     * and the new amount are added.
     * At the end a check will be done weather to add the item to 
     * the shopping list.
     * @param item item to add to list
     * @param amount amount of item added to list
     * @see #checkAddToShopping(Item, int)
     */
    public void add(Item item, int amount) {
        if (hasItem(item)) {
            Integer currentAmount = m_content.get(item);
            if(currentAmount == null) {
                currentAmount = 0;
            }
            int newAmount = amount + currentAmount;
            
            m_content.put(item,newAmount);
        } else {
            m_content.put(item, amount);
        }
        checkAddToShopping(item, amount);
    }

    /**
     * Completely removes an item from the list
     * @param itemId id of the item to remove
     */
    public void remove(int itemId) {
        m_content.remove(ItemProvider.getInstance().getItemById(itemId));
    }

    /**
     * removes a certain amount of an item from the list.
     * If the item is on the list the current amount will be reduced
     * by the amount given. 
     * If the new amount is <= 0 the item will be removed from the list,
     * otherwise the items amount will be updated
     * At the end a check will be done weather to add the item to
     * the shopping list.
     * @param item the item to remove from
     * @param amount the amount to remove
     * @see #checkAddToShopping(Item, int)
     */
    public void remove(Item item, int amount) {
        if(hasItem(item)) {
            Integer currentAmount = m_content.get(item);
            if(currentAmount == null) {
                currentAmount = 0;
            }
            int newAmount = currentAmount - amount;

            if(newAmount <= 0) {
                m_content.remove(item);
            } else {
                m_content.put(item, newAmount);
            }
            checkAddToShopping(item, newAmount);
        }
    }

    /**
     * Check if the amount mapped to the item is smaller than the
     * critical value of the item. This is only done for items in
     * the inventory, which need to get added to the shopping list.
     * If the current amount is below the defined critical amount
     * the Item will be added to shopping list.
     * @param item the item to check
     * @param newAmount the new amount to check
     */
    private void checkAddToShopping(Item item, int newAmount) {
        if(newAmount <= item.getM_critValue() &&
                id == INVENTORY_LIST_ID) {
            if(ListProvider.getInstance().getListById(SHOPPING_LIST_ID).hasItem(item)) {
                return;
            }
            ListProvider.getInstance().getListById(SHOPPING_LIST_ID).add(item,0);
        }
    }

    /**
     * This is basically a wrapper for m_contents.containsKey to
     * be able to be accessed from outside.
     * @param item The item to check
     * @return true if the item is in the content, false otherwise
     */
    public boolean hasItem(Item item) {
        return m_content.containsKey(item);
    }

    /**
     * Returns the amount associated with the itemId in this list.
     * If the item is not present at all, -1 will be returned.
     * @param itemId id of the item to get amount for
     * @return amount of the item in the list, -1 if not in the list
     */
    public int getAmountForId(int itemId) {
        Item item = ItemProvider.getInstance().getItemById(itemId);
        if(hasItem(item)) {
            Integer tmp = m_content.get(item);
            if(tmp != null) {
                return tmp;
            }
        }
        return -1;
    }

    /////////////
    // GETTERS //
    /////////////

    int getId() {
        return id;
    }

    public HashMap<Item, Integer> getM_content() {
        return m_content;
    }
}
