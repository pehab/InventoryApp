/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.app;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import de.phaberland.inventoryApp.data.Item;
import de.phaberland.inventoryApp.data.ItemList;
import de.phaberland.inventoryApp.data.ItemProvider;
import de.phaberland.inventoryApp.data.ListProvider;

/**
 * CsvExImporter is used to save and load
 * application data.
 * The data will be stored in csv format.
 * Functions are provided to store and read
 *  - Items and lists
 *  - Applications state
 *  All the methods are static, so no instance
 *  is needed to use this class.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
class CsvExImporter {
    // how to export
    private static final String FILENAME = "export.csv";
    private static final String APPSTATEFILE = "appState.csv";
    private static final String CHARSET = "UTF-8";

    // what to export
    private static final int INVENTORY = 0;
    private static final int STATE = 1;

    //////////////////////
    // Public functions //
    //////////////////////

    /**
     * Exports the items and lists to the download
     * folder. To be able to do that this function
     * need the permission to access the external
     * file resources.
     * Be sure to check permission before calling:
     *  - Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @return true when successful, false otherwise
     */
    static boolean exportCsvToDownloads() {
        File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, FILENAME);
        return export(file, INVENTORY);
    }

    /**
     * Exports the items and lists to the application files
     * folder. The data stored here, will be removed, when
     * the app is uninstalled.
     */
    static void exportCsvToCache(Context context) {
        File cacheDir = context.getFilesDir();
        File file = new File(cacheDir, FILENAME);
        export(file, INVENTORY);
    }

    /**
     * Exports the application state to the application files
     * folder. The data stored here, will be removed, when
     * the app is uninstalled.
     */
    static void exportAppState(Context context) {
        File cacheDir = context.getFilesDir();
        File file = new File(cacheDir, APPSTATEFILE);
        export(file, STATE);
    }

    /**
     * Import the items and lists from the download
     * folder. To be able to do that this function
     * need the permission to access the external
     * file resources.
     * Be sure to check permission before calling:
     *  - Manifest.permission.READ_EXTERNAL_STORAGE
     * @return true when successful, false otherwise
     */
    static boolean importCsvFromDownloads() {
        File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, FILENAME);
        return importe(file, INVENTORY);
    }

    /**
     * Import the items and lists from the application files
     * folder. The data stored here, will be removed, when
     * the app is uninstalled.
     */
    static void importCsvFromCache(Context context) {
        File cacheDir = context.getFilesDir();
        File file = new File(cacheDir, FILENAME);
        importe(file, INVENTORY);
    }

    /**
     * Import the application state from the application files
     * folder. The data stored here, will be removed, when
     * the app is uninstalled.
     */
    static boolean importAppState(Context context) {
        File cacheDir = context.getFilesDir();
        File file = new File(cacheDir, APPSTATEFILE);
        return importe(file, STATE);
    }

    ////////////////////////
    // Internal functions //
    ////////////////////////

    /**
     * Creates an OutputStreamWriter instance for
     * the specified file and depending on what to export
     * will call the corresponding write function to
     * write to the stream.
     * This function also takes care of the exception
     * handling.
     * @param file File to write to
     * @param what What to write (0=inventory, 1=application state)
     * @return true when successful, false otherwise
     */
    private static boolean export(File file, int what) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fos, CHARSET);
            // finally write the data
            if(what == INVENTORY) {
                writeInventory(streamWriter);
            } else {
                writeState(streamWriter);
            }

            streamWriter.flush();
            streamWriter.close();

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Creates a BufferedReader instance for
     * the specified file and depending on what to import
     * will call the corresponding read function to read
     * from the stream.
     * This function also takes care of the exception
     * handling.
     * @param file File to read from
     * @param what What to read (0=inventory, 1=application state)
     * @return true when successful, false otherwise
     */
    private static boolean importe(File file, int what) {
        try {
            FileInputStream inputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, CHARSET);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // finally read
            if(what == INVENTORY) {
                readInventory(bufferedReader);
            } else {
                readState(bufferedReader);
            }

            inputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loops through all the Items in ItemProvider and adds
     * their members to the file in a csv format.
     * Also it adds the values in the existing lists (-1 if not in the list)
     * The format will be:
     *  id, name, unit, critValue, defValue, inventoryAmount, shopAmount
     * @param streamWriter an OutputStreamWriter instance to write to
     * @throws IOException if something went wrong when writing
     */
    private static void writeInventory(OutputStreamWriter streamWriter) throws IOException {
        for(HashMap.Entry<Integer, Item> entry :ItemProvider.getInstance().getAllItems().entrySet()) {
            // item properties  (0->id,1->name,2->unit,3->crit,4->def,5->inv,6->shop)
            String line = entry.getKey().toString()
                    + "," + entry.getValue().getM_name()
                    + "," + entry.getValue().getM_unit()
                    + "," + entry.getValue().getM_critValue()
                    + "," + entry.getValue().getM_defValue()
                    // list inventory amounts
                    + "," + ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID).getAmountForId(entry.getKey())
                    // also add shopping, to get the list
                    + "," + ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID).getAmountForId(entry.getKey())
                    // endl
                    + System.getProperty("line.separator");
            streamWriter.write(line);
        }
    }

    /**
     * Writes the application state information to the specified file
     * @param streamWriter an OutputStreamWriter instance to write to
     * @throws IOException if something went wrong when writing
     */
    private static void writeState(OutputStreamWriter streamWriter) throws IOException {
        InventoryApp.AppState state = InventoryApp.getAppState();
        String stringState = Integer.toString(state.currentSelectedList);
        streamWriter.write(stringState.trim());
    }

    /**
     * Reads from the file line by line, until no readLine returns null.
     * Parses the line for the items and their corresponding values.
     * If a value for a list is -1 the item will not be added to the list.
     * The format will be:
     *  id(0), name(1), unit(2), critValue(3), defValue(4), inventoryAmount(5), shopAmount(6)
     * @param bufferedReader a BufferedReader instance to read from
     * @throws IOException if something went wrong when reading
     */
    private static void readInventory(BufferedReader bufferedReader) throws IOException {
        String receiveString;

        ItemProvider.getInstance().clear();
        ListProvider.getInstance().clear();

        while ( (receiveString = bufferedReader.readLine()) != null ) {
            String[] values = receiveString.split(",");
            // (0->id,1->name,2->unit,3->crit,4->def,5->inv,6->shop)
            if(values.length != 7) {
                continue;
            }
            Item.UNIT unit = Item.UNIT.PIECE;
            if(values[2].equals(Item.UNIT.GRAMM.toString())) {
                unit = Item.UNIT.GRAMM;
            } else if(values[2].equals(Item.UNIT.MILILITER.toString())) {
                unit = Item.UNIT.MILILITER;
            }
            int itemId = ItemProvider.getInstance().addItem(values[1], unit, false);
            Item item = ItemProvider.getInstance().getItemById(itemId);
            int crit = Integer.parseInt(values[3]);
            item.setM_critValue(crit);
            item.setM_defValue(Integer.parseInt(values[4]));
            // inventory
            if(!values[5].isEmpty() && !values[5].equals("-1")) {
                ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID).add(item, Integer.parseInt(values[5]));
            }
            // shopping
            if(!values[6].isEmpty() && !values[6].equals("-1")) {
                ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID).add(item, Integer.parseInt(values[6]));
            }
        }
        ItemProvider.getInstance().sortItems();
    }

    /**
     * Reads the application state information from the specified file
     * @param bufferedReader a BufferedReader instance to read from
     * @throws IOException if something went wrong when reading
     */
    private static void readState(BufferedReader bufferedReader) throws IOException {
        InventoryApp.AppState state = new InventoryApp.AppState();
        String receiveString;
        if ( (receiveString = bufferedReader.readLine()) != null ) {
            receiveString = receiveString.trim();
            if(!receiveString.isEmpty()) {
                state.currentSelectedList = Integer.parseInt(receiveString);
            } else {
                state.currentSelectedList = ItemList.INVENTORY_LIST_ID;
            }
        }
        InventoryApp.setAppState(state);
    }
}
