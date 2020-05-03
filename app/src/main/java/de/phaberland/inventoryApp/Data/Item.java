package de.phaberland.inventoryApp.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Item implements Serializable {
    public enum UNIT {
        GRAMM,
        MILILITER,
        PIECE
    }
    private int m_id;
    private String m_name;
    private int m_critValue;
    private int m_defValue;
    private UNIT m_unit;

    Item(String name, UNIT unit) {
        m_id = ItemProvider.getInstance().getNextId();
        m_name = name;
        m_unit = unit;
        int tmp = 1;
        if(!unit.equals(UNIT.PIECE)) {
            tmp = 1000;
        }
        m_critValue = tmp;
        m_defValue = tmp;
    }

    public int getM_id() {
        return m_id;
    }

    public String getM_name() {
        return m_name;
    }

    public int getM_critValue() {
        return m_critValue;
    }

    public void setM_critValue(int m_critValue) {
        this.m_critValue = m_critValue;
    }

    public int getM_defValue() {
        return m_defValue;
    }

    public void setM_defValue(int m_defValue) {
        this.m_defValue = m_defValue;
    }

    public UNIT getM_unit() {
        return m_unit;
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        m_id = aInputStream.readInt();
        m_name = aInputStream.readUTF();
        String tmpUnit = aInputStream.readUTF();
        switch (tmpUnit) {
            case "ML": m_unit = UNIT.MILILITER; break;
            case "GR": m_unit = UNIT.GRAMM; break;
            case "PC": m_unit = UNIT.PIECE; break;
        }
        int tmp = 1;
        if(!m_unit.equals(UNIT.PIECE)) {
            tmp = 1000;
        }

        m_critValue = aInputStream.readInt();
        if(m_critValue < 0) {
            m_critValue = tmp;
        }
        m_defValue = aInputStream.readInt();
        if(m_defValue < 0) {
            m_defValue = tmp;
        }
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeInt(m_id);
        aOutputStream.writeUTF(m_name);
        String tmp = "";
        switch (m_unit) {
            case GRAMM: tmp = "GR"; break;
            case MILILITER: tmp = "ML"; break;
            case PIECE: tmp = "PC"; break;
        }
        aOutputStream.writeUTF(tmp);
        aOutputStream.writeInt(m_critValue);
        aOutputStream.writeInt(m_defValue);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item2Comp = (Item)o;
        return item2Comp.getM_name().equals(getM_name()) && item2Comp.getM_unit() == getM_unit();
    }

    @Override
    public int hashCode() {
        return m_name.hashCode() + m_unit.hashCode();
    }
}
