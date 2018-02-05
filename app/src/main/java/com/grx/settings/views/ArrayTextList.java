package com.grx.settings.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grx.settings.R;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


public class ArrayTextList extends LinearLayout {

    int id_array;
    private int mHeight=0;

    public ArrayTextList(Context context) {
        super(context);
    }

    public ArrayTextList(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini_params(context, attrs);
    }

    private void ini_params(Context context, AttributeSet attributeSet) {

        //id_array = Utils.get_array_id(getContext(), attributeSet.getAttributeValue(null, "grxA_entries"));

        id_array=attributeSet.getAttributeResourceValue(null, "grxA_entries", 0);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);

        TextView textView = new TextView(context);
        float bulletsize = textView.getTextSize()/2;
        float normalsize = textView.getTextSize();
        int normal_color = textView.getCurrentTextColor();
        TypedArray a = getContext().getTheme().obtainStyledAttributes( new int[] {R.attr.complemnt_accent_color});
        int bullet_color =a.getColor(0,0);
        a.recycle();

        String linefeed="\r\n\r\n";



        String[] mStringsArray = getContext().getResources().getStringArray(id_array);
        int array_lengh=mStringsArray.length;
        if (mStringsArray != null) {
            for (int i = 0; i < array_lengh;i++) {
                Spannable bullet=new SpannableString("\u2022"+"\u0020"+"\u0009");
                bullet.setSpan(new ForegroundColorSpan(bullet_color),0,bullet.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.append(bullet);
               // bullet.setSpan(new AbsoluteSizeSpan((int) bulletsize), 0, bullet.length(), SPAN_INCLUSIVE_INCLUSIVE);
                Spannable text =new SpannableString(mStringsArray[i]);
                text.setSpan(new ForegroundColorSpan(normal_color),0,text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
               // text.setSpan(new AbsoluteSizeSpan((int) normalsize), 0, text.length(), SPAN_INCLUSIVE_INCLUSIVE);
                textView.append(text);
                if(i<(array_lengh-1)) textView.append(linefeed);
            }
        }
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        addView(textView);
    }


}