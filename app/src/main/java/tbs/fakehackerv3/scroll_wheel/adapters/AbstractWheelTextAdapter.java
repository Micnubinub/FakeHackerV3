/*
 * android-spinnerwheel
 * https://github.com/ai212983/android-spinnerwheel
 *
 * based on
 *
 * Android Wheel Control.
 * https://code.google.com/p/android-wheel/
 *
 * Copyright 2011 Yuri Kanivets
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tbs.fakehackerv3.scroll_wheel.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tbs.fakehackerv3.custom_views.HackerTextView;

/**
 * Abstract spinnerwheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

    /**
     * Text view resource. Used as a default view for adapter.
     */
    private static final int TEXT_VIEW_ITEM_RESOURCE = -1;
    /**
     * Default text color
     */
    private static final int DEFAULT_TEXT_COLOR = 0xFF555555;
    /**
     * Default text size
     */
    private static final int DEFAULT_TEXT_SIZE = 24;
    /**
     * No resource constant.
     */
    private static final int NO_RESOURCE = 0;
    // Text settings
    private final int textColor = DEFAULT_TEXT_COLOR;
    private final int textSize = DEFAULT_TEXT_SIZE;
    // Current context
    private final Context context;
    // Layout inflater
    private final LayoutInflater inflater;
    // Items resources
    private final int itemResourceId;
    private final int itemTextResourceId;
    // Empty items resources
    private int emptyItemResourceId;
    /// Custom text typeface
    private Typeface textTypeface;


    /**
     * Constructor
     *
     * @param context the current context
     */
    AbstractWheelTextAdapter(Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     *
     * @param context      the current context
     * @param itemResource the resource ID for a layout file containing a HackerTextView to use when instantiating items views
     */
    private AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, NO_RESOURCE);
    }

    /**
     * Constructor
     *
     * @param context          the current context
     * @param itemResource     the resource ID for a layout file containing a HackerTextView to use when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     */
    private AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
        this.context = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Returns text for specified item
     *
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            HackerTextView HackerTextView = getHackerTextView(convertView, itemTextResourceId);

            if (HackerTextView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                HackerTextView.setText(text);
                configureHackerTextView(HackerTextView);
            }
            return convertView;
        }
        return null;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(emptyItemResourceId, parent);
        }
        if (convertView instanceof HackerTextView) {
            configureHackerTextView((HackerTextView) convertView);
        }

        return convertView;
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     *
     * @param view the text view to be configured
     */
    void configureHackerTextView(HackerTextView view) {
        if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
            view.setTextColor(textColor);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(textSize);
            view.setLines(1);
        }
        if (textTypeface != null) {
            view.setTypeface(textTypeface);
        } else {
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
    }

    /**
     * Loads a text view from view
     *
     * @param view         the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private HackerTextView getHackerTextView(View view, int textResource) {
        HackerTextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof HackerTextView) {
                text = (HackerTextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (HackerTextView) view.findViewById(textResource);
            }
        } catch (ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a HackerTextView");
            throw new IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a HackerTextView", e);
        }

        return text;
    }

    /**
     * Loads view from resources
     *
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(int resource, ViewGroup parent) {
        switch (resource) {
            case NO_RESOURCE:
                return null;
            case TEXT_VIEW_ITEM_RESOURCE:
                return new HackerTextView(context);
            default:
                return inflater.inflate(resource, parent, false);
        }
    }
}
