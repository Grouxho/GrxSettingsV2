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
import com.grx.settings.prefs_dlgs.DlgFrGrxSortList;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

public class GrxSortList extends GrxBasePreference implements DlgFrGrxSortList.OnSortedList {

    private boolean mSortIcon;
    private String mDefValue;
    int iconsValueTint =0;

    public GrxSortList(Context context, AttributeSet attrs){
        super(context,attrs);
        initAttributes(context,attrs);
    }

    public GrxSortList(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,true, true);
        

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxSortlist);
        mSortIcon=ta.getBoolean(R.styleable.grxSortlist_showShortIcon,getContext().getResources().getBoolean(R.bool.grxb_showShortIcon_default) );
        if(ta.hasValue(R.styleable.grxSortlist_iconsValueTint))
                iconsValueTint = ta.getInt(R.styleable.grxSortlist_iconsValueTint, 0);

        
        ta.recycle();

        mDefValue=myPrefAttrsInfo.getMyStringDefValue();
        if(mDefValue==null || mDefValue.isEmpty()){
            mDefValue= GrxPrefsUtils.getFormattedStringFromArrayResId(getContext(),myPrefAttrsInfo.getMyValuesArrayId(),myPrefAttrsInfo.getMySeparator());
          }
        setDefaultValue(mDefValue);
        setSummary(myPrefAttrsInfo.getMySummary());
    }

    @Override
    public void resetPreference(){
        if(mStringValue.isEmpty()) return;
        mStringValue= mDefValue;
        saveNewStringValue(mStringValue);
    }

    @Override
    public void showDialog(){
        GrxPreferenceScreen grxPreferenceScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(grxPreferenceScreen !=null){
            DlgFrGrxSortList dlg = (DlgFrGrxSortList) grxPreferenceScreen.getFragmentManager().findFragmentByTag("DlgFrGrxSortList");
            if(dlg==null){
                dlg = DlgFrGrxSortList.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, getKey(), getTitle().toString(), mStringValue, myPrefAttrsInfo.getMySeparator(),
                        myPrefAttrsInfo.getMyOptionsArrayId(), myPrefAttrsInfo.getMyValuesArrayId(), myPrefAttrsInfo.getMyIconsArrayId(),iconsValueTint,mSortIcon);
                dlg.show(grxPreferenceScreen.getFragmentManager(),"DlgFrGrxSortList");
            }
        }
    }

    @Override
    public void saveSortedList(String value){
        if(!mStringValue.equals(value)) {
            mStringValue= value;
            saveNewStringValue(mStringValue);
        }
    }



}
