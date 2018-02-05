
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

package com.grx.settings.prefssupport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

import java.net.URISyntaxException;

public class GrxAccessInfo {

    private String mUri;

    private String mLabel = null;
    private String mGrxDrawableName = null;
    private String mGrxValue = null;
    private String mGrxIconPath = null;
    private int mGrxTypeOfAccess = -1;

    Drawable mDrawableIcon;


    public GrxAccessInfo(String uri, Context c){
        ini_access_info(uri, c);

    }


    public void ini_access_info(String uri, Context context){
        mUri=uri;

        Intent intent;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {
            return;
        }

        mLabel = GrxPrefsUtils.getActivityLabelFromIntent(context, intent);
        mGrxIconPath = GrxPrefsUtils.getFileNameFromGrxIntent(intent);
        mGrxDrawableName = intent.getStringExtra(Common.EXTRA_URI_DRAWABLE_NAME);
        mGrxValue = intent.getStringExtra(Common.EXTRA_URI_VALUE);
        mGrxTypeOfAccess = intent.getIntExtra(Common.EXTRA_URI_TYPE,-1);
        mDrawableIcon = GrxPrefsUtils.getDrawableFromGrxIntent(context,intent);

      }


    public void update_uri(String uri){mUri=uri;}

    public int get_access_type(){return mGrxTypeOfAccess;}

    public Drawable get_icon_drawable(){return mDrawableIcon;}

    public String get_uri(){return mUri;}

    public String get_label(){return (mLabel == null) ? "?" : mLabel;}

    public String get_icon_path(){return mGrxIconPath;}

    public String get_drawable_name(){return mGrxDrawableName;}

}
