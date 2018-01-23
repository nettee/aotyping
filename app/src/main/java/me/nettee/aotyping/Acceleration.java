package me.nettee.aotyping;

import android.hardware.SensorEvent;

public class Acceleration {

    public float x;
    public float y;
    public float z;

    public Acceleration(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Acceleration fromEvent (SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        return new Acceleration(x, y, z);
    }

    public static Acceleration zero() {
        return new Acceleration(0.0f, 0.0f, 0.0f);
    }

    public boolean isZero() {
        return x == 0.0 && y == 0.0 && z == 0.0;
    }

    public float getValue() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Acceleration sub(Acceleration that) {
        float dx = this.x - that.x;
        float dy = this.y - that.y;
        float dz = this.z - that.z;
        return new Acceleration(dx, dy, dz);
    }

    @Override
    public String toString() {
        return String.format("加速度: \nx = %.2f, \ny = %.2f, \nz = %.2f", x, y, z);
    }
}
