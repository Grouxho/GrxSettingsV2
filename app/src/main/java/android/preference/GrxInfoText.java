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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grx.settings.R;
import com.grx.settings.prefssupport.PrefAttrsInfo;
import com.grx.settings.utils.Common;

public class GrxInfoText extends GrxBasePreference{

    private int mRightIconTint =0;

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

        TypedArray ta;
        if(Common.mContextWrapper!=null) ta = Common.mContextWrapper.obtainStyledAttributes(attrs, R.styleable.grxInfotext);
        else ta = context.obtainStyledAttributes(attrs, R.styleable.grxInfotext);

        //   TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxInfotext);
        Drawable mRightIcon=ta.getDrawable(R.styleable.grxInfotext_rightIcon);

        if(ta.hasValue(R.styleable.grxInfotext_rightIconTint)) {
            try {
                mRightIconTint = ta.getInt(R.styleable.grxInfotext_rightIconTint, 0);
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
        if(vWidgetIcon!=null && mRightIconTint !=0) {
            vWidgetIcon.setColorFilter(mRightIconTint);
        }
        return view;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refreshView();
    }


}
