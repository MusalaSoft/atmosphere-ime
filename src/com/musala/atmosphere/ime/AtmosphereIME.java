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

/**
 * Based entirely on the example provided by Google SDK in their Soft Keyboard Example.
 */
public class AtmosphereIME extends InputMethodService {
    private static final String EXTRA_TEXT_NAME = "text";

    private static final String EXTRA_INTERVAL_NAME = "interval";

    private static final int INTERVAL_DEFAULT_VALUE = 0;

    private IncomingReceiver intentListener;

    private InputConnection inputConnection;

    public class IncomingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            KeyboardAction intentAction = KeyboardAction.fromAction(intent.getAction());

            if (intentAction != null) {
                switch (intentAction) {
                    case CUSTOM_INPUT_TEXT_INTENT:
                        onReceiveInputText(intent);
                        break;
                    case CUSTOM_DELETE_ALL_INTENT:
                        onReceiveDelete();
                        break;
                    default:
                        break;
                }
            }
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
        filter.addAction(KeyboardAction.CUSTOM_INPUT_TEXT_INTENT.action);
        filter.addAction(KeyboardAction.CUSTOM_DELETE_ALL_INTENT.action);
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

    public void onReceiveInputText(Intent intent) {
        int[] intentExtraArray = intent.getIntArrayExtra(EXTRA_TEXT_NAME);
        int inputInterval = intent.getIntExtra(EXTRA_INTERVAL_NAME, INTERVAL_DEFAULT_VALUE);

        inputConnection = getCurrentInputConnection();

        if (intentExtraArray.length != 0) {
            StringBuilder textToCommit = new StringBuilder();

            for (int intentExtraChar : intentExtraArray) {
                textToCommit.append((Character.toChars(intentExtraChar)));
            }

            String text = textToCommit.toString();

            if (inputInterval == INTERVAL_DEFAULT_VALUE) {
                inputConnection.commitText(text, text.length());
            } else {
                for (char current : text.toCharArray()) {
                    String input = new String(Character.toString(current));
                    inputConnection.commitText(input, input.length());

                    try {
                        Thread.sleep(inputInterval);
                    } catch (InterruptedException e) {
                        // Interrupted sleep. Nothing to do here.
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onReceiveSelectAll() {
        inputConnection = getCurrentInputConnection();
        inputConnection.performContextMenuAction(KeyboardAction.CUSTOM_SELECT_ALL_INTENT.id);
    }

    public void onReceiveDelete() {
        onReceiveSelectAll();

        inputConnection = getCurrentInputConnection();
        inputConnection.commitText("", 0);
    }
}