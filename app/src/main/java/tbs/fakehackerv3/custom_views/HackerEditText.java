package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Michael on 7/9/2015.
 */
public class HackerEditText extends EditText {

    private static Typeface font, fontBold, fontItalic;

    public HackerEditText(Context context) {
        super(context);
        init(context);
    }

    public HackerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HackerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setTextColor(0xff22ccff);
        setHintTextColor(0xff0a99dd);
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
