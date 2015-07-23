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
    private static final LayoutParams param = new LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private static float animatedValue;
    private static View view;
    private static final Runnable invalidator = new Runnable() {
        @Override
        public void run() {
            try {
                view.invalidate();
                MainActivity.mainView.requestLayout();
                MainActivity.mainView.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private static AnimationType animationType;
    private static FrameLayout container;
    private static int viewHeight, y;
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
    private static final View.OnClickListener clickListener = new OnClickListener() {
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
            container.setVisibility(VISIBLE);
            animationType = AnimationType.IN;
            view.post(new Runnable() {
                @Override
                public void run() {

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
                container.setY(y + (viewHeight - (viewHeight * animatedValue)));
                break;
            case OUT:
                container.setY(y + (viewHeight * animatedValue));
                break;
        }
        if (view != null)
            view.postDelayed(invalidator, 8);
    }

    private void init() {
        view = View.inflate(getContext(), R.layout.disconnected_button, null);
        view.setOnClickListener(clickListener);
        final HackerTextView text = (HackerTextView) view.findViewById(R.id.text);
        text.setSelected(true);
        final HackerTextView closeButton = (HackerTextView) view.findViewById(R.id.close_button);
        closeButton.setTextColor(0xffd01716);
        closeButton.setOnClickListener(clickListener);
        closeButton.setBackgroundResource(R.drawable.round_rect);
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
        y = MainActivity.mainView.getHeight() - h;
    }

    private enum AnimationType {
        IN, OUT
    }
}
