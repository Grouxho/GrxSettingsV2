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
import android.widget.ImageView;

import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;



public class GrxOpenIntent extends GrxBasePreference{

    ImageView vWidgetIcon;
    ImageView vAndroidIcon;
    private String mMyDependencyRule;



    public GrxOpenIntent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxOpenIntent(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }


    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setTypeOfPreference(PrefAttrsInfo.PREF_TYPE.NEUTRAL);
        checkDepRuleAndAssignKeyIfNeeded();
    }

}
