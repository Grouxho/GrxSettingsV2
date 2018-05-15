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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grx.settings.R;
import com.grx.settings.utils.Common;


public class GrxButtonPreference extends GrxBasePreference {

    TextView vButton =null;
    String mButtonText=null;


    private int buttonbgcolor=0, buttontextcolor=0;

    int mButtonStyle=0;

    public GrxButtonPreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        initAttributes(context, attrs);
    }

    public GrxButtonPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray ta;
        if(Common.mContextWrapper!=null) ta = Common.mContextWrapper.obtainStyledAttributes(attrs, R.styleable.grxPreferenceButton);
        else ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferenceButton);
        //TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferenceButton);
        mButtonStyle=ta.getInt(R.styleable.grxPreferenceButton_button_Style,0);
        mButtonText=ta.getString(R.styleable.grxPreferenceButton_button_Text);

        if(ta.hasValue(R.styleable.grxPreferenceButton_button_backgroundColor))
            buttonbgcolor = ta.getInt(R.styleable.grxPreferenceButton_button_backgroundColor, 0);

        if(ta.hasValue(R.styleable.grxPreferenceButton_button_textColor))
            buttontextcolor = ta.getInt(R.styleable.grxPreferenceButton_button_textColor, 0);

        ta.recycle();

        switch (mButtonStyle){
            case 0:
                break;
            case 1:
                setWidgetLayoutResource(R.layout.pref_grxbutton_widget);
                if(mButtonText==null) mButtonText=getContext().getString(R.string.grxs_apply);
                break;
            case 2:
                setLayoutResource(R.layout.pref_grxbutton);
                setWidgetLayoutResource(R.layout.pref_grxbutton_widget);
                if(mButtonText==null) {
                    mButtonText=getTitle().toString();
                    setTitle("");
                }
                break;
        }

        initIntPrefsCommonAttributes(getContext(),attrs,0,false);
        mDisableDoubleClick=true;

    }

    @Override
    public void saveintValueInSettings(int valor){

    }

    private void setButtonAndTextColors(){
        if(buttonbgcolor!=0) {
            Drawable drawable = vButton.getBackground();
            if(drawable!=null) drawable.setTint(buttonbgcolor);
            vButton.setBackground(drawable);
        }

        if(buttontextcolor!=0){
            if(vButton!=null) vButton.setTextColor(buttontextcolor);
        }

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        switch (mButtonStyle){
            case 0:
                break;
            case 1:
                vButton =(Button) view.findViewById(R.id.gid_button);
                vButton.setText(mButtonText);
                setButtonAndTextColors();
                vButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                break;
            case 2:
                vButton =(Button) view.findViewById(R.id.gid_button);
                vButton.setText(mButtonText);
                setButtonAndTextColors();
                vButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if(getIcon()==null){
                    LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.gid_icon_frame);
                    if(linearLayout!=null) linearLayout.setVisibility(View.GONE);

                }
                break;
            default:
                break;
        }

        if(vButton!=null){
            setSelectable(false);
            vButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    execButtonAction();
                }
            });
        }

        return view;
    }


    public void execButtonAction(){

        getOnPreferenceChangeListener().onPreferenceChange(this,mIntValue);

    }

    @Override
    protected void onClick() {
        execButtonAction();
    }


}
