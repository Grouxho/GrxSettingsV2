
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.app_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grx.settings.utils.Common;
import com.grx.settings.R;
import com.grx.settings.adapters.AdapterBackups;

import java.io.File;
import java.io.FileFilter;

import static com.grx.settings.app_fragments.DlgFrRestore.RESTORE_STATE.ASK_BACKUP;
import static com.grx.settings.app_fragments.DlgFrRestore.RESTORE_STATE.CONFIRM_BACKUP_FILE;

public class DlgFrRestore extends DialogFragment {





    DlgFrRestore.OnDlgFrRestoreActionListener mCallBack = null;
    String mTitle = "";
    String mText = "";
    RESTORE_STATE mRestoreState = ASK_BACKUP;

    private boolean isError=false;
    private String errorString = "";

    String mReturnedString="";

    boolean isUserdismissed = false;



    public enum RESTORE_STATE {
        ASK_BACKUP, CONFIRM_BACKUP_FILE, RESTORING_BACKUP,ERROR;

        public static RESTORE_STATE indexOf(int index) {
            switch (index) {
                case 0:
                    return ASK_BACKUP;
                case 1:
                    return CONFIRM_BACKUP_FILE;
                case 2:
                    return RESTORING_BACKUP;
                case 3:
                    return ERROR;
            }
            return ASK_BACKUP;
        }
    }

    public DlgFrRestore() {
    }

    public interface OnDlgFrRestoreActionListener {
        void processDlgFrRestoreAction(boolean ok, RESTORE_STATE next_action, String info);
    }

    public static DlgFrRestore newInstance(DlgFrRestore.OnDlgFrRestoreActionListener callback, String title, String message, RESTORE_STATE state) {
        DlgFrRestore ret = new DlgFrRestore();

        ret.setText(message);
        ret.setTitle(title);
        ret.setCallBack(callback);
        ret.setState(state);

        return ret;
    }

    public void setState(RESTORE_STATE state) {
        mRestoreState = state;
    }

    public void setCallBack(DlgFrRestore.OnDlgFrRestoreActionListener callback) {
        mCallBack = callback;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if(isError) report_error(errorString);
        if(!isUserdismissed) return;;
        if(mCallBack!=null)
             mCallBack.processDlgFrRestoreAction(true,mRestoreState,mReturnedString);


    }



    @Override
    public Dialog onCreateDialog(Bundle state) {
        super.onCreateDialog(state);
        isUserdismissed=false;
        isError=false;
        mReturnedString="";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
       switch (mRestoreState) {
            case ASK_BACKUP:
                View view = getBackupsListView();
                builder.setMessage(mText);
                if(view!=null) builder.setView(view);
                builder.setView(getBackupsListView());
                builder.setCancelable(true);
                break;
            case CONFIRM_BACKUP_FILE:
                builder.setCancelable(true);
                builder.setMessage(getString(R.string.grxs_restore_confirm_message, mText+"."+getString(R.string.grxs_backups_files_extension)));
                builder.setPositiveButton(getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isUserdismissed=true;
                        isError=false;
                        mRestoreState=RESTORE_STATE.RESTORING_BACKUP;
                        mReturnedString = mText;
                        dismiss();
                    }
                });
                break;
           case RESTORING_BACKUP:
               builder.setCancelable(false);
               builder.setMessage(mText);
               ProgressBar progressBar = new ProgressBar(getActivity());
               progressBar.setIndeterminate(true);
               progressBar.setVisibility(View.VISIBLE);
               break;
            default:
                isError=true;
                break;

        }
        if(isError) {
            dismiss();
        }
        return (builder.create());
    }


    private void report_error(String error){
        if(mCallBack!=null) mCallBack.processDlgFrRestoreAction(false,RESTORE_STATE.ERROR,error);
    }



    private View getBackupsListView(){

        ListView lv = new ListView(getActivity());
        File ficheros = new File(Common.BackupsDir+File.separator);
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String ruta;
                if(pathname.isFile()){
                    ruta = pathname.getAbsolutePath().toLowerCase();
                    if(ruta.contains("."+getString(R.string.grxs_backups_files_extension) )){
                        return true;
                    }
                }
                return false;
            }
        };
        File fa[]=ficheros.listFiles(ff);
        if(fa==null || fa.length==0) {
            isError=true;
            errorString = getString(R.string.grxs_no_backups);
            return null;
        }
        else {

            AdapterBackups ab = new AdapterBackups();
            ab.AdapterBackups(getActivity(), fa);
            ListView lista = new ListView(getActivity());
            lista.setAdapter(ab);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    isUserdismissed=true;
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    String file_name = tv.getText().toString();
                    File f = new File(Common.BackupsDir + File.separator + file_name + "." + getString(R.string.grxs_backups_files_extension));
                    if (f.exists()) {
                        mRestoreState = CONFIRM_BACKUP_FILE;
                        mReturnedString=file_name;
                    } else {
                        isError = true;
                        errorString = getString(R.string.grxs_restore_unknown_error);
                    }
                    dismiss();
                }
            });

            return lista;

        }
    }

}