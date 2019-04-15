/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefssupport.OnClickRuleHelper;
import com.grx.settings.utils.Common;
import com.grx.settings.prefssupport.PrefAttrsInfo;
import com.grx.settings.utils.GrxPrefsUtils;


public class GrxSwitchPreference extends SwitchPreference implements GrxPreferenceScreen.CustomDependencyListener {


    ImageView vAndroidIcon;
    public PrefAttrsInfo myPrefAttrsInfo;
    private SwitchCompat mSwitch;

    private int mColor=0, mTrackColor=0;
    private int mLefticonColor =0;


    public GrxSwitchPreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        ini_preference(context, attrs);
    }

    public GrxSwitchPreference(Context context) {
        super(context);
    }

    public GrxSwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ini_preference(context, attrs);
    }




    private void ini_preference(Context context, AttributeSet attrs){

        TypedArray ta;
        if(Common.mContextWrapper!=null) ta = Common.mContextWrapper.obtainStyledAttributes(attrs, R.styleable.grxSwitchPreference);
        else ta = context.obtainStyledAttributes(attrs, R.styleable.grxSwitchPreference);
        //TypedArray ta = getContext().obtainStyledAttributes(att, R.styleable.grxSwitchPreference);
        if(ta.hasValue(R.styleable.grxSwitchPreference_switchColor)) {
            try {
                mColor = ta.getColor(R.styleable.grxSwitchPreference_switchColor, 0);
            } catch (Exception e) {
            }
        }
        ta.recycle();
        setWidgetLayoutResource(R.layout.switch_widget);
        if(mColor!=0) {
            mTrackColor =  Color.argb(Color.alpha(mColor)/4, Color.red(mColor),Color.green(mColor),Color.blue(mColor));
        }
        myPrefAttrsInfo = new PrefAttrsInfo(context, attrs, getTitle(), getSummary(),getKey());
        mLefticonColor = myPrefAttrsInfo.getMyIconTintColor();

    }





    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = (View) super.onCreateView(parent);
        vAndroidIcon = (ImageView) view.findViewById(android.R.id.icon);
        mSwitch = (SwitchCompat) view.findViewById(R.id.switch_ctrl);
        if(mColor!=0){
            if(isChecked() && isEnabled()) {
                mSwitch.getThumbDrawable().setTint(mColor);
                mSwitch.getTrackDrawable().setTint(mTrackColor);

            }
        }

        mSwitch.setChecked(isChecked());
        if(vAndroidIcon!=null) {
            vAndroidIcon.setLayoutParams(Common.AndroidIconParams);
            if(mLefticonColor !=0) vAndroidIcon.setColorFilter(mLefticonColor);
        }
        return view;
    }


    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        float alpha = (isEnabled() ? (float) 1.0 : (float) 0.4);
        if(vAndroidIcon!=null) vAndroidIcon.setAlpha(alpha);

        if(mSwitch!=null)    mSwitch.setAlpha(alpha);

    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        boolean defvalue= a.getBoolean(index,false);
        return defvalue;
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        int real = myPrefAttrsInfo.getMyBooleanDefValue() ? 1 : 0;

        if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED){
            if (restorePersistedValue) {
                setChecked(getPersistedBoolean(myPrefAttrsInfo.getMyBooleanDefValue()));
            } else {
                setChecked(myPrefAttrsInfo.getMyBooleanDefValue());
                if(!myPrefAttrsInfo.isValidKey()) return;;
                persistBoolean(isChecked());
            }
            saveValueInSettings(isChecked());
        }else updateFromSettingsValue();
    }





    public void saveValueInSettings(boolean checked){
        if(!myPrefAttrsInfo.isValidKey()) return;

        try {


            switch (myPrefAttrsInfo.getMySystemPrefType()){
                case SHARED:
                    if(myPrefAttrsInfo.isAllowedToBeSavedInSettingsDb()){
                        int value = (checked) ? 1:0;
                        int real;
                        try {
                            real = Settings.System.getInt(getContext().getContentResolver(), this.getKey());
                            if(real!=value){
                                Settings.System.putInt(getContext().getContentResolver(), this.getKey(), value);

                            }
                        } catch (Settings.SettingNotFoundException e) {
                            Settings.System.putInt(getContext().getContentResolver(), this.getKey(), value);
                        }
                    }
                    break;
                case SYSTEM:
                    Settings.System.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), (checked) ? 1:0  );
                    break;
                case SECURE:
                    Settings.Secure.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), (checked) ? 1:0  );
                    break;
                case GLOBAL:
                    Settings.Global.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), (checked) ? 1:0  );
                    break;
                default:
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**********   on click rules support  *********/

    public void performOnclickRule(String rule){
        if(OnClickRuleHelper.shouldPerformOnClickForBooleanPref(isChecked(),rule)) onClick();
    }


    /**********  Onpreferencechangelistener - add custom dependency rule *********/

    @Override
    public void setOnPreferenceChangeListener(Preference.OnPreferenceChangeListener onPreferenceChangeListener){
        GrxPreferenceScreen grxPreferenceScreen = (GrxPreferenceScreen) onPreferenceChangeListener;
        if(!Common.SyncUpMode){
            if(!myPrefAttrsInfo.isBuildPropEnabled()){
                grxPreferenceScreen.addPreferenceToRemoveList(this);
            }
            else{
                super.setOnPreferenceChangeListener(onPreferenceChangeListener);
                String mydeprule = myPrefAttrsInfo.getMyDependencyRule();
                if(mydeprule!=null){
                    grxPreferenceScreen.addCustomDependency(this,mydeprule,null);
                }
                if(myPrefAttrsInfo.getMySystemPrefType()!= PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED)
                    grxPreferenceScreen.addSupportForSettingsKey(getKey());
            }
        }else {
            if(myPrefAttrsInfo.isBuildPropEnabled()) {
                grxPreferenceScreen.addCommonBroadCastValuesForSyncUp(myPrefAttrsInfo.getMyCommonBcExtra(),myPrefAttrsInfo.getMyCommonBcExtraValue());
                grxPreferenceScreen.addGroupKeyForSyncUp(myPrefAttrsInfo.getMyGroupKey());
                grxPreferenceScreen.addBroadCastToSendForSyncUp(myPrefAttrsInfo.getMyBroadCast1(), myPrefAttrsInfo.getMyBroadCast1Extra(),
                        myPrefAttrsInfo.getMyBroadCast2(), myPrefAttrsInfo.getMyBroadCast2Extra());

            }
        }

    }


    /************ custom dependencies ****************/
/*
    @Override
    public void setEnabled(boolean enabled){
        if(myPrefAttrsInfo.isBuildPropEnabled()) super.setEnabled(enabled);
        else super.setEnabled(false);
    }*/

    public void OnCustomDependencyChange(boolean state){
        setEnabled(state);
    }

    /***** settings system, global and secure support */

    public void updateFromSettingsValue(){

        int real = myPrefAttrsInfo.getMyBooleanDefValue() ? 1 : 0;
        switch (myPrefAttrsInfo.getMySystemPrefType()){
            case SYSTEM:
                real =GrxPrefsUtils.getIntValueFromSettingsSystem(getContext(),myPrefAttrsInfo.getMyKey(),real,false);
                break;
            case SECURE:
                real =GrxPrefsUtils.getIntValueFromSettingsSecure(getContext(),myPrefAttrsInfo.getMyKey(),real,false);
                break;
            case GLOBAL:
                real =GrxPrefsUtils.getIntValueFromSettingsGlobal(getContext(),myPrefAttrsInfo.getMyKey(),real,false);
                break;
            default:
                break;
        }
        boolean checked = (real==1) ? true : false;
        setChecked(checked);
        persistBoolean(checked);
    }

    /**** provide my info */

    public PrefAttrsInfo getPrefAttrsInfo() { return myPrefAttrsInfo;}


}
