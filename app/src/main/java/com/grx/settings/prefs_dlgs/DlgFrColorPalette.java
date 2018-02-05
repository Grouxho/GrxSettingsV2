
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
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.grx.settings.R;


import java.io.File;
import java.io.FileInputStream;


public class DlgFrColorPalette extends DialogFragment implements View.OnClickListener {


    ImageView vImage;
    ImageView vColorSample;
    View vContainer;


    ImageView v_c;
    ImageView v_vb;
    ImageView v_m;
    ImageView v_lvb;
    ImageView v_dvb;
    ImageView v_lm;
    ImageView v_dm;
    int central_color;
    int c_vb;
    int c_m;
    int c_lvb;
    int c_dvb;
    int c_dm;
    int c_lm;
    int current_color;

    private onColorAutoListener ColorAutoListener;

    TextView vSampleText;

    String mCallbackFragmentName;
    String mImgFile;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    public DlgFrColorPalette() {
    }

    public static DlgFrColorPalette newInstance(String callback_fragment, String img_file_name) {
        DlgFrColorPalette ret = new DlgFrColorPalette();
        ret.iniPaletteFragment(callback_fragment, img_file_name);

        return ret;

    }

    private void iniPaletteFragment(String callback_fragment, String img_file_name) {

        mCallbackFragmentName = callback_fragment;
        mImgFile = img_file_name;

    }

    private View getDialogView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_colorpicker_palette, null);
        vImage = (ImageView) view.findViewById(R.id.gid_result_color_imageview);
        vColorSample = (ImageView) view.findViewById(R.id.gid_color_imageview);
        vContainer = view.findViewById(R.id.gid_result_container);

        v_vb = (ImageView) view.findViewById(R.id.gid_vibrant);
        v_vb.setOnClickListener(this);
        v_c = (ImageView) view.findViewById(R.id.gid_calculated_palette);
        v_c.setOnClickListener(this);
        v_m = (ImageView) view.findViewById(R.id.gid_muted);
        v_m.setOnClickListener(this);
        v_lvb = (ImageView) view.findViewById(R.id.gid_light_vibrant);
        v_lvb.setOnClickListener(this);
        v_dvb = (ImageView) view.findViewById(R.id.gid_dark_vibrant);
        v_dvb.setOnClickListener(this);
        v_lm = (ImageView) view.findViewById(R.id.gid_light_muted);
        v_lm.setOnClickListener(this);
        v_dm = (ImageView) view.findViewById(R.id.gid_dark_muted);

        v_dm.setOnClickListener(this);
        vSampleText = (TextView) view.findViewById(R.id.gid_color_text);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] colors = new int[]{current_color, central_color, c_m, c_dm, c_lm, c_vb, c_lvb, c_dvb};
        outState.putIntArray("colors", colors);
        outState.putString("target_fragment",mCallbackFragmentName);
        outState.putString("file_name",mImgFile);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        if (state != null) {
            int[] colors = new int[8];
            colors = state.getIntArray("colors");
            current_color = colors[0];
            central_color = colors[1];
            c_m = colors[2];
            c_dm = colors[3];
            c_lm = colors[4];
            c_vb = colors[5];
            c_lvb = colors[6];
            c_dvb = colors[7];
            mCallbackFragmentName=state.getString("target_fragment");
            mImgFile=state.getString("file_name");

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.grxs_auto_color);
        builder.setView(getDialogView());
        builder.setNegativeButton(R.string.grxs_cancel, null);
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (ColorAutoListener == null) {
                    ColorAutoListener = (onColorAutoListener) getFragmentManager().findFragmentByTag(mCallbackFragmentName);
                }
                if (ColorAutoListener != null) ColorAutoListener.onColorAuto(current_color, true);
                else dismiss();
            }
        });

        if(state==null) buildPalette();
        else drawColors();

        AlertDialog d = builder.create();

        return d;
    }


    private Bitmap readBmpFile() {
        Bitmap bitmap = null;
        if (mImgFile != null) {
            File f = new File(mImgFile);
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (Exception e) {
                bitmap = null;
            }

        }
        return bitmap;
    }

    private Drawable getDrawableFromBmp() {
        BitmapDrawable drawable = null;
        if (mImgFile != null) {
            drawable = new BitmapDrawable(getActivity().getResources(), mImgFile);
        }
        return drawable;
    }

    public interface onColorAutoListener {
        public void onColorAuto(int color, boolean auto);
    }


    @Override
    public void onClick(View v) {

        String txt = "";
        switch (v.getId()) {

            case R.id.gid_calculated_palette:
                current_color = central_color;
                txt = "Central";
                break;
            case R.id.gid_vibrant:
                current_color = c_vb;
                txt = "Vibrant";
                break;
            case R.id.gid_light_vibrant:
                current_color = c_lvb;
                txt = "Light Vibrant";
                break;
            case R.id.gid_dark_vibrant:
                current_color = c_dvb;
                txt = "Dark Vibrant";
                break;

            case R.id.gid_muted:
                current_color = c_m;
                txt = "Muted";
                break;

            case R.id.gid_light_muted:
                current_color = c_lm;
                txt = "Light Muted";
                break;

            case R.id.gid_dark_muted:
                current_color = c_dm;
                txt = "Dark Muted";
                break;
        }
        vColorSample.setColorFilter(current_color, PorterDuff.Mode.MULTIPLY);
        vSampleText.setText(txt);
        vContainer.setBackgroundColor(current_color);
    }



    private void drawColors(){

        v_m.setColorFilter(c_m, PorterDuff.Mode.MULTIPLY);
        v_dm.setColorFilter(c_dm, PorterDuff.Mode.MULTIPLY);
        v_lm.setColorFilter(c_lm, PorterDuff.Mode.MULTIPLY);
        v_vb.setColorFilter(c_vb, PorterDuff.Mode.MULTIPLY);
        v_lvb.setColorFilter(c_lvb, PorterDuff.Mode.MULTIPLY);
        v_dvb.setColorFilter(c_dvb, PorterDuff.Mode.MULTIPLY);
        vColorSample.setColorFilter(central_color,PorterDuff.Mode.MULTIPLY);
        v_c.setColorFilter(central_color,PorterDuff.Mode.MULTIPLY);
        vContainer.setBackgroundColor(central_color);
        vImage.setImageDrawable(getDrawableFromBmp());
    }

    public void buildPalette() {

        Bitmap bm = readBmpFile();

        if(bm==null) {
            dismiss();
            return;
        }

        final int color_central = bm.getPixel(bm.getWidth() / 2, bm.getHeight() / 2);
        central_color = color_central;
        current_color = color_central;
        Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                c_m = palette.getMutedColor(color_central);
                c_dm = palette.getDarkMutedColor(color_central);
                c_lm = palette.getLightMutedColor(color_central);
                c_vb = palette.getVibrantColor(color_central);
                c_lvb = palette.getLightVibrantColor(color_central);
                c_dvb = palette.getDarkVibrantColor(color_central);

                drawColors();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void setListener(onColorAutoListener listener){
        ColorAutoListener = listener;
    }



}
