/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */


package android.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;

import java.util.Calendar;


public class GrxTimePicker extends GrxBasePreference {

    public GrxTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxTimePicker(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        setWidgetLayoutResource(R.layout.widget_text);
        initIntPrefsCommonAttributes(getContext(),attrs,0,false);
        setDefaultValue(myPrefAttrsInfo.getMyIntDefValue());

    }

    public int getIntFromStringTime(String time){
        int h=0;
        int m=0;
        String arr[]=time.split(":");
        if (arr.length==2){
            h=Integer.valueOf(arr[0]);
            m=Integer.valueOf(arr[1]);
        }
        return ((h*60)+m);
    }

    @Override
    public void configIntPreference(int value){
        mIntValue=value;
        setFormattedValue(value);
        if(vWidgetText!=null) vWidgetText.setText(mStringValue);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        vWidgetText.setText(mStringValue);
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null) prefsScreen.showGrxTimePickerDialog(getKey(), mIntValue);
    }

    @Override
    public void resetPreference(){
        saveNewIntValue(myPrefAttrsInfo.getMyIntDefValue());
        configIntPreference(mIntValue);
    }

    public void setNewValue(int value){
        saveNewIntValue(value);
        configIntPreference(mIntValue);
    }

    private void setFormattedValue(int value){
        String hs;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,(int)value/60);
        cal.set(Calendar.MINUTE,(int)value%60);
        android.text.format.DateFormat df;
        hs = android.text.format.DateFormat.getTimeFormat(getContext()).format(cal.getTime());
        //if (value < (int) 600) hs = "0"+hs;
        mStringValue=hs;
    }
}




