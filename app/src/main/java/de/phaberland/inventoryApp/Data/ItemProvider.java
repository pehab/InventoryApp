package de.phaberland.inventoryApp.Data;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.util.HashMap;

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
        Item newItem = new Item(name, unit);
        m_allItems.put(newItem.getM_id(), newItem);
        return newItem.getM_id();
    }

    public HashMap<Integer,Item> getAllItems() {
        return m_allItems;
    }

    int getNextId() {
        return m_allItems.size();
    }
}
