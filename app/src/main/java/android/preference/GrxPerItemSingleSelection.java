package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefs_dlgs.DlgFrGrxPerItemSingleSelection;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.regex.Pattern;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

public class GrxPerItemSingleSelection extends GrxBasePreference implements DlgFrGrxPerItemSingleSelection.PerItemSingleSelectionDialogListener {

    int iconsValueTint =0;
    boolean shortout = false;
    int spinnerOptionsArrayId = 0, spinnerValuesArrayId=0;


    public GrxPerItemSingleSelection(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxPerItemSingleSelection(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,true, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxperitemsingleselection);

        if(ta.hasValue(R.styleable.grxperitemsingleselection_iconsValueTint)) {
            try {
                iconsValueTint = ta.getInt(R.styleable.grxperitemsingleselection_iconsValueTint, 0);

            } catch (Exception e) {
            }
        }

        shortout = ta.getBoolean(R.styleable.grxperitemsingleselection_allowSortOut,false);
        spinnerOptionsArrayId = ta.getResourceId(R.styleable.grxperitemsingleselection_spinnerOptionsArray,0);
        spinnerValuesArrayId = ta.getResourceId(R.styleable.grxperitemsingleselection_spinnerValuesArray,0);

        ta.recycle();
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){
            DlgFrGrxPerItemSingleSelection dlg = (DlgFrGrxPerItemSingleSelection) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRPERITEMSINGLESELECTION);
            if(dlg==null){
                dlg = DlgFrGrxPerItemSingleSelection.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(), myPrefAttrsInfo.getMyTitle(),mStringValue,
                        myPrefAttrsInfo.getMyOptionsArrayId(), myPrefAttrsInfo.getMyValuesArrayId(), myPrefAttrsInfo.getMyIconsArrayId(), spinnerOptionsArrayId, spinnerValuesArrayId,
                        iconsValueTint,
                        myPrefAttrsInfo.getMySeparator(),shortout);
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRGRPERITEMSINGLESELECTION);
            }
        }
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

    }

    @Override
    public void resetPreference(){
        mStringValue = myPrefAttrsInfo.getMyStringDefValue();
        saveNewStringValue(mStringValue);
    }


    @Override
    public void configStringPreference(String value){

    }

    public void onPerItemSingleSelectionSet(String value){
        mStringValue=value;
        saveNewStringValue(mStringValue);
    }

}
