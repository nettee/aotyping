package me.nettee.aotyping;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText mEditText;
    private TextView mTextView;
    private SeekBar mSeekBar;
    private TextView mLogTextView;
    private Button mClearButton;
    private SoftKeyboard mKeyboard;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Acceleration mLastAcc = new Acceleration(0, 0, 0);
    private float mLargestAccValue = 0.0f;
    private List<Float> mAccList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText_word);
        mTextView = (TextView) findViewById(R.id.textView_word);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mLogTextView = (TextView) findViewById(R.id.textView_log);
        mKeyboard = new SoftKeyboard(MainActivity.this);

        mSeekBar.setEnabled(false);
        mSeekBar.setMax(2000);

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyboard.attachTo(mEditText);
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Acceleration acc = Acceleration.fromEvent(event);
            if (!mLastAcc.isZero()) {
                Acceleration diff = acc.sub(mLastAcc);
                float value = diff.getValue();
                mSeekBar.setProgress((int) (value * 100));
                if (value > mLargestAccValue) {
                    mLargestAccValue = value;
                }
            }
            mLastAcc = acc;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onSoftKeyPress(char c) {
        if (mEditText.getText().toString().length() == 0) {
            mLogTextView.setText("");
        }
        mLogTextView.append(String.format("Press %c (largest acc = %.2f)\n", c, mLargestAccValue));
        mAccList.add(mLargestAccValue);
    }

    public void onSoftKeyRelease(char c) {
//        mLogTextView.append(String.format("Release %c (and reset acc values)\n", c));
        mLargestAccValue = 0.0f;
    }

    public void onSoftKeyboardFinish() {
        String word = mEditText.getText().toString();
        mEditText.getText().clear();
        if (word.length() > 0) {
            mTextView.setText(String.format("已输入单词: %s", word));
            for (float a : mAccList) {
                mLogTextView.append(String.format("%.2f ", a));
            }
            mLogTextView.append("\n");
            mAccList.clear();
        }
    }
}