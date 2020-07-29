package com.grx.settings.prefssupport;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.grx.settings.R;
import com.grx.settings.utils.Common;


import java.util.HashMap;
import java.util.Map;

public class GroupedValueInfo {

    String mGroupedValueKey;
    String mGroupedValueSystemType = null;
    String mBroadCastAction=null;
    boolean mExistButtons = false;

    String mValuesSeparator;
    String mKeyValueSeparator;

    Map<String, String> mKeysAlias = new HashMap<>();
    Map<String, String> mKeysValues = new HashMap<>();
    Map<String, PrefAttrsInfo.PREF_TYPE> mPrefTypes = new HashMap<>();
    Map<String, Object> mPrefDefVals = new HashMap<>();

    boolean mStillDebugging = false;

    String mGroupedValue="";

    Context mContext;

    boolean mSaveKeyNames = true;


    public GroupedValueInfo(String groupedKey, Context context){
        Resources resources = context.getResources();
        mValuesSeparator = resources.getString(R.string.grxs_groupedValuekey_valuesSeparator);
        mKeyValueSeparator = resources.getString(R.string.grxs_groupedValueKey_keyvalueSeparator);
        mSaveKeyNames = resources.getBoolean(R.bool.grxb_groupedValuekey_savekeysnames);
        mGroupedValueKey=groupedKey;
        mContext = context;
    }


    public void addPreferenceConfiguration(String prefkey, Object defval, PrefAttrsInfo.PREF_TYPE preftype, String alias, String systemtype, String broadcastaction){
        setBroadCastAction(broadcastaction);
        setGroupedValueSystemType(systemtype);
        addKeyAlias(prefkey, alias);
        if(preftype!= PrefAttrsInfo.PREF_TYPE.BUTTON ){
            mPrefTypes.put(prefkey, preftype);
            mPrefDefVals.put(prefkey,defval);
        }else {
            mExistButtons=true;
        }

        if (updateKeyValueInMap(prefkey) ) calculateGroupedValue(); ;
    }

    public boolean updateKeyValueInMap(String prefKey) {
        // if(mKeysValues.get(prefKey) == null ) return false;
        if (mPrefDefVals.get(prefKey) == null) return false;
        if( mPrefTypes.get(prefKey) == null ) return false;

        switch (mPrefTypes.get(prefKey)) {
            case BOOL:
                boolean defvalue;
                if ( mPrefDefVals.get(prefKey) == null ) defvalue = false;
                else defvalue = (boolean) mPrefDefVals.get(prefKey);
                boolean currval = Common.sp.getBoolean(prefKey,defvalue);
                mKeysValues.put(prefKey, currval ? "1" : "0"  ) ;
                return true;

            case INT:
                int defval ;
                if(mPrefDefVals.get(prefKey) == null) defval = 0;
                else defval = (int) mPrefDefVals.get(prefKey);
                int currvali = Common.sp.getInt(prefKey,defval);
                mKeysValues.put(prefKey,String.valueOf(currvali));
                return true;

            case STRING:
                String defvals ;
                if(mPrefDefVals.get(prefKey) == null ) defvals="";
                else defvals = (String) mPrefDefVals.get(prefKey);
                String currvals = Common.sp.getString(prefKey,defvals);
                if(currvals ==null ) currvals = "";
                mKeysValues.put(prefKey, currvals);
                return true;
            default: break;
        }

        return  false;
    }



    private void setGroupedValueSystemType(String systemType){
        if ( (mGroupedValueSystemType==null && !TextUtils.isEmpty(systemType) ) )
                    mGroupedValueSystemType = systemType;
    }

    private void setBroadCastAction(String bc){
        if(mBroadCastAction==null && !TextUtils.isEmpty(bc))
                    mBroadCastAction = bc;
    }

    public void setExistButtons() {
        mExistButtons = true;
    }

    private void addKeyAlias(String key, String alias){
        if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(key) ) return;
        mKeysAlias.put(key, alias);
    }


    public void updatePreferenceValue(String prefkey) {
        if (updateKeyValueInMap( prefkey) ) calculateGroupedValue(); ;
        if(!mExistButtons) {
            notifyGroupedValueChanged();
        }
    }

    public void notifyGroupedValueChanged() {
        boolean error = false;

        if(TextUtils.isEmpty(mGroupedValueSystemType)  || mContext.getResources().getBoolean(R.bool.grxb_demo_mode)) return;
        if(!mContext.getResources().getBoolean(R.bool.grxb_global_enable_settingsdb)) return;

        switch (mGroupedValueSystemType){
            case "secure":
                try{
                    Settings.Secure.putString(mContext.getContentResolver(), mGroupedValueKey, mGroupedValue);
                }catch (Exception e){
                    error = true;
                    Log.d("grxgrx ", e.toString());
                }
                break;
            case "global" :
                try{
                    Settings.Global.putString(mContext.getContentResolver(), mGroupedValueKey, mGroupedValue);
                }catch (Exception e){
                    error = true;
                    Log.d("grxgrx ", e.toString());
                }
                break;
            case "system":
                try{
                    Settings.System.putString(mContext.getContentResolver(), mGroupedValueKey, mGroupedValue);
                }catch (Exception e){
                    error = true;
                    Log.d("grxgrx ", e.toString());
                }
                break;
            default:
                break;
        }

        if(error) return;
        if(TextUtils.isEmpty(mBroadCastAction)) return;
        Intent intent = new Intent();
        intent.setAction(mBroadCastAction);
        intent.putExtra(mGroupedValueKey,mGroupedValue);
        mContext.sendBroadcast(intent);
    }

    private void calculateGroupedValue(){
        mGroupedValue = "";
        for(String key : mKeysValues.keySet()) {
            if(mSaveKeyNames) {
                String alias = mKeysAlias.get(key);
                if(TextUtils.isEmpty(alias)) alias = key;
                mGroupedValue+=alias;
                mGroupedValue+=mKeyValueSeparator;
            }
            String value = mKeysValues.get(key);
            if(value==null) value="";
            mGroupedValue+=value;
            mGroupedValue+=mValuesSeparator;
        }
        Common.sp.edit().putString(mGroupedValueKey,mGroupedValue).commit();
        if(mStillDebugging) Log.d("grxgrx grouped value = ", mGroupedValueKey+"="+mGroupedValue);
    }


    public void onGroupedValueButtonPressed(){
        calculateGroupedValue();
        notifyGroupedValueChanged();
    }

    public String getGroupedValueSystemType() {
        return mGroupedValueSystemType;
    }


    public String getGroupedValue(){
        return mGroupedValue;
    }

    public void recalculateGroupedValueForSync(){
        calculateGroupedValue();
        notifyGroupedValueChanged();
    }
}
