package de.phaberland.inventoryApp.App;


import android.content.Context;

import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

public class InventoryApp {
    private Context m_appContext;
    //private EventHandler eventHandler;

    public InventoryApp(Context context) {
        m_appContext = context;
    }

    /* init will initialize the list Provider,
     */
    public void init() {
        ListProvider.init(m_appContext);
        ItemProvider.init(m_appContext);
    }

    public void deinit() {
        ListProvider.deinit();
        ItemProvider.deinit();
    }

    public void addToList(int listId, int itemId, int amount) {
        ItemList list = ListProvider.getInstance().getListById(listId);
        if(list != null) {
            list.add(itemId, amount);
        }
    }
}
