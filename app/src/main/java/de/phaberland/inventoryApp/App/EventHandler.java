package de.phaberland.inventoryApp.App;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.phaberland.inventoryApp.R;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;
import de.phaberland.inventoryApp.Frontend.AddFromShoppingDialog;
import de.phaberland.inventoryApp.Frontend.RemoveFromInventoryDialog;
import de.phaberland.inventoryApp.Interfaces.EventCallback;

public class EventHandler implements View.OnClickListener {

    public enum EventHandlerMode {
        INVENTORYLISTCLICK,
        SHOPPINGLISTREMOVECLICKED,
        SHOPPINGLISTADDCLICK,
    }

    public static class EventHandlerParams {
        public EventHandlerMode m_mode;
        public int m_itemId;
        public AppCompatActivity m_activity;
        public EventCallback m_callback;
    }

    private EventHandlerParams m_params;

    public EventHandler(EventHandlerParams params) {
        m_params = params;
    }

    @Override
    public void onClick(View v) {
        switch (m_params.m_mode) {
            case INVENTORYLISTCLICK: onInventoryListButtonClicked(); break;
            case SHOPPINGLISTREMOVECLICKED: onShoppingButtonRemoveClicked(); break;
            case SHOPPINGLISTADDCLICK: onShoppingButtonAddClicked(); break;
        }
    }

    private void onInventoryListButtonClicked() {
        Item item = ItemProvider.getInstance().getItemById(m_params.m_itemId);
        if(item == null) {
            return;
        }
        RemoveFromInventoryDialog dlg = new RemoveFromInventoryDialog(m_params.m_callback, m_params.m_itemId);
        dlg.show(m_params.m_activity.getSupportFragmentManager(), m_params.m_activity.getString(R.string.tag_remove_item_dlg));
    }

    private void onShoppingButtonRemoveClicked() {
        ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID).remove(m_params.m_itemId);
        if(m_params.m_callback != null) {
            m_params.m_callback.update();
        }
    }

    private void onShoppingButtonAddClicked() {
        Item item = ItemProvider.getInstance().getItemById(m_params.m_itemId);
        if(item == null) {
            return;
        }
        AddFromShoppingDialog dlg = new AddFromShoppingDialog(m_params.m_callback, m_params.m_itemId);
        dlg.show(m_params.m_activity.getSupportFragmentManager(), m_params.m_activity.getString(R.string.tag_add_item_dlg));
    }
}
