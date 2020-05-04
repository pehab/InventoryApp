package de.phaberland.inventoryApp.App;

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

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

class CsvExImporter {
    static private final String FILENAME = "export.csv";

    static void exportCsvToDownloads() {
        File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, FILENAME);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fos, "UTF-8");
            for(HashMap.Entry<Integer, Item> entry :ItemProvider.getInstance().getAllItems().entrySet()) {
                // item properties  (0->id,1->name,2->unit,3->crit,4->def,5->inv,6->shop)
                String line = entry.getKey().toString()
                        + "," + entry.getValue().getM_name()
                        + "," + entry.getValue().getM_unit()
                        + "," + entry.getValue().getM_critValue()
                        + "," + entry.getValue().getM_defValue()
                // list amounts
                        + "," + ListProvider.getInstance().getListById(ItemList.INVENTORY_LIST_ID).getAmountForId(entry.getKey())
                        // also add shopping, to get the list
                        + "," + ListProvider.getInstance().getListById(ItemList.SHOPPING_LIST_ID).getAmountForId(entry.getKey())
                // endl
                        + System.getProperty("line.separator");
                streamWriter.write(line);
            }

            streamWriter.flush();
            streamWriter.close();

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void importCsvFromDownloads() {
        File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, FILENAME);
        try {
            FileInputStream inputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
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

            inputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
