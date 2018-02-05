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

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefs_dlgs.DlgFrGrxPerItemColor;

import java.util.regex.Pattern;



public class GrxPerItemColor extends GrxBasePreference implements  DlgFrGrxPerItemColor.GrxItemsColorsListener{


    private int idDefaultColors;
    private int defaultColor;
    int iconsValueTint =0;

    public GrxPerItemColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxPerItemColor(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,true, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        idDefaultColors=ta.getResourceId(R.styleable.grxPreferences_colorsArray,0);
        defaultColor=ta.getInt(R.styleable.grxPreferences_defaultColor,0xffffffff);
        if(ta.hasValue(R.styleable.grxPreferences_iconsValueTint)) {
            try {
               iconsValueTint = ta.getInt(R.styleable.grxPreferences_iconsValueTint, 0);
            } catch (Exception e) {

            }
        }    
        ta.recycle();
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }

    @Override
    public void resetPreference(){
        String[] uris = mStringValue.split(Pattern.quote(myPrefAttrsInfo.getMySeparator()));
        mStringValue= myPrefAttrsInfo.getMyStringDefValue();
        configStringPreference(mStringValue);
        saveNewStringValue(mStringValue);
    }

    @Override
    public void configStringPreference(String value){
        setSummary(myPrefAttrsInfo.getMySummary());
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            DlgFrGrxPerItemColor dlg = (DlgFrGrxPerItemColor) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRITEMSCOLORS);
            if(dlg==null){
                dlg = DlgFrGrxPerItemColor.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(), myPrefAttrsInfo.getMyTitle(),
                        mStringValue,myPrefAttrsInfo.getMyOptionsArrayId(), myPrefAttrsInfo.getMyValuesArrayId(), myPrefAttrsInfo.getMyIconsArrayId(), iconsValueTint, idDefaultColors,
                        defaultColor, myPrefAttrsInfo.getMySeparator());
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRGRITEMSCOLORS);
            }
        }

    }

    public void onItemsColorsSelected(String value){
        if(!mStringValue.equals(value)){
            mStringValue=value;
            saveNewStringValue(mStringValue);
            configStringPreference(mStringValue);
        }
    }

}
