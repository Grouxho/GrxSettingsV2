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
import android.view.ViewGroup;


import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefs_dlgs.DlgFrMultiSelect;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.regex.Pattern;


public class GrxSingleSelection extends GrxBasePreference implements DlgFrMultiSelect.GrxMultiSelectListener{

    private String mLabel;
    int iconsValueTint =0;

    String sep_summary =" - ";

    public GrxSingleSelection(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxSingleSelection(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxsiconsarray);

        if(ta.hasValue(R.styleable.grxsiconsarray_iconsValueTint))
            iconsValueTint = ta.getInt(R.styleable.grxsiconsarray_iconsValueTint, 0);


        ta.recycle();

        if(myPrefAttrsInfo.getMySummary()==null || myPrefAttrsInfo.getMySummary().isEmpty()) sep_summary ="";
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        if(vWidgetIcon!=null && iconsValueTint !=0) vWidgetIcon.setColorFilter(iconsValueTint);
        return view;
    }

    @Override
    public void configStringPreference(String value){
        setWidgetIcon(null);
        String vals_array[] = getContext().getResources().getStringArray(myPrefAttrsInfo.getMyValuesArrayId());
        int pos = -1;
        for(int i=0;i<vals_array.length;i++) {
            if (value.equals(vals_array[i])) {
                pos = i;
                break;
            }
        }
        if(pos!=-1) {
            String opt_array[] = getContext().getResources().getStringArray(myPrefAttrsInfo.getMyOptionsArrayId());
            mLabel = opt_array[pos];
            if(myPrefAttrsInfo.getMyIconsArrayId()!=0){
                TypedArray icons_array=getContext().getResources().obtainTypedArray(myPrefAttrsInfo.getMyIconsArrayId());
                mWidgetIcon = icons_array.getDrawable(pos);
                icons_array.recycle();
            }
        }else {
            mLabel= myPrefAttrsInfo.getMySummary();
            mWidgetIcon=null;
        }

        String summary = myPrefAttrsInfo.getMySummary();
        if(mLabel!=null && !mLabel.isEmpty() ) summary+= sep_summary +mLabel;
        setSummary(summary);

    }



    @Override
    public void resetPreference(){
        //   delete files(mValue);
        String[] uris = mStringValue.split(Pattern.quote(myPrefAttrsInfo.getMySeparator()));
        for(int i=0;i<uris.length;i++) GrxPrefsUtils.deleteGrxIconFileFromUriString(uris[i]);

        mStringValue= myPrefAttrsInfo.getMyStringDefValue();
        configStringPreference(mStringValue);
        saveNewStringValue(mStringValue);
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(prefsScreen!=null){

            DlgFrMultiSelect dlg = (DlgFrMultiSelect) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRMULTISELECT);
            if(dlg==null){
                dlg = DlgFrMultiSelect.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(), myPrefAttrsInfo.getMyTitle(),mStringValue,
                        myPrefAttrsInfo.getMyOptionsArrayId(), myPrefAttrsInfo.getMyValuesArrayId(), myPrefAttrsInfo.getMyIconsArrayId(), iconsValueTint,"", 1);
                dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRGRMULTISELECT);
            }
        }
    }

    private void set_summary(){

    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refreshView();
        String summary = myPrefAttrsInfo.getMySummary();
        if(mLabel!=null && !mLabel.isEmpty() ) summary+= sep_summary +mLabel;
        setSummary(summary);
    }

    public void GrxSetMultiSelect(String value){
        if(!mStringValue.equals(value)){
            mStringValue=value;
            saveNewStringValue(mStringValue);
            configStringPreference(mStringValue);
        }
    }


}