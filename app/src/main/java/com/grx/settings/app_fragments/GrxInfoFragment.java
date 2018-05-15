

/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.app_fragments;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.grx.settings.utils.Common;
import com.grx.settings.GrxSettingsActivity;
import com.grx.settings.R;
import com.fab.ObservableScrollView;
import com.grx.settings.utils.SlidingTabLayout;



public class GrxInfoFragment extends Fragment {

    ViewPager mViewPager;

    private int mNumOfTabs;
    private String[] mTabsNames;
    private String[] mTabsLayouts;
    private ObservableScrollView[] mViews;

    public GrxInfoFragment() {
    }

    public interface onSlidingTabChanged {
        public void SetObservableScrollView(ObservableScrollView observableScrollView);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isdemo = getResources().getBoolean(R.bool.grxb_demo_mode);

        mTabsLayouts=  isdemo ? getResources().getStringArray(R.array.demo_tabs_layouts) : getResources().getStringArray(R.array.rom_tabs_layouts);
        mTabsNames= isdemo ? getResources().getStringArray(R.array.demo_tabs_names) : getResources().getStringArray(R.array.rom_tabs_names);
        mNumOfTabs=0;
        if(mTabsLayouts!=null) mNumOfTabs=mTabsLayouts.length;
        create_views();
    }

    private ObservableScrollView get_view(String layout_name){
        return (ObservableScrollView) View.inflate(getActivity(),getResources().getIdentifier(layout_name,"layout",getActivity().getPackageName()),null);
    }


    public void create_views(){
        mViews=new ObservableScrollView[mNumOfTabs];
        for(int i=0; i<mNumOfTabs;i++){
            mViews[i] = get_view(mTabsLayouts[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        switch (mNumOfTabs){
            case 0: return null;
            //case 1: return mViews[0];
            default:
                View view = inflater.inflate(R.layout.grx_info_tabs, container, false);
                mViewPager = (ViewPager) view.findViewById(R.id.gid_viewpager);
                mViewPager.setAdapter(new CustomPagerAdapter(getActivity()));
                // Assiging the Sliding Tab Layout View
                final SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.gid_tabs);
                tabs.setDistributeEvenly(false);
                //grx tab indicator color
                TypedArray a = getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.tabs_indicator_color});
                final int color_tint = a.getColor(0, 0);
                a.recycle();

                tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return color_tint;
                    }
                });
                tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        GrxSettingsActivity grxsettingsactivity = (GrxSettingsActivity) getActivity();
                        if (grxsettingsactivity != null)
                            grxsettingsactivity.SetObservableScrollView((ObservableScrollView) mViews[position]);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                int pos= 0;
                if(Common.sp.getBoolean(Common.S_APPOPT_REMEMBER_SCREEN, getResources().getBoolean(R.bool.grxb_remember_screen_default))) pos= Common.sp.getInt("tab_pos",0);
                if(pos<mViewPager.getAdapter().getCount()) {
                    mViewPager.setCurrentItem(pos);
                }
                tabs.setViewPager(mViewPager);
                if(mNumOfTabs==1) tabs.setVisibility(View.GONE);

                return view;

        }




    }



    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            collection.addView( mViews[position]);
            return mViews[position];
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsNames[position];
        }

    }

    @Override
    public void onDestroyView(){
        if(mNumOfTabs>1) Common.sp.edit().putInt("tab_pos",mViewPager.getCurrentItem()).commit();
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

}
