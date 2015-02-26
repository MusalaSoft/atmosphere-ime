package com.musala.atmosphere.ime;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;

import com.musala.atmosphere.commons.ime.KeyboardAction;

/**
 * Input method for simulating text manipulations.
 */
public class AtmosphereIME extends InputMethodService {
    private static final long DELAY_DEFAULT_VALUE = 0;

    private static final float ROTATION_ANIMATION_BEGIN = 0;

    private static final float ROTATION_ANIMATION_END = 360;

    private IncomingReceiver intentListener;

    private ImageView logo;

    private View contentView;

    private ValueAnimator logoRotateAnimator;

    private long logoAnimationTime;

    private boolean shouldDisplayLayout;

    private boolean shouldDisplayAnimation;

    private boolean deviceHasHardwareKeyboard;

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
                    case CUT_TEXT:
                        onReceiveCut();
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

        public void onReceiveCut() {
            cut();
        }
    }

    private class InputTaskParameters {
        public final String text;

        public final long delay;

        public InputTaskParameters(String text, long delay) {
            this.text = text;
            this.delay = delay;
        }
    }

    private class DelayedInputTask extends AsyncTask<InputTaskParameters, Character, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resumeAnimation();
        }

        @Override
        protected Void doInBackground(InputTaskParameters... parametersArray) {
            for (InputTaskParameters parameters : parametersArray) {
                char[] chars = parameters.text.toCharArray();

                for (int i = 0; i < chars.length; i++) {
                    try {
                        Thread.sleep(parameters.delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    inputText(Character.toString(chars[i]));
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pauseAnimation();
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
        filter.addAction(KeyboardAction.CUT_TEXT.intentAction);

        getApplicationContext().registerReceiver(intentListener, filter);

        logoRotateAnimator = ValueAnimator.ofFloat(ROTATION_ANIMATION_BEGIN, ROTATION_ANIMATION_END);
        logoRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        logoRotateAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (logo != null) {
                    logo.setRotation((Float) animation.getAnimatedValue());
                }
            }
        });
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
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        contentView = layoutInflater.inflate(R.layout.keyboard, null, false);
        logoAnimationTime = 0;
        logo = (ImageView) contentView.findViewById(R.id.logo);

        refreshPreferences();

        return contentView;
    }

    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        refreshPreferences();
        return super.onShowInputRequested(flags, configChange);
    }

    private void refreshPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        shouldDisplayAnimation = sharedPreferences.getBoolean(getString(R.string.display_animation_key), true);
        shouldDisplayLayout = sharedPreferences.getBoolean(getString(R.string.display_ime_key), true);

        if (contentView != null) {
            if (shouldDisplayLayout) {
                contentView.setVisibility(View.VISIBLE);
            } else {
                contentView.setVisibility(View.GONE);
            }
        }
    }

    private void resumeAnimation() {
        if (shouldDisplayAnimation) {
            logoRotateAnimator.setCurrentPlayTime(logoAnimationTime);
            logoRotateAnimator.start();
        }
    }

    private void pauseAnimation() {
        if (shouldDisplayAnimation) {
            logoAnimationTime = logoRotateAnimator.getCurrentPlayTime();
            logoRotateAnimator.cancel();
        }
    }

    public void inputText(String text) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.commitText(text, text.length());
        }
    }

    public void inputText(String text, long delayInterval) {
        if (text != null && delayInterval >= 0) {

            // Make the animation run a full cycle for exactly the time it needs
            // to input the provided text
            logoRotateAnimator.setDuration(text.length() * delayInterval);
            new DelayedInputTask().execute(new InputTaskParameters(text, delayInterval));
        }
    }

    public void selectAll() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.performContextMenuAction(KeyboardAction.SELECT_ALL.id);
        }
    }

    public void delete() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.commitText("", 0);
        }
    }

    public void paste() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.performContextMenuAction(KeyboardAction.PASTE_TEXT.id);
        }
    }

    public void copy() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.performContextMenuAction(KeyboardAction.COPY_TEXT.id);
        }
    }

    public void cut() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.performContextMenuAction(KeyboardAction.CUT_TEXT.id);
        }
    }
}