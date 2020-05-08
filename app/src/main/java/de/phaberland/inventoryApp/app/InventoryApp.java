/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.app;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.phaberland.inventoryApp.BuildConfig;
import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.data.ListProvider;

/**
 * InventoryApp is the main class of the InventoryApplication.
 * Its main intend is to initialize the main Data classes and
 * provide access to application state information.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class InventoryApp {
    public static final String DISPLAY_VERSION = BuildConfig.VERSION_NAME;
    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;

    static class AppState {
        // todo: add more stuff that we want to save, when exiting the app like settings, user data etc...
        int currentSelectedList;
    }
    private static AppState m_appState;
    private final Activity m_activity;

    /**
     * Constucts the InventoryApp object.
     * The constructor needs an instance of Activity to be able to run.
     * The activity is used for requesting user permissions and setting
     * up the Serializer for writing app data.
     *
     * @param activity the activity using the Inventory apps backend.
     */
    public InventoryApp(Activity activity) {
        m_activity = activity;
    }

    /////////////////
    // INIT/DEINIT //
    /////////////////

    /**
     * Initializes the Providers and triggers the import
     * of application state and items/lists from the
     * application directory.
     * @see ListProvider
     * @see ItemProvider
     */
    public void init() {
        // init providers
        ItemProvider.getInstance().init();
        ListProvider.getInstance().init();

        // read from app directory
        if(!CsvExImporter.importAppState(m_activity)) {
            m_appState = new AppState();
            m_appState.currentSelectedList = ItemList.INVENTORY_LIST_ID;
        }

        CsvExImporter.importCsvFromCache(m_activity);

        // Todo: move that to when we actually want to ex-/import
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * Writes the current application data to file.
     * Using the serializer the application state information will be written to the file system.
     * Also the contents of ItemProvider and ListProvider will be written to the file system,
     * for later retrieval
     * @see ListProvider
     * @see ItemProvider
     */
    public void deinit() {
        CsvExImporter.exportAppState(m_activity);
        CsvExImporter.exportCsvToCache(m_activity);

        // Todo: once we have the export in settings, remove this
        exportCsv();
    }

    ///////////////////////////////////
    // ANDROID INTERACTION FUNCTIONS //
    ///////////////////////////////////

    /**
     * checks if the permission specified in permission param was granted by the system/user.
     * If the permission is not granted, it will spawn a dialog asking the user for the permission,
     * however it will still return directly, without waiting the answer, so the approval of
     * the permission will only get noticed once this function is called again.
     *
     * @param permission the string representation of the permission that should be checked.
     * @return true if permission is granted, false otherwise.
     */
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

    /**
     * Tries to import a csv file from Downloads directory.
     * This is used to share data on a basic level or get an initial setup, which was created
     * on a different device. Format is: id, name, unit, crit, default, inventory entry, shopping entry
     * The function will check the permission to read from external storage using
     * checkPermissions function with READ_EXTERNAL_STORAGE permission.
     * If the permission is granted it will return the result of the importCsvFromDownload function,
     * false otherwise.
     * 
     * @return true if the cvs file was read successfully, false otherwise
     * @see #checkPermission(String)
     * @see CsvExImporter#importCsvFromDownloads() 
     */
    public boolean importCsv() {
        if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return CsvExImporter.importCsvFromDownloads();
        }
        return false;
    }

    /**
     * Tries to export a csv file to Downloads directory.
     * This is used to share data on a basic level or get an initial setup, which was created
     * on a different device. Format is: id, name, unit, crit, default, inventory entry, shopping entry
     * The function will check the permission to write to external storage using
     * checkPermissions function with WRITE_EXTERNAL_STORAGE permission.
     * If the permission is granted it will return the result of the exportCsvToDownload function,
     * false otherwise.
     *
     * @return true if the cvs file was written successfully, false otherwise
     * @see #checkPermission(String)
     * @see CsvExImporter#exportCsvToDownloads()
     */
    private boolean exportCsv() {
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return CsvExImporter.exportCsvToDownloads();
        }
        return false;
    }

    ///////////////////
    // SETTER/GETTER //
    ///////////////////

    static AppState getAppState() {
        return m_appState;
    }

    static void setAppState(AppState state) {
        m_appState = state;
    }

    public void setActiveList(int listId) {
        m_appState.currentSelectedList = listId;
    }

    public int getActiveList() {
        return m_appState.currentSelectedList;
    }
}
