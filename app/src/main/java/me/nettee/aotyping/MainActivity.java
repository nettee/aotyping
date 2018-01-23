package me.nettee.aotyping;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText mEditText;
    private TextView mTextView;
    private SeekBar mSeekBar;
    private TextView mLogTextView;
    private TextView mResultTextView;
    private SoftKeyboard mKeyboard;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Acceleration mLastAcc = new Acceleration(0, 0, 0);

    private float mLargestAccValue = 0.0f;
    private List<Float> mTouchSizes = new LinkedList<>();

    private List<Float> mMaxAccList = new LinkedList<>();
    private List<Float> mAvgTouchSizeList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText_word);
        mTextView = (TextView) findViewById(R.id.textView_word);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mLogTextView = (TextView) findViewById(R.id.textView_log);
        mResultTextView = (TextView) findViewById(R.id.textView_result);
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

        // Reset word
        if (mEditText.getText().toString().length() == 0) {
            mLogTextView.setText("");
        }

        mLogTextView.append(String.format("Press %c (largest acc = %.2f)\n", c, mLargestAccValue));
        mMaxAccList.add(mLargestAccValue);
    }

    public void onSoftKeyRelease(char c) {

        float averageTouchSize = average(mTouchSizes);
        mLogTextView.append(String.format("Release %c (average touch size = %.4f)\n", c, averageTouchSize));
        mAvgTouchSizeList.add(averageTouchSize);

        // Reset letter
        mLargestAccValue = 0.0f;
        mTouchSizes.clear();
    }

    public void onSoftKeyboardTouch(MotionEvent event) {
        float size = event.getSize();
        mTouchSizes.add(size);
    }

    public void onSoftKeyboardFinish() {
        String word = mEditText.getText().toString();
        mEditText.getText().clear();
        if (word.length() > 0) {
            mTextView.setText(String.format("已输入单词: %s", word));

            mResultTextView.setText("");
            mResultTextView.append(String.format("Max acc list: %s\n", toString(mMaxAccList, "%.2f")));
            mResultTextView.append(String.format("Avg touch size list: %s\n", toString(mAvgTouchSizeList, "%.4f")));

            // Reset word
            mMaxAccList.clear();
            mAvgTouchSizeList.clear();
        }
    }

    private static float average(List<Float> fs) {
        float s = 0.0f;
        for (float f : fs) {
            s += f;
        }
        return s / fs.size();
    }

    private static String toString(List<Float> fs, String formatter) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < fs.size(); i++) {
            sb.append(String.format(formatter, fs.get(i)));
            if (i < fs.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}