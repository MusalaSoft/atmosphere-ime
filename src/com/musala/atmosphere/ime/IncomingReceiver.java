package com.musala.atmosphere.ime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputConnection;

public class IncomingReceiver extends BroadcastReceiver {
    private AtmosphereIME atmosphereIme;

    public IncomingReceiver(AtmosphereIME atmosphereIme) {
        this.atmosphereIme = atmosphereIme;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        InputConnection inputConnection = atmosphereIme.getCurrentInputConnection();

        if (intent.getAction().equals(AtmosphereIME.CUSTOM_INTENT)) {
            int[] intentExtraArray = intent.getIntArrayExtra("text");
            int inputInterval = intent.getIntExtra("interval", -1); // If it returns -1 it means no interval has been
            // inputted.
            if (intentExtraArray.length != 0) {
                StringBuilder textToCommit = new StringBuilder();
                for (int iterator = 0; iterator < intentExtraArray.length; iterator++) {
                    textToCommit.append((Character.toChars(intentExtraArray[iterator])));
                }
                if (inputInterval == -1) {
                    inputConnection.commitText(textToCommit.toString(), textToCommit.length());
                } else {
                    String text = textToCommit.toString();
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
    }
}