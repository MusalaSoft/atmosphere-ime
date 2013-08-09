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

import android.content.IntentFilter;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Based entirely on the example provided by Google SDK in their Soft Keyboard Example.
 */

public class AtmosphereIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener
{

	public static final String CUSTOM_INTENT = "atmosphere.intent.action.TEXT";

	/**
	 * Main initialization of the input method component. Be sure to call to super class.
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		IncomingReceiver intentListener = new IncomingReceiver(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(CUSTOM_INTENT);
		this.getApplicationContext().registerReceiver(intentListener, filter);
	}

	/**
	 * This is the point where you can do all of your UI initialization. It is called after creation and any
	 * configuration change.
	 */
	@Override
	public void onInitializeInterface()
	{
		new Keyboard(this, R.xml.blank);
	}

	@Override
	public View onCreateInputView()
	{
		LinearLayout layout = new LinearLayout(this);

		layout.setBackgroundColor(Color.BLACK);

		ImageView image = new ImageView(this);
		image.setImageResource(R.drawable.story);
		image.setPadding(30, 30, 30, 30);
		layout.setGravity(Gravity.CENTER);
		layout.addView(image);

		return layout;
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes)
	{
	}

	@Override
	public void onPress(int primaryCode)
	{
	}

	@Override
	public void onRelease(int primaryCode)
	{
	}

	@Override
	public void onText(CharSequence text)
	{
	}

	@Override
	public void swipeDown()
	{
	}

	@Override
	public void swipeLeft()
	{
	}

	@Override
	public void swipeRight()
	{
	}

	@Override
	public void swipeUp()
	{
	}

}