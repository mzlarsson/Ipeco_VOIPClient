package se.chalmers.fleetspeak.gui.connected;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by David Gustafsson on 2015-08-12.
 */
public class CustomViewPager extends ViewPager {
    private boolean enabled;
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabled = false;
    }
    public void setEnabled(boolean b){
        enabled = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return enabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return enabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        return enabled && super.dispatchKeyEvent(event);
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event){
        return enabled && super.executeKeyEvent(event);
    }

    
}
