package de.phaberland.inventoryApp.Data;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.phaberland.inventoryApp.App.Serializer;

public class ItemProvider{
    @SuppressLint("StaticFieldLeak")
    private static ItemProvider instance = null;
    private Activity m_activity;
    private HashMap<Integer,Item> m_allItems;

    public static ItemProvider getInstance() {
        if(instance == null) {
            instance = new ItemProvider();
        }
        return instance;
    }

    public static void init(Activity activity) {
        if(instance == null) {
            instance = new ItemProvider();
        }

        instance.m_activity = activity;

        Serializer ser = new Serializer(instance.m_activity);
        instance.m_allItems = ser.readAllItems();
        instance.sortItems();
    }

    public static void deinit() {
        Serializer ser = new Serializer(instance.m_activity);
        ser.writeAllItems(instance.m_allItems);
    }

    public void clear() {
        m_allItems.clear();
    }

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

    public Item getItemById(int id) {
        if(!m_allItems.containsKey(id)) {
            return null;
        }
        return m_allItems.get(id);
    }

    public int addItem(String name, Item.UNIT unit) {
        return addItem(name, unit, true);
    }

    public int addItem(String name, Item.UNIT unit, boolean sortList) {
        Item newItem = new Item(name, unit);
        m_allItems.put(newItem.getM_id(), newItem);
        if(sortList) {
            sortItems();
        }
        return newItem.getM_id();
    }

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

        int idCount = 0;
        m_allItems.clear();
        for(HashMap.Entry<Integer, Item> entry : listOfEntries){
            m_allItems.put(idCount, entry.getValue());
            entry.getValue().setM_id(idCount);
            idCount++;
        }
    }

    public HashMap<Integer,Item> getAllItems() {
        return m_allItems;
    }

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

    int getNextId() {
        return m_allItems.size();
    }
}
