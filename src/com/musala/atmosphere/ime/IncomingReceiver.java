package com.musala.atmosphere.ime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputConnection;

public class IncomingReceiver extends BroadcastReceiver
{
	private AtmosphereIME atmosphereIme;

	public IncomingReceiver(AtmosphereIME atmosphereIme)
	{
		this.atmosphereIme = atmosphereIme;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		InputConnection inputConnection = atmosphereIme.getCurrentInputConnection();

		if (intent.getAction().equals(AtmosphereIME.CUSTOM_INTENT))
		{
			int intentExtra = intent.getIntExtra("t", -1); // If it returns -1 it means no characters have been
															// inputted. This usually can't happen.
			if (intentExtra != -1)
			{
				String textToInput = new String(Character.toChars(intentExtra));
				inputConnection.commitText(textToInput, textToInput.length());
			}
		}
	}
}