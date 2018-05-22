
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.prefs_dlgs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.GrxSettingsActivity;
import com.grx.settings.R;
import com.grx.settings.act.GrxImagePicker;
import com.grx.settings.utils.GrxImageHelper;
import com.grx.settings.utils.Common;
import com.qfcolorpicker.CircleColorDrawable;
import com.qfcolorpicker.ColorPickerView;
import com.qfcolorpicker.OnColorChangedListener;
import com.qfcolorpicker.Utils;
import com.qfcolorpicker.builder.ColorWheelRendererBuilder;
import com.qfcolorpicker.renderer.ColorWheelRenderer;
import com.qfcolorpicker.slider.AlphaSlider;
import com.qfcolorpicker.slider.LightnessSlider;
import com.smcolorpicker.ColorPickerPanelView;
import com.smcolorpicker.ColorPickerPreference;

import java.util.Locale;



public class DlgFrGrxColorPicker extends DialogFragment implements
        DlgFrColorPalette.onColorAutoListener

{

    private OnGrxColorPickerListener mCallBack;
    private String mTitle;
    private String mKey;
    private boolean showAlphaSlider;
    private boolean showAutoButton;
    private String mHelperFragmentName;
    private int pickerStyle;
    private int mCurrentColor;
    private int mOriginalColor;
    private boolean mSaveOnFly=false;

    /**** QuadFlask Color Picker */


    private boolean mQFWheelType;
    private Integer[] mQFinitialColors = new Integer[]{null, null, null, null, null};
    private ColorPickerView mQFcolorPickerView;
    private ImageView mQFColorPreview;



    /******** Sergey Margaritov **********/

    private com.smcolorpicker.ColorPickerView mSMColorPicker;

    private ColorPickerPanelView mSMOldColorView;
    private ColorPickerPanelView mSMNewColorView;
    private EditText mSMHexValView;
    private ColorStateList mSMHexDefaultTextColor;
    private int mSMOrientation;


    /********************************/


    public DlgFrGrxColorPicker(){}

    public interface OnGrxColorPickerListener{
        void onGrxColorSet(int color);
    }

    public static DlgFrGrxColorPicker newInstance(OnGrxColorPickerListener callback, String helperfragment, String title, String key, int initialcolor, int style, boolean alphaslider, boolean auto, boolean saveonfly){
        DlgFrGrxColorPicker ret = new DlgFrGrxColorPicker();
        ret.ini_picker(callback, helperfragment, title,key,initialcolor, style,alphaslider, auto, saveonfly);
        return ret;
    }

    private void ini_picker(OnGrxColorPickerListener callback, String helperfragment,  String title, String key, int initialcolor, int style, boolean alphaslider, boolean auto, boolean saveonfly){

        mCallBack=callback;
        mHelperFragmentName=helperfragment;
        mTitle=title;
        mKey=key;
        mOriginalColor=initialcolor;
        mCurrentColor =initialcolor;
        pickerStyle=style;
        showAlphaSlider =alphaslider;
        showAutoButton =auto;
        mSaveOnFly=saveonfly;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(pickerStyle!=2) mCurrentColor = mQFcolorPickerView.getSelectedColor();
        else mCurrentColor=getSMColor();

        outState.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY, mHelperFragmentName);
        outState.putString("title",mTitle);
        outState.putString(Common.EXTRA_KEY, mKey);
        outState.putInt("value", mCurrentColor);
        outState.putInt("orig_value", mOriginalColor);
        outState.putInt("pickerstyle", pickerStyle);
        outState.putBoolean("alpha", showAlphaSlider);
        outState.putBoolean("auto", showAutoButton);
        outState.putBoolean("saveonfly", mSaveOnFly);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(mCallBack==null) {
            if(mHelperFragmentName.equals(Common.TAG_PREFSSCREEN_FRAGMENT)){
                GrxPreferenceScreen prefsScreen =(GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                mCallBack=(DlgFrGrxColorPicker.OnGrxColorPickerListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxColorPicker.OnGrxColorPickerListener) getFragmentManager().findFragmentByTag(mHelperFragmentName);
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle state) {
        if(state!=null){
            mHelperFragmentName= state.getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
            mTitle=state.getString("title");
            mKey=state.getString(Common.EXTRA_KEY);
            mCurrentColor = state.getInt("value");
            mOriginalColor=state.getInt("orig_value");
            pickerStyle=state.getInt("pickerstyle");
            showAlphaSlider =state.getBoolean("alpha");
            showAutoButton =state.getBoolean("auto");
            mSaveOnFly=state.getBoolean("saveonfly");

        }

        if(pickerStyle!=2){/**** QF DIALOG *****/
            mQFinitialColors[0] = mCurrentColor;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setView(getQFcolorPickerView())
                    .setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mCallBack!=null) {
                                mCallBack.onGrxColorSet(mQFcolorPickerView.getSelectedColor());
                            }
                        }
                    });

            if(showAutoButton) builder.setNeutralButton("Auto",null);
            final AlertDialog ad = builder.create();
            if(showAutoButton){
                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = ad.getButton(DialogInterface.BUTTON_NEUTRAL);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if((getTag()!=null && !getTag().isEmpty())){
                                    GrxSettingsActivity grxSettingsActivity = (GrxSettingsActivity) getActivity();
                                    Intent intent = new Intent(grxSettingsActivity, GrxImagePicker.class);
                                    intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,getTag());
                                    intent = GrxImageHelper.intent_img_crop_circular(intent);
                                    grxSettingsActivity.do_fragment_gallery_image_picker(intent);
                                }
                            }
                        });
                    }
                });
            }

            return ad;

        }else { /** Sergey margaritov color picker dialog ***/

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mTitle)
                    .setView(getSMcolorPickerView(mCurrentColor, mOriginalColor))
                    .setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mCallBack!=null) {
                                mCallBack.onGrxColorSet(mSMNewColorView.getColor());
                            }
                        }
                    });
            if(showAutoButton) builder.setNeutralButton("Auto",null);
            final AlertDialog ad = builder.create();
            if(showAutoButton){
                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = ad.getButton(DialogInterface.BUTTON_NEUTRAL);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if((getTag()!=null && !getTag().isEmpty())){
                                    GrxSettingsActivity activity = (GrxSettingsActivity) getActivity();
                                    Intent intent = new Intent(activity, GrxImagePicker.class);
                                    intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,getTag());
                                    intent = GrxImageHelper.intent_img_crop_circular(intent);
                                    activity.do_fragment_gallery_image_picker(intent);
                                }
                            }
                        });
                    }
                });
            }
            return ad;

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        String sFile = data.getStringExtra(GrxImagePicker.S_DIR_IMG);

        if(sFile!=null) {
            DlgFrColorPalette dlg_palette = DlgFrColorPalette.newInstance(Common.TAG_DLGFRGRXCOLORPICKER, sFile);
            dlg_palette.setListener(this);
            dlg_palette.show(getFragmentManager(),Common.TAG_DLGFRGRCOLORPALETTE);
        }

    }

    public void onColorAuto(int color, boolean auto){
        if(auto) {
            if(pickerStyle!=2) setQFcolor(color);
            else {
                setSMColor(color,true,false);
                mSMColorPicker.setColor(color);
            }
        }
    }


    /****************************/
    /**** QuadFlask Color Picker */
    /****************************/

    private View getQFcolorPickerView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_qf_colorpicker,null);
        mQFcolorPickerView = (ColorPickerView) view.findViewById(R.id.colorpickerview);
        mQFcolorPickerView.setInitialColors(mQFinitialColors, getQFStartOffset(mQFinitialColors));

        ColorPickerView.WHEEL_TYPE wheelType;

        if(pickerStyle==1) wheelType=ColorPickerView.WHEEL_TYPE.CIRCLE;
        else wheelType=ColorPickerView.WHEEL_TYPE.FLOWER;

        ColorWheelRenderer renderer = ColorWheelRendererBuilder.getRenderer(wheelType);
        mQFcolorPickerView.setRenderer(renderer);
        if(showAlphaSlider){
            AlphaSlider alphaSlider = (AlphaSlider) view.findViewById(R.id.alpha_slider);
            alphaSlider.setVisibility(View.VISIBLE);
            mQFcolorPickerView.setAlphaSlider(alphaSlider);
            alphaSlider.setColor(getQFStartColor(mQFinitialColors));
            TextView textView =(TextView) view.findViewById(R.id.v_txt_alfa);
            textView.setVisibility(View.VISIBLE);

        }

        LightnessSlider lightnessSlider = (LightnessSlider) view.findViewById(R.id.lightness_slider);
        mQFcolorPickerView.setLightnessSlider(lightnessSlider);
        lightnessSlider.setColor(getQFStartColor(mQFinitialColors));

        TypedArray a = getActivity().getTheme().obtainStyledAttributes( new int[] {R.attr.main_bg_color});
        int bgcolor = a.getColor(0,0);
        a.recycle();

        mQFcolorPickerView.setBackgroundDrawable(new CircleColorDrawable(bgcolor)) ;
        EditText colorEdit = (EditText) view.findViewById(R.id.edit_text);
        colorEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        colorEdit.setSingleLine();
        colorEdit.setVisibility(View.GONE);
        int maxLength = showAlphaSlider ? 9 : 7;
        colorEdit.setText(Utils.getHexString(getQFStartColor(mQFinitialColors), showAlphaSlider));
        colorEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        mQFcolorPickerView.setColorEdit(colorEdit);
        mQFcolorPickerView.setDensity(12);
        mQFColorPreview = (ImageView) view.findViewById(R.id.color_preview);
        mQFColorPreview.setImageDrawable(new CircleColorDrawable( mQFinitialColors[0] ));
        mQFcolorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                mQFColorPreview.setImageDrawable(new CircleColorDrawable( selectedColor));
                if(mSaveOnFly) {
                    if(mCallBack!=null) mCallBack.onGrxColorSet(selectedColor);
                }
            }
        });


        return view;
    }

    private int getQFStartColor(Integer[] colors) {
        Integer startColor = getQFStartOffset(colors);
        return startColor == null ? Color.WHITE : colors[startColor];
    }

    private Integer getQFStartOffset(Integer[] colors) {
        Integer start = 0;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == null) {
                return start;
            }
            start = (i + 1) / 2;
        }
        return start;
    }

    public void setQFcolor(int color){
        this.mQFcolorPickerView.setColor(color,true);
        mQFColorPreview.setImageDrawable(new CircleColorDrawable( color));
        mCurrentColor =color;
    }


    /******** Sergey Margaritov **********/


    private View getSMcolorPickerView(int currentColor, int originalColor){

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mLayout = inflater.inflate(R.layout.dlg_sm_colorpicker, null);
        mSMOrientation = getActivity().getResources().getConfiguration().orientation;

        mSMColorPicker = (com.smcolorpicker.ColorPickerView) mLayout.findViewById(R.id.color_picker_view);
        mSMColorPicker.setAlphaSliderVisible(showAlphaSlider);

        mSMOldColorView = (ColorPickerPanelView) mLayout.findViewById(R.id.old_color_panel);
        mSMNewColorView = (ColorPickerPanelView) mLayout.findViewById(R.id.new_color_panel);

        mSMHexValView = (EditText) mLayout.findViewById(R.id.hex_val);
        mSMHexValView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mSMHexValView.setVisibility(View.VISIBLE);
        updateSMHexLengthFilter();
        mSMHexDefaultTextColor = mSMHexValView.getTextColors();
        mSMHexValView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String s = mSMHexValView.getText().toString();
                    if (s.length() > 5 || s.length() < 10) {
                        try {
                            int c = ColorPickerPreference.convertToColorInt(s.toString());
                            mSMColorPicker.setColor(c, true);
                            mSMHexValView.setTextColor(mSMHexDefaultTextColor);
                        } catch (IllegalArgumentException e) {
                            mSMHexValView.setTextColor(Color.RED);
                        }
                    } else {
                        mSMHexValView.setTextColor(Color.RED);
                    }
                    return true;
                }
                return false;
            }
        });

        ((LinearLayout) mSMOldColorView.getParent()).setPadding(
                Math.round(mSMColorPicker.getDrawingOffset()),
                0,
                Math.round(mSMColorPicker.getDrawingOffset()),
                0
        );

        mSMColorPicker.setOnColorChangedListener(new com.smcolorpicker.ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                setSMColor(color,true, false);
            }
        });
        mSMOldColorView.setColor(originalColor);
        mSMOldColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentColor=mOriginalColor;
                mSMNewColorView.setColor(mOriginalColor);
                updateSMHexValue(mOriginalColor);
                mSMColorPicker.setColor(mOriginalColor,false);
            }
        });
        mSMNewColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallBack!=null)
                    mCallBack.onGrxColorSet(mSMNewColorView.getColor());
                dismiss();
            }
        });

        mSMColorPicker.setColor(currentColor, true);
        updateSMHexValue(getSMColor());
        return mLayout;
    }


    private void setSMColor(int color, boolean updatetext, boolean callback){
        mCurrentColor=color;
    //    mSMColorPicker.setColor(mCurrentColor, callback);
        mSMNewColorView.setColor(mCurrentColor);
        if(updatetext) updateSMHexValue(mCurrentColor);
        if(mSaveOnFly){
            if(mCallBack!=null){
                mCallBack.onGrxColorSet(mCurrentColor);
            }
        }
    }

    private void updateSMHexLengthFilter() {
        if (showAlphaSlider)
            mSMHexValView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        else
            mSMHexValView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
    }

    private void updateSMHexValue(int color) {
        if (showAlphaSlider) {
            mSMHexValView.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()));
        } else {
            mSMHexValView.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        }
        mSMHexValView.setTextColor(mSMHexDefaultTextColor);
    }

    public int getSMColor() {
        return mSMColorPicker.getColor();
    }


}
