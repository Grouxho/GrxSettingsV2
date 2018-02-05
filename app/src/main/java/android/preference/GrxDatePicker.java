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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GrxDatePicker extends GrxBasePreference{

    private String mFormattedValue="";

    public GrxDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxDatePicker(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_text);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }

    @Override
    public void configStringPreference(String value){
        mStringValue=value;
        setFormattedValue(mStringValue);
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        vWidgetText.setText(mFormattedValue);
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null) prefsScreen.ShowGrxDatePickerDialog(getKey(), getPersistedString(""));
    }

     public void setNewValue(String value){
         mStringValue=value;
         saveNewStringValue(mStringValue);
         setFormattedValue(mStringValue);
    }

    @Override
    public void resetPreference(){
        mStringValue=myPrefAttrsInfo.getMyStringDefValue();
        setFormattedValue(mStringValue);
        saveNewStringValue(mStringValue);
    }

    private void setFormattedValue(String value){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        mFormattedValue="";
        if(value==null || value.isEmpty()) return;
        Date date = null;
        try {
            date = sdf.parse(value);
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
            mFormattedValue=dateFormat.format(date);
            if(vWidgetText!=null) vWidgetText.setText(mFormattedValue);
        } catch (ParseException e) {e.printStackTrace();}
    }


}
