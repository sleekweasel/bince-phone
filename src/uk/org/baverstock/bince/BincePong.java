package uk.org.baverstock.bince;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

public class BincePong extends View implements Runnable {
    public static final int SK = 150;
    private Paint paint;
    private String TAG = BincePong.class.getCanonicalName();
    private float lastTouchAngle;
    private float angle;
    private DashPathEffect dashes = new DashPathEffect(new float[]{10, 20}, 0);
    private Paint faint;
    private int height;
    private int width;
    private PongGame game;
    private String hist = "";
    TextView tv;

    public BincePong(Context context) {
        super(context);
        init();
    }

    public BincePong(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BincePong(Context context, AttributeSet attrs, int defStyleAttr) {
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

        game = new PongGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchAngle = (float)Math.atan2(event.getX() - width/2, event.getY() - height/2);
        if (lastTouchAngle - touchAngle > Math.PI) {
            lastTouchAngle -= 2 * Math.PI;
        }
        if (touchAngle - lastTouchAngle > Math.PI) {
            lastTouchAngle += 2 * Math.PI;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchAngle = touchAngle;
				if (!game.isServed()) {
				    game.serve();
				}
                break;
            case MotionEvent.ACTION_MOVE:
				if (game.isServed()) {
                    angle += (touchAngle - lastTouchAngle) * 180;
                    game.setAngle(angle);
				}
                lastTouchAngle = touchAngle;
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
        game.setRad(Math.min(width, height) * 9 / 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(125, 0, 0, 188);
        canvas.save();
        canvas.translate(width / 2, height / 2);

        paint.setPathEffect(dashes);
        canvas.drawCircle(0,0, game.getRad(), paint);
        paint.setPathEffect(null);

        canvas.drawRect(-20, -20, 20, 20, paint);

        float[] bat = game.getBat();
        canvas.drawLine(bat[0], bat[1], bat[2], bat[3], paint);

        float[] ball = game.getBall();
        canvas.drawCircle(ball[0], ball[1], 3, paint);

//        float[] bistory = game.getBallHistory();
//        for (int h=0; h < bistory.length; h += 2) {
//			canvas.drawCircle(bistory[h], bistory[h+1], 3, paint);
//		}
		
		if (game.isServed()) {
            String update = game.update();
            while (countOf("\n", hist) > 10) {
                hist = hist.substring(hist.indexOf("\n")+1);
            }
            hist += update;
            if (tv == null) {
                tv = (TextView) getRootView().findViewById(R.id.txt);
            }
            tv.setText(hist);
        }
        
        canvas.restore();

        postDelayed(this, 16);
    }

    private int countOf(String s, String hist) {
        int count = 0;
        int start = 0;
        while ((start = hist.indexOf(s, start)) != -1) {
            ++count;
            ++start;
        }
        return count;
    }

    @Override
	public void run()
	{
		invalidate();
	}
}
