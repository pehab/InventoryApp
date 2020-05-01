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
    private HashMap<Integer, Integer> m_content;

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

    public HashMap<Integer, Integer> getM_content() {
        return m_content;
    }

    public void add(Integer itemId, int amount) {
        if (m_content.containsKey(itemId)) {
            int newAmount = amount + m_content.get(itemId);
            m_content.put(itemId,newAmount);
        } else {
            m_content.put(itemId, amount);
        }
         checkAddToShopping(itemId, amount);
    }

    public void remove(int itemId) {
        m_content.remove(itemId);
    }

    public void remove(int itemId, int amount) {
        if(m_content.containsKey(itemId)) {
            int newAmount = m_content.get(itemId) - amount;

            if(newAmount <= 0) {
                m_content.remove(itemId);
            } else {
                m_content.put(itemId, newAmount);
            }

            checkAddToShopping(itemId, newAmount);

        }
    }

    private void checkAddToShopping(int itemId, int newAmount) {
        if(newAmount <= ItemProvider.getInstance().getItemById(itemId).getM_critValue() &&
                id == INVENTORY_LIST_ID) {
            if(ListProvider.getInstance().getListById(SHOPPING_LIST_ID).hasItem(itemId)) {
                return;
            }
            ListProvider.getInstance().getListById(SHOPPING_LIST_ID).add(itemId,0);
        }
    }

    public boolean hasItem(int id) {
        return m_content.containsKey(id);
    }

    public int getAmountForId(int itemId) {
        if(hasItem(itemId)) {
            return m_content.get(itemId);
        } else {
            return -1;
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        m_content = (HashMap<Integer, Integer>)aInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeObject(m_content);
    }
}
