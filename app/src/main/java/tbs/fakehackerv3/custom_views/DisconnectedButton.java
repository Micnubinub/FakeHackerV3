package tbs.fakehackerv3.custom_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import tbs.fakehackerv3.P2PManager;
import tbs.fakehackerv3.R;

/**
 * Created by Michael on 7/18/2015.
 */
public class DisconnectedButton extends FrameLayout {
    private static final android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(0, 1);
    private static final DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private static final LayoutParams param = new LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private static final View.OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_button)
                hide();
            else
                P2PManager.startScan();
        }
    };
    private static float animatedValue;
    private static View view;
    private static final Runnable invalidator = new Runnable() {
        @Override
        public void run() {
            try {
                view.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private static AnimationType animationType;
    private static final android.animation.ValueAnimator.AnimatorListener listener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            switch (animationType) {
                case IN:
                    animationType = AnimationType.IDLING;
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hide();
                        }
                    }, 5500);
                    break;
                case OUT:
                    try {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setVisibility(GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case IDLING:
                    hide();
                    break;
            }
        }
    };
    private static int viewHeight;
    private static final android.animation.ValueAnimator.AnimatorUpdateListener updateListener = new android.animation.ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
            animatedValue = ((Float) animation.getAnimatedValue());
            update();
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

    }

    public static void show() {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void update() {
        switch (animationType) {
            case IN:
                view.setY((viewHeight * animatedValue) - viewHeight);
                break;
            case OUT:
                view.setY(-(viewHeight * animatedValue));
                break;
            case IDLING:
                view.setY(0);
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
        addView(view, param);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
    }

    private enum AnimationType {
        IN, OUT, IDLING
    }
}
