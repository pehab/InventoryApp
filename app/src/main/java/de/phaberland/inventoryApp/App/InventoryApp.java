package de.phaberland.inventoryApp.App;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

public class InventoryApp {
    static class AppState {
        // todo: add more stuff that we want to save, when exiting the app
        int currentSelectedList;
    }
    AppState m_appState;
    private Activity m_activity;

    public InventoryApp(Activity activity) {
        m_activity = activity;
    }

    private boolean checkPermission(String permission) {
        if(ContextCompat.checkSelfPermission(m_activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(m_activity,
                    new String[]{permission},
                    1);
            return false;
        }
        return true;
    }

    public void setActiveList(int listId) {
        m_appState.currentSelectedList = listId;
    }

    public int getActiveList() {
        return m_appState.currentSelectedList;
    }

    /* init will initialize the list Provider,
     */
    public void init() {
        ListProvider.init(m_activity);
        ItemProvider.init(m_activity);

        Serializer ser = new Serializer(m_activity);
        m_appState = ser.readAppState();

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void deinit() {
        ListProvider.deinit();
        ItemProvider.deinit();
        Serializer ser = new Serializer(m_activity);
        ser.writeAppState(m_appState);

        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CsvExImporter.exportCsvToDownloads();
        }
    }

    public void importCsv() {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            CsvExImporter.importCsvFromDownloads();
        }
    }

    public void addToList(int listId, Item item, int amount) {
        ItemList list = ListProvider.getInstance().getListById(listId);
        if(list != null) {
            list.add(item, amount);
        }
    }
}
