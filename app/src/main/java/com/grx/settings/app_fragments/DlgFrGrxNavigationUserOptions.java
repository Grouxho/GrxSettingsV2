
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.app_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.AlertDialog;

import com.grx.settings.GrxSettingsActivity;
import com.grx.settings.R;
import com.grx.settings.utils.Common;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static android.content.Context.MODE_PRIVATE;


public class DlgFrGrxNavigationUserOptions extends DialogFragment{

    private DlgFrGrxNavigationUserOptionsCallBack mCallback;
    private int mTdialog;

    public interface DlgFrGrxNavigationUserOptionsCallBack {
        void onNavigationUserOptionSet(int tdialog, int opt);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (GrxSettingsActivity) getActivity();
    }




    public static DlgFrGrxNavigationUserOptions newInstance(int t_dialog){

        DlgFrGrxNavigationUserOptions ret = new DlgFrGrxNavigationUserOptions();
        Bundle save = new Bundle();
        save.putInt(Common.S_DLG_T_KEY, t_dialog);
        ret.setArguments(save);
        ret.ini_dlgFragment(t_dialog);
        return ret;
    }

    private void ini_dlgFragment(int tdialog){
        mTdialog=tdialog;
    }


    private int sp_val(String key, int defv){
        int ret = defv;
        try{
            SharedPreferences sp = getActivity().createPackageContext(getActivity().getPackageName(),CONTEXT_IGNORE_SECURITY).getSharedPreferences(getActivity().getPackageName()+"_preferences",MODE_PRIVATE);
            ret=sp.getInt(key, defv);

        }catch (PackageManager.NameNotFoundException e){

        }
        return ret;
    }

    private String s_sp_val(String key, String defv){
        String ret = defv;
        try{
            SharedPreferences sp = getActivity().createPackageContext(getActivity().getPackageName(),CONTEXT_IGNORE_SECURITY).getSharedPreferences(getActivity().getPackageName()+"_preferences",MODE_PRIVATE);
            ret=sp.getString(key, defv);

        }catch (PackageManager.NameNotFoundException e){

        }
        return ret;
    }

    private Dialog dlg_fabPosition(){

        final int fab_pos = sp_val(Common.S_APPOPT_FAB_POS, 0);

        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_fab_position)
                .setSingleChoiceItems(R.array.grxa_fab_position, fab_pos,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null && sel!=fab_pos ){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();

        return adb;
    }


    private Dialog dlg_exitConfirmation(){
        AlertDialog adb = new AlertDialog.Builder(getActivity())
            .setTitle(R.string.grxs_exit_title)
            .setMessage(R.string.grxs_exit_message)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCallback!=null ){
                            mCallback.onNavigationUserOptionSet(mTdialog,1);
                        }
                    }
        }).create();

        return adb;
    }

    private Dialog dlg_dividerHeight(){

        final int div_height = sp_val( Common.S_APPOPT_DIV_HEIGHT, getResources().getInteger(R.integer.grxi_default_list_divider_height));

        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_divider_height)
                .setSingleChoiceItems(R.array.grxa_divider_height, div_height,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null && sel!=div_height ){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();

        return adb;
    }


    private Dialog dlg_panelHeaderBg(){

        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_nav_header_bg_title)
                .setSingleChoiceItems(R.array.grxa_panel_header_options,1,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();

        return adb;
    }


    private Dialog dlg_selectTheme(){

        String mTheme =  Common.sp.getString(Common.S_APPOPT_USER_SELECTED_THEME_NAME,getString(R.string.grxs_default_theme));

        int pos = 0;
        String[] values = getResources().getStringArray(R.array.grxa_theme_values);
        for(int i = 0; i< values.length; i++){
            if(values[i].equals(mTheme)) {
                pos = i;
                break;
            }
        }

        //final int curr_theme = sp_val(Common.S_APPOPT_USER_SELECTED_THEME, getResources().getInteger(R.integer.grxi_default_theme));
        final int curr_theme = pos;

        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_select_theme)
                .setSingleChoiceItems(R.array.grxa_theme_list, curr_theme,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null && sel!=curr_theme ){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();


        return adb;

    }


    private Dialog dlg_setColorPickerStyle(){
        String curr = s_sp_val(Common.S_APPOPT_COLORPICKERSTYLE,getResources().getString(R.string.grxs_colorPickerStyle_default));
        final int index = Common.getColorPickerStyleIndex(curr);
        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_color_picker_style)
                .setSingleChoiceItems(R.array.grxa_colorpickerstyles, index,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null && sel!=index ){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();


        return adb;


    }


    private Dialog dlg_runTool(){
        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_tools)
                .setSingleChoiceItems(R.array.grxa_threedots_tools, -1,null)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sel = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if(mCallback!=null ){
                            mCallback.onNavigationUserOptionSet(mTdialog,sel);
                        }
                    }
                }).create();


        return adb;


    }



    private Dialog dlg_resetAllPreferences(){
        AlertDialog adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.grxs_tit_reset_all_prefs)
                .setMessage(R.string.grxs_msg_reset_all_prefs)
                .setNegativeButton(R.string.grxs_cancel,null)
                .setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCallback!=null){
                            mCallback.onNavigationUserOptionSet(mTdialog,1);
                        }
                    }
                }).create();
        return adb;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTdialog=getArguments().getInt(Common.S_DLG_T_KEY);
        switch (mTdialog){
            case Common.INT_ID_APPDLG_FAV_POS: return dlg_fabPosition();
            case Common.INT_ID_APPDLG_DIV_HEIGHT: return dlg_dividerHeight();
            case Common.INT_ID_APPDLG_EXIT_CONFIRM: return dlg_exitConfirmation();
            case Common.INT_ID_APPDLG_SET_THEME: return dlg_selectTheme();
            case Common.INT_ID_APPDLG_SET_BG_PANEL_HEADER: return dlg_panelHeaderBg();
            case Common.INT_ID_APPDLG_SET_COLORPICKER_STYLE: return dlg_setColorPickerStyle();
            case Common.INT_ID_APPDLG_RESET_ALL_PREFERENCES: return dlg_resetAllPreferences();
            case Common.INT_ID_APPDLG_TOOLS: return dlg_runTool();

        }
        return null;
    }


}
