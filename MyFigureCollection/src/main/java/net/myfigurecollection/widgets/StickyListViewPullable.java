package net.myfigurecollection.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

/**
 * Created by Climbatize on 31/12/13.
 */
public class StickyListViewPullable extends StickyListHeadersListView implements ViewDelegate {
    public StickyListViewPullable(Context context) {
        super(context);
    }

    public StickyListViewPullable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyListViewPullable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isReadyForPull(View view, float v, float v2) {
        View childView = getWrappedList().getChildAt(0);
        int top = (childView == null) ? 0 : childView.getTop();
        return top >= 0;
    }
}
