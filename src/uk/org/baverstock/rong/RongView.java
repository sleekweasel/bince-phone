package uk.org.baverstock.rong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

public class RongView extends View implements Runnable {
    public static final int SK = 150;
    private Paint paint;
    private String TAG = RongView.class.getCanonicalName();
    private float lastx;
    private float angle;
    private int rad;
    private DashPathEffect dashes = new DashPathEffect(new float[]{10, 20}, 0);
    private float[] bat = new float[4];
    private float[] norm = new float[] {0,0, 1,0};
    private float[] ball = new float[4];
    private float[] ballv = new float[] {3,3};
	private float[] bistory = new float[10];
	private int bix = 0;
	private boolean served = false;
    private Paint faint;
    private int height;
    private int width;

    public RongView(Context context) {
        super(context);
        init();
    }

    public RongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setARGB(255, 192, 192, 32);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);

        faint = new Paint();
        faint.setARGB(255, 255, 255, 255);
        faint.setAntiAlias(true);
        faint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float a = (float)Math.atan2(event.getX() - width/2, event.getY() - height/2);
        if (lastx - a > Math.PI) {
            lastx -= 2 * Math.PI;
        }
        if (a - lastx > Math.PI) {
            lastx += 2 * Math.PI;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastx = a;
				if (!served) {
				    served = true;
				    ball[0] = 0;
				    ball[1] = 0;
				}
                break;
            case MotionEvent.ACTION_MOVE:
				if (served) {
                    angle += (a - lastx) * 180;
				}
                lastx = a;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        rad = Math.min(width, height) * 9 / 20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(125, 0, 0, 188);
        canvas.save();
        int size = 10;
        canvas.translate(width/2, height/2);
        paint.setPathEffect(dashes);
        canvas.drawCircle(0,0, rad, paint);
        paint.setPathEffect(null);
        bat[0] = (float) (rad * Math.sin((angle - size)/40));
        bat[1] = (float) (rad * Math.cos((angle - size)/40));
        bat[2] = (float) (rad * Math.sin((angle + size)/40));
        bat[3] = (float) (rad * Math.cos((angle + size)/40));
        canvas.drawRect(-20, -20, 20, 20, paint);
        canvas.drawLine(bat[0], bat[1], bat[2], bat[3], paint);
		
        canvas.drawCircle(ball[0], ball[1], 3, paint);

		for (int h=0; h < bistory.length; h += 2) {
			canvas.drawCircle(bistory[h], bistory[h+1], 3, paint);
		}
		
		if (served) {
			update(canvas);
		}
        
        canvas.restore();

        postDelayed(this, 16);
    }

    private void update(Canvas canvas) {

        //canvas.scale(SK, SK);
        //faint.setStrokeWidth(2.f/SK);

        Matrix toNorm = new Matrix();
        Matrix fromNorm = new Matrix();

        ball[2] = ball[0] + ballv[0];
        ball[3] = ball[1] + ballv[1];

        toNorm.setPolyToPoly(bat, 0, norm, 0, 2);
        fromNorm.setPolyToPoly(norm, 0, bat, 0, 2);

        toNorm.mapPoints(ball);
		
		bistory[bix] = ball[0];
		bistory[bix+1] = ball[1];
		bix = (bix + 2) % bistory.length;

		
        //canvas.drawLine(ball[0], ball[1], ball[2], ball[3], faint);
        //canvas.drawLine(norm[0], norm[1], norm[2], norm[3], faint);

		String hist ="";
		hist += "("+ball[0]+", "+ ball[1]+")\n";
        hist += "("+ball[2]+", "+ ball[3]+")\n\n";
        if (bounced(canvas)) {
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
			
			TextView t = (TextView)getRootView().findViewById(R.id.txt);
			t.setText(hist);
        }
    }

    private boolean bounced(Canvas canvas) {
        float x0 = ball[0] + ball[1] * (ball[2] - ball[0]) / (ball[3] - ball[1]);
        //canvas.drawCircle(0, 0, 3.f/SK, faint);
        //canvas.drawCircle(1, 0, 3.f/SK, faint);
        //canvas.drawCircle(x0, 0, 3.f/SK, faint);
        if (Math.signum(ball[1]) != Math.signum(ball[3])) {
            if (0 <= x0 && x0 <= 1) {
                return true;
            }
        }
		return false;
	}

	@Override
	public void run()
	{
		invalidate();
	}
}
