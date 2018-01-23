package me.nettee.aotyping;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Method;

public class SoftKeyboard {

    public boolean isUpper = false;
    private MainActivity mMainActivity;
    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;
    private EditText mEditText;

    public SoftKeyboard(MainActivity activity) {
        mMainActivity = activity;
        mKeyboard = new Keyboard(mMainActivity, R.xml.keyboard);
        mKeyboardView = (SoftKeyboardView) mMainActivity.findViewById(R.id.keyboard_view);
    }

    /**
     * 绑定自定义键盘
     */
    public void attachTo(EditText editText) {
        this.mEditText = editText;
        hideSystemSofeKeyboard(mMainActivity.getApplicationContext(), mEditText);

        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);

        mKeyboardView.setOnKeyboardActionListener(new KeyBoardListener());

        mKeyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mMainActivity.onSoftKeyboardTouch(event);
                return false;
            }
        });

    }

    private class KeyBoardListener implements KeyboardView.OnKeyboardActionListener {

        @Override
        public void onPress(int code) {
            if (code == Keyboard.KEYCODE_CANCEL || code == Keyboard.KEYCODE_DELETE || code == Keyboard.KEYCODE_SHIFT) {
                return;
            }
            char c = (char) code;
            mMainActivity.onSoftKeyPress(c);
        }

        @Override
        public void onRelease(int code) {
            if (code == Keyboard.KEYCODE_CANCEL || code == Keyboard.KEYCODE_DELETE || code == Keyboard.KEYCODE_SHIFT) {
                return;
            }
            char c = (char) code;
            mMainActivity.onSoftKeyRelease(c);
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                hide();
                mMainActivity.onSoftKeyboardFinish();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                shift();
                mKeyboardView.setKeyboard(mKeyboard);
            } else if (primaryCode == 57419) { // go left
                if (start > 0) {
                    mEditText.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) { // go right
                if (start < mEditText.length()) {
                    mEditText.setSelection(start + 1);
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence charSequence) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    }

    private void shift() {
        for (Keyboard.Key key : mKeyboard.getKeys()) {
            if (key.label == null) {
                continue;
            }
            String keyString = key.label.toString();
            if (isAlphabet(keyString)) {
                if (isUpper) {
                    key.label = keyString.toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else {
                    key.label = keyString.toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            } else if (keyString.equals("小写")) {
                if (isUpper) {
                    key.label = "大写";
                } else {
                    key.label = "小写";
                }
            }

        }
        isUpper = !isUpper;
    }

    public void hide() {
        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isAlphabet(String str){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        if (alphabet.indexOf(str.toLowerCase())>-1) {
            return true;
        }
        return false;
    }

    private static void hideSystemSofeKeyboard(Context context, EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}