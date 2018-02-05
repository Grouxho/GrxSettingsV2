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
import com.grx.settings.prefs_dlgs.DlgGrxAppSelection;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

public class GrxAppSelection extends GrxBasePreference implements DlgGrxAppSelection.OnGrxAppListener{

    private boolean showSystemApps;
    private boolean saveActivityName;
    private String mLabel;


    public GrxAppSelection(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxAppSelection(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        showSystemApps = ta.getBoolean(R.styleable.grxPreferences_showSystemapps,getContext().getResources().getBoolean(R.bool.grxb_showSystemapps_default));
        saveActivityName=ta.getBoolean(R.styleable.grxPreferences_saveActivityname, getContext().getResources().getBoolean(R.bool.grxb_saveActivityname_default));
        ta.recycle();
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }

    @Override
    public void configStringPreference(String value){

        if(value!=null && value.contains("/")){
            mWidgetIcon = GrxPrefsUtils.getIconFromPackageActivityString(getContext(),value);
            mLabel = GrxPrefsUtils.getLabelFromPackageActivityString(getContext(),mStringValue);
        }else {
            mLabel = GrxPrefsUtils.getApplicationLabel(getContext(),mStringValue);
            mWidgetIcon= GrxPrefsUtils.getApplicationIcon(getContext(),mStringValue);
        }


        if(mLabel.isEmpty()) mLabel=myPrefAttrsInfo.getMySummary();
        setSummary(mLabel);

    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refreshView();
        setSummary(mLabel);
    }


    @Override
    public void showDialog(){
        DlgGrxAppSelection dlg;
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            dlg = (DlgGrxAppSelection) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRSELECTAPP);
            if(dlg==null){
                dlg = DlgGrxAppSelection.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyTitle(),showSystemApps, saveActivityName, true);
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRGRSELECTAPP);
            }
        }
    }

    @Override
    public void resetPreference(){
        mStringValue= myPrefAttrsInfo.getMyStringDefValue();
        saveNewStringValue(mStringValue);
        configStringPreference(mStringValue);

    }

    @Override
    public void onGrxAppSel(DlgGrxAppSelection dialog, String app_selected){
        if(!mStringValue.equals(app_selected)){
            mStringValue=app_selected;
            saveNewStringValue(mStringValue);
            configStringPreference(mStringValue);

            //


        }
    }

}




