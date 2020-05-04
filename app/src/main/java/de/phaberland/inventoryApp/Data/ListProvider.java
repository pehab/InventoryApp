package de.phaberland.inventoryApp.Data;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.util.HashMap;

import de.phaberland.inventoryApp.App.Serializer;

public class ListProvider {
    @SuppressLint("StaticFieldLeak")
    private static ListProvider instance = null;
    private Activity m_activity;
    private HashMap<Integer,ItemList> m_allLists;

    public static ListProvider getInstance() {
        if(instance == null) {
            instance = new ListProvider();
        }
        return instance;
    }

    public static void init(Activity activity) {
        if(instance == null) {
            instance = new ListProvider();
        }

        instance.m_activity = activity;
        instance.m_allLists = new HashMap<>();

        Serializer ser = new Serializer(instance.m_activity);

        instance.m_allLists = ser.readLists();
        if(instance.m_allLists.isEmpty()) {
            instance.addList(new ItemList()); // id = 0, we need to initialize a inventory list
            instance.addList(new ItemList()); // id = 1, we need to initialize a shopping list
        }
    }

    public static void deinit() {
        Serializer ser = new Serializer(instance.m_activity);

        ser.writeLists(instance.m_allLists);
    }

    public void clear() {
        m_allLists.clear();
        // we always want to have inventory and shopping list.
        addList(new ItemList());
        addList(new ItemList());
    }

    public ItemList getListById(int id) {
        if(m_allLists.containsKey(id)) {
            return m_allLists.get(id);
        }
        else {
            return null;
        }
    }

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

    private void addList(ItemList list) {
        m_allLists.put(list.getId(), list);
    }

    int getNextId() {
        return m_allLists.size();
    }
}
