package com.example.rolling_balls;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    Sensor Accelerometer;
    Sensor magneticField;
    SensorManager mSensorManager;
    int w, h, radius = 60;
    float x, y, vx = 0, vy = 0, ax = 0, ay = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        mSensorManager.registerListener(sListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    Paint paint = new Paint();
    class DrawView extends View {
        public DrawView(Context context) {

            super(context);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            h = displayMetrics.heightPixels;
            w = displayMetrics.widthPixels;

            x = w / 2;

            y = h / 2;

        }

        @Override
        protected void onDraw(Canvas canvas) {

            paint.setColor(Color.GREEN); // установим зелёный цвет
            paint.setStyle(Paint.Style.FILL);


            ax = (float) (9.81 * Math.sin(orientation[2]));
            ay = (float) (-9.81 * Math.sin(orientation[1]));

            vx += 0.5 * ax;
            vy += 0.5 * ay;

            x += 0.5 * vx;
            y += 0.5 * vy;

            if (x < radius) { x = radius; vx = 0; }
            if (x > w - radius) { x = w - radius; vx = 0; }

            if (y < radius) { y = radius; vy = 0; }
            if (y > h - radius) { y = h - radius; vy = 0; }

            canvas.drawCircle(x, y, radius, paint);
            invalidate();
        }

    }



    @Override
    protected void onResume() {

        mSensorManager.registerListener(sListener, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sListener, magneticField, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(sListener);
    }

    float [] accel = new float [3];
    float [] magnet = new float [3];

    float [] rotationMatrix = new float [16];
    float [] orientation = new float [3];

    SensorEventListener sListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accel = sensorEvent.values;

            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magnet = sensorEvent.values;

            }

            SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet);
            SensorManager.getOrientation(rotationMatrix, orientation);

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}