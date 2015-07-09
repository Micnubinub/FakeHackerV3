package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Michael on 7/9/2015.
 */
public class HackerTextView extends TextView {

    private static Typeface font, fontBold, fontItalic;

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
        if (fontItalic == null) {
            fontItalic = Typeface.createFromAsset(context.getAssets(), "exo_italic.otf");
        }

        setTypeface(font);
    }

    @Override
    public void setTypeface(Typeface tf) {
        switch (tf.getStyle()) {
            case Typeface.BOLD:
                tf = fontBold;
                break;
            case Typeface.ITALIC:
                tf = fontItalic;
                break;
            default:
                tf = font;
                break;
        }
        super.setTypeface(tf);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {

        switch (style) {
            case Typeface.BOLD:
                tf = fontBold;
                break;
            case Typeface.ITALIC:
                tf = fontItalic;
                break;
            default:
                tf = font;
                break;
        }
        super.setTypeface(tf, style);
    }

}
