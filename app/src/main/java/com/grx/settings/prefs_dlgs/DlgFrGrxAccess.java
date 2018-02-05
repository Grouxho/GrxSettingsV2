
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

package com.grx.settings.prefs_dlgs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxImageHelper;
import com.grx.settings.utils.GrxPrefsUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DlgFrGrxAccess extends DialogFragment implements AdapterView.OnItemClickListener, ExpandableListView.OnChildClickListener {

    List<ResolveInfo> mShortCutsList=null;
    List<ResolveInfo> mUsuAppsList=null;
    List<GrxActivityInfo> mActivitiesList=null;
    List<GrxCustomActionInfo> mCustomActionsList=null;
    private ArrayList<ItemSpinner> mSpinnerList;

    AsyncTask<Void, Void, Void> loader;

    private Spinner mSpinner;
    private ProgressBar mProgressBar;
    private TextView vtxtprogressbar;
    private ListView vListView;
    private ListView vListViewApps;
    private ExpandableListView vExpListView;
    private LinearLayout vSelectionContainer;
    private LinearLayout vButtonsContainer;
    private LinearLayout vExpandButton;
    private LinearLayout vCollapseButton;

    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;
    private int mIconsTintColor;
    private boolean mShowShortCuts;
    private boolean mShowApplications;
    private boolean mShowActivities;
    private boolean mShowCustomActions;
    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private GrxAccesListener mCallBack;
    private boolean mMultiOptionMode;
    private boolean mSaveCustomActionsIcons;
    private Intent mCurrentSelectedIntent;
    private String mOriValue;
    private boolean mDeleteTmpFileOnDismiss=true;
    private String mAuxShortCutActivityLabel=null;



    public DlgFrGrxAccess() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        mDeleteTmpFileOnDismiss = true;
        super.onResume();
    }

    public interface GrxAccesListener {
        void GrxSetAccess(String value);
    }

    public static DlgFrGrxAccess newInstance(GrxAccesListener callback,String HelperFragment, String key, String title, String value,
                                             boolean show_shortcuts, boolean show_apps, boolean show_activities,
                                             int id_array_options, int id_array_values, int id_array_icons, int iconstintcolor, boolean save_icons,
                                             boolean multi_mode){


        DlgFrGrxAccess ret = new DlgFrGrxAccess();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putBoolean("show_shortcuts",show_shortcuts);
        bundle.putBoolean("show_aps",show_apps);
        bundle.putBoolean("show_activities",show_activities);
        bundle.putInt("opt_arr_id",id_array_options);
        bundle.putInt("val_array_id",id_array_values);
        bundle.putInt("icons_array_id", id_array_icons );
        bundle.putInt("icons_tintcolor", iconstintcolor);
        bundle.putBoolean("save_icons",save_icons);
        bundle.putBoolean("mode_multi",multi_mode);
        ret.setArguments(bundle);
        ret.saveCallback(callback);
        return ret;

    }

    private void saveCallback(DlgFrGrxAccess.GrxAccesListener callback){
        mCallBack =callback;
    }


    public void setMultioptionMode(boolean modo){
        mMultiOptionMode=modo;
    }

    /*********************** MAIN LOGIC **********************************************/


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int op = mSpinner.getSelectedItemPosition();
        op=mSpinnerList.get(op).getId();

        switch (op){
            case Common.ID_ACCESS_SHORCUT:
                getShortCut(position);
                break;
            case Common.ID_ACCESS_APPS:
                getIntentFromUserapp(position);
                break;
            case Common.ID_ACCESS_CUSTOM:
                getIntentFromCustomAction(position);
                break;
            default:
                break;
        }
    }

    /**** logic for shortcuts *****/

    private void getShortCut(int pos){
        ResolveInfo ri = (ResolveInfo) mShortCutsList.get(pos);
        mAuxShortCutActivityLabel=null;
        try {
            mAuxShortCutActivityLabel = ri.loadLabel(getActivity().getPackageManager()).toString();
        }catch (Exception e){

        }  //save app label name to a temp var, now lets try to get shortcut  now. This is f.e. Whatsapp chat:  - let´s try to get the contact - group - ... intent now

        Intent intent = new  Intent(Intent.ACTION_CREATE_SHORTCUT);
        ComponentName c_n = new ComponentName(ri.activityInfo.packageName,ri.activityInfo.name);
        intent.setComponent(c_n);
        startActivityForResult(intent,Common.REQ_CODE_GET_SHORTCUT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String tmp_name = null;

        if(requestCode == Common.REQ_CODE_GET_SHORTCUT){
            if(resultCode== Activity.RESULT_OK){
                Bitmap ico = null;
                Intent.ShortcutIconResource iconResource = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                if(iconResource!=null){
                    try {
                        final Context pkgContext = getActivity().createPackageContext(iconResource.packageName,Context.CONTEXT_IGNORE_SECURITY);
                        final Resources pkgRes = pkgContext.getResources();
                        final int id_dr = pkgRes.getIdentifier(iconResource.resourceName, "drawable", iconResource.packageName);
                        ico = BitmapFactory.decodeResource(pkgRes,id_dr);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(ico==null){
                    ico = (Bitmap) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
                }

                if(ico!=null){
                    tmp_name = getCurrenttimemillisFileIconName();
                    if(!GrxImageHelper.save_png_from_bitmap(ico,tmp_name)) tmp_name = null;
                    else GrxPrefsUtils.setReadWriteFilePermissions(tmp_name);
                }
                getIntentFromShortcut((Intent)data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT), data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),tmp_name);
            }
        }
    }


    private void getIntentFromShortcut(Intent intent, String text, String tmp_name ){
        if(intent==null){
            Toast.makeText(getActivity(),getString(R.string.grxs_shortcut_error), Toast.LENGTH_SHORT).show();
        }else{
            GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent, Common.TMP_PREFIX);
            mCurrentSelectedIntent=intent;
            String act_label="";
            String label="";
            if(mAuxShortCutActivityLabel!=null) act_label=mAuxShortCutActivityLabel;
            if(text!=null) label=text;
            if(act_label.equals(label)) mCurrentSelectedIntent.putExtra(Common.EXTRA_URI_LABEL,act_label );
            else mCurrentSelectedIntent.putExtra(Common.EXTRA_URI_LABEL,act_label+" : "+label);
            if(tmp_name!=null){
                mCurrentSelectedIntent.putExtra(Common.EXTRA_URI_ICON,tmp_name);
            }
            mCurrentSelectedIntent.putExtra(Common.EXTRA_URI_TYPE,Common.ID_ACCESS_SHORCUT);
            showCurrentUserSelection();
        }
    }

    private void getIntentFromUserapp(int pos){
        ResolveInfo ri_app = (ResolveInfo) mUsuAppsList.get(pos);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra(Common.EXTRA_URI_TYPE,Common.ID_ACCESS_APPS);
        ComponentName cn = new ComponentName(ri_app.activityInfo.packageName,ri_app.activityInfo.name);
        intent.setComponent(cn);
        GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent, Common.TMP_PREFIX);
        mCurrentSelectedIntent=intent;
        showCurrentUserSelection();
    }


    private void getIntentFromCustomAction(int pos){

        Drawable dr=null;
        String icon_name=null;
        String label = null;
        String val = null;
        String drawable_name=null;

        GrxCustomActionInfo grxInfoAccion = mCustomActionsList.get(pos);

        Intent intent = new Intent();
        intent.putExtra(Common.EXTRA_URI_TYPE,Common.ID_ACCESS_CUSTOM);

        label = grxInfoAccion.getLabel();
        if(label!=null) intent.putExtra(Common.EXTRA_URI_LABEL,label);

        val = grxInfoAccion.getVal();
        if(val!=null) intent.putExtra(Common.EXTRA_URI_VALUE,val);

        drawable_name = grxInfoAccion.getDrawableName();
        if(drawable_name!=null) intent.putExtra(Common.EXTRA_URI_DRAWABLE_NAME,drawable_name);

        if(mSaveCustomActionsIcons){
            dr=grxInfoAccion.getIcon();
            if(dr!=null){
                icon_name= getCurrenttimemillisFileIconName();
                if(GrxImageHelper.save_png_from_bitmap(GrxImageHelper.drawableToBitmap(dr),icon_name ))
                    GrxPrefsUtils.setReadWriteFilePermissions(icon_name);
            }
        }

        if(icon_name!=null) intent.putExtra(Common.EXTRA_URI_ICON,icon_name);

        GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent, Common.TMP_PREFIX);
        mCurrentSelectedIntent=intent;
        showCurrentUserSelection();

    }


    public void showCurrentUserSelection(){

        Drawable icon = null;
        String label=null;
        if(mCurrentSelectedIntent!=null){
            vSelectionContainer.removeAllViews();
            int type = mCurrentSelectedIntent.getIntExtra(Common.EXTRA_URI_TYPE,-1);
            if(type == -1 ) {
                GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent, Common.TMP_PREFIX);
                mCurrentSelectedIntent=null;
                addViewsFromSelectedIntent(null,getResources().getString(R.string.grxs_no_access_selection),false,0);
                return;
            }

            switch (type){
                case Common.ID_ACCESS_SHORCUT:
                case Common.ID_ACCESS_APPS:
                case Common.ID_ACCESS_ACTIVITIES:

                    label = GrxPrefsUtils.getActivityLabelFromIntent(getActivity(), mCurrentSelectedIntent);
                    icon = GrxPrefsUtils.getDrawableFromGrxIntent(getActivity(),mCurrentSelectedIntent);
                    addViewsFromSelectedIntent(icon,label,true,0);
                    break;
                case Common.ID_ACCESS_CUSTOM:
                    label = GrxPrefsUtils.getActivityLabelFromIntent(getActivity(), mCurrentSelectedIntent);
                    icon = GrxPrefsUtils.getDrawableFromGrxIntent(getActivity(),mCurrentSelectedIntent);
                    addViewsFromSelectedIntent(icon,label,false,mIconsTintColor);
                    break;
                default:
                    break;
            }
        }else {
            addViewsFromSelectedIntent(null,getResources().getString(R.string.grxs_no_access_selection),false,0);
        }
    }

    private void addViewsFromSelectedIntent(Drawable drawable, String text, boolean clickable, int tintcolor){

        vSelectionContainer.removeAllViews();
        LinearLayout ll = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_item,null);

        ImageView iv = (ImageView) ll.findViewById(R.id.gid_access_item_img);
        if (tintcolor!=0) iv.setColorFilter(tintcolor);
        if(drawable==null) iv.setVisibility(View.GONE);
        else iv.setImageDrawable(drawable);

        //bbbbbbbbbbbbbbb

        TextView tv = (TextView) ll.findViewById(R.id.gid_access_item_text);
        if(text!=null) tv.setText(text);
        GrxPrefsUtils.animateTextviewMarquee(tv);
        tv.setSelected(true);
        ImageView arrow = (ImageView) ll.findViewById(R.id.gid_arrow_icon);
        if(clickable) arrow.setVisibility(View.VISIBLE);
       //ll.setClickable(clickable);
        vSelectionContainer.addView(ll);
        vSelectionContainer.setClickable(clickable);
    }



    private String getCurrenttimemillisFileIconName(){
        return Common.CacheDir + File.separator + Common.TMP_PREFIX+String.valueOf(System.currentTimeMillis()) + ".png";
    }


    /******************* DIALOG ,  VIEW , SAVED INSTANCE, DISMISS *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mDeleteTmpFileOnDismiss=false;
        super.onSaveInstanceState(outState);
        String current_uri=null;
        if(mCurrentSelectedIntent!=null) current_uri= mCurrentSelectedIntent.toUri(0);
        if(current_uri== null ) current_uri = "";
        outState.putString("curr_val", current_uri);
    }

    private View getDialogView(){

        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxaccess, null);
        vButtonsContainer= (LinearLayout) view.findViewById(R.id.gid_buttons_container);
        vExpandButton = (LinearLayout) vButtonsContainer.findViewById(R.id.gid_button_expand);
        vCollapseButton = (LinearLayout) vButtonsContainer.findViewById(R.id.gid_button_close);
        vButtonsContainer.setVisibility(View.GONE);
        vExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandActivitiesGroups();
            }
        });

        vCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseActivitiesGroups();
            }
        });
        vSelectionContainer = (LinearLayout) view.findViewById(R.id.gid_aux_info_container);
        vSelectionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mCurrentSelectedIntent!=null) startActivity(mCurrentSelectedIntent);
                }catch ( Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),getString(R.string.grxs_shortcut_not_allowed),Toast.LENGTH_LONG).show();
                }
            }
        });

        mSpinner = (Spinner) view.findViewById(R.id.gid_spinner);

        vListView =(ListView) view.findViewById(R.id.gid_listview);
        vExpListView = (ExpandableListView) view.findViewById(R.id.gid_expandable_listview);
        vExpListView.setDivider(getResources().getDrawable(R.drawable.list_divider));
        vExpListView.setChildDivider(getResources().getDrawable(R.drawable.list_divider));

        vListView.setOnItemClickListener(this);
        vExpListView.setOnChildClickListener(this);

        vtxtprogressbar =(TextView) view.findViewById(R.id.gid_progressbar_txt);
        vtxtprogressbar.setText(getString(R.string.grxs_building_sorting_list));
        mProgressBar = (ProgressBar) view.findViewById(R.id.gid_progressbar);

        vListView.setDividerHeight(Common.cDividerHeight);
        vExpListView.setDividerHeight(Common.cDividerHeight);

        vListView.setFastScrollEnabled(false);
        vListView.setScrollingCacheEnabled(false);
        vListView.setAnimationCacheEnabled(false);

        vListViewApps = (ListView) view.findViewById(R.id.gid_apps_listview);
        vListViewApps.setDividerHeight(Common.cDividerHeight);

        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if((loader!=null) && (loader.getStatus()== AsyncTask.Status.RUNNING)){
            loader.cancel(true);
            loader=null;
        }

        if(mShortCutsList!=null) mShortCutsList.clear();
        if(mUsuAppsList!=null) mUsuAppsList.clear();
        if(mActivitiesList!=null) mActivitiesList.clear();
        if(mCustomActionsList!=null) mCustomActionsList.clear();

        if(mCurrentSelectedIntent!=null){
            String current_uri = mCurrentSelectedIntent.toUri(0);
            if(!current_uri.equals(mOriValue)){
                if(mDeleteTmpFileOnDismiss && isAdded()) GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent);
            }
        }
    }


    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxAccess.GrxAccesListener) prefsScreen.findAndGetCallBack(mKey);
            }else {
                mCallBack=(DlgFrGrxAccess.GrxAccesListener) getFragmentManager().findFragmentByTag(mHelperFragment);
            }
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle state) {

        mHelperFragment=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey=getArguments().getString("key");
        mTitle=getArguments().getString("tit");
        mOriValue=getArguments().getString("val");
        mValue=mOriValue;
        mShowShortCuts = getArguments().getBoolean("show_shortcuts");
        mShowApplications=getArguments().getBoolean("show_aps");
        mShowActivities=getArguments().getBoolean("show_activities");
        mIdOptionsArr= getArguments().getInt("opt_arr_id");
        mIdValuesArr=getArguments().getInt("val_array_id");
        mIdIconsArray=getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mSaveCustomActionsIcons=getArguments().getBoolean("save_icons");
        mMultiOptionMode=getArguments().getBoolean("mode_multi");

        if(mIdOptionsArr==0 || mIdValuesArr == 0 ) mShowCustomActions=false;
        else mShowCustomActions=true;

        if (state != null) mValue =  state.getString("curr_val");

        if(mValue!=null && !mValue.isEmpty()){
            try{
                mCurrentSelectedIntent=Intent.parseUri(mValue,0);
            }catch (URISyntaxException e){}

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(getDialogView());
        builder.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeleteTmpFileOnDismiss=true;
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCallback();
                mDeleteTmpFileOnDismiss=false;
                if(mCallBack!=null && mCurrentSelectedIntent!=null) {
                    String curr_uri = mCurrentSelectedIntent.toUri(0);
                    if(mOriValue.equals(curr_uri)) mDeleteTmpFileOnDismiss = true;
                    else mCallBack.GrxSetAccess(curr_uri);
                } else mDeleteTmpFileOnDismiss = true;
            }
        });

        iniSpinner();
        showCurrentUserSelection();
        return builder.create();

    }


    /***************** SPINNER *****************************/

    private void iniSpinner() {
        mSpinnerList = new ArrayList<>();
        if(mShowShortCuts) mSpinnerList.add(new ItemSpinner(getString(R.string.grxs_shortcuts), Common.ID_ACCESS_SHORCUT));
        if(mShowApplications) mSpinnerList.add(new ItemSpinner(getString(R.string.grxs_user_apps), Common.ID_ACCESS_APPS));
        if(mShowActivities)mSpinnerList.add(new ItemSpinner(getString(R.string.grxs_activities), Common.ID_ACCESS_ACTIVITIES));
        if(mShowCustomActions) mSpinnerList.add(new ItemSpinner(getString(R.string.grxs_actions), Common.ID_ACCESS_CUSTOM));

        if(mSpinnerList.size()<2) {
            mSpinner.setBackgroundDrawable(null);
            mSpinner.setPadding(0,0,0,0);
            mSpinner.setEnabled(false);

        }
        mSpinner.setAdapter(SpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int op = mSpinnerList.get(position).getId();
                switch (op){
                    case Common.ID_ACCESS_SHORCUT:
                        iniShortcutsList();
                        break;
                    case Common.ID_ACCESS_APPS:
                        iniAppsList();
                        break;
                    case Common.ID_ACCESS_ACTIVITIES:
                        iniActivitiesList();
                        break;
                    case Common.ID_ACCESS_CUSTOM:
                        iniCustomActionsList();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    private class ItemSpinner {
        private String Texto;
        private int id;

        public ItemSpinner(String texto, int i) {
            Texto = texto;
            id = i;
        }

        public String getLabel() {
            return Texto;
        }

        public int getId() {
            return id;
        }
    }

    private BaseAdapter SpinnerAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSpinnerList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSpinnerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mSpinnerList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_spinner, null);
                cvh.vCtxt = (TextView) convertView.findViewById(R.id.gid_text);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            ItemSpinner itemSpinner = (ItemSpinner) this.getItem(position);
            cvh.vCtxt.setText(itemSpinner.getLabel());
            if(mSpinnerList.size()==1){
                //cvh.vCtxt.setGravity(Gravity.CENTER);
                cvh.vCtxt.setPadding(60,0,0,0);

            }
            return convertView;
        }

        class CustomViewHolder {
            public TextView vCtxt;
        }
    };



    /************************************************************************************/

    /**************************** ShortCuts **********************************************/


    private void buildShortcutsList(){
        Intent intent = new Intent();
        List<PackageInfo> ListaPaquetes = getActivity().getPackageManager().getInstalledPackages(0);
        intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
        for (PackageInfo packinfo : ListaPaquetes){
            intent.setPackage(packinfo.packageName);
            List<ResolveInfo> mActivitiesList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            for(ResolveInfo resolveInfo : mActivitiesList) {
                mShortCutsList.add(resolveInfo);
            }
        }
        Collections.sort(mShortCutsList, new ResolveInfo.DisplayNameComparator(getActivity().getPackageManager()));
    }

    private void iniShortcutsList(){
        vExpListView.setVisibility(View.GONE);
        vButtonsContainer.setVisibility(View.GONE);
        vListView.setVisibility(View.GONE);
        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSpinner.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                vListView.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.VISIBLE);
                mProgressBar.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(mShortCutsList==null){
                    mShortCutsList = new ArrayList<ResolveInfo>();
                    buildShortcutsList();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                vtxtprogressbar.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                vListView.setAdapter(null);
                vListView.setAdapter(mShortCutsAdapter);
                vListView.setVisibility(View.VISIBLE);
                if(mSpinnerList.size()>1) mSpinner.setEnabled(true);;
            }
        }.execute();
    }



    private BaseAdapter mShortCutsAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mShortCutsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mShortCutsList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_item, null);
                cvh.vImg = (ImageView) convertView.findViewById(R.id.gid_access_item_img);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_access_item_text);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            //ResolveInfo item = (ResolveInfo) this.getItem(position);
            ResolveInfo item = (ResolveInfo) mShortCutsList.get(position);
            cvh.vTxt.setText(item.loadLabel(getActivity().getPackageManager()));
            cvh.vImg.setImageDrawable(item.loadIcon(getActivity().getPackageManager()));

            return convertView;
        }

        class CustomViewHolder {
            public ImageView vImg;
            public TextView vTxt;
        }
    };


    /*****************************************************************************/
    /****************************** CUSTOM ACTIONS *******************************/
    /*****************************************************************************/



    private void buildCustomActionsList(){
        TypedArray icons_array=null;
        String vals_array[] = getResources().getStringArray(mIdValuesArr);
        String opt_array[] = getResources().getStringArray(mIdOptionsArr);
        if(mIdIconsArray!=0){
            icons_array = getResources().obtainTypedArray(mIdIconsArray);
        }
        mCustomActionsList = new ArrayList<GrxCustomActionInfo>();
        for(int i=0;i<vals_array.length;i++){
            Drawable drwtmp = null;
            String drawable_name = null;
            if(icons_array!=null) {
                drwtmp = icons_array.getDrawable(i);
                int id_drw = icons_array.getResourceId(i,0);
                if (id_drw!=0) {
                    drawable_name = getResources().getResourceEntryName(id_drw);
                }
            }

            mCustomActionsList.add(new GrxCustomActionInfo(opt_array[i],vals_array[i], drwtmp, drawable_name) );
        }
        if(icons_array!=null) icons_array.recycle();
    }


    private void iniCustomActionsList(){
        vListView.setVisibility(View.GONE);
        vExpListView.setVisibility(View.GONE);
        vButtonsContainer.setVisibility(View.GONE);
        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSpinner.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                vtxtprogressbar.setVisibility(View.VISIBLE);
                mProgressBar.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(mCustomActionsList==null){
                    buildCustomActionsList();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                vtxtprogressbar.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                vListView.setAdapter(mCustomActionAdapter);
                vListView.setVisibility(View.VISIBLE);
                if(mSpinnerList.size()>1) mSpinner.setEnabled(true);
            }
        }.execute();
    }



    private BaseAdapter mCustomActionAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mCustomActionsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCustomActionsList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_item, null);
                cvh.vImg = (ImageView) convertView.findViewById(R.id.gid_access_item_img);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_access_item_text);
                if(mIconsTintColor!=0) cvh.vImg.setColorFilter(mIconsTintColor);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            GrxCustomActionInfo item = (GrxCustomActionInfo) mCustomActionsList.get(position);
            cvh.vTxt.setText(item.getLabel());
            if(item.getIcon()!=null) cvh.vImg.setImageDrawable( item.getIcon());
            // else cvh.vImg.setImageDrawable(getResources().getDrawable(R.drawable.circle));
            else cvh.vImg.setVisibility(View.GONE);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vImg;
            public TextView vTxt;
        }
    };



    private class GrxCustomActionInfo{
        private String mLabel;
        private String mValue;
        private Drawable mIcon;
        private String mDrawableName;

        public GrxCustomActionInfo(String label, String val, Drawable icon, String drawable_name) {
            mValue  = val;
            mLabel = label;
            mIcon = icon;
            mDrawableName = drawable_name;
        }

        public String getLabel() {
            return mLabel;

        }

        public String getVal() {
            return mValue;

        }

        public String getDrawableName(){
            return mDrawableName;
        }

        public Drawable getIcon(){
            return mIcon;
        }
    }



    /*****************************************************************************/
    /******************    Activities ********************************************/
    /*****************************************************************************/


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String label;

        Drawable dr=null;
        boolean error=false;
        Intent intent;

        ActivityInfo ai[]=mActivitiesList.get(groupPosition).getPackageInfo().activities;
        if(ai!=null) {
            intent = new Intent();
            intent.setComponent(new ComponentName(ai[childPosition].packageName,ai[childPosition].name ));
            intent.putExtra(Common.EXTRA_URI_TYPE,Common.ID_ACCESS_ACTIVITIES);
            label= GrxPrefsUtils.getGrxLabelFromPackagenameActivityname(getActivity(), ai[childPosition].packageName,ai[childPosition].name);
            intent.putExtra(Common.EXTRA_URI_LABEL,label);
            GrxPrefsUtils.deleteGrxIconFileFromIntent(mCurrentSelectedIntent, Common.TMP_PREFIX);
            mCurrentSelectedIntent = intent;
            showCurrentUserSelection();
        }

        return true;
    }


    private void expandActivitiesGroups(){
        for(int i= 0; i<mActivitiesList.size();i++){
            vExpListView.expandGroup(i);
        }
    }

    private void collapseActivitiesGroups(){
        for(int i= 0; i<mActivitiesList.size();i++){
            vExpListView.collapseGroup(i);
        }
    }


    private void buildActivitiesList(){
        List<PackageInfo> ListaPaquetes = getActivity().getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        PackageManager pm = getActivity().getPackageManager();
        int i=0;
        mActivitiesList = new ArrayList<GrxActivityInfo>();
        for(i=0;i<ListaPaquetes.size();i++){
            PackageInfo pi = ListaPaquetes.get(i);
            ActivityInfo ai[] = pi.activities;

            if (ai!=null && ai.length!=0){
                mActivitiesList.add(new GrxActivityInfo(pi, pi.applicationInfo.loadLabel(pm).toString()));
            }

        }

        try {
            Collections.sort(mActivitiesList, new Comparator<GrxActivityInfo>() {
                @Override
                public int compare(GrxActivityInfo A_actinfo, GrxActivityInfo actinfo) {
                    try {
                        return String.CASE_INSENSITIVE_ORDER.compare(A_actinfo.getLabel(), actinfo.getLabel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private class GrxActivityInfo{
        private PackageInfo pinfo;
        private String label;

        public GrxActivityInfo(PackageInfo pi, String etiqueta) {
            pinfo=pi;
            label=etiqueta;
        }

        public String getLabel() {
            return label;

        }

        public PackageInfo getPackageInfo() {
            return pinfo;
        }

    }


    private void iniActivitiesList(){
        vListView.setVisibility(View.GONE);
        vExpListView.setVisibility(View.GONE);
        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSpinner.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                vExpListView.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.VISIBLE);
                mProgressBar.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(mActivitiesList==null){
                    buildActivitiesList();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                vtxtprogressbar.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                //vListView.setAdapter(mShortCutsAdapter);
                vExpListView.setAdapter(mActivityGroupAdapter);
                vExpListView.setVisibility(View.VISIBLE);
                vButtonsContainer.setVisibility(View.VISIBLE);
                if(mSpinnerList.size()>1) mSpinner.setEnabled(true);
            }
        }.execute();
    }



    BaseExpandableListAdapter mActivityGroupAdapter = new BaseExpandableListAdapter(){

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            String nombre_actividad=mActivitiesList.get(groupPosition).getPackageInfo().activities[childPosition].name;
            String nombre_paquete=mActivitiesList.get(groupPosition).getPackageInfo().packageName;
            return GrxPrefsUtils.getGrxLabelFromPackagenameActivityname(getActivity(), nombre_paquete, nombre_actividad);
            //return mActivitiesList.get(groupPosition).getPackageInfo().activities[childPosition].name;//string - label
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mActivitiesList.get(groupPosition).getPackageInfo().activities.length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mActivitiesList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mActivitiesList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private Drawable get_activity_icon(ActivityInfo ai, PackageManager pm){
            boolean error=false;
            Drawable dr=null;
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(ai.packageName, ai.name));
            ResolveInfo ri = pm.resolveActivity(intent,0);
            if(ri==null) error=true;
            if(!error){
                try{
                    dr=ri.loadIcon(pm);
                }catch (Exception e){
                    error=true;

                }
            }

            if(error) dr = getActivity().getResources().getDrawable(R.drawable.circle);
            return dr;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            PackageManager pm;
            pm = getActivity().getPackageManager();
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_child_activity_item, null);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_access_item_text);
                cvh.vImgIcono = (ImageView) convertView.findViewById(R.id.gid_access_item_img);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            GrxActivityInfo item = (GrxActivityInfo) mActivitiesList.get(groupPosition);
            String txt_act = (String) getChild(groupPosition,childPosition);

            cvh.vImgIcono.setImageDrawable(get_activity_icon(item.getPackageInfo().activities[childPosition],pm));
            cvh.vTxt.setText(txt_act);
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                //convertView = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, null);
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_item, null);
                //cvh.vTxt = (TextView) convertView.findViewById(android.R.id.text1);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_access_item_text);
                cvh.vImgIcono = (ImageView) convertView.findViewById(R.id.gid_access_item_img);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            GrxActivityInfo item = (GrxActivityInfo) mActivitiesList.get(groupPosition);
            cvh.vTxt.setTypeface(Typeface.DEFAULT_BOLD);
            cvh.vTxt.setText(item.getLabel());
            cvh.vImgIcono.setImageDrawable(item.getPackageInfo().applicationInfo.loadIcon(getActivity().getPackageManager()));
                        return convertView;
        }

        class CustomViewHolder {
            public TextView vTxt;
            public ImageView vImgIcono;
        }

    };


    /*****************************************************************************************/
    /**********************  APPS ************************************************************/
    /*****************************************************************************************/

    private void buildAppsList(){
        List<PackageInfo> ListaPaquetes = getActivity().getPackageManager().getInstalledPackages(0);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        for (PackageInfo packinfo : ListaPaquetes){
            intent.setPackage(packinfo.packageName);
            List<ResolveInfo> mActivitiesList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            for(ResolveInfo resolveInfo : mActivitiesList) {
                mUsuAppsList.add(resolveInfo);
            }
        }

        Collections.sort(mUsuAppsList, new ResolveInfo.DisplayNameComparator(getActivity().getPackageManager()));
        // Toast.makeText(getActivity(),String.valueOf(mUsuAppsList.size()),Toast.LENGTH_SHORT).show();
    }

    private void iniAppsList(){
        vExpListView.setVisibility(View.GONE);
        vButtonsContainer.setVisibility(View.GONE);
        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSpinner.setEnabled(false);
                vListView.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(mUsuAppsList==null) {
                    mUsuAppsList = new ArrayList<ResolveInfo>();
                    buildAppsList();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                vtxtprogressbar.setVisibility(View.GONE);
                vListView.setAdapter(null);
                vListView.setAdapter(mUserAppAdapter);
                mProgressBar.setVisibility(View.GONE);
                vListView.setVisibility(View.VISIBLE);
                if(mSpinnerList.size()>1) mSpinner.setEnabled(true);;
            }
        }.execute();
    }



    private BaseAdapter mUserAppAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mUsuAppsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsuAppsList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxaccess_item, null);
                cvh.vImgIcono = (ImageView) convertView.findViewById(R.id.gid_access_item_img);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_access_item_text);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            //ResolveInfo item = (ResolveInfo) this.getItem(position);
            ResolveInfo item = (ResolveInfo) mUsuAppsList.get(position);
            cvh.vTxt.setText(item.loadLabel(getActivity().getPackageManager()));
            cvh.vImgIcono.setImageDrawable(item.loadIcon(getActivity().getPackageManager()));

            return convertView;
        }

        class CustomViewHolder {
            public ImageView vImgIcono;
            public TextView vTxt;
        }
    };



}
