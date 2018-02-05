
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.prefssupport;

import android.graphics.drawable.Drawable;



public class GrxCustomOptionInfo {

    String mTitle;
    String mValue;
    Drawable mIcon;
    boolean mIsSelected;


    public GrxCustomOptionInfo(String tit, String val, Drawable icon){
        mTitle=tit;
        mIcon = icon;
        mValue=val;
        mIsSelected=false;

    }

    public Drawable get_icon(){return mIcon;}

    public String get_title(){return mTitle;}

    public String get_value(){return mValue;}

    public void set_selected(boolean est){mIsSelected=est;}

    public boolean is_selected(){return mIsSelected;}


}
