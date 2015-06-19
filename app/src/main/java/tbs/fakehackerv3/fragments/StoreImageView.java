package tbs.fakehackerv3.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Michael on 6/18/2015.
 */
public class StoreImageView extends ImageView {
    public float innerCircleScale = .9f;
    public int innerCircleColor = 0xffffbb00;
    public int outerCircleColor = 0xff00bbff;
    private static final Paint paint = new Paint();
    private int cx, cy, r;

    public StoreImageView(Context context) {
        super(context);
    }

    public StoreImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoreImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.innerCircleColor = innerCircleColor;
    }

    public void setInnerCircleScale(float innerCircleScale) {
        this.innerCircleScale = innerCircleScale;
    }

    public void setOuterCircleColor(int outerCircleColor) {
        this.outerCircleColor = outerCircleColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(outerCircleColor);
        canvas.drawCircle(cx, cy, r, paint);
        paint.setColor(innerCircleColor);
        canvas.drawCircle(cx, cy, r * innerCircleScale, paint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2;
        cy = h / 2;
        r = Math.min(cy, cx);
    }
}
