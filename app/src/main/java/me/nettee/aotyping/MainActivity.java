package me.nettee.aotyping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText_word);
        final AllKeyboardUtil keyboardUtil = new AllKeyboardUtil(MainActivity.this);

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardUtil.attachTo(mEditText);
            }
        });
    }
}