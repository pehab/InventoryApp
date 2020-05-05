package de.phaberland.inventoryApp.App;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import de.phaberland.inventoryApp.Data.Item;
import de.phaberland.inventoryApp.Data.ItemList;
import de.phaberland.inventoryApp.Data.ItemProvider;
import de.phaberland.inventoryApp.Data.ListProvider;

public class Serializer {
    private static final String SUBFOLDER = "/inventoryData";
    private static final String LISTFILE = "allLists.ser";
    private static final String ITEMFILE = "allItems.ser";
    private static final String APPSTATEFILE = "appState.ser";

    private Context m_appContext;

    public Serializer(Context context) {
        m_appContext = context;
    }

    public void writeLists(HashMap<Integer, ItemList> lists) {
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);


        if (!appDirectory.exists()) {
            if(!appDirectory.mkdirs()) {
                return;
            }
        }

        File fileName = new File(appDirectory, LISTFILE);

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(lists);
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception ignored) {

            }
        }
    }

    public HashMap<Integer,ItemList> readLists() {
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);

        if (!appDirectory.exists()) return new HashMap<>(); // File does not exist

        File fileName = new File(appDirectory, LISTFILE);

        FileInputStream fis = null;
        ObjectInputStream in = null;

        HashMap<Integer, ItemList> myHashMap = new HashMap<>();
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            myHashMap = (HashMap<Integer, ItemList> ) in.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if(fis != null) {
                    fis.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myHashMap;
    }

    public HashMap<Integer, Item> readAllItems() {
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);

        if (!appDirectory.exists()) return new HashMap<>(); // File does not exist

        File fileName = new File(appDirectory, ITEMFILE);

        FileInputStream fis = null;
        ObjectInputStream in = null;

        HashMap<Integer,Item>  myHashMap = new HashMap<>();
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            myHashMap = (HashMap<Integer,Item> ) in.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if(fis != null) {
                    fis.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myHashMap;
    }

    public void writeAllItems(HashMap<Integer, Item> m_allItems) {
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);


        if (!appDirectory.exists()) {
            if(!appDirectory.mkdirs()) {
                return;
            }
        }

        File fileName = new File(appDirectory, ITEMFILE);

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(m_allItems);
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception ignored) {

            }
        }
    }

    InventoryApp.AppState readAppState() {
        InventoryApp.AppState state = new InventoryApp.AppState();
        // if we do not find something just init with 0;
        state.currentSelectedList = 0;
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);

        if (!appDirectory.exists()) {
            appDirectory.mkdirs();
        }
        File fileName = new File(appDirectory, APPSTATEFILE);

        try {
            FileInputStream inputStream = new FileInputStream(fileName);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;

            if ( (receiveString = bufferedReader.readLine()) != null ) {
                state.currentSelectedList = Integer.parseInt(receiveString);
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    void writeAppState(InventoryApp.AppState state) {
        File cacheDir = m_appContext.getFilesDir();
        String BaseFolder = cacheDir.getAbsolutePath();
        File appDirectory = new File(BaseFolder + SUBFOLDER);

        if (!appDirectory.exists()) {
            appDirectory.mkdirs();
        }
        File fileName = new File(appDirectory, APPSTATEFILE);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(Integer.toString(state.currentSelectedList));

            outputStream.flush();
            outputStreamWriter.flush();
            bufferedWriter.flush();

            outputStream.close();
            outputStreamWriter.close();
            bufferedWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
