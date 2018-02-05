/*
 * Copyright 2015 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sublimenavigationview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.grx.settings.R;
/**
 * View implementation for Switch menu item.
 *
 * Created by Vikram.
 */
public class SublimeSwitchItemView extends SublimeBaseItemView {

    private SwitchCompat mSwitch;

    public SublimeSwitchItemView(Context context) {
        this(context, null);
    }

    public SublimeSwitchItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SublimeSwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.sublime_menu_switch_item_content, this, true);
        initializeViews();
    }

    @Override
    protected void initializeViews() {
        super.initializeViews();
        mSwitch = (SwitchCompat) findViewById(R.id.switch_ctrl);
    }

    @Override
    public void initialize(SublimeBaseMenuItem itemData, SublimeThemer themer) {
        setCheckableItemTintList(themer.getCheckableItemTintList());
        super.checkbox_switch_initialize(itemData, themer);
    }

    @Override
    public void setItemTextColor(ColorStateList textColor) {
        super.setItemTextColor(textColor);
        mSwitch.setTextColor(textColor);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mSwitch.setEnabled(enabled);
    }

    @Override
    public void setItemChecked(boolean checked) {
        super.setItemChecked(checked);
        mSwitch.setChecked(checked);
    }

    public void setCheckableItemTintList(ColorStateList checkableItemTintList) {

    }

}