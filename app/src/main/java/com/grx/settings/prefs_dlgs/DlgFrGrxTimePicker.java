
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.prefs_dlgs;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.util.Log;
import android.widget.TimePicker;

import com.grx.settings.R;
import com.grx.settings.utils.Common;



public class DlgFrGrxTimePicker extends DialogFragment {


    private String mkey;
    private int mValue;

    private DlgFrGrxTimePicker.OnGrxTimeSetListener mCallback;

    public DlgFrGrxTimePicker(){}


    public static DlgFrGrxTimePicker newInstance(String key, int value){

        DlgFrGrxTimePicker ret = new DlgFrGrxTimePicker();
        Bundle bundle = new Bundle();
        bundle.putString("key",key);
        bundle.putInt("val",value);
        ret.setArguments(bundle);
        return ret;
    }

    public interface OnGrxTimeSetListener{
        void onGrxTimeSet(int value, String key);
    }

    public void setOnGrxTimeSetListener(DlgFrGrxTimePicker.OnGrxTimeSetListener callback){
        mCallback=callback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(mCallback==null) mCallback=(DlgFrGrxTimePicker.OnGrxTimeSetListener) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

        mkey=getArguments().getString("key");
        mValue=getArguments().getInt("val",0);

        int hours = (mValue/60);
        int minutes = mValue - (hours*60);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(),R.style.GrxDialogStyle, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(mCallback!=null) mCallback.onGrxTimeSet((hourOfDay*60)+minute,mkey);
                else   Log.d("grxsettings", "null callback in timepicker");
            }
        },hours,minutes,true);

        /*if(getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            tpd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            View view = getActivity().getLayoutInflater().inflate(R.layout.lay_view_titulo, null);
            TextView tit = (TextView) view.findViewById(R.id.txt_titulo);
            tit.setText("Prueba");
            tpd.setCustomTitle(view);
        }*/

        return tpd;
    }




}
