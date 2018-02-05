package com.sublimenavigationview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.grx.settings.R;

/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */
 

public class GrxSublimeCenteredTextItemView extends SublimeBaseItemView {


    public GrxSublimeCenteredTextItemView(Context context) {
        this(context, null);
    }

    public GrxSublimeCenteredTextItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrxSublimeCenteredTextItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.sublime_grx_menu_centered_text_content, this, true);
        initializeViews();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        mText.setGravity(Gravity.CENTER);
    }

    @Override
    public void initialize(SublimeBaseMenuItem itemData, SublimeThemer themer) {
        super.initialize(itemData, themer);
        setItemTextColor(themer.getCenteredTextTintList());

        mIconHolder.setVisibility(GONE);
        mHint.setVisibility(GONE);
        setClickable(false);
    }
}

