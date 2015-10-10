package se.chalmers.fleetspeak.structure.connected;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by David Gustafsson on 2015-08-12.
 */
public class CViewPager extends ViewPager {
    private boolean enabled;
    public CViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabled = false;
    }
    public void setEnabled(boolean b){
        enabled = b;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(enabled){
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(enabled){
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
