package se.chalmers.fleetspeak.gui.connected;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by David Gustafsson on 2015-08-12.
 */
public class CustomViewPager extends ViewPager implements ActionBar.TabListener{

    private ActionBar actionBar;
    private List<AppConnectFragment> fragmentList;
    private List<Fragment> specialFragmentsList;
    private HashMap<String, Integer> indexMap;
    private boolean enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabled = false;
        fragmentList = new ArrayList<>();
        specialFragmentsList = new ArrayList<>();
        indexMap = new HashMap<>();

        this.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < fragmentList.size()) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });
    }

    public void setEnabled(boolean b){
        enabled = b;
    }

    public void setHostActivity(ActionBarActivity activity){
        this.setAdapter(new PagerAdapter(activity.getSupportFragmentManager()));

        this.actionBar = activity.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void addTab(AppConnectFragment fragment, int drawableIcon, int nameResource){
        addTab(fragment, drawableIcon, nameResource, null);
    }

    public void addTab(AppConnectFragment fragment, int drawableIcon, int nameResource, String fragmentName){
        if(actionBar == null){
            throw new IllegalStateException("Cannot add tabs without an initiated actionbar.");
        }

        //Add fragment to list
        fragmentList.add(fragment);

        //Setup tab
        ActionBar.Tab tab = actionBar.newTab();
        tab.setIcon(drawableIcon);
        String name = getResources().getString(nameResource);
        tab.setText(name);
        tab.setTabListener(this);
        actionBar.addTab(tab);

        //Save index
        if(fragmentName != null){
            indexMap.put(fragmentName, fragmentList.size()-1);
        }
    }

    public void addSpecialTab(Fragment fragment, String fragmentName){
        specialFragmentsList.add(fragment);
        indexMap.put(fragmentName, specialFragmentsList.size()-1);
    }

    public void displayTabs(){
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        this.getAdapter().notifyDataSetChanged();
    }

    public int getTabIndex(String name, boolean special){
        Integer index = indexMap.get(name);
        if(index == null){
            return -1;
        }else if(special){
            return fragmentList.size()+index;
        }else{
            return index;
        }
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        Log.d("ConnectionActivity", " clicked tab" + tab.getPosition());
        if(this.getCurrentItem() < fragmentList.size()) {
            this.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {}
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {}


    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int index) {
            if (index < fragmentList.size()) {
                return fragmentList.get(index);
            } else if (index >= fragmentList.size()) {
                return specialFragmentsList.get(index-fragmentList.size());
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragmentList.size()+specialFragmentsList.size();
        }
    }
}
