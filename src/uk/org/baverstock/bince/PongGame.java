package uk.org.baverstock.bince;

import android.graphics.Matrix;
import android.widget.TextView;

/**
 * Created by tim on 16/07/14.
 */
public class PongGame {
    private boolean served = false;
    private int rad;
    private float[] bat = new float[4];
    private float[] norm = new float[] {0,0, 1,0};
    private float[] ball = new float[4];
    private float[] ballv = new float[] {3,3};
    private float[] bistory = new float[10];
    private int bix = 0;
    private float size = 10;
    private float angle;
    private float batAngle = 99;
    private String hist;

    boolean isServed() {
        return served;
    }

    public void serve() {
        served = true;
        ball[0] = 0;
        ball[1] = 0;
    }

    public String update() {
        Matrix toNorm = new Matrix();
        Matrix fromNorm = new Matrix();

        ball[2] = ball[0] + ballv[0];
        ball[3] = ball[1] + ballv[1];

        updateBat();

        toNorm.setPolyToPoly(bat, 0, norm, 0, 2);
        fromNorm.setPolyToPoly(norm, 0, bat, 0, 2);

        toNorm.mapPoints(ball);

        hist = String.format("(%7f, %7f) - (%7f, %7f) %f\n",
                ball[0], ball[1], ball[2], ball[3], angle);

        if (bounced()) {
            ball[3] = -ball[3];
            toNorm.mapVectors(ballv);
            ballv[1] = -ballv[1];
            fromNorm.mapVectors(ballv);
        }

        ball[0] = ball[2];
        ball[1] = ball[3];

        fromNorm.mapPoints(ball);

        if (ball[0] * ball[0] + ball[1] * ball[1] > rad * rad) {
            served=false;
            for (int h=0; h < bistory.length; h += 2) {
                hist += "("+bistory[h]+", "+ bistory[h+1]+")\n";
            }
        }

        return hist;
    }

    private boolean bounced() {
        float x0 = ball[0] + ball[1] * (ball[2] - ball[0]) / (ball[3] - ball[1]);
        hist += String.format(" x0=%f", x0);
        if (Math.signum(ball[3]) >= 0) {
            if (0 <= x0 && x0 <= 1) {
                return true;
            }
        }
        if (Math.signum(ball[1]) != Math.signum(ball[3])) {
            hist += "Old bounce";
        }
        return false;
    }


    public void setRad(int rad) {
        this.rad = rad;
    }

    public int getRad() {
        return rad;
    }

    public float[] getBat() {
        updateBat();
        return bat;
    }

    private void updateBat() {
        if (batAngle != angle) {
            batAngle = angle;
            bat[0] = (float) (rad * Math.sin((angle - size)/40));
            bat[1] = (float) (rad * Math.cos((angle - size)/40));
            bat[2] = (float) (rad * Math.sin((angle + size)/40));
            bat[3] = (float) (rad * Math.cos((angle + size)/40));
        }
    }

    public float[] getBall() {
        return ball;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
