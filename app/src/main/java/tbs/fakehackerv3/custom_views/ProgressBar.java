package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Michael on 7/24/2015.
 */
public class ProgressBar extends View {
    private static final Paint paint = new Paint();
    private static float progress;
    private static int width, height;
    private static View view;
    private static Runnable hide = new Runnable() {
        @Override
        public void run() {
            view.setVisibility(GONE);
        }
    };
    private static Runnable show = new Runnable() {
        @Override
        public void run() {
            view.setVisibility(VISIBLE);
        }
    };

    public ProgressBar(Context context) {
        super(context);
        init(context);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public static void setProgress(float progress) {
        ProgressBar.progress = progress;
        if (progress >= 0.9999f) {
            view.post(hide);
        } else {
            view.post(show);
        }

    }

    public void init(Context context) {
        paint.setColor(0xffffbb00);
//        paint.setColor(Tools.getTextColor(context));
        view = this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, width * (progress / 100f), height, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}
