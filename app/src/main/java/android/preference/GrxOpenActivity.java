/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */


package android.preference;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Toast;

import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.regex.Pattern;


public class GrxOpenActivity extends GrxBasePreference{

    private Intent mIntent;
    private String mLabel;
    private boolean mIsInstalled=false;
    private Drawable mIcon=null;



    public GrxOpenActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxOpenActivity(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setTypeOfPreference(PrefAttrsInfo.PREF_TYPE.NEUTRAL);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxOpenactivity);
        mStringValue=ta.getString(R.styleable.grxOpenactivity_activity);
        ta.recycle();
        checkDepRuleAndAssignKeyIfNeeded();
        if(mStringValue.contains("/")){
            mIntent = new Intent();
            String aux[]=mStringValue.split(Pattern.quote("/"));
            ComponentName componentName = new ComponentName(aux[0], aux[1] );
            mIntent.setComponent(componentName);
            mLabel = GrxPrefsUtils.getActivityLabelFromIntent(getContext(),mIntent);
        }else {
            mIntent=getContext().getPackageManager().getLaunchIntentForPackage(mStringValue);
            mLabel=GrxPrefsUtils.getApplicationLabel(getContext(),mStringValue);
        }
        if(mIntent!=null) mIcon = GrxPrefsUtils.getIconFromIntent(getContext(),mIntent);
        if(mLabel==null) mLabel="";
        ResolveInfo resolveInfo = getContext().getPackageManager().resolveActivity(mIntent, 0);
        if(resolveInfo!=null) mIsInstalled = true;
        setSummary(mIsInstalled ? mLabel : getContext().getResources().getString(R.string.grxs_not_installed));
        setEnabled(mIsInstalled);
    }

    @Override
    public void configNeutralPreference(){

    }

    @Override
    protected void onClick() {
        super.onClick();
        if (mIsInstalled) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(mIntent);
        }else Toast.makeText(getContext(),getContext().getResources().getString(R.string.grxs_not_installed),Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        if(mIcon!=null) setIcon(mIcon);
    }

    /************ custom dependencies ****************/
    @Override
    public void OnCustomDependencyChange(boolean state){
        if(mIsInstalled) {
            setEnabled(state);
        }else setEnabled(false);
    }

}
