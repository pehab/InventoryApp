/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * ItemProvider is a singleton instance to be used the keep track
 * of all items used in the application.
 * Basic functions are keeping track of unique items,
 * finding items, adding newly created items and sorting.
 *
 * The ItemProvider should be initialized with a Map of items
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class ItemProvider{

    private static ItemProvider instance = null;
    private HashMap<Integer,Item> m_allItems;

    /**
     * standard singleton style getInstance.
     * @return the static instance of ItemProvider
     */
    public static ItemProvider getInstance() {
        if(instance == null) {
            instance = new ItemProvider();
        }
        return instance;
    }

    /**
     * init should be called before using the ItemProvider
     * to initialize the available items within the application.
     *
     * The function initializes m_allItems with an empty HashMap
     */
    public void init() {
        m_allItems = new HashMap<>();
    }

    public void clear() {
        m_allItems.clear();
    }

    /**
     * Calls addItem function with sortList parameter set to true.
     * @param name name of the item
     * @param unit unit of the item
     * @return id of the newly created item
     * @see #addItem(String, Item.UNIT, boolean)
     */
    public int addItem(String name, Item.UNIT unit) {
        return addItem(name, unit, true);
    }

    /**
     * Creates a new Item with the given name und unit.
     * The created item will be added to m_allItems.
     * If sortList is true, sortItems will be called.
     * Finally the id of the newly created item will be returned.
     *
     * You should call findExistingItem, before calling addItem,
     * to make sure you do not replace an already existing item.
     * @param name name of the item to create
     * @param unit unit if the item to create
     * @param sortList should the list be sorted at the end?
     * @return the id of the newly created item.
     * @see #sortItems()
     */
    public int addItem(String name, Item.UNIT unit, boolean sortList) {
        Item newItem = new Item(name, unit);
        m_allItems.put(newItem.getM_id(), newItem);
        if(sortList) {
            sortItems();
        }
        return newItem.getM_id();
    }

    /**
     * Sorts m_allItems alphabetically by name of
     * the items.
     */
    public void sortItems() {
        Comparator<HashMap.Entry<Integer, Item>> valueComparator = new Comparator<HashMap.Entry<Integer,Item>>() {

            @Override
            public int compare(HashMap.Entry<Integer, Item> e1, HashMap.Entry<Integer, Item> e2) {
                String v1 = e1.getValue().getM_name();
                String v2 = e2.getValue().getM_name();
                return v1.compareTo(v2);
            }
        };
        Set<HashMap.Entry<Integer, Item>> entries = m_allItems.entrySet();

        List<HashMap.Entry<Integer, Item>> listOfEntries = new ArrayList<>(entries);

        Collections.sort(listOfEntries, valueComparator);

        m_allItems.clear();
        for(HashMap.Entry<Integer, Item> entry : listOfEntries){
            entry.getValue().setM_id(ItemProvider.getInstance().getNextId());
            m_allItems.put(ItemProvider.getInstance().getNextId(), entry.getValue());
        }
    }

    /////////////
    // GETTERS //
    /////////////

    /**
     * Returns the id of an item identified by name and unit.
     * If no item with the specified name and unit is existing
     * -1 will be returned.
     * @param name name of the item to search
     * @param unit unit of the item to search
     * @return id of the item, or -1 if none is found
     */
    public int findExistingItem(String name, Item.UNIT unit) {
        int id = -1;
        for (java.util.Map.Entry<Integer, Item> integerItemEntry : m_allItems.entrySet()) {
            Item tmp = (Item) ((HashMap.Entry) integerItemEntry).getValue();
            if (tmp.getM_unit().equals(unit) && tmp.getM_name().equals(name)) {
                id = tmp.getM_id();
                break;
            }
        }
        return id;
    }

    /**
     * Returns the item identified by the given id.
     * If no item with the provided id is registered with the
     * ItemProvider null will be returned.
     * @param id id if the item return
     * @return an item identified by the given id, null if no item is available
     */
    public Item getItemById(int id) {
        if(!m_allItems.containsKey(id)) {
            return null;
        }
        return m_allItems.get(id);
    }

    /**
     * Will create a temporary list of items filtered by a string.
     * This function will filter m_allItems by the item names. The
     * comparison will be case insensitive. Items which names contain
     * the filter text will be added to the resulting list.
     * @param filter String specifying the filter
     * @return a HashMap containing all Items, where the filter applies
     */
    public HashMap<Integer, Item> getAllItemsFiltered(String filter) {
        if(filter.isEmpty()){
            return getAllItems();
        }
        HashMap<Integer,Item> filteredList = new HashMap<>();
        for (HashMap.Entry<Integer,Item> entry : m_allItems.entrySet()) {
            Item item = entry.getValue();
            if(item != null) {
                String name =  item.getM_name().toLowerCase();
                if(name.contains(filter.toLowerCase())) {
                    filteredList.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return filteredList;
    }

    public HashMap<Integer,Item> getAllItems() {
        return m_allItems;
    }

    int getNextId() {
        return m_allItems.size();
    }
}
