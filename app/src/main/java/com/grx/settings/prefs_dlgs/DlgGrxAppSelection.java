
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


public class DlgGrxAppSelection extends DialogFragment implements AdapterView.OnItemClickListener {


    ArrayList<GrxQuickAppInfo> mAppsInfo;

    private ListView mListView;
    private OnGrxAppListener mCallBack;


    private ProgressBar pb;
    private TextView vtxtprogressbar;

    AsyncTask<Void, Void, Void> loader;
    private boolean bOrdenar;
    private boolean bSistema;
    private String Title;
    private String key;
    private String mHelperFragmentName;

    private int lastposition=0;

    public boolean mSaveActivityname;


    public interface OnGrxAppListener{
        void onGrxAppSel(DlgGrxAppSelection dialog, String packagename);

    }

    public DlgGrxAppSelection(){}


    public static DlgGrxAppSelection newInstance(OnGrxAppListener callback, String help_frg, String key, String tit, boolean sysapps, boolean saveactivityname, boolean sort){

        DlgGrxAppSelection ret = new DlgGrxAppSelection();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,help_frg);
        bundle.putBoolean("appsys", sysapps);
        bundle.putBoolean("sort", sort);
        bundle.putString("key",key);
        bundle.putString("tit",tit);
        bundle.putBoolean("save_actname", saveactivityname);
        ret.setArguments(bundle);
        ret.setCallBack(callback);
        return ret;
    }
    private void setCallBack(OnGrxAppListener callback){
        mCallBack=callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lastposition= mListView.getFirstVisiblePosition();
        outState.putInt("lastpos", lastposition);

    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        mHelperFragmentName=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        bSistema=getArguments().getBoolean("appsys");
        bOrdenar=getArguments().getBoolean("sort");
        key=getArguments().getString("key");
        Title=getArguments().getString("tit");
        mSaveActivityname = getArguments().getBoolean("save_actname",getActivity().getResources().getBoolean(R.bool.grxb_saveActivityname_default));

        if(state!=null){
            lastposition=state.getInt("lastpos");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(Title)
                .setView(getDialogView())
                .setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }


    public View getDialogView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxappselection,null);
        mListView = (ListView) view.findViewById(R.id.gid_listview);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        mListView.setScrollingCacheEnabled(false);
        mListView.setAnimationCacheEnabled(false);


        vtxtprogressbar =(TextView) view.findViewById(R.id.gid_progressbar_txt);
        vtxtprogressbar.setVisibility(View.VISIBLE);
        if(bOrdenar) vtxtprogressbar.setText(getString(R.string.grxs_building_sorting_list));
        else vtxtprogressbar.setText(getString(R.string.grxs_building_list));
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
                mAppsInfo = getAppList();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pb.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.GONE);
                mListView.setAdapter(new Qadapter(mAppsInfo));
                mListView.setSelection(lastposition);
            }
        }.execute();
        mListView.setDividerHeight(Common.cDividerHeight);
        return view;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mCallBack==null) {
            if(mHelperFragmentName.equals(Common.TAG_PREFSSCREEN_FRAGMENT)){
                GrxPreferenceScreen prefsScreen =(GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                mCallBack=(DlgGrxAppSelection.OnGrxAppListener) prefsScreen.findAndGetCallBack(key);
            }else mCallBack=(DlgGrxAppSelection.OnGrxAppListener) getFragmentManager().findFragmentByTag(mHelperFragmentName);
        }
        if(mCallBack!=null) mCallBack.onGrxAppSel(this, mAppsInfo.get(position).getValue());
        if(mAppsInfo!=null) mAppsInfo.clear();
        dismiss();
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


    private ArrayList<GrxQuickAppInfo> getAppList(){
        List<ApplicationInfo> AppsTmp;
        ApplicationInfo applicationInfo;

        ArrayList<GrxQuickAppInfo> mQuickInfoTmp = new ArrayList<GrxQuickAppInfo>();

        AppsTmp = getActivity().getPackageManager().getInstalledApplications(0);
        for(int ind=0;ind<AppsTmp.size();ind++) {
            applicationInfo=AppsTmp.get(ind);
            try {
                if(bSistema) {
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

        List<DlgGrxAppSelection.GrxQuickAppInfo> mList;
        HashMap<String, Integer> mapIndex;
        String[] sections;


        Qadapter(List<DlgGrxAppSelection.GrxQuickAppInfo> qlist) {
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
            DlgGrxAppSelection.Qadapter.CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new DlgGrxAppSelection.Qadapter.CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxappselection_item, null);
                cvh.vimgLogo = (ImageView) convertView.findViewById(R.id.gid_edit_button);
                cvh.vtxtName = (TextView) convertView.findViewById(R.id.gid_edit_item_text);
                cvh.vtxtpaquete = (TextView) convertView.findViewById(R.id.gid_packagename_text);
                convertView.setTag(cvh);
            } else {
                cvh = (DlgGrxAppSelection.Qadapter.CustomViewHolder) convertView.getTag();
            }

            DlgGrxAppSelection.GrxQuickAppInfo item = (DlgGrxAppSelection.GrxQuickAppInfo) this.getItem(position);

            cvh.vtxtName.setText(item.getLabel());
            cvh.vimgLogo.setImageDrawable(item.getIcon());
            cvh.vtxtpaquete.setText(item.getValue());
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vimgLogo;
            public TextView vtxtName;
            public TextView vtxtpaquete;
        }
    }

    private class GrxQuickAppInfo {

        private String mPackageName;
        private String mActivityName;
        private String mLabel;
        private Drawable mIcon;

        public  GrxQuickAppInfo(String packagename,  String activity, String label, Drawable icon){
            mPackageName=packagename;
            mActivityName = activity;
            mLabel=label;
            mIcon = icon;
        }

        public String getLabel(){
            return mLabel;
        }

        public String getValue(){
            if(mSaveActivityname){
                if(mActivityName!=null && !mActivityName.isEmpty()) return mPackageName+"/"+mActivityName;
                else return mPackageName;
            }else return mPackageName;
        }

        public String getPackageName(){
            return mPackageName;
        }

        public Drawable getIcon(){
            return mIcon;
        }

    }


}
