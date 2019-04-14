package com.grx.settings.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.utils.Common;
import com.grx.settings.R;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.LinkedList;




/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

public class GrxFloatingRecents extends FrameLayout {
    private WindowManager mWindowmanager;
    public WindowManager.LayoutParams mLayoutParams;
    public int lastX, lastY;
    public int paramX, paramY;
    public int mTextColor;
    ListView mListView;
    private boolean mPendingUpdate = false;
    private float mAlpha=readSavedAlphaValue();
    private boolean mWasMoving =false;
    private boolean mHideWithBackKey=false;
    private boolean mHideWhenOutside=false;

    private int mMaxSeen;
    private int mMaxManaged;

    private int mNumClicks;
    private Runnable DoubleClickRunnable;
    public android.os.Handler mHandler;
    private boolean mDoubleClickPending;
    private long mDoubleTapTimeOut = Long.valueOf(ViewConfiguration.getDoubleTapTimeout());



    int textviewHeight;

    LinkedList<ScreenInfo> mLastScreens;

    GrxFloatingRecents.OnGrxFWsetScreenCallback mCallback=null;



    public GrxFloatingRecents(Context context, WindowManager wm, int bgcolor, int contrastcolor) {
        super(context);
        mNumClicks =0;
        mWindowmanager=wm;
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;

        mMaxManaged=getContext().getResources().getInteger(R.integer.grxi_floatingrecents_total_items);
        mMaxSeen=getContext().getResources().getInteger(R.integer.grxi_floatingrecents_max_items_seen);

        mHandler = new android.os.Handler();

        textviewHeight = GrxPrefsUtils.dip_to_pixels(getContext(),50);
        /* adding WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE makes the keyboard to getfocusable but
        back key methods added here are useless. For now i will leave in this way
         */
        mLayoutParams = //new WindowManager.LayoutParams(200,100,0x830,0x40008,-0x3 );
                new WindowManager.LayoutParams(
                        (int) ((float)width/2.2f), ViewGroup.LayoutParams.WRAP_CONTENT,
                      //  WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.TYPE_APPLICATION, // FOR BETTER COMPATIBILITY WITH ALL TARGET SDKs
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSPARENT);

        int flags = mLayoutParams.flags;
        flags = flags | 0x1000000;
        mLayoutParams.flags=flags;

        mLayoutParams.gravity=0x33;
        loadCoordinates();

         GradientDrawable gradientDrawable = new GradientDrawable();
         gradientDrawable.setCornerRadius(12f);
         gradientDrawable.setColor(bgcolor);
         setBackground(gradientDrawable);

        mTextColor=contrastcolor;


        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getContext());
        textView.setText(getContext().getText(R.string.grxs_recents_screens_title));
        textView.setTextColor(contrastcolor);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0,5,0,5);
        linearLayout.addView(textView);

        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3));
        imageView.setBackgroundColor(contrastcolor);
        imageView.setPadding(12,5,12,5);
        linearLayout.addView(imageView);

        mLastScreens = new LinkedList<>();

        mListView = new ListView(getContext());
        mListView.setDividerHeight(0);
        mListView.setMinimumHeight(0);
        mListView.setScrollbarFadingEnabled(true);

        mListView.setPadding(0,20,0,10);
/*        int[] attrs = new int[] { android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        mSelectorBg = ta.getDrawable(0 );
        ta.recycle();
        mListView.setSelector(mSelectorBg);
*/
        mListView.setAdapter(mAdapter);
        linearLayout.addView(mListView);
        addView(linearLayout);
        setPadding(10,8,10,8);
        setUpDoubleTapUp();
        setAlpha(mAlpha);
    }


    private void setUpDoubleTapUp(){
        mHandler = new Handler();
        mDoubleClickPending =false;
        DoubleClickRunnable = new Runnable() {
            @Override
            public void run() {
                if(mDoubleClickPending && mNumClicks == 2 ){
                    mNumClicks=0;
                    mDoubleClickPending =false;
                    if(mAlpha>=1.0f) mAlpha=0.4f;
                    else mAlpha=mAlpha+0.1f;
                    saveAlphaValue(mAlpha);
                    setAlpha(mAlpha);

                 }else {
                    mHandler.removeCallbacks(DoubleClickRunnable);
                    mNumClicks=0;
                    mDoubleClickPending =false;
                }
            }
        };
    }


    private void updateRecents(){
        if(mPendingUpdate && getVisibility()==VISIBLE) {
            int items = mLastScreens.size();
            if(items>mMaxSeen) mListView.getLayoutParams().height=mMaxSeen*textviewHeight;
            else mListView.getLayoutParams().height=items*textviewHeight;
            invalidate();
            mAdapter.notifyDataSetChanged();
            mPendingUpdate=false;
        }
    }

    @Override
    public void setVisibility(int visibility){
        super.setVisibility(visibility);
        if(visibility==VISIBLE) {
            updateRecents();
        }
    }

    public void clearRecents(){
        if(mLastScreens!=null) mLastScreens.clear();
    }

    public void setGrxFWCallBack(GrxFloatingRecents.OnGrxFWsetScreenCallback callBack){
        mCallback=callBack;
    }

    public String getRecentsStringValueToSave(){
        if(mLastScreens==null || mLastScreens.isEmpty()) return "";
        else {
            String value ="";
            for(int i=0; i<mLastScreens.size();i++) value+=mLastScreens.get(i).getmScreenName()+"|";
            return value;
        }
    }

    public interface OnGrxFWsetScreenCallback{
        void setScreenFromGrxFW(String xml_name, int id);

        void backPressedFromGrxFW();
    }

    private int getPositionForXmlName(String xml_name){
        int postiion = -1;
        for(int i=0;i<mLastScreens.size();i++){
            if(mLastScreens.get(i).getmScreenName().equals(xml_name)){
                postiion=i;
                break;
            }
        }
        return postiion;
    }


    public void addScreen(String title, String xml_name, int id){
        int index = getPositionForXmlName(xml_name);
        if(index==-1){ // it does not exist
            if(mLastScreens.size()>=mMaxManaged) mLastScreens.removeLast();
        }else{
            mLastScreens.remove(index);
        }
        mLastScreens.addFirst(new ScreenInfo(title,xml_name, id));;
        mPendingUpdate=true;
        Common.sp.edit().putString(Common.S_CTRL_RECENTS_SCREENS,getRecentsStringValueToSave()).commit();
        updateRecents();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(getVisibility()==GONE) {
            return super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
        if(action == MotionEvent.ACTION_OUTSIDE) {
            if(mHideWhenOutside) setVisibility(GONE);
        }else{

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    paramX = mLayoutParams.x;
                    paramY = mLayoutParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    setAlpha(0.4f);
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    mLayoutParams.x = paramX + dx;
                    mLayoutParams.y = paramY + dy;
                    mWindowmanager.updateViewLayout(this,mLayoutParams);
                    mWasMoving =true;
                    break;
                 default:
                     break;

                case MotionEvent.ACTION_UP:
                    if(mWasMoving && !mDoubleClickPending) {
                        setAlpha(mAlpha);
                        saveCoordinates();
                    }
                    mNumClicks++;
                    if(!mDoubleClickPending){
                        mHandler.removeCallbacks(DoubleClickRunnable);
                        mDoubleClickPending =true;
                        mHandler.postDelayed(DoubleClickRunnable, mDoubleTapTimeOut);
                        }
                    mWasMoving =false;

                   break;
            }
        }
        return true;
    }


        public void setHideOptions(boolean back, boolean outside){
            mHideWithBackKey = back;
            mHideWhenOutside = outside;
        }

       private void loadCoordinates(){
            try{
                mLayoutParams.y=Common.sp.getInt("fw_y",300);
                mLayoutParams.x=Common.sp.getInt("fw_x",0);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        private float readSavedAlphaValue(){
           float alpha = 1.0f;

            try{
              alpha=Common.sp.getFloat("fw_alpha",1.0f);
            }catch (Exception e){
                e.printStackTrace();
            }
            return alpha;
        }

        private void saveAlphaValue(float alpha){
            try{
                Common.sp.edit().putFloat("fw_alpha",alpha).commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void saveCoordinates(){
                try{
                    Common.sp.edit().putInt("fw_x",mLayoutParams.x).commit();
                    Common.sp.edit().putInt("fw_y",mLayoutParams.y).commit();
                }catch (Exception e){
                    e.printStackTrace();
                }
        }

        public WindowManager.LayoutParams getLayoutParams(){
        return mLayoutParams;

    }


    /* for now back key always hides the floating area **/

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if(getVisibility()==View.GONE) return super.dispatchKeyEvent(event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            if(event.getAction()==KeyEvent.ACTION_UP){
                /*if(mHideWithBackKey) */ setVisibility(View.GONE);
                /*else { */
                 //   if(mCallback!=null) mCallback.backPressedFromGrxFW();
                //}
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {

            return mLastScreens.size();

        }

        @Override
        public Object getItem(int i) {
            return mLastScreens.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            HorizontalScrollView horizontalScrollView= new HorizontalScrollView(getContext());
            horizontalScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textviewHeight));

            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(mTextColor);


            textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,textviewHeight,TEXT_ALIGNMENT_CENTER));
            textView.setSingleLine();
            textView.setClickable(true);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(4,0,4,0);

            ScreenInfo screenInfo = (ScreenInfo) getItem(i);

           if(screenInfo!=null){
               textView.setTag(screenInfo.getmScreenName());
               textView.setText(screenInfo.getScreenTitle());
               horizontalScrollView.addView(textView);
               horizontalScrollView.setHorizontalFadingEdgeEnabled(true);
               horizontalScrollView.setHorizontalScrollBarEnabled(true);
               horizontalScrollView.setScrollBarSize(3);
               textView.setTag(String.valueOf(i));
               textView.setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       int pos = Integer.valueOf( (String) view.getTag() );
                       if(mCallback!=null) mCallback.setScreenFromGrxFW(mLastScreens.get(pos).getmScreenName(), mLastScreens.get(pos).getScreenId());
                   }
               });
               return horizontalScrollView;
           }
            return null;
        }
    };


    private void show_toast(String text){
        Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
    }


    private class ScreenInfo {
            String mTitle="";
            String mScreenName="";
            int mId = 0;

            public ScreenInfo(String screen_title, String screen_name, int id){
                mTitle=screen_title;
                mScreenName=screen_name;
                mId = id;
            }

            public String getmScreenName(){return mScreenName;}
            public String getScreenTitle(){return mTitle;}
            public int getScreenId(){return mId;}
    }

}
