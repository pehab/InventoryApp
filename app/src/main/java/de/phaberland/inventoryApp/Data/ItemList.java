package de.phaberland.inventoryApp.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ItemList implements Serializable {
    public static final int INVENTORY_LIST_ID = 0;
    public static final int SHOPPING_LIST_ID = 1;

    private int id;
    //private String m_name;
    private HashMap<Item, Integer> m_content;

    ItemList(boolean bTemp) {
        if(bTemp) {
            id = -1;
        } else {
            id = ListProvider.getInstance().getNextId();
        }

        /*if(id == 0) {
            m_name = "Inventory";
        }
        else if(id == 1) {
            m_name = "Shopping";
        }*/

        m_content = new HashMap<>();
    }

    ItemList() {
        this(false);
    }

    int getId() {
        return id;
    }

    /*public String getM_name() {
        return m_name;
    }*/

    /*public void setM_name(String m_name) {
        this.m_name = m_name;
    }*/

    public HashMap<Item, Integer> getM_content() {
        return m_content;
    }

    public void add(Item item, int amount) {
        if (m_content.containsKey(item)) {
            int newAmount = amount + m_content.get(item);
            m_content.put(item,newAmount);
        } else {
            m_content.put(item, amount);
        }
        checkAddToShopping(item, amount);
    }

    public void remove(int itemId) {
        m_content.remove(ItemProvider.getInstance().getItemById(itemId));
    }

    public void remove(Item item, int amount) {
        if(m_content.containsKey(item)) {
            int newAmount = m_content.get(item) - amount;

            if(newAmount <= 0) {
                m_content.remove(item);
            } else {
                m_content.put(item, newAmount);
            }

            checkAddToShopping(item, newAmount);

        }
    }

    private void checkAddToShopping(Item item, int newAmount) {
        if(newAmount <= item.getM_critValue() &&
                id == INVENTORY_LIST_ID) {
            if(ListProvider.getInstance().getListById(SHOPPING_LIST_ID).hasItem(item)) {
                return;
            }
            ListProvider.getInstance().getListById(SHOPPING_LIST_ID).add(item,0);
        }
    }

    public boolean hasItem(Item item) {
        return m_content.containsKey(item);
    }

    public int getAmountForId(int itemId) {
        Item item = ItemProvider.getInstance().getItemById(itemId);
        if(hasItem(item)) {
            return m_content.get(item);
        } else {
            return -1;
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        m_content = (HashMap<Item, Integer>)aInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeObject(m_content);
    }
}
