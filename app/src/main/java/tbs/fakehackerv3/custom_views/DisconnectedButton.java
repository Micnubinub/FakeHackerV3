package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import tbs.fakehackerv3.MainActivity;
import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;

/**
 * Created by Michael on 7/18/2015.
 */
public class DisconnectedButton extends FrameLayout {
    private static final android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(0, 1);
    private static final DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private static final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    private static float animatedValue;
    private static View view;
    private static final Runnable invalidator = new Runnable() {
        @Override
        public void run() {
            try {
                view.invalidate();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };
    private static AnimationType animationType;
    private static FrameLayout container;
    private static int viewHeight, y, setY;
    private static final android.animation.ValueAnimator.AnimatorUpdateListener updateListener = new android.animation.ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
            animatedValue = ((Float) animation.getAnimatedValue());
            update();
        }
    };
    private static Runnable viewHider = new Runnable() {
        @Override
        public void run() {
            try {
                container.setVisibility(VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private static final OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_button)
                hide();
            else
                P2PManager.startScan();
        }
    };

    public DisconnectedButton(Context context) {
        super(context);
        init();
    }

    public DisconnectedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DisconnectedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static void hide() {
        try {
            if (animationType == AnimationType.OUT)
                return;

            animationType = AnimationType.OUT;
            view.post(new Runnable() {
                @Override
                public void run() {
                    animator.start();
                }
            });

            view.postDelayed(viewHider, animator.getDuration() + 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show() {
        try {
            if (animationType == AnimationType.IN)
                return;

            animationType = AnimationType.IN;
            view.post(new Runnable() {
                @Override
                public void run() {
                    container.setVisibility(VISIBLE);
                    animator.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void update() {
        switch (animationType) {
            case IN:
                setY = Math.round(y + (viewHeight - (viewHeight * animatedValue)));
                break;
            case OUT:
                setY = Math.round(y + (viewHeight * animatedValue));
                break;
        }
        container.setY(setY);
        if (view != null)
            view.postDelayed(invalidator, 8);
    }

    private void init() {
        view = View.inflate(getContext(), R.layout.disconnected_button, null);
        view.setOnClickListener(clickListener);
        final HackerTextView text = (HackerTextView) view.findViewById(R.id.text);
        text.setSelected(true);
        view.findViewById(R.id.close_button).setOnClickListener(clickListener);
        container = this;
        addView(view, param);

        animator.setInterpolator(interpolator);
        animator.setDuration(750);
        animator.addUpdateListener(updateListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        try {
            y = MainActivity.mainView.getHeight() - h;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private enum AnimationType {
        IN, OUT
    }
}
