/*
 * Copyright 2020 Peter Haberland
 *
 * No licensing, you may use/alter that code as you wish.
 */
package de.phaberland.inventoryApp.interfaces;

/**
 * YesNoCallback interface is used as callback
 * for simple Yes/No dialogs, usually used for
 * approving an action.
 * If the user approves by pressing Yes
 * yesClicked will be called.
 *
 * @author      Peter Haberland
 * @version     %I%, %G%
 */
public interface YesNoCallback {
    /**
     * classes implementing this interface need
     * to override this function. It will be called
     * when the used clicks yes in a simple y/n.
     */
    void yesClicked();
}
