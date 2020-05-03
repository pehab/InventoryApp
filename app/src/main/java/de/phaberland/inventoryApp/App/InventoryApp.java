package de.phaberland.inventoryApp.App;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

public class InventoryApp {
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

    /* init will initialize the list Provider,
     */
    public void init() {
        ListProvider.init(m_activity);
        ItemProvider.init(m_activity);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void deinit() {
        ListProvider.deinit();
        ItemProvider.deinit();
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CsvExImporter.exportCsvToDownloads();
        }
    }

    public void importCsv() {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            CsvExImporter.importCsvFromDownloads();
        }
    }

    public void addToList(int listId, int itemId, int amount) {
        ItemList list = ListProvider.getInstance().getListById(listId);
        if(list != null) {
            list.add(itemId, amount);
        }
    }
}
