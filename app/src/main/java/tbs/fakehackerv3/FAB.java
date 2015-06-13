package tbs.fakehackerv3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by Michael on 6/13/2015.
 */
public class FAB extends ImageView {
    private static int cr, cx, cy;
    private static final Paint paint = new Paint();
    private static State state = State.IDLE;
    private long lastUpdate, rotation;
    private static Bitmap scanningBitmap, tickBitmap;
    private ValueAnimator animator = new ValueAnimator(ValueAnimator.Interpolator.DECELERATE);
    //ROTATION
    private static final Matrix rotationMatrix = new Matrix();

    public FAB(Context context) {
        super(context);
        init();

    }

    public FAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setState(State.SCANNING);
        setBackgroundResource(R.drawable.bg_circle);
    }

    public void setState(State state) {
        FAB.state = state;

        switch (state) {
            case SCANNING:
                setImageResource(R.drawable.refresh);
                animator.start();
                break;
            case IDLE:
                setImageResource(R.drawable.refresh);
                break;
            case CONNECTING:
                setImageDrawable(null);
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lastUpdate = System.currentTimeMillis();


        switch (state) {
            case CONNECTING:

                break;
            case SCANNING:
                if (animator.isRunning())
                    animator.update();
//                if (refreshBitmap != null)
//                    canvas.drawBitmap(refreshBitmap, rotationMatrix, paint);
                break;
            case IDLE:

                break;
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final int r = dpToPixels(40);
        scanningBitmap = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.scanning), r, r);
        tickBitmap = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tick_green), r, r);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scanningBitmap.recycle();
        tickBitmap.recycle();
    }

    private void setBitmapRotation(float rotation) {
        rotationMatrix.reset();
//        rotationMatrix.setTranslate(cx, cx);
        rotationMatrix.postRotate(rotation, cx, cy);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        final Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = dpToPixels(8);
        cy = dpToPixels(8);
        cr = (Math.min(w, h) / 2) - cx - cx;
    }

    public enum State {
        SCANNING, IDLE, CONNECTING
    }

    private final ValueAnimator.UpdateListener updateListener = new ValueAnimator.UpdateListener() {
        @Override
        public void update(double animatedValue) {
            setBitmapRotation((float) animatedValue);
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationFinish() {

        }
    };
}
