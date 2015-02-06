package com.musala.atmosphere.ime;

import android.R;

/**
 * Keyboard action enumeration. This enumeration contains custom keyboard intent actions and identifiers.
 * 
 * @author yavor.stankov
 * 
 */
public enum KeyboardAction {
    CUSTOM_INPUT_TEXT_INTENT("atmosphere.intent.ime.action.INPUT", R.id.input),
    CUSTOM_SELECT_ALL_INTENT("atmosphere.intent.ime.action.SELECTALL", R.id.selectAll),
    CUSTOM_DELETE_ALL_INTENT("atmosphere.intent.ime.action.DELETE", 1);

    public final String action;

    public final int id;

    private KeyboardAction(String action, int id) {
        this.action = action;
        this.id = id;
    }

    public static KeyboardAction fromAction(String action) {
        for (KeyboardAction keyboardAction : values()) {
            if (keyboardAction.action.equals(action)) {
                return keyboardAction;
            }
        }
        return null;
    }

}
