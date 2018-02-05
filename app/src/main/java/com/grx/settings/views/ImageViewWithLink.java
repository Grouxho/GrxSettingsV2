package com.grx.settings.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxImageHelper;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


public class ImageViewWithLink extends ImageView implements View.OnClickListener {

    private String mUrl;
    private boolean mCircular;

    public ImageViewWithLink(Context context){
        super(context);
    }

    public ImageViewWithLink(Context context, AttributeSet attrs){
        super(context, attrs);
        ini_params(context,attrs);
    }

    private void ini_params(Context context, AttributeSet attributeSet){
        mUrl = attributeSet.getAttributeValue(null, Common.INFO_ATTR_ULR);
        mCircular = attributeSet.getAttributeBooleanValue(null,Common.INFO_ATTR_ROUND_ICON,false);
        Drawable drawable = getDrawable();
        if(drawable!=null && mCircular){
            Bitmap bitmap=GrxImageHelper.get_circular_bitmap(GrxImageHelper.drawableToBitmap(drawable));
            Drawable newdrawable = new BitmapDrawable(context.getResources(), bitmap);
            setImageDrawable(newdrawable);
        }

        if(mUrl!=null) {
            setClickable(true);
            setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view){
        if(mUrl!=null){
            Intent myintent=new Intent(Intent.ACTION_VIEW);
            myintent.setData(Uri.parse(mUrl));
            getContext().startActivity(myintent);
        }
    }

}
