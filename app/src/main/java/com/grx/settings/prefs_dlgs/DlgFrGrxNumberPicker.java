
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
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;



public class DlgFrGrxNumberPicker extends DialogFragment {

    String mkey;
    String mHelperFragmentName;
    String mTitle;
    int mMin;
    int mMax;
    int mValue;
    String mUnits;
    private OnGrxNumberPickerSetListener mCallback;
    NumberPicker numberPicker;

    public DlgFrGrxNumberPicker(){}

    public static DlgFrGrxNumberPicker newInstance(OnGrxNumberPickerSetListener callback, String key, String title, int value, int min_value, int max_value, String units, String callback_fragment_finder_name){

        DlgFrGrxNumberPicker ret = new DlgFrGrxNumberPicker();
        String aux_key="";
        if(key!=null) aux_key=key;
        Bundle bundle = new Bundle();
        bundle.putString(Common.EXTRA_KEY, aux_key);
        bundle.putString("title", title);
        bundle.putInt("value",value);
        bundle.putInt("min",min_value);
        bundle.putInt("max",max_value);
        bundle.putString("units",units);
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY, callback_fragment_finder_name);
        ret.setArguments(bundle);
        ret.iniCallback(callback);
        return ret;
    }


    private void iniCallback(OnGrxNumberPickerSetListener callback){
        mCallback=callback;
    }

    public interface OnGrxNumberPickerSetListener{
        void onGrxNumberPickerSet(int value, String key);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mkey = null;


        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mkey = getArguments().getString(Common.EXTRA_KEY);
            mTitle = getArguments().getString("title");
            mValue = getArguments().getInt("value");
            mMin = getArguments().getInt("min");
            mMax = getArguments().getInt("max");
            mUnits = getArguments().getString("units");
            mHelperFragmentName = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        } else {
            mkey = savedInstanceState.getString(Common.EXTRA_KEY);
            mTitle = savedInstanceState.getString("title");
            mValue = savedInstanceState.getInt("value");
            mMin = savedInstanceState.getInt("min");
            mMax = savedInstanceState.getInt("max");
            mUnits = savedInstanceState.getString("units");
            mHelperFragmentName = savedInstanceState.getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue = numberPicker.getValue();
        outState.putString(Common.EXTRA_KEY, mkey);
        outState.putString("title",mTitle);
        outState.putInt("value", mValue);
        outState.putInt("min", mMin);
        outState.putInt("max", mMax);
        outState.putString("units", mUnits);
        outState.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY, mHelperFragmentName);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(mCallback==null) {
            if(mHelperFragmentName.equals(Common.TAG_PREFSSCREEN_FRAGMENT)){
                GrxPreferenceScreen prefsScreen =(GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                mCallback=(DlgFrGrxNumberPicker.OnGrxNumberPickerSetListener) prefsScreen.findAndGetCallBack(mkey);

            }else mCallback=(DlgFrGrxNumberPicker.OnGrxNumberPickerSetListener) getFragmentManager().findFragmentByTag(mHelperFragmentName);
        }
    }


    private LinearLayout number_picker_view(){
        LinearLayout viewGroup = new LinearLayout(getActivity());
        viewGroup.setOrientation(LinearLayout.HORIZONTAL);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        viewGroup.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout numberLayout = new LinearLayout(getActivity());
        numberLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        numberLayout.setOrientation(LinearLayout.HORIZONTAL);
        numberLayout.setGravity(Gravity.CENTER_VERTICAL);

        numberPicker = new NumberPicker(getActivity());
        numberPicker.setLayoutParams(new NumberPicker.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        numberPicker.setMaxValue(mMax);
        numberPicker.setScaleX((float) 1.25);
        numberPicker.setScaleY((float) 1.25);
        numberPicker.setMinValue(mMin > 0 ? mMin : 0);
        numberPicker.setValue(mValue);
        numberPicker.setWrapSelectorWheel(false);
        numberLayout.addView(numberPicker);
        if ((mUnits != null) && (!mUnits.isEmpty())) {
            TextView unitTextView = new TextView(getActivity());
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setMargins(50, 0, 0, 0);
            unitTextView.setLayoutParams(ll);
            unitTextView.setPadding(20,0,0,0);
            unitTextView.setTextSize(16);
            unitTextView.setText(mUnits);
            numberLayout.addView(unitTextView);
        }


        viewGroup.addView(numberLayout);
        return viewGroup;
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        AlertDialog dlg = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(number_picker_view())
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCallback!=null) {
                            mValue = numberPicker.getValue();
                            mCallback.onGrxNumberPickerSet(mValue,mkey);
                        }
                    }
                })
                .create();
        return dlg;
    }

}
