
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
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;
import com.grx.settings.utils.GrxPrefsUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;



public class DlgFrGrxMultipleWidgets extends DialogFragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {

    DlgFrGrxMultipleWidgets.OnWidgetsSelectedListener mCallBack;

    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;
    private int mMax;
    private boolean mMultimode;


    public int mNumSelected =0;

    private SlideAndDragListView DragListView;
    private ListView SelectItemsList;
    TextView vTxtSelectedInfo;
    String SelectedItemsInfo;
    private int mCurrentView=0;
    private Button mNegativeButton;
    private LinearLayout vDeleteButton;
    private LinearLayout vBackButton;
    private LinearLayout vAddButton;
    private LinearLayout vHelpButton;
    private LinearLayout vSeparator;
    private LinearLayout vButtonsContainer;

    private int mIdArrayWidgets;


    ArrayList<WidgetInfo> mInstalledWidgets;
    ArrayList<WidgetInfo> mSelectedWidgets;

    AsyncTask<Void, Void, Void> loader;

    private ProgressBar pb;
    private TextView vtxtprogressbar;

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

    public interface OnWidgetsSelectedListener{
        void OnWidgetsSelected(String widgets);
    }


    public DlgFrGrxMultipleWidgets(){}


    public static DlgFrGrxMultipleWidgets newInstance(DlgFrGrxMultipleWidgets.OnWidgetsSelectedListener callback, String HelperFragment, boolean multimode, String key, String title, String value,
                                                      int id_widgets_array, String separtor, int maxnum){
        DlgFrGrxMultipleWidgets ret = new DlgFrGrxMultipleWidgets();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putBoolean("multimode", multimode);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putInt("widgets_array",id_widgets_array);
        bundle.putString("separator", separtor);
        bundle.putInt("max_items", maxnum);


        ret.setArguments(bundle);
        ret.saveCallback(callback);
        return ret;
    }


    private void saveCallback(DlgFrGrxMultipleWidgets.OnWidgetsSelectedListener callback){
        mCallBack =callback;
    }

    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxMultipleWidgets.OnWidgetsSelectedListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxMultipleWidgets.OnWidgetsSelectedListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }


    private int existWdiget(String value){

        for(WidgetInfo info : mInstalledWidgets){
            if(info.getValue().equals(value)) return info.getIndex();
        }
     return -1;
    }

    private void setInitialSelectedWidgets(String value){
        if(mSelectedWidgets!=null) mSelectedWidgets.clear();
        else mSelectedWidgets = new ArrayList<WidgetInfo>();
        if(value==null || value.isEmpty()) return;

        if(mMultimode){
            String[] wv = value.split(Pattern.quote(mSeparator));
            if(wv==null || wv.length==0) return;

            for(int i=0; i<wv.length;i++){
                int index = existWdiget(wv[i]);
                if (index != -1) {
                    mInstalledWidgets.get(index).setSelected(true);
                    mSelectedWidgets.add(mInstalledWidgets.get(index));
                }
            }

        }else{
            int index = existWdiget(value);
            if(index!=-1){
                mInstalledWidgets.get(index).setSelected(true);
                mSelectedWidgets.add(mInstalledWidgets.get(index));
            }
        }



    }

    private String getReturnValue(){
        String returnvalue = "";
        for(int i=0; i<mSelectedWidgets.size();i++) {
            returnvalue+=mSelectedWidgets.get(i).getValue();
            if(mMultimode) returnvalue+=mSeparator;
        }
        return returnvalue;
    }

    private WidgetInfo getSpecialWidgetInfo(String value){

        boolean exists = false;
        WidgetInfo widgetInfo=null;

        if(value==null || value.isEmpty()) return null;

        String w[] = value.split(Pattern.quote("/"));
        if(w==null || w.length!=2) return null;

        ComponentName componentName = new ComponentName(w[0],w[1]);
        AppWidgetManager manager = AppWidgetManager.getInstance(getActivity());
        AppWidgetHost appWidgetHost = new AppWidgetHost(getActivity(),Integer.valueOf(0x4b455889));
        int widgetid = appWidgetHost.allocateAppWidgetId();

        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetCategory", 2);

        try {

            Class[] classes = new Class[3];
            classes[0] = Integer.TYPE;
            classes[1] = ComponentName.class;
            classes[2] = Bundle.class;

            Method bindAppWidgetId = AppWidgetManager.class.getDeclaredMethod("bindAppWidgetId", classes );

            Object[] objects = new Object[3];

            objects[0]= Integer.valueOf(widgetid);
            objects[1] = componentName;
            objects[2] = bundle;

            bindAppWidgetId.invoke(manager, objects);

            exists = true;

        }catch (Exception e){
            Log.w("GrxWidget: ",e.toString() );
            exists= false;
        }

        AppWidgetProviderInfo appWidgetProviderInfo = manager.getAppWidgetInfo(widgetid);

        if(exists) {
            if(appWidgetProviderInfo==null) exists=false;
            else {
                AppWidgetHostView appWidgetHostView = appWidgetHost.createView(getActivity(),widgetid,appWidgetProviderInfo);
                appWidgetHostView.setAppWidget(widgetid,appWidgetProviderInfo);
            }
        }

        if(exists)
        {
            widgetInfo = new WidgetInfo(appWidgetProviderInfo);

        }
        appWidgetHost.deleteHost();

        return widgetInfo;
    }



    private ArrayList<WidgetInfo> getInstalledWidgetsList(){

        List<ApplicationInfo> AppsTmp;
        AppsTmp = getActivity().getPackageManager().getInstalledApplications(0);
        AppWidgetManager manager = AppWidgetManager.getInstance(getActivity());
        List<AppWidgetProviderInfo> infoList = manager.getInstalledProviders();
        ArrayList<WidgetInfo> widgetlist = new ArrayList<WidgetInfo>();

        for(AppWidgetProviderInfo info : infoList){
            widgetlist.add(new WidgetInfo(info));
        }

        /* process widgets array explicit values */

        if(mIdArrayWidgets!=0){
            String widgets[] = getResources().getStringArray(mIdArrayWidgets);
            if (!(widgets==null || widgets.length==0)){
                for(int i=0;i<widgets.length;i++) {
                    WidgetInfo widgetInfo = getSpecialWidgetInfo(widgets[i]);
                    if(widgetInfo!=null) widgetlist.add(widgetInfo);
                }
            }
        }

        try{
            Collections.sort(widgetlist, new Comparator<WidgetInfo>() {
                @Override
                public int compare(WidgetInfo A_widgetinfo, WidgetInfo widgetInfo) {
                    try{
                        return String.CASE_INSENSITIVE_ORDER.compare(A_widgetinfo.getLabel(), widgetInfo.getLabel());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

        for(int ii=0; ii<widgetlist.size();ii++){
            widgetlist.get(ii).setIndex(ii);
        }

        return widgetlist;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("curr_val", getReturnValue());
        outState.putInt("current_view",mCurrentView);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

       mHelperFragment = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
       mMultimode=getArguments().getBoolean("multimode");
        mKey = getArguments().getString("key");
        mTitle = getArguments().getString("tit");
        mOriValue = getArguments().getString("val");
        mValue = mOriValue;
        mIdArrayWidgets= getArguments().getInt("widgets_array");
        mSeparator = getArguments().getString("separator");
        mMax = getArguments().getInt("max_items");
        if (state != null) {
            mValue = state.getString("curr_val");
            mCurrentView=state.getInt("current_view");
        }else mCurrentView=0;


       //WidgetInfo test1 = getSpecialWidgetInfo("com.sec.android.daemonapp/com.sec.android.daemonapp.appwidget.WeatherAppWidget2x145");
        //int dlg_grxappledpulse = existWdiget("com.google.android.googlequicksearchbox/com.google.android.apps.gsa.soundsearchwidget.IntentWidgetProvider");

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
                mValue = getReturnValue();
                if(!mValue.equals(mOriValue)) {
                    checkCallback();
                    if (mCallBack != null) mCallBack.OnWidgetsSelected(mValue);
                }
                mSelectedWidgets.clear();
                mInstalledWidgets.clear();
                dismiss();

            }
        });
        builder.setView(getDialogView());
        return builder.create();
    }


    private View getDialogView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxmultiplewidgets_grxselectsortitems, null);
        vButtonsContainer = (LinearLayout) view.findViewById(R.id.gid_buttons_container);
        vButtonsContainer.setVisibility(View.GONE);
        DragListView = (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);
        SelectItemsList=(ListView)view.findViewById(R.id.gid_listview);
        vSeparator=(LinearLayout) view.findViewById(R.id.gid_separator);

        vtxtprogressbar =(TextView) view.findViewById(R.id.gid_progressbar_txt);
        vtxtprogressbar.setVisibility(View.VISIBLE);
        vtxtprogressbar.setText(getString(R.string.grxs_building_sorting_list));

        pb = (ProgressBar) view.findViewById(R.id.gid_progressbar);


        vHelpButton = (LinearLayout) view.findViewById(R.id.gid_help_button);
        vHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showHelp();
            }
        });
        if(!mMultimode) vHelpButton.setVisibility(View.GONE);
        vTxtSelectedInfo = (TextView) view.findViewById(R.id.gid_items_selected);
        if(!mMultimode) vTxtSelectedInfo.setVisibility(View.GONE);
        vAddButton = (LinearLayout) view.findViewById(R.id.gid_item);
        vAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectableItems();
            }
        });

        vDeleteButton = (LinearLayout) view.findViewById(R.id.gid_delete_button);
        vDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelectedItems();
            }
        });

        vBackButton = (LinearLayout) view.findViewById(R.id.gid_button_back);
        vBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedItems();
            }
        });


        DragListView.setDividerHeight(Common.cDividerHeight);
        SelectItemsList.setDividerHeight(Common.cDividerHeight);

        if(!mMultimode) mCurrentView=1;

        loader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb.setVisibility(View.VISIBLE);
                pb.refreshDrawableState();
            }

            @Override
            protected Void doInBackground(Void... params) {
                mInstalledWidgets= getInstalledWidgetsList();
                setInitialSelectedWidgets(mValue);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                SelectItemsList.setAdapter(mAdapter2);
                SelectItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //ggggg

                        if(mInstalledWidgets.get(position).isSelected()) {
                            mInstalledWidgets.get(position).setSelected(false);
                            removeWidgetFromSelected(mInstalledWidgets.get(position).getIndex());
                            mAdapter.notifyDataSetChanged();
                            mAdapter2.notifyDataSetChanged();
                            setSummary();

                        }else{
                            if(mMax!=0){
                                if(mSelectedWidgets.size()>=mMax) {
                                    if(mMultimode)
                                        Toast.makeText(getActivity(),getString(R.string.grxs_max_choices_warning),Toast.LENGTH_SHORT).show();


                                }else addSelectedItem(position);
                            }else addSelectedItem(position);
                        }
                    }
                });
                iniDragAndDropListview();
                updateAddbutton();
                if (mCurrentView==0) showSelectedItems();
                else showSelectableItems();
                vButtonsContainer.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                vtxtprogressbar.setVisibility(View.GONE);
                setSummary();

            }
        }.execute();


        if(!mMultimode) {
            vDeleteButton.setVisibility(View.GONE);
            vAddButton.setVisibility(View.GONE);
            DragListView.setVisibility(View.GONE);
        }

        return view;

    }

    private void removeWidgetFromSelected(int index){
        for(WidgetInfo widgetInfo : mSelectedWidgets){
            if(widgetInfo.getIndex()==index) {
                mSelectedWidgets.remove(widgetInfo);
                break;
            }
        }
    }


    private void clearAllSelectedItems(){
        if(mSelectedWidgets.size()>0){
            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setTitle(getString(R.string.grxs_delete_list));
            ad.setMessage(getString(R.string.grxs_help_delete_all_values));
            ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for(WidgetInfo info : mSelectedWidgets){
                        mInstalledWidgets.get(info.getIndex()).setSelected(false);
                    }
                    mSelectedWidgets.clear();
                    mAdapter.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();
                    setSummary();
                }
            });
            ad.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.grxs_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad.show();
        }
    }



    private void showHelp(){

        AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
        ad.setTitle(getString(R.string.grxs_help));
        ad.setMessage(getString(R.string.grxs_select_sort_help));
        ad.show();
    }


    private void showSelectableItems(){
        mCurrentView=1;
        this.setCancelable(false);
        if(mNegativeButton!=null) mNegativeButton.setText(getString(R.string.grxs_back));
        DragListView.setVisibility(View.GONE);
        vSeparator.setVisibility(View.INVISIBLE);
        SelectItemsList.setVisibility(View.VISIBLE);
        if(mMultimode) vBackButton.setVisibility(View.VISIBLE);
        else vBackButton.setVisibility(View.GONE);
        vAddButton.setVisibility(View.GONE);
        vDeleteButton.setVisibility(View.GONE);
    }


    private void showSelectedItems(){
        mCurrentView=0;
        this.setCancelable(true);
        if(mNegativeButton!=null) mNegativeButton.setText(getString(R.string.grxs_cancel));
        SelectItemsList.setVisibility(View.GONE);
        vBackButton.setVisibility(View.GONE);
        if(!mMultimode) {
            vDeleteButton.setVisibility(View.GONE);
            vAddButton.setVisibility(View.GONE);
            DragListView.setVisibility(View.GONE);
        }else{
            vDeleteButton.setVisibility(View.VISIBLE);
            vAddButton.setVisibility(View.VISIBLE);
            DragListView.setVisibility(View.VISIBLE);
            updateAddbutton();
        }

    }


    private void setSummary(){
         if(!mMultimode) return;
        if(mMax!=0) SelectedItemsInfo = getString(R.string.grxs_num_items_selected,mSelectedWidgets.size())+ "  "+ getString(R.string.grxs_current_max_choices, mMax);
        else SelectedItemsInfo= String.valueOf(mSelectedWidgets.size() )+"/"+String.valueOf(mInstalledWidgets.size())+" "+getString(R.string.grxs_items_selected);
        vTxtSelectedInfo.setText(SelectedItemsInfo);
    }


    private void addSelectedItem(int position){
        if(!mInstalledWidgets.get(position).isSelected()) {
            mInstalledWidgets.get(position).setSelected(true);
            mSelectedWidgets.add(mInstalledWidgets.get(position));
            mAdapter.notifyDataSetChanged();
            mAdapter2.notifyDataSetChanged();
            setSummary();
        }
    }



    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSelectedWidgets.size();
        }

        @Override
        public Object getItem(int position) {
            return mSelectedWidgets.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxmultiplewidgets_item, null);
                cvh.vLabel= (TextView) convertView.findViewById(R.id.gid_label);
                cvh.vDetail= (TextView) convertView.findViewById(R.id.gid_detail);
                cvh.vImage= (ImageView) convertView.findViewById(R.id.gid_image);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            WidgetInfo item = (WidgetInfo) this.getItem(position);
            cvh.vLabel.setText(item.getLabel());
            cvh.vDetail.setText(item.getComponentName().toString());
            cvh.vLabel.setGravity(Gravity.CENTER);
            cvh.vDetail.setGravity(Gravity.CENTER);
            cvh.vImage.setImageDrawable(item.getDrawable());
            return convertView;
        }

        class CustomViewHolder {
            public TextView vLabel;
            public TextView vDetail;
            public ImageView vImage;
        }
    };



    private BaseAdapter mAdapter2 = new BaseAdapter() { //grx
        @Override
        public int getCount() {
            return mInstalledWidgets.size();
        }

        @Override
        public Object getItem(int position) {
            return mInstalledWidgets.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxmultiplewidgets_item, null);
                cvh.vLabel= (TextView) convertView.findViewById(R.id.gid_label);
                cvh.vDetail= (TextView) convertView.findViewById(R.id.gid_detail);
                cvh.vImage= (ImageView) convertView.findViewById(R.id.gid_image);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            WidgetInfo item = (WidgetInfo) this.getItem(position);
            cvh.vLabel.setText(item.getLabel());
            cvh.vDetail.setText(item.getComponentName().toString());
            cvh.vLabel.setGravity(Gravity.CENTER);
            cvh.vDetail.setGravity(Gravity.CENTER);
            cvh.vImage.setImageDrawable(item.getDrawable());

            if(item.isSelected()) {
                convertView.setAlpha((float)0.5);

            }else{
                convertView.setAlpha((float)1);
            }
            return convertView;
        }

        class CustomViewHolder {
            public TextView vLabel;
            public TextView vDetail;
            public ImageView vImage;
        }
    };




    private void iniDragAndDropListview(){

        TypedArray a = getActivity().getTheme().obtainStyledAttributes( new int[] {R.attr.complemnt_accent_color});
        int bgcolor = a.getColor(0,0);
        a.recycle();

        Menu menu = new Menu(true,false);
        menu.addItem(new MenuItem.Builder().setWidth( (int) getResources().getDimension(R.dimen.slv_item_bg_btn_width)*2   )
                .setBackground(new ColorDrawable(bgcolor))
                .setText(getString(R.string.grxs_remove))
                .setTextColor(GrxPrefsUtils.getContrastTextColor(bgcolor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());

        DragListView.setMenu(menu);
        DragListView.setAdapter(mAdapter);
        DragListView.setOnListItemLongClickListener(this);
        DragListView.setOnDragListener(this,mSelectedWidgets);
        DragListView.setOnListItemClickListener(this);
        DragListView.setOnSlideListener(this);
        DragListView.setOnMenuItemClickListener(this);
        DragListView.setOnItemDeleteListener(this);
    }



    private void updateAddbutton(){
        if(true) return;; // for now list selection edit is allowed
        int max;
        if(mMax==0) max=mInstalledWidgets.size();
        else max = mMax;
        if(mSelectedWidgets.size()<max){
            vAddButton.setClickable(true);
            vAddButton.setAlpha((float) 1.0);
        }else{
            vAddButton.setClickable(false);
            vAddButton.setAlpha((float) 0.4);
        }

    }


    @Override
    public void onDragViewStart(int position) {
    }

    @Override
    public void onDragViewMoving(int position) {
    }

    @Override
    public void onDragViewDown(int position) {
    }


    @Override
    public void onListItemClick(View v, final int position) {

    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onListItemLongClick(View view, int position) {
    }


    @Override
    public void onItemDelete(View view, int position) {
        int index = mSelectedWidgets.get(position).getIndex();
        mInstalledWidgets.get(index).setSelected(false);
        mSelectedWidgets.remove(position);
        mAdapter.notifyDataSetChanged();
        mAdapter2.notifyDataSetChanged();
        setSummary();
        updateAddbutton();
    }


    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                return Menu.ITEM_SCROLL_BACK;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                    default:
                        return Menu.ITEM_NOTHING;
                }
        }
        return Menu.ITEM_NOTHING;
    }


    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if((loader!=null) && (loader.getStatus()== AsyncTask.Status.RUNNING)){
            loader.cancel(true);
            loader=null;
            if(mInstalledWidgets!=null) mInstalledWidgets.clear();
            if(mSelectedWidgets!=null) mSelectedWidgets.clear();
        }

    }
    private class WidgetInfo{

        ComponentName mComponentName;
        Drawable mDrawable;
        String mLabel;
        boolean mSelected;
        String mValue;
        int mIndex;

        public WidgetInfo(AppWidgetProviderInfo info){
            ComponentName componentName = info.provider;
            String label = info.loadLabel(getActivity().getPackageManager());
            Drawable drawable = info.loadPreviewImage(getActivity(), getActivity().getResources().getDisplayMetrics().densityDpi);
            if(drawable==null) {
                info.loadIcon(getActivity(), getActivity().getResources().getDisplayMetrics().densityDpi);
            }
            if(drawable==null){
                String packagename = componentName.getPackageName();
                drawable = GrxPrefsUtils.getApplicationIcon(getActivity(),packagename);
            }
            mComponentName = componentName;
            mValue = mComponentName.getPackageName()+"/"+mComponentName.getClassName();
            mDrawable=drawable;
            mLabel=label;
            mSelected=false;

        }

        public void setSelected(boolean selected) {
            mSelected=selected;
        }

        public boolean checkifmatch(String value){
            if(mValue.equals(value)) return true;
            else return false;
        }


        public void setIndex(int index) {mIndex=index;}

        public boolean isSelected(){
            return mSelected;
        }

        public Drawable getDrawable(){
            return mDrawable;
        }

        public String getLabel(){
            return mLabel;
        }

        public String getValue() {return mValue; }

        public int getIndex(){return mIndex;}

        public ComponentName getComponentName(){
            return mComponentName;
        }

    }


}
