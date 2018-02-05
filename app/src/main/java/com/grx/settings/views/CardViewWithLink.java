package com.grx.settings.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.grx.settings.utils.Common;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


public class CardViewWithLink extends CardView implements View.OnClickListener {

    String mUrl;


    public CardViewWithLink(Context context){
        super(context);
    }

    public CardViewWithLink(Context context, AttributeSet attrs){
        super(context,attrs);
        ini_view(context, attrs);
    }

    CardViewWithLink(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,defStyleAttr);
        ini_view(context, attrs);
    }

    private void ini_view(Context context, AttributeSet attributeSet){
            mUrl = attributeSet.getAttributeValue(null, Common.INFO_ATTR_ULR);
            if(mUrl!=null) {
                setClickable(true);
                setOnClickListener(this);
            }
     }


    @Override
    public void onClick(View view) {
        if(mUrl!=null) {
            Intent myintent = new Intent(Intent.ACTION_VIEW);
            myintent.setData(Uri.parse(mUrl));
            getContext().startActivity(myintent);
        }
    }
}
