
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
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;



public class DlgGrxMultipleAppSelection extends DialogFragment implements AdapterView.OnItemClickListener {


    DlgGrxMultipleAppSelection.OnAppsSelectedListener mCallBack;

    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;
    private int lastposition=0;
    private int mMaxAllowed;
    private boolean mShowSystemApps;
    private boolean mSaveActivityname;

    public int mNumSelected =0;
    private String mCurrentValue;

    ArrayList<GrxQuickAppInfo> mAppsInfo;
    ArrayList<String> mSelectedApps;

    private ListView vList;
    private ProgressBar pb;
    private TextView vtxtprogressbar;
    AsyncTask<Void, Void, Void> loader;

    private TextView vSelectedTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public interface OnAppsSelectedListener{
        void OnAppsSelected(String apps_selected);
    }

    public DlgGrxMultipleAppSelection(){}

    public static DlgGrxMultipleAppSelection newInstance(DlgGrxMultipleAppSelection.OnAppsSelectedListener callback, String HelperFragment, String key, String title, String value,
                                                         String separtor, boolean allapps, int maxnum, boolean saveactivityname){
        DlgGrxMultipleAppSelection ret = new DlgGrxMultipleAppSelection();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putString("separator", separtor);
        bundle.putInt("max_items", maxnum);
        bundle.putBoolean("all_apps", allapps);
        bundle.putBoolean("save_actname", saveactivityname);
        ret.setArguments(bundle);
        ret.setCallBack(callback);
        return ret;
    }


    private void setCallBack(DlgGrxMultipleAppSelection.OnAppsSelectedListener callback){
        mCallBack =callback;
    }


    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if((loader!=null) && (loader.getStatus()== AsyncTask.Status.RUNNING)){
            loader.cancel(true);
            loader=null;
            if(mAppsInfo!=null) mAppsInfo.clear();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("curr_val", getReturnString());
        lastposition=vList.getFirstVisiblePosition();
        outState.putInt("lastpos", lastposition);

    }

    public String getReturnString(){  //grxgrxgrx
        String resultado="";
        for(String packagename_activity : mSelectedApps){
            resultado+=packagename_activity;
            resultado+=mSeparator;
        }
        return resultado;
    }

    private void checkCallBack(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgGrxMultipleAppSelection.OnAppsSelectedListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgGrxMultipleAppSelection.OnAppsSelectedListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }

    private void initSelectedItemsList(){
        mNumSelected = 0;
        mSelectedApps.clear();
       if(mValue!=null && !mValue.isEmpty()) {
           String[] auxarray =null;
           auxarray=mValue.split(Pattern.quote(mSeparator));
            for(int i=0;i<auxarray.length;i++) {
                    if(GrxPrefsUtils.isPackageActivityInstalled(getActivity(), auxarray[i])) mSelectedApps.add(i,auxarray[i]);  //grxgrx
            }
           mNumSelected = mSelectedApps.size();
       }
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

        mSelectedApps = new ArrayList<String>();
        mHelperFragment = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey = getArguments().getString("key");
        mTitle = getArguments().getString("tit");
        mOriValue = getArguments().getString("val");
        mValue = mOriValue;
        mSeparator = getArguments().getString("separator");
        mMaxAllowed = getArguments().getInt("max_items");
        mShowSystemApps = getArguments().getBoolean("all_apps",false);
        mSaveActivityname = getArguments().getBoolean("save_actname",getActivity().getResources().getBoolean(R.bool.grxb_saveActivityname_default));


        if (state != null) {
            mValue = state.getString("curr_val");
            lastposition = state.getInt("lastpos");
        }

        initSelectedItemsList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    mValue = getReturnString();
                    if(!mValue.equals(mOriValue)) {
                        checkCallBack();
                        if (mCallBack != null) mCallBack.OnAppsSelected(mValue);
                    }
                mAppsInfo.clear();
                mSelectedApps.clear();
                dismiss();

            }
        });
        builder.setView(getDialogView());

        updateDialgoSummary();

        return builder.create();
    }

    private void set_result_and_exit(){

    }

    private void updateDialgoSummary(){
        if(mMaxAllowed !=0)
            vSelectedTxt.setText(getString(R.string.grxs_num_selected,mSelectedApps.size())+" ( " + String.valueOf(mMaxAllowed)+" max )");
        else
            vSelectedTxt.setText(getString(R.string.grxs_num_selected,mSelectedApps.size()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       boolean checked = mAppsInfo.get(position).isSelected();
        if(checked){
            checked = false;
            mSelectedApps.remove(mAppsInfo.get(position).getValue());
        }else {
            if(mMaxAllowed !=0 && mSelectedApps.size()>= mMaxAllowed) return;
            checked=true;
            mSelectedApps.add(mAppsInfo.get(position).getValue()); //grxgrx
        }
        mNumSelected=mSelectedApps.size();
        mAppsInfo.get(position).setIsChecked(checked);
        CheckBox checkBox= (CheckBox) view.findViewById(R.id.gid_checkbox);
        checkBox.setChecked(checked);
        updateDialgoSummary();
    }

    public View getDialogView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxmultipleappselection,null);
        vList = (ListView) view.findViewById(R.id.gid_listview);
        vList.setFastScrollEnabled(true);
        vList.setScrollingCacheEnabled(false);
        vList.setAnimationCacheEnabled(false);
        vSelectedTxt = (TextView) view.findViewById(R.id.gid_items_selected);

        if(mMaxAllowed ==0) vSelectedTxt.setVisibility(View.GONE);

        vList.setDividerHeight(Common.cDividerHeight);

        vList.setOnItemClickListener(this);
        vtxtprogressbar =(TextView) view.findViewById(R.id.gid_progressbar_txt);
        vtxtprogressbar.setVisibility(View.VISIBLE);
        vtxtprogressbar.setText(getString(R.string.grxs_building_sorting_list));
        pb = (ProgressBar) view.findViewById(R.id.gid_progressbar);
        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb.setVisibility(View.VISIBLE);
                pb.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                mAppsInfo = buildAppsList();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pb.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.GONE);
                vList.setAdapter(new Qadapter(mAppsInfo));
                vList.setSelection(lastposition);
                vSelectedTxt.setVisibility(View.VISIBLE);
            }
        }.execute();

        vList.setFadingEdgeLength(2);
        vList.setDividerHeight(Common.cDividerHeight);
        return view;

    }



    private ArrayList<GrxQuickAppInfo> buildAppsList(){
        List<ApplicationInfo> AppsTmp;
        ApplicationInfo applicationInfo;

        ArrayList<GrxQuickAppInfo> mQuickInfoTmp = new ArrayList<GrxQuickAppInfo>();

        AppsTmp = getActivity().getPackageManager().getInstalledApplications(0);
        for(int ind=0;ind<AppsTmp.size();ind++) {
            applicationInfo=AppsTmp.get(ind);
            try {
                if(mShowSystemApps) {

                    mQuickInfoTmp.add(new GrxQuickAppInfo(
                            applicationInfo.packageName,
                            (mSaveActivityname) ? GrxPrefsUtils.getMainActivityFromPackageName(getActivity(), applicationInfo.packageName) : null,
                            applicationInfo.loadLabel(getActivity().getPackageManager()).toString(),
                            applicationInfo.loadIcon(getActivity().getPackageManager()))
                    );


                }
                else {
                    if (getActivity().getPackageManager().getLaunchIntentForPackage(AppsTmp.get(ind).packageName) != null) {
                        mQuickInfoTmp.add(new GrxQuickAppInfo(
                                applicationInfo.packageName,
                                (mSaveActivityname) ? GrxPrefsUtils.getMainActivityFromPackageName(getActivity(), applicationInfo.packageName) : null,
                                applicationInfo.loadLabel(getActivity().getPackageManager()).toString(),
                                applicationInfo.loadIcon(getActivity().getPackageManager()))
                        );

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try{
            Collections.sort(mQuickInfoTmp, new Comparator<GrxQuickAppInfo>() {
                @Override
                public int compare(GrxQuickAppInfo A_appinfo, GrxQuickAppInfo appinfo) {
                    try{
                        return String.CASE_INSENSITIVE_ORDER.compare(A_appinfo.getLabel(), appinfo.getLabel());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        return mQuickInfoTmp;

    }


    private class Qadapter extends BaseAdapter implements SectionIndexer {

        List<GrxQuickAppInfo> mList;
        HashMap<String, Integer> mapIndex;
        String[] sections;


        Qadapter(List<GrxQuickAppInfo> qlist) {
            this.mList = qlist;

            mapIndex = new LinkedHashMap<String, Integer>();

            for (int x = 0; x < mList.size(); x++) {
                String ch = mList.get(x).getLabel().substring(0, 1).toUpperCase();
                if(!mapIndex.containsKey(ch))
                        mapIndex.put(ch, x);
            }

            Set<String> sectionLetters = mapIndex.keySet();
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
        }


        @Override
        public int getSectionForPosition(int position) {
            for (int x = sections.length - 1; x >= 0; x--) {
                if (position >= mapIndex.get(sections[x])) {
                    return x;
                }
            }
            return 0;
        }

        @Override
        public int getPositionForSection(int section) {
            return mapIndex.get(sections[section]);
        }

        @Override
        public Object[] getSections() {
            return sections;
        }


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxmultipleappselection_item, null);
                cvh.vimgLogo = (ImageView) convertView.findViewById(R.id.gid_edit_button);
                cvh.vtxtLabel = (TextView) convertView.findViewById(R.id.gid_edit_item_text);
                cvh.vtxtPkgActName = (TextView) convertView.findViewById(R.id.gid_packagename_text);
                cvh.vCheckbox =(CheckBox) convertView.findViewById(R.id.gid_checkbox);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            GrxQuickAppInfo item = (GrxQuickAppInfo) this.getItem(position);

            cvh.vtxtLabel.setText(item.getLabel());
            cvh.vimgLogo.setImageDrawable(item.getIcon());
            cvh.vtxtPkgActName.setText(item.getValue());
            cvh.vCheckbox.setChecked(item.isSelected());
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vimgLogo;
            public TextView vtxtLabel;
            public TextView vtxtPkgActName;
            public CheckBox vCheckbox;
        }
    }


    private class GrxQuickAppInfo {

        private String mPackageName;
        private String mActivityName;
        private String mLabel;
        private Drawable mIcon;
        private boolean mSelected;

        public  GrxQuickAppInfo(String packagename, String activity, String label, Drawable icon){
            mPackageName=packagename;
            mLabel=label;
            mIcon = icon;
            mActivityName = activity;
            mSelected = mSelectedApps.contains(getValue()) ? true : false;

        }

        public String getLabel(){
            return mLabel;
        }

        public String getPackageName(){
            return mPackageName;
        }

        public String getValue(){
            if(mSaveActivityname){
                if(mActivityName!=null && !mActivityName.isEmpty()) return mPackageName+"/"+mActivityName;
                else return mPackageName;
            }else return mPackageName;
        }

        public Drawable getIcon(){
            return mIcon;
        }

        public boolean isSelected(){
            return mSelected;
        }


        public void setIsChecked(boolean checked){
            mSelected = checked;
        }

    }

}
