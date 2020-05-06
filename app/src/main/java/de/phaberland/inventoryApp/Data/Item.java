/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */

package de.phaberland.inventoryApp.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Item class is the base Item within the lists.
 * Item has:
 *  - id, from which it is identified uniquely,
 *  - name, which is human readable and will be displayed
 *  - unit specifying the measuring unit of the item
 *  - critValue specifying the critical value to mark the item running out and adding it to shopping
 *  - defValue default shopping value, which is the standard value shopped for that item
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public class Item implements Serializable {
    /**
     * specifies the measuring unit of an item.
     * Gramm, Mililiter or piece.
     */
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

    /**
     * Constructs an instance of an Item.
     * Id will be provided by the ItemProvider, name and unit are set from the parameter.
     * Mostly this is all we get for an item, so the constructor will also initialize
     * default values for defValue and critValue depending on the unit.
     * @param name String name of the item
     * @param unit unit of the item
     * @see UNIT
     */
    Item(String name, UNIT unit) {
        m_id = ItemProvider.getInstance().getNextId();
        m_name = name;
        m_unit = unit;
        int tmpDef = 1;
        int tmpCrit = 0;
        if(!unit.equals(UNIT.PIECE)) {
            tmpDef = 1000;
            tmpCrit = 500;
        }
        m_critValue = tmpCrit;
        m_defValue = tmpDef;
    }

    ///////////////////
    // GETTER/SETTER //
    ///////////////////

    public int getM_id() {
        return m_id;
    }

    /**
     * dangerous... should only be used by the itemProvider when sorting...
     */
    void setM_id(int id) {
        m_id = id;
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

    //////////////////////////////////
    // OBJECT OVERRIDES FOR COMPARE //
    //////////////////////////////////

    /**
     * Checks if the provided item is the same as this item.
     * To be considered the same the following things are checked:
     *  - is it the same object?
     *  - is it the same class of object
     *  - is the name the same
     *  - is the unit the same
     *  Essentially 2 items are considered the same, if they have the same name and unit
     * @param o object to compare with
     * @return true if the objeccts are the considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item2Comp = (Item)o;
        return item2Comp.getM_name().equals(getM_name()) && item2Comp.getM_unit() == getM_unit();
    }

    /**
     * Object override, add the return values of hashCode from
     * name and unit to create a hashCode
     * @return sum of hashCode function from name and unit
     */
    @Override
    public int hashCode() {
        return m_name.hashCode() + m_unit.hashCode();
    }
}
