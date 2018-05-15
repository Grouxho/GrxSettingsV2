

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
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;
import com.root.RootUtils;
import com.smcolorpicker.ColorPickerPanelView;
import com.smcolorpicker.ColorPickerPreference;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.grx.settings.GrxSettingsApp.getContext;


public class DlgFrAppLedPulse extends DialogFragment implements  DlgGrxAppSelection.OnGrxAppListener {

    AppLedPulseListener mCallBack;
    private String mTitle;
    private String mKey;
    private String mHelperFragmentName;
    private boolean mShowTest;
    private boolean mShowApp;
    private boolean mShowAllApps;
    private boolean mShowPulse;

    private String mOriValue;

    private com.smcolorpicker.ColorPickerView mSMColorPicker;

    private ColorPickerPanelView mSMOldColorView;
    private ColorPickerPanelView mSMNewColorView;
    private EditText mSMHexValView;
    private ColorStateList mSMHexDefaultTextColor;

    private NotificationManager mNotificationManager;

    private int mCurrentColor=0xffffffff;
    private int mOriginalColor=0xffffffff;

    private Spinner mSpinnerOn, mSpinnerOff;
    private ArrayAdapter<String> mSpinnerOnAdapter, mSpinnerOffAdapter;

    private ImageView mAppIconView;
    private TextView mAppLabelView;
    private TextView mAppPackageNameView;

    private String mCurrentValue;
    private String mPackageName;
    private int mLedOnTime;
    private int mLedoffTime;

    private Handler mHanler=new Handler();
    private Notification mNotification;

    BroadcastReceiver mScreenOnReceiver, mScreenOffReceiver;



    public interface AppLedPulseListener{
        void onAppLedPulseSet(String value);

    }

    public DlgFrAppLedPulse(){}

    public static DlgFrAppLedPulse newInstance(AppLedPulseListener callback, String help_frg, String key, String tit, boolean show_test, boolean show_app, boolean showallapps, boolean show_pulse, String value){

        DlgFrAppLedPulse ret = new DlgFrAppLedPulse();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,help_frg);
        bundle.putString("key",key);
        bundle.putString("tit",tit);
        bundle.putBoolean("show_test",show_test);
        bundle.putBoolean("show_app",show_app);
        bundle.putBoolean("showall",showallapps);
        bundle.putBoolean("show_pulse",show_pulse);
        bundle.putString("value",value);
        ret.setArguments(bundle);
        ret.setCallBack(callback);
        return ret;
    }

    private void setCallBack(AppLedPulseListener callback){
        mCallBack=callback;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        DlgFrGrxAccess dlgFrGrxAccess = (DlgFrGrxAccess) getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRACCESS);
        if(dlgFrGrxAccess!=null){
            getFragmentManager().beginTransaction().show(dlgFrGrxAccess);
        }
    }

    private void check_callback(){

        if(mCallBack==null) {
            if (mHelperFragmentName.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrAppLedPulse.AppLedPulseListener) prefsScreen.findAndGetCallBack(mKey);
            }else {
                mCallBack=(DlgFrAppLedPulse.AppLedPulseListener) getFragmentManager().findFragmentByTag(mHelperFragmentName);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", mKey);
        outState.putString("tit",mTitle);
        outState.putBoolean("show_test",mShowTest);
        outState.putBoolean("show_app",mShowApp);
        outState.putBoolean("showall",mShowAllApps);
        outState.putBoolean("show_pulse",mShowPulse);
        outState.putString("value",mOriValue);
        mCurrentValue=getReturnValue();
        outState.putString("curr_val",getReturnValue());

    }


    @Override
    public Dialog onCreateDialog(Bundle state) {

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mScreenOnReceiver = new ScreenOnListener();
        getActivity().registerReceiver(mScreenOnReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));
        mScreenOffReceiver = new ScreenOffListener();
        getActivity().registerReceiver(mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        mHelperFragmentName = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey = getArguments().getString("key");
        mTitle = getArguments().getString("tit");
        mShowTest = getArguments().getBoolean("show_test");
        mShowApp=getArguments().getBoolean("show_app");
        mShowAllApps=getArguments().getBoolean("showall");
        mShowPulse=getArguments().getBoolean("show_pulse");
        mOriValue=getArguments().getString("value");

        if(mOriValue==null) mOriValue="";

        if(state!=null){
            mCurrentValue=state.getString("curr_val");

        }

        if(mCurrentValue==null || mCurrentValue.isEmpty()) mCurrentValue=mOriValue;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setView(getDialogView())
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCurrentValue=getReturnValue();
                        check_callback();
                        if(mCallBack!=null) {
                            mCallBack.onAppLedPulseSet(mCurrentValue);
                        }
                        mNotificationManager.cancel(1);
                        dismiss();

                    }
                });
        builder.setNegativeButton(getString(R.string.grxs_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNotificationManager.cancel(1);
                dismiss();

            }
        });
        if(mShowTest) builder.setNeutralButton("Test",null);
        final AlertDialog ad = builder.create();
        if(mShowTest){
            ad.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = ad.getButton(DialogInterface.BUTTON_NEUTRAL);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            turnScreenOff();
                            updateLedNotification();
                            mNotificationManager.notify(1,mNotification);
                        }
                    });
                }
            });


        }

        if(mShowPulse) initSpinnersFromValue(mCurrentValue);

        updateInfo();

        return ad;
    }


    public View getDialogView() {
        View mLayout = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxappledpulse, null);



        /**** Color Picker ****/

        mSMColorPicker = (com.smcolorpicker.ColorPickerView) mLayout.findViewById(R.id.color_picker_view);
        mSMColorPicker.setAlphaSliderVisible(false);

        mSMOldColorView = (ColorPickerPanelView) mLayout.findViewById(R.id.old_color_panel);
        mSMNewColorView = (ColorPickerPanelView) mLayout.findViewById(R.id.new_color_panel);

        LinearLayout color_container = (LinearLayout) mLayout.findViewById(R.id.gid_color_container);


        mSMHexValView = (EditText) mLayout.findViewById(R.id.hex_val);
        mSMHexValView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mSMHexValView.setVisibility(View.VISIBLE);
        mSMHexValView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});

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

        mOriginalColor=getIntColorFromValue(mOriValue);
        mCurrentColor=getIntColorFromValue(mCurrentValue);
        mSMOldColorView.setColor(mOriginalColor);
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

            }
        });

        mSMColorPicker.setColor(mCurrentColor, true);
        updateSMHexValue(getSMColor());


        /*** Pulse Spinners **/


        if(mShowPulse){
            mSpinnerOn=(Spinner) mLayout.findViewById(R.id.gid_on_spinner);
            mSpinnerOff=(Spinner) mLayout.findViewById(R.id.gid_off_spinner);

            mSpinnerOnAdapter = new ArrayAdapter<String>(getActivity(),R.layout.dlg_grxapppledpulse_spinner,getActivity().getResources().getStringArray(R.array.grxa_ledpulse_ton_options));
            mSpinnerOn.setAdapter(mSpinnerOnAdapter);
            mSpinnerOn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mSpinnerOff.setEnabled(i==0 ? false : true);
                    if(i==0) mLedoffTime=0;
                    mLedOnTime=Integer.valueOf(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_ton_values)[i]);
                    updateLedNotification();
                    mNotificationManager.notify(1,mNotification); //*****
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mSpinnerOffAdapter = new ArrayAdapter<String>(getActivity(),R.layout.dlg_grxapppledpulse_spinner,getActivity().getResources().getStringArray(R.array.grxa_ledpulse_toff_options));
            mSpinnerOff.setAdapter(mSpinnerOffAdapter);
            mSpinnerOff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mLedoffTime=Integer.valueOf(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_toff_values)[i]);
                    updateLedNotification();
                    mNotificationManager.notify(1,mNotification); //*****
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else{
            LinearLayout linearLayout = (LinearLayout) mLayout.findViewById(R.id.gid_pulses_container);
            linearLayout.setVisibility(View.GONE);
        }


        /********* app selection ***/

        LinearLayout appcontainer = (LinearLayout) mLayout.findViewById(R.id.gid_app_container);
        if(mShowApp) {

            mAppIconView = (ImageView) mLayout.findViewById(R.id.gid_app_icon);
            mAppLabelView = (TextView) mLayout.findViewById(R.id.gid_app_label);
            mAppPackageNameView = (TextView) mLayout.findViewById(R.id.gid_package_name);
            appcontainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAppSelectionDialog();
                }
            });

        }else {
            appcontainer.setVisibility(View.GONE);

        }


        return mLayout;
    }

    private void showAppSelectionDialog(){
        DlgGrxAppSelection dlg = (DlgGrxAppSelection) getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRSELECTAPP);
        if(dlg==null){
            dlg = DlgGrxAppSelection.newInstance(this, Common.TAG_DLGFRAPPLEDPULSE, mKey,getString(R.string.grxs_select_app),true, false, true);
            dlg.show(getFragmentManager(),Common.TAG_DLGFRGRSELECTAPP);
        }
    }

    private void setSMColor(int color, boolean updatetext, boolean callback){

        updateLedNotification();
        mNotificationManager.notify(1,mNotification);

        mCurrentColor=color;

        mSMNewColorView.setColor(mCurrentColor);
        if(updatetext) updateSMHexValue(mCurrentColor);
    }

    private void updateSMHexValue(int color) {
        mSMHexValView.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()));
        mSMHexValView.setTextColor(mSMHexDefaultTextColor);
    }
    public int getSMColor() {
        return mSMColorPicker.getColor();
    }


    private void updateInfo(){
        if(mShowApp){
            mPackageName = getPackageNameFromValue(mCurrentValue);
            if( GrxPrefsUtils.isAppInstalled(getActivity(),mPackageName)){
                String label = GrxPrefsUtils.getApplicationLabel(getActivity(),mPackageName);
                if(label==null || label.isEmpty()) {
                    mAppLabelView.setText( getString( R.string.grxs_is_not_installed,mPackageName ));
                    mAppLabelView.setVisibility(View.VISIBLE);
                    mAppIconView.setVisibility(View.GONE);
                    mAppPackageNameView.setText(mPackageName);
                }else {
                    mAppLabelView.setText(label);
                    mAppLabelView.setVisibility(View.VISIBLE);
                    mAppPackageNameView.setText(mPackageName);
                    mAppIconView.setVisibility(View.VISIBLE);
                    mAppIconView.setImageDrawable(GrxPrefsUtils.getApplicationIcon(getActivity(),mPackageName));
                }
            }else {
                mAppIconView.setVisibility(View.GONE);
                mAppPackageNameView.setText( getString( R.string.grxs_pulse_app_null));
                mAppLabelView.setVisibility(View.GONE);
            }
        }

        if(mShowPulse){

        }

    }





    @Override
    public void onDismiss(DialogInterface dialog) {
        mNotificationManager.cancel(1);
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy(){
        mNotificationManager.cancel(1);
        if(mScreenOffReceiver!=null) getActivity().unregisterReceiver(mScreenOffReceiver);
        if(mScreenOnReceiver!=null) getActivity().unregisterReceiver(mScreenOnReceiver);
        super.onDestroy();
    }


    private void updateLedNotification(){

        final int color = mSMColorPicker.getColor();


        final Bundle extras = new Bundle();
        extras.putBoolean("GRXTESTLED", true);

        final Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setLights(getSMColor(), mLedOnTime, mLedoffTime);

        builder.setExtras(extras);

        // Set a notification
        builder.setSmallIcon(R.drawable.ic_settings_leds);
        builder.setContentTitle("Test Led");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText("Testing Led Colors.= " + mSMHexValView.getText());
        //builder.setContentText("Testing Led Colors.= ." + GrxPrefsUtils.getHexString(color,true));
        builder.setOngoing(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setVibrate(new long[] {0, 0, 0,0 }); // if not set, ... cannot be tested on samsung devices ???

        mNotification = builder.build();

        mNotification.flags = Notification.FLAG_SHOW_LIGHTS;

    }



    private void turnScreenOff(){
        try{

            Class c = Class.forName("android.os.PowerManager");
            PowerManager  mPowerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);

            for(Method m : c.getDeclaredMethods()){
                if(m.getName().equals("goToSleep")){
                    m.setAccessible(true);
                    if(m.getParameterTypes().length == 1){
                        m.invoke(mPowerManager, SystemClock.uptimeMillis()-2);
                    }
                }
            }
        } catch (Exception e){

            if(RootUtils.runCommand("input keyevent 26")==null) Toast.makeText(getContext(),getString(R.string.grxs_pulse_no_screenoff),Toast.LENGTH_SHORT).show();
            String error = e.toString();
            Log.e("GrxSettings", "turnScreenOff: "+e.toString());
        }
    }

    /** get individual values from composed String : packagename;color;pulseon;pulseoff **/

    private String getPackageNameFromValue(String value){
        if(value==null || value.isEmpty()) return null;
        String[] array = value.split(Pattern.quote(";"));
        if(array==null) return null;
        return array[0];
    }

    private int getIntColorFromValue(String value){
        if(value==null || value.isEmpty()) return 0xffffffff;
        String[] array = value.split(Pattern.quote(";"));
        if(array==null) return 0xffffffff;
        String color;
        if(mShowApp) color=array[1];
        else{
            if(mShowPulse) color=array[0];
            else color=value;
        }
        return Color.parseColor(color);
    }


    private void initSpinnersFromValue(String value){
        String ton = getStringPulseOnFromValue(value);
        int poson = getPositionInArray(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_ton_values),ton,3);
        mSpinnerOn.setSelection(poson);
        mLedOnTime=Integer.valueOf(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_ton_values)[poson]);

        String toff = getStringPulseOffFromValue(value);
        int posoff = getPositionInArray(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_toff_values),toff,2);
        mSpinnerOff.setSelection(posoff);
        mLedoffTime=Integer.valueOf(getActivity().getResources().getStringArray(R.array.grxa_ledpulse_toff_values)[posoff]);
    }

    private String getStringPulseOnFromValue(String value){
        if(value==null || value.isEmpty()) return null;
        int i = (mShowApp ? 2 : 1);
        String[] array = value.split(Pattern.quote(";"));
        if(array==null) return null;
        if(array.length<(i+1)) return null;
        return array[i];

    }

    private String getStringPulseOffFromValue(String value){
        if(value==null || value.isEmpty()) return null;
        int i = (mShowApp ? 3 : 2);
        String[] array = value.split(Pattern.quote(";"));
        if(array==null) return null;
        if(array.length<(i+1)) return null;
        return array[i];
    }

    private int getPositionInArray(String[] array, String value, int defpos){
        if(value==null || value.isEmpty()) return defpos;
        int i=0;
        for(i = 0; i<array.length;i++){
            if(array[i].equals(value)) break;
        }
        return i;
    }

    private String getReturnValue(){


        String returnvalue="";
        if(mShowApp){
            if(mPackageName==null ) mPackageName="";
            returnvalue+=mPackageName;
            returnvalue+=";";
        }

        returnvalue+=mSMHexValView.getText().toString();
        if(mShowPulse) {
            returnvalue+=";";
            String ton = getActivity().getResources().getStringArray(R.array.grxa_ledpulse_ton_values)[mSpinnerOn.getSelectedItemPosition()];
            String toff = getActivity().getResources().getStringArray(R.array.grxa_ledpulse_toff_values)[mSpinnerOff.getSelectedItemPosition()];
            returnvalue += ton + ";" + toff;
        }
        return returnvalue;
    }


    @Override
    public void onGrxAppSel(DlgGrxAppSelection dialog, String packagename){
        mPackageName=packagename;
        mCurrentValue=getReturnValue();
        updateInfo();
    }


    private class ScreenOffListener extends BroadcastReceiver
    {
        public ScreenOffListener() {}

        public void onReceive(Context paramContext, Intent paramIntent)
        {
            //     updateLedNotification();
            //    mNotificationManager.notify(1, mNotification);
       /*     Notification notif = new Notification();
            notif.ledARGB = 0xFFFF0000; // Red
            notif.flags = Notification.FLAG_SHOW_LIGHTS;
            notif.ledOnMS = 200;
            notif.ledOffMS = 200;
            mNotificationManager.notify(1, notif);*/
        }
    }

    private class ScreenOnListener extends BroadcastReceiver
    {
        public ScreenOnListener() {}

        public void onReceive(Context paramContext, Intent paramIntent)
        {
            if(mShowTest) mNotificationManager.cancel(1);
        }
    }



}
