/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.data;

import android.annotation.SuppressLint;

import java.util.HashMap;

/**
 * ListProvider is a singleton instance to be used the keep track
 * of all lists used in the application.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class ListProvider {
    @SuppressLint("StaticFieldLeak")
    private static ListProvider instance = null;
    private HashMap<Integer,ItemList> m_allLists;

    /**
     * standard singleton style getInstance.
     * @return the static instance of ListProvider
     */
    public static ListProvider getInstance() {
        if(instance == null) {
            instance = new ListProvider();
        }
        return instance;
    }

    /**
     * Initializes the internal list of lists.
     * Should always be called before using ListProvider.
     * m_allItems will be set to an empty HashMap.
     * The function will also add the two initial Lists,
     * that always need to be available,
     * which is Inventory list (id=0) and Shopping list (id=1)
     */
    public void init() {
        m_allLists = new HashMap<>();

        if(m_allLists.isEmpty()) {
            addList(); // id = 0, we need to initialize a inventory list
            addList(); // id = 1, we need to initialize a shopping list
        }
    }

    /**
     * clears all lists and initializes Inventory and
     * Shopping list as empty lists.
     */
    public void clear() {
        m_allLists.clear();
        // we always want to have inventory and shopping list.
        addList();
        addList();
    }

    private void addList() {
        ItemList list = new ItemList();
        m_allLists.put(list.getId(), list);
    }

    /**
     * Will check if a list with the provided id is available
     * and return the list with that id. If there is no list
     * with that id, null will be returned.
     * @param id id of the list to look for
     * @return the list with the id or null if there is none
     */
    public ItemList getListById(int id) {
        if(m_allLists.containsKey(id)) {
            return m_allLists.get(id);
        }
        else {
            return null;
        }
    }

    /**
     * Will create a temporary list of items filtered by a string.
     * This function will filter the contend of the ItemList by the
     * item names. The comparison will be case insensitive.
     * Items which names contain the filter text will be added to
     * the resulting list.
     * @param id id of the list to filter
     * @param filter String specifying the filter
     * @return a temporary ItemList containing all Items, where the filter applies
     */
    public ItemList getFilteredList(int id, String filter) {
        if(filter.isEmpty()) {
            return getListById(id);
        }

        HashMap<Item, Integer> list = getListById(id).getM_content();
        ItemList filteredList = new ItemList(true);
        for (HashMap.Entry<Item, Integer> entry : list.entrySet()) {
            Item item = entry.getKey();
            if(item != null) {
                String name =  item.getM_name().toLowerCase();
                if(name.contains(filter.toLowerCase())) {
                    filteredList.add(entry.getKey(), entry.getValue());
                }
            }
        }
        return filteredList;
    }

    int getNextId() {
        return m_allLists.size();
    }
}
