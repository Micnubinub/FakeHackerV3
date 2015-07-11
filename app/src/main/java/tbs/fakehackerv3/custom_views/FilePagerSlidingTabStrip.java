/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tbs.fakehackerv3.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import tbs.fakehackerv3.R;


public class FilePagerSlidingTabStrip extends FrameLayout {

    public static OnPageChangeListener delegatePageListener;
    private static ViewPager pager;
    private static int currentPosition;
    private static HackerTextView localTitle, externalTitle;
    private final PageListener pageListener = new PageListener();
    private final View.OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.local:
                    selectPage(0);
                    break;
                case R.id.external:
                    selectPage(1);
                    break;
            }
        }
    };

    public FilePagerSlidingTabStrip(Context context) {
        this(context, null);
        init(context);
    }

    public FilePagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public FilePagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public static void selectPage(int i) {
        i %= 2;
        if (pager != null)
            pager.setCurrentItem(i);
        scrollToChild(i);
    }

    public static void scrollToChild(int pos) {
        pos %= 2;
        localTitle.setTextColor((pos == 0) ? 0xffffffff : 0xff999999);
        externalTitle.setTextColor((pos == 1) ? 0xffffffff : 0xff999999);
    }

    public void init(Context context) {
        final View view = View.inflate(context, R.layout.file_manager_titles, null);
        localTitle = (HackerTextView) view.findViewById(R.id.local);
        externalTitle = (HackerTextView) view.findViewById(R.id.external);

        externalTitle.setTypeFaceStyle(Typeface.BOLD);
        localTitle.setTypeFaceStyle(Typeface.BOLD);

        localTitle.setOnClickListener(listener);
        externalTitle.setOnClickListener(listener);

        selectPage(0);
        addView(view);
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position + Math.round(positionOffset);
            scrollToChild(currentPosition);
            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                scrollToChild(pager.getCurrentItem());

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {

            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

}
