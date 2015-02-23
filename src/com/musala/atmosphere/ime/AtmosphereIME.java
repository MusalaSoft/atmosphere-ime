/*
 * Copyright (C) 2008-2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.musala.atmosphere.ime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.musala.atmosphere.commons.ime.KeyboardAction;

/**
 * Based entirely on the example provided by Google SDK in their Soft Keyboard Example.
 */
public class AtmosphereIME extends InputMethodService {
    private static final long DELAY_DEFAULT_VALUE = 0;

    private IncomingReceiver intentListener;

    public class IncomingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeyboardAction intentAction = KeyboardAction.fromAction(intent.getAction());

            if (intentAction != null) {
                switch (intentAction) {
                    case INPUT_TEXT:
                        onReceiveInputText(intent);
                        break;
                    case DELETE_ALL:
                        onReceiveDeleteAll();
                        break;
                    case SELECT_ALL:
                        onReceiveSelectAll();
                        break;
                    case PASTE_TEXT:
                        onReceivePaste();
                        break;
                    case COPY_TEXT:
                        onReceiveCopy();
                        break;
                    default:
                        break;
                }
            }
        }

        public void onReceiveInputText(Intent intent) {
            String text = intent.getStringExtra(KeyboardAction.INTENT_EXTRA_TEXT);
            long inputInterval = intent.getLongExtra(KeyboardAction.INTENT_EXTRA_INPUT_SPEED, DELAY_DEFAULT_VALUE);

            if (text == null) {
                text = "";
            }

            if (inputInterval <= DELAY_DEFAULT_VALUE) {
                inputText(text);
            } else {
                inputText(text, inputInterval);
            }
        }

        public void onReceiveSelectAll() {
            selectAll();
        }

        public void onReceiveDeleteAll() {
            selectAll();
            delete();
        }

        public void onReceivePaste() {
            paste();
        }

        public void onReceiveCopy() {
            copy();
        }
    }

    /**
     * Main initialization of the input method component. Be sure to call to super class.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        intentListener = new IncomingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(KeyboardAction.INPUT_TEXT.intentAction);
        filter.addAction(KeyboardAction.DELETE_ALL.intentAction);
        filter.addAction(KeyboardAction.SELECT_ALL.intentAction);
        filter.addAction(KeyboardAction.PASTE_TEXT.intentAction);
        filter.addAction(KeyboardAction.COPY_TEXT.intentAction);
        this.getApplicationContext().registerReceiver(intentListener, filter);
    }

    /**
     * This is the point where you can do all of your UI initialization. It is called after creation and any
     * configuration change.
     */
    @Override
    public void onInitializeInterface() {
        new Keyboard(this, R.xml.blank);
    }

    @Override
    public View onCreateInputView() {
        LinearLayout layout = new LinearLayout(this);

        layout.setBackgroundColor(Color.BLACK);

        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.story);
        image.setPadding(30, 30, 30, 30);
        layout.setGravity(Gravity.CENTER);
        layout.addView(image);
        return layout;
    }

    public void inputText(String text) {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.commitText(text, text.length());
    }

    public void inputText(String text, long delayInterval) {
        if (text != null && delayInterval >= 0) {

            char[] chars = text.toCharArray();
            for (char ch : chars) {
                try {
                    Thread.sleep(delayInterval);
                } catch (InterruptedException e) {
                    // Interrupted sleep. Nothing to do here.
                    e.printStackTrace();
                }
                inputText(Character.toString(ch));
            }
        }
    }

    public void selectAll() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(KeyboardAction.SELECT_ALL.id);
    }

    public void delete() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.commitText("", 0);
    }

    public void paste() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(KeyboardAction.PASTE_TEXT.id);
    }

    public void copy() {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(KeyboardAction.COPY_TEXT.id);
    }
}