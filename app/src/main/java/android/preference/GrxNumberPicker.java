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
import android.util.AttributeSet;
import android.view.View;


import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.prefs_dlgs.DlgFrGrxNumberPicker;


public class GrxNumberPicker extends GrxBasePreference implements DlgFrGrxNumberPicker.OnGrxNumberPickerSetListener {


    private String mUnits;
    private int min=0;
    private int max=5;


    public GrxNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxNumberPicker(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        setWidgetLayoutResource(R.layout.widget_text);
        initIntPrefsCommonAttributes(getContext(),attrs,0,false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        min=ta.getInt(R.styleable.grxPreferences_minValue,0);
        max=ta.getInt(R.styleable.grxPreferences_maxValue,0);
        mUnits=ta.getString(R.styleable.grxPreferences_units);
        ta.recycle();
        if(myPrefAttrsInfo.getMyIntDefValue()>=min && myPrefAttrsInfo.getMyIntDefValue()<=max)
                setDefaultValue(myPrefAttrsInfo.getMyIntDefValue());
        else setMyIntDefaultValue(min);
    }


    @Override
    public void configIntPreference(int value){
        mStringValue = getTextValue();
        if(vWidgetText!=null) vWidgetText.setText(mStringValue);

    }


    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        vWidgetText.setText(mStringValue);

    }


    public String getTextValue(){
        String value;
        value =  Integer.toString(mIntValue);
        if ((mUnits != null) && (!mUnits.isEmpty())){
            value = value.concat(" ").concat(mUnits);
        }
        return value;
    }


    @Override
    public void resetPreference(){
        saveNewIntValue(myPrefAttrsInfo.getMyIntDefValue());
        configIntPreference(mIntValue);
    }


    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            DlgFrGrxNumberPicker dlgFrGrxNumberPicker = (DlgFrGrxNumberPicker) prefsScreen.getFragmentManager().findFragmentByTag("DlgFrGrxNumberPicker");
            if (dlgFrGrxNumberPicker==null){
                dlgFrGrxNumberPicker = DlgFrGrxNumberPicker.newInstance(this,getKey(), myPrefAttrsInfo.getMyTitle(), mIntValue,min,max,mUnits, Common.TAG_PREFSSCREEN_FRAGMENT);
                dlgFrGrxNumberPicker.show(prefsScreen.getFragmentManager(), "DlgFrGrxNumberPicker");
            }
        }
    }

    public void onGrxNumberPickerSet(int value, String key){
        saveNewIntValue(value);
        configIntPreference(mIntValue);
    }

}

