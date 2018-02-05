
/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */



package android.preference;

import android.content.Context;
import android.util.AttributeSet;

import com.grx.settings.prefssupport.PrefAttrsInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class GrxFilePreference extends GrxSwitchPreference {

    private File file;

    public GrxFilePreference(Context context) {
        super(context);
    }


    public GrxFilePreference(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public GrxFilePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        file = new File(getContext().getFilesDir()+File.separator+getKey());

        int real = myPrefAttrsInfo.getMyBooleanDefValue() ? 1 : 0;
        if(myPrefAttrsInfo.getMySystemPrefType()== PrefAttrsInfo.SETTINGS_PREF_TYPE.SHARED){
            if (restorePersistedValue) {
                setChecked(getPersistedBoolean(myPrefAttrsInfo.getMyBooleanDefValue()));
            } else {
                setChecked(myPrefAttrsInfo.getMyBooleanDefValue());
                if(!myPrefAttrsInfo.isValidKey()) return;;
                persistBoolean(isChecked());
            }
            saveValueInSettings(isChecked());
        }
    }


    private void makeNewFile(){
        try {
            file.createNewFile();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 16 * 1024);
            bufferedOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void saveValueInSettings(boolean checked){
        if(!myPrefAttrsInfo.isValidKey()) return;

        try{
        if(myPrefAttrsInfo.isValidKey()){
            if(checked) {
                if(!file.exists()) makeNewFile();
            }else file.delete();
        }

    }catch (Exception e){
        e.printStackTrace();
    }

}


    @Override
    public void updateFromSettingsValue(){

    }

}
