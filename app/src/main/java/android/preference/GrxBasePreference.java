/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

package android.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grx.settings.prefssupport.OnClickRuleHelper;
import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;
import com.grx.settings.utils.GrxPrefsUtils;


public class GrxBasePreference extends Preference implements
        GrxPreferenceScreen.CustomDependencyListener{


    PrefAttrsInfo.PREF_TYPE mTypeOfPreference= PrefAttrsInfo.PREF_TYPE.UNKNOWN;

    /*click*/
    private Runnable DoubleClickRunnable;
    private Handler mHandler;
    private boolean mDoubleClickPending;
    private Long mLongClickTimeOut;
    private int mNumClicks;

    public boolean mDisableDoubleClick=false;


    public int mLeftIconColor=0;
    public int mArrowColor =0;

    /* icons and widget arrow */

    public ImageView vAndroidIcon;
    public ImageView vWidgetArrow =null;
    public ImageView vWidgetIcon =null;
    public TextView vWidgetText = null;


    public boolean mArrowNeeded =true;
    public Drawable mWidgetIcon;

    public boolean showWidgetArrow =true;


    /**** string type ****/

    public String mStringValue ="";

    /***** int type  **/

    public int mIntValue=0;



    PrefAttrsInfo  myPrefAttrsInfo = null;




    public GrxBasePreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        mNumClicks =0;
    }

    public GrxBasePreference(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        mNumClicks =0;
    }

    /*** type of pref */


    public void setTypeOfPreference(PrefAttrsInfo.PREF_TYPE  type){
        mTypeOfPreference=type;
    }


    /*** initialize string prefs **/

    public void initStringPrefsCommonAttributes(Context context, AttributeSet attrs, boolean isMultiValue, boolean iniArrays ){
        setTypeOfPreference(PrefAttrsInfo.PREF_TYPE.STRING);
        myPrefAttrsInfo = new PrefAttrsInfo(context, attrs, getTitle(), getSummary(),getKey(), isMultiValue);
        if(iniArrays) myPrefAttrsInfo.initArraysIds(context,attrs);
    }



    public void initIntPrefsCommonAttributes(Context context, AttributeSet attrs, int defvalue,boolean iniArrays){
        setTypeOfPreference(PrefAttrsInfo.PREF_TYPE.INT);
        myPrefAttrsInfo = new PrefAttrsInfo(context,attrs,getTitle(),getSummary(),getKey(),defvalue);
        if(iniArrays) myPrefAttrsInfo.initArraysIds(context,attrs);
    }



    public void initArraysIds(Context context, AttributeSet attrs){
        initArraysIds(context, attrs);
    }

    public void setWidgetIcon(Drawable drawable){
        mWidgetIcon = drawable;
        if(mWidgetIcon!=null) {
            showWidgetArrow = false;
        }else {
            if(mArrowNeeded) showWidgetArrow = true;
        }
    }


    public void setMyIntDefaultValue(int value){myPrefAttrsInfo.setmMyIntDefaultValue(value);}

    /****** view **///

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        vWidgetArrow = (ImageView) view.findViewById(R.id.gid_widget_arrow);
        vWidgetIcon = (ImageView) view.findViewById(R.id.gid_widget_icon);
        vAndroidIcon = (ImageView) view.findViewById(android.R.id.icon);
        vWidgetText = (TextView) view.findViewById(R.id.gid_widget_text);
        if(vAndroidIcon!=null) {
            vAndroidIcon.setLayoutParams(Common.AndroidIconParams);
            if(mLeftIconColor==0) mLeftIconColor = myPrefAttrsInfo.getMyIconTintColor();
            if(mLeftIconColor!=0) vAndroidIcon.setColorFilter(mLeftIconColor);
        }
        if(vWidgetArrow!=null ){
            if(mArrowColor==0 ) {
                mArrowColor = myPrefAttrsInfo.getMyArrowTint();
            }
            if(mArrowColor !=0){
                int states[][] = {{android.R.attr.state_checked}, {}};
                int colors[] = {mArrowColor, mArrowColor};
                vWidgetArrow.setBackgroundTintList(new ColorStateList(states, colors));
            }else {
                vWidgetArrow.setVisibility(View.GONE);
                mArrowNeeded=false;
            }
        }
        return view;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        float alpha = (isEnabled() ? (float) 1.0 : (float) 0.4);
        if(vWidgetText!=null) vWidgetText.setAlpha(alpha);
        if(vWidgetArrow!=null) {
            if(mArrowColor==0) mArrowColor = myPrefAttrsInfo.getMyArrowTint();
            if(mArrowColor!=0) {
                int states[][] = {{android.R.attr.state_checked}, {}};
                int colors[] = {mArrowColor, mArrowColor};
                vWidgetArrow.setBackgroundTintList(new ColorStateList(states, colors));
            }
            vWidgetArrow.setAlpha(alpha);
        }
        if(vWidgetIcon!=null) vWidgetIcon.setAlpha(alpha);
        if(vAndroidIcon!=null) vAndroidIcon.setAlpha(alpha);
    }

    public void refreshView(){
        if(vWidgetIcon!=null){
            vWidgetIcon.setImageDrawable(mWidgetIcon);
            if(mWidgetIcon!=null) vWidgetIcon.setVisibility(View.VISIBLE);
            else vWidgetIcon.setVisibility(View.GONE);
        }
        if(vWidgetArrow!=null) {

            if(mWidgetIcon!=null){
                vWidgetArrow.setVisibility(View.GONE);
            }else {
                if(mArrowNeeded) vWidgetArrow.setVisibility(View.VISIBLE);
                else vWidgetArrow.setVisibility(View.GONE);
            }
        }
    }



    /*** default and initial values */

    public void updateFromSettingsValue(){
        switch (mTypeOfPreference){
            case INT:
                mIntValue=getSettingsIntValue();
                persistInt(mIntValue);
                configIntPreference(mIntValue);
                break;
            case STRING:
                mStringValue = getSettingsStringValue();
                persistString(mStringValue);
                configStringPreference(mStringValue);
                break;
            default:
                break;

        }

    }

    private String getSettingsStringValue(){
        String returnvalue = myPrefAttrsInfo.getMyStringDefValue();
        if(returnvalue==null) returnvalue="";
        if(!myPrefAttrsInfo.isValidKey()) return returnvalue;
        switch (myPrefAttrsInfo.getMySystemPrefType()){
            case GLOBAL:
                returnvalue = GrxPrefsUtils.getStringValueFromSettingsGlobal(getContext(),myPrefAttrsInfo.getMyKey(),returnvalue,false);
                break;
            case SECURE:
                returnvalue = GrxPrefsUtils.getStringValueFromSettingsSecure(getContext(),myPrefAttrsInfo.getMyKey(),returnvalue,false);
                break;
            case SYSTEM:
                returnvalue = GrxPrefsUtils.getStringValueFromSettingsSystem(getContext(),myPrefAttrsInfo.getMyKey(),returnvalue,false);
                break;
            default:
                break;
        }
        if(returnvalue==null) return myPrefAttrsInfo.getMyStringDefValue();
        else return returnvalue;

    }


    private int getSettingsIntValue(){
        int returnvalue=0;
        switch (myPrefAttrsInfo.getMySystemPrefType()){
            case SYSTEM:
                returnvalue = GrxPrefsUtils.getIntValueFromSettingsSystem(getContext(),myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyIntDefValue(),false);
                break;
            case SECURE:
                returnvalue =GrxPrefsUtils.getIntValueFromSettingsSecure(getContext(),myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyIntDefValue(),false);
                break;
            case GLOBAL:
                returnvalue =GrxPrefsUtils.getIntValueFromSettingsGlobal(getContext(),myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyIntDefValue(),false);
                break;
            default:
                break;
        }
        return returnvalue;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        switch (mTypeOfPreference){
            case STRING:
                if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED) {

                    if (restorePersistedValue) {
                        mStringValue = getPersistedString(myPrefAttrsInfo.getMyStringDefValue());
                    } else {
                        mStringValue = myPrefAttrsInfo.getMyStringDefValue();
                        if (!myPrefAttrsInfo.isValidKey()) return;
                        persistString(mStringValue);
                    }
                    saveStringValueInSettings(mStringValue);
                    configStringPreference(mStringValue);

                }else {
                    updateFromSettingsValue();
                }
                break;
            case INT:
                if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED) {
                    if (restorePersistedValue) {
                        mIntValue = getPersistedInt(myPrefAttrsInfo.getMyIntDefValue());
                    } else {
                        mIntValue = myPrefAttrsInfo.getMyIntDefValue();
                        if (getKey() == null || getKey().isEmpty()) return;
                        persistInt(mIntValue);
                    }
                    saveintValueInSettings(mIntValue);
                    configIntPreference(mIntValue);
                }else {
                    updateFromSettingsValue();
                }

                break;
            case NEUTRAL:
                configNeutralPreference();
                break;
            default:
                break;
        }
    }


    /*** stock dependency ***/

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);
    }


    /*** click - double click********/


    @Override
    protected void onClick() {
        if(mHandler ==null) setUpDoubleClick();
        if(DoubleClickRunnable ==null) showDialog();
        else{
            mNumClicks++;
            if(!mDoubleClickPending){
                mHandler.removeCallbacks(DoubleClickRunnable);
                mDoubleClickPending =true;
                mHandler.postDelayed(DoubleClickRunnable, mLongClickTimeOut);
            }
        }
    }

    private void setUpDoubleClick(){
        mHandler = new Handler();
        mLongClickTimeOut = Long.valueOf(ViewConfiguration.getDoubleTapTimeout());
        mDoubleClickPending =false;

        DoubleClickRunnable = new Runnable() {
            @Override
            public void run() {
                if(!mDoubleClickPending){
                    actionClick();
                }else {
                    if(mNumClicks ==0) actionClick();
                    else if(mNumClicks !=2) {
                        actionClick();
                    }else {
                        if(!mDisableDoubleClick) actionDoubleClick();
                    }

                }
            }
        };
    }



    private void actionClick(){
        mNumClicks =0;
        mDoubleClickPending =false;
        mHandler.removeCallbacks(DoubleClickRunnable);
        showDialog();
    }

    private void actionDoubleClick(){
        mNumClicks =0;
        mDoubleClickPending =false;
        mHandler.removeCallbacks(DoubleClickRunnable);
        showResetPreferenceDialog();

    }


    public void showDialog(){

    }


    public void showResetPreferenceDialog(){

        switch (mTypeOfPreference){
            case INT:
                if(mIntValue==myPrefAttrsInfo.getMyIntDefValue()) return;
                break;
            case STRING:
                if(mStringValue ==null ) return;
                if(mStringValue.equals(myPrefAttrsInfo.getMyStringDefValue())) return;
                break;
            case NEUTRAL:
                return;
            default:
                break;
        }

        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
        dlg.setTitle(getContext().getResources().getString(R.string.grxs_reset_values));
        dlg.setMessage(getContext().getResources().getString(R.string.grxs_reset_message));
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetPreference();
            }
        });
        dlg.show();
    }


    public void resetPreference(){

    }


    /***************** save values  *************/

    public void saveNewStringValue(String value) {
        mStringValue =value;
        //   if(mStringValue ==null) mStringValue ="";
        if(!myPrefAttrsInfo.isValidKey()) return;
        persistString(mStringValue);
        saveStringValueInSettings(mStringValue);
        //check onclick rule
        String clickrule = myPrefAttrsInfo.getMyOnClickRule();
        if(clickrule!=null) {
            myPrefAttrsInfo.setKeyToClick(OnClickRuleHelper.getKeyToClickFromRules(clickrule,mStringValue));
        }
        notifyChanged();
        getOnPreferenceChangeListener().onPreferenceChange(this, mStringValue);
        //sendBroadcastsAndChangeGroupKey();
    }




    public void saveStringValueInSettings(String value){
        if(!myPrefAttrsInfo.isValidKey()) return;
        if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED) {
            if (myPrefAttrsInfo.isAllowedToBeSavedInSettingsDb()) {
                String real = Settings.System.getString(getContext().getContentResolver(), this.getKey());
                if (real == null) real = "N/A";
                if (!real.equals(value)) {
                    Settings.System.putString(getContext().getContentResolver(), this.getKey(), value);
                }
            }
        }else {
            switch (myPrefAttrsInfo.getMySystemPrefType()){
                case SYSTEM:
                    Settings.System.putString(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), value  );
                    break;
                case SECURE:
                    Settings.Secure.putString(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), value );
                    break;
                case GLOBAL:
                    Settings.Global.putString(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(),value );
                    break;
                default:
                    break;
            }
        }
    }


    public void saveintValueInSettings(int value){
        if(!myPrefAttrsInfo.isValidKey()) return;

        if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED) {
            if (myPrefAttrsInfo.isAllowedToBeSavedInSettingsDb()) {
                int real = Settings.System.getInt(getContext().getContentResolver(), this.getKey(), myPrefAttrsInfo.getMyIntDefValue());
                if (real != value) {
                    Settings.System.putInt(getContext().getContentResolver(), this.getKey(), value);
                }
            }
        }else {
            switch (myPrefAttrsInfo.getMySystemPrefType()){
                case SYSTEM:
                    Settings.System.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(), value  );
                    break;
                case SECURE:
                    Settings.Secure.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(),value );
                    break;
                case GLOBAL:
                    Settings.Global.putInt(getContext().getContentResolver(),myPrefAttrsInfo.getMyKey(),value );
                    break;
                default:
                    break;
            }

        }
    }

    public void saveNewIntValue(int value){
        // if(mIntValue==value) return;
        mIntValue=value;
        if(!myPrefAttrsInfo.isValidKey()) return;
        persistInt(mIntValue);
        notifyChanged();
        saveintValueInSettings(mIntValue);
        //check onclick rule
        String clickrule = myPrefAttrsInfo.getMyOnClickRule();
        if(clickrule!=null) {
            myPrefAttrsInfo.setKeyToClick(OnClickRuleHelper.getKeyToClickFromRules(clickrule,String.valueOf(mIntValue)));
        }
        getOnPreferenceChangeListener().onPreferenceChange(this,mIntValue);
        notifyChanged();
        //sendBroadcastsAndChangeGroupKey();
    }

    /**********   on click rules support  *********/

    public void performOnclickRule(String rule){
        if(mTypeOfPreference.equals(PrefAttrsInfo.PREF_TYPE.INT)){
            if(OnClickRuleHelper.shouldPerformOnClickForIntPref(mIntValue,rule) ) onClick();
        } else if(mTypeOfPreference.equals(PrefAttrsInfo.PREF_TYPE.STRING)){
            if(OnClickRuleHelper.shouldPerformOnClickForStringPref(mStringValue,rule) ) onClick();
        }

    }



    /** configure preference **/

    public void configStringPreference(String value){

    }


    public void configIntPreference(int value){

    }


    public void configNeutralPreference(){

    }


    /*** other */

    public void checkDepRuleAndAssignKeyIfNeeded(){  // in some type of prefs, we need to make sure we have a key for dep rules
        String deprul = myPrefAttrsInfo.getMyDependencyRule();
        if(deprul!=null && !deprul.isEmpty()){
            if(!myPrefAttrsInfo.isValidKey()){
                setKey(getClass().getName() + "_" + getOrder());
            }
        }
    }


    /*********** listener and connections to GrxPreferenceScreen ****************/


    /******  Onpreferencechangelistener - add custom dependency rule, remove preference based on BP rules *********/

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
                            myPrefAttrsInfo.getMyBroadCast2(),myPrefAttrsInfo.getMyBroadCast2Extra());

            }
        }
    }


    public void OnCustomDependencyChange(boolean state){
        setEnabled(state);
    }

    /****  provide pref info to GrxPreferenceScreen to process reboot and kill apps options  */

    public PrefAttrsInfo getPrefAttrsInfo() { return myPrefAttrsInfo;}


}
