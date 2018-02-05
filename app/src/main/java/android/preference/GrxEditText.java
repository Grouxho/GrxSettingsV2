
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
import com.grx.settings.prefs_dlgs.DlgFrEditText;
import com.grx.settings.utils.Common;


public class GrxEditText extends  GrxBasePreference implements DlgFrEditText.OnGrxEditTextListener{

    public GrxEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }


    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }



    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        setSummary();

    }


    @Override
    public void configStringPreference(String value){
        setSummary();
    }

    @Override
    public void resetPreference(){

        mStringValue= myPrefAttrsInfo.getMyStringDefValue();
        saveNewStringValue(mStringValue);
        configStringPreference(mStringValue);

    }


    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            prefsScreen.forceHideFloatingWindow();
            DlgFrEditText dlg = (DlgFrEditText)prefsScreen.getFragmentManager().findFragmentByTag("Common.TAG_DLGFRGREDITTEXT");
            if(dlg==null) {
                dlg = DlgFrEditText.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT,myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyTitle(),mStringValue);
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRGREDITTEXT);
            }
        }
    }

    private void setSummary(){
        String summary = myPrefAttrsInfo.getMySummary();
        if(summary.equals("%")) setSummary(mStringValue);
        else {
           setSummary(mStringValue.isEmpty() ? summary : mStringValue);
            }
    }

    public void onEditTextDone(String value){
        if(value.equals(mStringValue)) return;
        saveNewStringValue(value);
        setSummary();
    }

}
