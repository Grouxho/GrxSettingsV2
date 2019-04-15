
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.prefssupport;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.grx.settings.R;
import com.grx.settings.utils.BPRulesUtils;
import com.grx.settings.utils.Common;

import java.util.regex.Pattern;


public class PrefAttrsInfo {


    public enum PREF_TYPE {
        STRING, INT, BOOL, NEUTRAL, UNKNOWN;
        public static PREF_TYPE indexOf(int index) {
            switch (index) {
                case 0:
                    return STRING;
                case 1:
                    return INT;
                case 2:
                    return BOOL;
                case 3:
                    return NEUTRAL;
                case 4:
                    return UNKNOWN;
            }
            return UNKNOWN;
        }
    }

    public enum SCRIPT_TYPE {
        NO_SCRIPT, ARRAY, FILE;
        public static SCRIPT_TYPE indexOf(int index) {
            switch (index) {
                case 0:
                    return NO_SCRIPT;
                case 1:
                    return ARRAY;
                case 2:
                    return FILE;
            }
            return NO_SCRIPT;
        }
    }


    public enum SETTINGS_PREF_TYPE {
        SHARED, SYSTEM, SECURE, GLOBAL;
        public static SETTINGS_PREF_TYPE indexOf(int index) {
            switch (index) {
                case 0:
                    return SHARED;
                case 1:
                    return SYSTEM;
                case 2:
                    return SECURE;
                case 3:
                    return GLOBAL;
            }
            return SHARED;
        }
    }


    public String mMyKey;
    public String mMyTitle;
    public String mMySummary;

    private boolean mIsMultiValue;
    private String mMyStringDefaultValue;
    private String mMyStringSeparator="";
    private int mMyMaxItemsNum= 0;

    private int mMyIntDefaultValue=0;

    private boolean mMyBooleanDefaultValue;


    private String mMyGroupKey;
    private boolean mSaveInSettingsAllowed;

    private String mMyDependencyRule;
    private String mMyDependencySeparator;

    private String mMyBPRule;

    private boolean mIsBPEnabled = true;

    private String mMyKillPkg=null;
    private boolean mMyKillPkgDlg;
    private boolean mMyRebootDevice;
    private boolean mMyRebootDeviceDlg;


    private int idOptionsArray;
    private int idValuesArrayy;
    private int idIconsArray;

    private String mBroadCast1="";
    private String mBroadCast2="";

    private String mBroadCast1Extra=null;

    private String mBroadCast2Extra=null;

    private SCRIPT_TYPE mScriptType=SCRIPT_TYPE.NO_SCRIPT;
    private int mScriptArrayId =0;
    private String mScriptFile = null;
    private String mScriptFileArguments=null;
    private String mScriptToast=null;
    private boolean mScriptConfirm=false;

    private String mOnClickRule=null;
    private String mKeyToClick=null;


    private String mProcessActionsOrder=null;

    private SETTINGS_PREF_TYPE mMySystemPrefType=SETTINGS_PREF_TYPE.SHARED;


    private int mIconTint, mArrowTint=0;

    private String mCommonBroadCastExtra;
    private String mCommonBroadCastExtraValue;


    /******** GrxBasePreference ***/

    public void initArraysIds(Context context, AttributeSet attrs){

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);

        idOptionsArray = ta.getResourceId(R.styleable.grxPreferences_optionsArray,0);
        idValuesArrayy = ta.getResourceId(R.styleable.grxPreferences_valuesArray,0);
        idIconsArray = ta.getResourceId(R.styleable.grxPreferences_iconsArray,0);

        ta.recycle();
    }



    public PrefAttrsInfo(Context context, AttributeSet attrs, CharSequence title, CharSequence summary, String key) { //checkbox, switches, preferencecategory

        mMyBooleanDefaultValue = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "defaultValue",false);
        ini_parameters(context, attrs, title, summary, key);

    }


    public PrefAttrsInfo(Context context, AttributeSet attrs, CharSequence title, CharSequence summary, String key, int defvalue) { //int
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        mMyIntDefaultValue=ta.getInt(R.styleable.grxPreferences_android_defaultValue,defvalue);
        ta.recycle();
        ini_parameters(context, attrs, title, summary, key);

    }

    /*** string */

    public PrefAttrsInfo(Context context, AttributeSet attrs, CharSequence title, CharSequence summary, String key, boolean isMultivalue) {
        mIsMultiValue = isMultivalue;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        mMyStringDefaultValue = ta.getString(R.styleable.grxPreferences_android_defaultValue);
        if(mMyStringDefaultValue==null) mMyStringDefaultValue="";

        if(mIsMultiValue){
            mMyStringSeparator = ta.getString(R.styleable.grxPreferences_separator);
            if(mMyStringSeparator== null) mMyStringSeparator = context.getResources().getString(R.string.grxs_separator_default);
            mMyMaxItemsNum=ta.getInt(R.styleable.grxPreferences_maxChoices,0);
        }
        ta.recycle();
        ini_parameters(context, attrs, title, summary, key);

    }




    /**********************************************/


    private void ini_parameters(Context context, AttributeSet attrs, CharSequence title, CharSequence summary, String key){

        /**** title, summary and key  *****/

        if(title!=null) mMyTitle = title.toString();
        else mMyTitle = "";

        if(summary!=null) mMySummary = summary.toString();
        else mMySummary = "";

        mMyKey = key;

        /*** common attributes */

        TypedArray ta;
        if(Common.mContextWrapper!=null) ta = Common.mContextWrapper.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        else ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);

        //TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);

        mMyGroupKey = ta.getString(R.styleable.grxPreferences_groupKey);

        if(context.getResources().getBoolean(R.bool.grxb_global_enable_settingsdb)) {
            mSaveInSettingsAllowed=ta.getBoolean(R.styleable.grxPreferences_saveSettings, context.getResources().getBoolean(R.bool.grxb_saveSettings_default) );
        }else mSaveInSettingsAllowed=false;

        mMySystemPrefType=SETTINGS_PREF_TYPE.indexOf(ta.getInt(R.styleable.grxPreferences_systemType, 0));

        mMyDependencyRule = ta.getString(R.styleable.grxPreferences_depRule);
        mMyDependencySeparator = ta.getString(R.styleable.grxPreferences_depSeparator);
        mMyBPRule = ta.getString(R.styleable.grxPreferences_bpRule);

        mMyRebootDevice = ta.getBoolean(R.styleable.grxPreferences_reboot,false);
        mMyRebootDeviceDlg = ta.getBoolean(R.styleable.grxPreferences_confirmReboot,context.getResources().getBoolean(R.bool.grxb_confirmReboot_default));

        mMyKillPkg = ta.getString(R.styleable.grxPreferences_killpackage);
        mMyKillPkgDlg =ta.getBoolean(R.styleable.grxPreferences_confirmKillpackage,context.getResources().getBoolean(R.bool.grxb_confirmKillpackage_default));

        mBroadCast1=ta.getString(R.styleable.grxPreferences_broadCast1);
        mBroadCast2=ta.getString(R.styleable.grxPreferences_broadCast2);
        mBroadCast1Extra=ta.getString(R.styleable.grxPreferences_broadCast1Extra);
        mBroadCast2Extra=ta.getString(R.styleable.grxPreferences_broadCast2Extra);

        mScriptArrayId = ta.getResourceId(R.styleable.grxPreferences_script,0);
        if(mScriptArrayId!=0){
            mScriptType=SCRIPT_TYPE.ARRAY;
        }else {
            mScriptFile=ta.getString(R.styleable.grxPreferences_script);
            if(mScriptFile!=null){
                mScriptType=SCRIPT_TYPE.FILE;
            }
        }

        mScriptConfirm = ta.getBoolean(R.styleable.grxPreferences_scriptConfirm,false);

        switch (mScriptType){
            case NO_SCRIPT:
                break;
            case FILE:
                mScriptFileArguments=ta.getString(R.styleable.grxPreferences_scriptArguments);
                mScriptToast = ta.getString(R.styleable.grxPreferences_scriptToast);
                break;
            case ARRAY:
                mScriptToast = ta.getString(R.styleable.grxPreferences_scriptToast);
                break;
        }

        mOnClickRule= ta.getString(R.styleable.grxPreferences_clickRules);

        mProcessActionsOrder = ta.getString(R.styleable.grxPreferences_processActionsOrder);

        if(ta.hasValue(R.styleable.grxPreferences_iconTint)) {
            try {
                mIconTint = ta.getInt(R.styleable.grxPreferences_iconTint, 0);
            } catch (Exception e) {
            }
        }

        if(ta.hasValue(R.styleable.grxPreferences_arrowColor)) {
            try {
                mArrowTint = ta.getColor(R.styleable.grxPreferences_arrowColor, 0);
            } catch (Exception e) {
            }
        }

        if(ta.hasValue(R.styleable.grxPreferences_commonBcExtra)){
            mCommonBroadCastExtra=ta.getString(R.styleable.grxPreferences_commonBcExtra);
        }else mCommonBroadCastExtra=null;

        if(ta.hasValue(R.styleable.grxPreferences_commonBcExtra)){
            mCommonBroadCastExtraValue=ta.getString(R.styleable.grxPreferences_commonBcExtraValue);
        }else mCommonBroadCastExtraValue="";


        ta.recycle();

        if(mMyBPRule!=null && !mMyBPRule.isEmpty()) {
            mIsBPEnabled = BPRulesUtils.isBPEnabled(mMyBPRule);
        }


    }

    public void setKeyToClick(String key) {mKeyToClick=key;}



    public String getMyTitle(){
        return mMyTitle;
    }

    public String getMySummary(){
        return mMySummary;
    }

    public String getMyKey(){
        return mMyKey;
    }

    public String getMySeparator(){
        return mMyStringSeparator;
    }

    public int getMyIntDefValue(){
        return mMyIntDefaultValue;
    }

    public String getMyStringDefValue(){
        return mMyStringDefaultValue;
    }

    public boolean getMyBooleanDefValue(){
        return mMyBooleanDefaultValue;
    }

    public String getMyDependencyRule(){
        return mMyDependencyRule;
    }

    public String getMyDependencySeparator(){
        return mMyDependencySeparator;
    }

    public String getMyGroupKey(){
        return mMyGroupKey;
    }


    public boolean isAllowedToBeSavedInSettingsDb(){
        return mSaveInSettingsAllowed;
    }

    public boolean isValidKey(){
        return !(mMyKey==null || mMyKey.isEmpty() );
    }

    public int getMyMaxItems(){return mMyMaxItemsNum;}

    public String getMyBpRule(){return mMyBPRule;}

    public boolean isBuildPropEnabled(){ return mIsBPEnabled;}

    public String getMyPackageToKill(){ return mMyKillPkg; }

    public boolean isNeededDialogToKillPackage(){ return mMyKillPkgDlg; }

    public boolean isNeededReboot(){return mMyRebootDevice;}

    public boolean isNeededRebootDialog(){return mMyRebootDeviceDlg;}

    public int getMyOptionsArrayId() {return idOptionsArray;}

    public int getMyValuesArrayId() { return idValuesArrayy;}

    public int getMyIconsArrayId() { return idIconsArray;}

    public void setmMyIntDefaultValue(int value){mMyIntDefaultValue=value;}

    public String getMyBroadCast1() {return mBroadCast1;}

    public String getMyBroadCast2() {return mBroadCast2;}

    public String getMyBroadCast1Extra() { return mBroadCast1Extra;}

    public String getMyBroadCast2Extra() { return mBroadCast2Extra;}

    public SCRIPT_TYPE getMyScriptType(){return mScriptType;}

    public int getMyScriptArrayId(){return mScriptArrayId;}

    public String getMyScriptFileName() {return mScriptFile;}

    public String getMyScriptFileArguments(){return mScriptFileArguments;}

    public String getMyScriptToast() {return mScriptToast;}

    public SETTINGS_PREF_TYPE getMySystemPrefType() {return mMySystemPrefType;}

    public String getMyProcessActionOrder() {return mProcessActionsOrder;}

    public boolean isNeededConfirmationDialgotToRunScript(){return mScriptConfirm;}

    public String getMyOnClickRule(){return mOnClickRule;}

    public String getmKeyToClick(){return mKeyToClick;}

    public int getMyIconTintColor(){ return  mIconTint;}

    public void setMyIcontTint(int color){
        mIconTint=color;
    }

    public int getMyArrowTint(){return mArrowTint;}

    public void setMyArrowTint(int color){mArrowTint=color;}

    public String getMyCommonBcExtra(){return mCommonBroadCastExtra;}

    public String getMyCommonBcExtraValue(){return mCommonBroadCastExtraValue;}
}
