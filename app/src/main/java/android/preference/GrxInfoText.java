/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */



package android.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;

public class GrxInfoText extends GrxBasePreference{

    private int mTintColor=0;

    public GrxInfoText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public GrxInfoText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setTypeOfPreference(PrefAttrsInfo.PREF_TYPE.NEUTRAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxInfotext);
        Drawable mRightIcon=ta.getDrawable(R.styleable.grxInfotext_rightIcon);

        if(ta.hasValue(R.styleable.grxInfotext_rightIconTint)) {
            try {
                mTintColor = ta.getInt(R.styleable.grxInfotext_rightIconTint, 0);
            } catch (Exception e) {
            }
        }
        ta.recycle();

            int a = 47;
        checkDepRuleAndAssignKeyIfNeeded();

        if(mRightIcon!=null) {
            setWidgetLayoutResource(R.layout.widget_icon_accent);
            setWidgetIcon(mRightIcon);
        }

        setSelectable(false);

    }

    @Override
    public void saveStringValueInSettings(String value){

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = (View) super.onCreateView(parent);
        TextView vTitle =  (TextView) view.findViewById(android.R.id.title);
        vTitle.setVisibility(View.GONE);
        String t = getSummary().toString();
        if(t!=null) setSummary(Html.fromHtml(getSummary().toString()));
        if(vWidgetIcon!=null && mTintColor!=0) {
            vWidgetIcon.setColorFilter(mTintColor);
        }
        return view;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refreshView();
    }



}
