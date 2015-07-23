package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import tbs.fakehackerv3.Tools;

/**
 * Created by Michael on 7/9/2015.
 */
public class HackerTextView extends TextView {

    private static Typeface font, fontBold;

    public HackerTextView(Context context) {
        super(context);
        init(context);
    }

    public HackerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HackerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        if (font == null) {
            font = Typeface.createFromAsset(context.getAssets(), "exo.otf");
        }

        if (fontBold == null) {
            fontBold = Typeface.createFromAsset(context.getAssets(), "exo_bold.otf");
        }
        setTypeface(font);
        setTextColor(Tools.getTextColor(context));
//        setBackgroundColor(Tools.getBackgroundColor(context));
    }

    public void setTypeFaceStyle(int typeFace) {
        switch (typeFace) {
            case Typeface.BOLD:
                setTypeface(fontBold);
                break;
            default:
                setTypeface(font);
                break;
        }
    }

    @Override
    public void setTypeface(Typeface tf) {
        if (tf != null) {
            switch (tf.getStyle()) {
                case Typeface.BOLD:
                    tf = fontBold;
                    break;
                default:
                    tf = font;
                    break;
            }
        } else {
            tf = font;
        }
        super.setTypeface(tf);
    }

    @Override
    protected void onAttachedToWindow() {
        setTypeface(getTypeface());
        super.onAttachedToWindow();
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        switch (style) {
            case Typeface.BOLD:
                tf = fontBold;
                break;
            default:
                tf = font;
                break;
        }
        super.setTypeface(tf, style);
    }

}
