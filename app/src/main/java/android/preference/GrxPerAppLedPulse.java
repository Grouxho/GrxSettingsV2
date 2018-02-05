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

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefs_dlgs.DlgFrGrxPerAppLedPulse;
import com.grx.settings.utils.Common;

import java.util.regex.Pattern;


public class GrxPerAppLedPulse extends GrxBasePreference implements DlgFrGrxPerAppLedPulse.PerAppLedPulseListener{

    private String mLabel;
    private boolean showSystemApps;


    public GrxPerAppLedPulse(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxPerAppLedPulse(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context, attrs, true, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        showSystemApps = ta.getBoolean(R.styleable.grxPreferences_showSystemapps,getContext().getResources().getBoolean(R.bool.grxb_showSystemapps_default));
        ta.recycle();
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }


    @Override
    public void resetPreference(){
        mStringValue = myPrefAttrsInfo.getMyStringDefValue();
        saveNewStringValue(mStringValue);
        configStringPreference(mStringValue);
    }


    @Override
    public void configStringPreference(String value){
        int numitems=0;
        if(! (mStringValue.isEmpty()||(mStringValue==null))  ){
            String[] arr = mStringValue.split(Pattern.quote(myPrefAttrsInfo.getMySeparator()));
            numitems=arr.length;
        }
        if(numitems==0) mLabel="";/*myPrefAttrsInfo.getMySummary();*/
        else mLabel = getContext().getString( R.string.grxs_num_selected,numitems ) ;
        setSummary(myPrefAttrsInfo.getMySummary() + " " +mLabel);
    }


    @Override
    public void showDialog(){
        DlgFrGrxPerAppLedPulse dlg;
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            dlg = (DlgFrGrxPerAppLedPulse) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRPERAPPAPPLEDPULSE);
            if(dlg==null){
                dlg = DlgFrGrxPerAppLedPulse.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyTitle(), mStringValue,
                        myPrefAttrsInfo.getMySeparator(), showSystemApps,myPrefAttrsInfo.getMyMaxItems());
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRPERAPPAPPLEDPULSE);
            }
        }

    }


    public void onAppsLedPulseSelected(String apps_selected){
        if(!mStringValue.equals(apps_selected)){
            mStringValue=apps_selected;
            configStringPreference(apps_selected);
            saveNewStringValue(apps_selected);
        }

    }

}
