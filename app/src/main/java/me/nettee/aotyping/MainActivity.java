package me.nettee.aotyping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private TextView mTextView;
    private SoftKeyboard mKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText_word);
        mTextView = (TextView) findViewById(R.id.textView_word);
        mKeyboard = new SoftKeyboard(MainActivity.this);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyboard.attachTo(mEditText);
            }
        });
    }

    public void onSoftKeyboardFinish() {
        String word = mEditText.getText().toString();
        mEditText.getText().clear();
        if (word.length() > 0) {
            mTextView.setText(String.format("已输入单词: %s", word));
        }
    }

}