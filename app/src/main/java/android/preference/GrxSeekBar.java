/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

package android.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;

import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;
import com.grx.settings.utils.Common;


public class GrxSeekBar extends GrxBasePreference implements OnSeekBarChangeListener{

    private final String TAG = getClass().getName();
    private int mMax;
    private int mMin;
    private int mInterval;
    private String mUnits;
    private boolean mPopup;

    private int mCurrValue;
    private SeekBar mSeekBar;


    TextView vTxtPopup;
    FrameLayout vPopup;
    TextView vTxtValue;
    TextView vTxtMax;
    TextView vTxtMin;
    private int mAuxValue;


    private int mSeekbarColor=0;




    public GrxSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }


    public GrxSeekBar(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }


    private void setSeekbarColors(){
        if(mSeekbarColor!=0) {


            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {mSeekbarColor, mSeekbarColor};
            mSeekBar.setThumbTintList(new ColorStateList(states, colors));
            //  mSeekBar.getThumb().setTint(mSeekbarColor);
            mSeekBar.getProgressDrawable().setColorFilter(mSeekbarColor, PorterDuff.Mode.MULTIPLY);
            vPopup.setBackgroundColor(mSeekbarColor);
        }

    }




    private void initAttributes(Context context, AttributeSet attrs) {
        setLayoutResource(R.layout.pref_grxseekbar);
        setWidgetLayoutResource(R.layout.pref_grxseekbar_widget);
        initIntPrefsCommonAttributes(getContext(),attrs,0,false);
        mDisableDoubleClick=true;

        // TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);

        TypedArray ta;
        if(Common.mContextWrapper!=null) ta = Common.mContextWrapper.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        else ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);

        mMin = ta.getInt(R.styleable.grxPreferences_minValue,0);
        mMax = ta.getInt(R.styleable.grxPreferences_maxValue,3);
        mUnits = ta.getString(R.styleable.grxPreferences_units);
        mPopup=ta.getBoolean(R.styleable.grxPreferences_showPopup,true);
        mInterval=ta.getInt(R.styleable.grxPreferences_interval,1);

        if(ta.hasValue(R.styleable.grxPreferences_seekbarColor))
            mSeekbarColor = ta.getInt(R.styleable.grxPreferences_seekbarColor, 0);

        ta.recycle();
        if(mUnits==null) mUnits="";

        if(myPrefAttrsInfo.getMyIntDefValue()>=mMin && myPrefAttrsInfo.getMyIntDefValue()<=mMax)
            setDefaultValue(myPrefAttrsInfo.getMyIntDefValue());
        else setMyIntDefaultValue(mMin);

    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.VERTICAL);
        vPopup = (FrameLayout) view.findViewById(R.id.gid_popup);
        vTxtPopup =(TextView) vPopup.findViewById(R.id.gid_popup_txt);
        vTxtValue =(TextView) view.findViewById(R.id.gid_txt);
        View widget = view.findViewById(android.R.id.widget_frame);;
        widget.setPadding(0,0,0,0);
        vTxtMax = (TextView) widget.findViewById(R.id.gid_seekbar_max_value);
        vTxtMin = (TextView) widget.findViewById(R.id.gid_seekbar_min_value);
        if(myPrefAttrsInfo!=null) {
            if(myPrefAttrsInfo.getMySummary().isEmpty()){
                FrameLayout.LayoutParams newpopupparams = (FrameLayout.LayoutParams) vPopup.getLayoutParams();
                newpopupparams.topMargin=0;
                vPopup.setPadding(30,6,30,6);
            }


        }

        return view;
    }

    @Override
    public void onBindView(View view) {
        vTxtMax.setText(String.valueOf(mMax));
        vTxtMin.setText(String.valueOf(mMin));
        mSeekBar = (SeekBar) view.findViewById(R.id.gid_seekbar);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setProgress(mIntValue - mMin);
        vTxtValue.setText(String.valueOf(mIntValue)+ " "+mUnits);
        setSeekbarColors();
        super.onBindView(view);
        if (view != null && !view.isEnabled()) {
            mSeekBar.setEnabled(false);
        }

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int newValue = progress + mMin;
        if (newValue > mMax)
            newValue = mMax;
        else if (newValue < mMin)
            newValue = mMin;
        else if (mInterval != 1 && newValue % mInterval != 0)
            newValue = Math.round(((float) newValue) / mInterval) * mInterval;
    /*  //if someone wants real time changes the following code should be executed. I prefer to update view and values only when the change of value is true

        if (!callChangeListener(newValue)) {
            seekBar.setProgress(mCurrValue - mMin);
            return;
        }*/
        mIntValue = newValue;
        seekBar.setProgress(mIntValue - mMin);
        if(vTxtValue!=null) vTxtValue.setText(String.valueOf(mIntValue)+ " "+mUnits);
        if(mUnits.isEmpty()) vTxtValue.setText(String.valueOf(mIntValue)+ " "+mUnits);
        if(mPopup) {
            if(mUnits.isEmpty()) vTxtPopup.setText(String.valueOf(mIntValue));
            else vTxtPopup.setText(String.valueOf(mIntValue)+ " "+mUnits);
        }
        //persistInt(newValue);  // un-comment for real time changes. BUT do not use customized dependencies if your seekbar´s values range is big and interval little or it will be laggy.
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mPopup) vPopup.setVisibility(View.VISIBLE);
        mAuxValue = mIntValue;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mPopup) vPopup.setVisibility(View.INVISIBLE);
        if(mIntValue!= mAuxValue){
            if(getKey()==null || getKey().isEmpty()) return;
            //persistInt(mIntValue);
            saveNewIntValue(mIntValue);
            //     saveintValueInSettings(mIntValue);
            //callChangeListener(mIntValue);
            //notifyChanged();

            //sendBroadcastsAndChangeGroupKey();
        }
    }



    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {


        int real;
        if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED) {

            if (restoreValue) {
                mIntValue = getPersistedInt(myPrefAttrsInfo.getMyIntDefValue());
            } else {
                int temp = 0;
                try {
                    temp = (Integer) defaultValue;
                } catch (Exception ex) {
                }

                mIntValue = temp;
                if (getKey() == null || getKey().isEmpty()) return;
                persistInt(temp);
            }

            if (myPrefAttrsInfo.isAllowedToBeSavedInSettingsDb()) {
                if (!myPrefAttrsInfo.isValidKey()) return;
                try {
                    real = Settings.System.getInt(getContext().getContentResolver(), this.getKey());
                    if (real != mIntValue) {
                        Settings.System.putInt(getContext().getContentResolver(), this.getKey(), mIntValue);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            updateFromSettingsValue();
        }
    }

    /**
     * make sure that the seekbar is disabled if the preference is disabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(mSeekBar!=null) mSeekBar.setEnabled(enabled);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
        if (mSeekBar != null) {
            mSeekBar.setEnabled(!disableDependent);
        }
    }


}
