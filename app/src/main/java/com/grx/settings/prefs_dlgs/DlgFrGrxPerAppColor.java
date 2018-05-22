
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

package com.grx.settings.prefs_dlgs;

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qfcolorpicker.CircleColorDrawable;
import com.grx.settings.prefssupport.GrxAppInfo;
import com.grx.settings.utils.GrxPrefsUtils;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;


public class DlgFrGrxPerAppColor extends DialogFragment implements
        DlgGrxAppSelection.OnGrxAppListener, DlgFrGrxColorPicker.OnGrxColorPickerListener,
        SlideAndDragListView.OnListItemLongClickListener, SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,  SlideAndDragListView.OnItemDeleteListener{

    private OnGrxMultiAppColorListener mCallBack;
    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mOriValue;

    private boolean mShowAllApps;
    private int mMaxNumOfApps;


    private int mDefColor;


    private boolean mAlpha;
    private boolean mAuto;

    private String mSeparator;

    private String mValue;
    private int mIdItemClicked;


    private LinearLayout vHelpButton;
    private LinearLayout vDeleteButton;
    private LinearLayout vAddButton;
    private LinearLayout vSortItemsButton;
    private TextView vTxtSelectedItems;
    private SlideAndDragListView ListDragView;

    private ArrayList<GrxAppInfo> mAppList;


    public interface OnGrxMultiAppColorListener{
       void onGrxMultiAppColorSel(DlgFrGrxPerAppColor dialog, int num, String apps);
    }

    public DlgFrGrxPerAppColor(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private View getDialogView(){

        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxperappcolor, null);
        vHelpButton = (LinearLayout) view.findViewById(R.id.gid_help_button);
        vHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelp();
            }
        });

        vTxtSelectedItems = (TextView) view.findViewById(R.id.gid_items_selected);

        vAddButton = (LinearLayout) view.findViewById(R.id.gid_add_button);
        vAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppSelectionDialog();
            }
        });

        vDeleteButton = (LinearLayout) view.findViewById(R.id.gid_delete_button);
        vDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteItemsDialog();
            }
        });

        vSortItemsButton = (LinearLayout) view.findViewById(R.id.gid_sort_button);
        vSortItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortItemList();
            }
        });

        ListDragView= (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);
        ListDragView.setDividerHeight(Common.cDividerHeight);

        return view;
    }



    private void save_callback(DlgFrGrxPerAppColor.OnGrxMultiAppColorListener callback){
        mCallBack =callback;
    }

    public static DlgFrGrxPerAppColor newInstance(DlgFrGrxPerAppColor.OnGrxMultiAppColorListener callback, String HelperFragment,
                                                  String key, String title, String value,
                                                  boolean show_allapps, int max_apps, int defcolor,
                                                  boolean showalpha, boolean showauto,
                                                  String separator){


        DlgFrGrxPerAppColor ret = new DlgFrGrxPerAppColor();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putBoolean("show_allapps", show_allapps);
        bundle.putInt("max_items", max_apps);
        bundle.putInt("defcolor", defcolor);
        bundle.putBoolean("showalpha",showalpha);
        bundle.putBoolean("showauto",showauto);
        bundle.putString("separator", separator);
        ret.setArguments(bundle);
        ret.save_callback(callback);
        return ret;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue= getResultFromItemList();
        outState.putString("curr_val", mValue);
        outState.putInt("clicked_id",mIdItemClicked);
    }


    @Override
    public Dialog onCreateDialog(Bundle state) {
        mHelperFragment=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey=getArguments().getString("key");
        mTitle=getArguments().getString("tit");
        mOriValue=getArguments().getString("val");
        mValue=mOriValue;
        mShowAllApps = getArguments().getBoolean("show_allapps");
        mMaxNumOfApps = getArguments().getInt("max_items",0);
        mDefColor = getArguments().getInt("defcolor");
        mAlpha = getArguments().getBoolean("showalpha");
        mAuto = getArguments().getBoolean("showauto");
        mSeparator=getArguments().getString("separator");
        mIdItemClicked=-1;

        if (state != null) {
            mValue =  state.getString("curr_val");
            mIdItemClicked = state.getInt("clicked_id");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(getDialogView());
        builder.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultAndDoCallback();
            }
        });

        AlertDialog ad = builder.create();

        mAppList = new ArrayList<>();
        iniAppList(mValue);
        iniDragListView();
        updateChanges();
        refreshAddButtonState();
        return ad;

    }


    private int getColorFromArrayElement(String element){
        String[] array = element.split(Pattern.quote("="));
        if(array==null) return mDefColor;
        else return Integer.valueOf(array[1]);
    }


    private String getPackageOrActivityNameFromArrayElement(String element, int index){
        String[] array;
        String package_activity="";
        array = element.split(Pattern.quote("="));
        package_activity = array[0];
        return package_activity;
    }


    private void iniAppList(String value) {
        mAppList.clear();
        if (!value.isEmpty()) {
            String[] arr = value.split(Pattern.quote(mSeparator));
            for (int ind = 0; ind < arr.length; ind++) {
                int color = getColorFromArrayElement(arr[ind]);
                String package_name = getPackageOrActivityNameFromArrayElement(arr[ind], 0);
                String activity_name = null;
                boolean isInstalled = false;
                if (package_name != null)
                    isInstalled = GrxPrefsUtils.isAppInstalled(getActivity(), package_name);
                if (isInstalled) {
                    String label = GrxPrefsUtils.getApplicationLabel(getActivity(), package_name);
                    Drawable icon = GrxPrefsUtils.getApplicationIcon(getActivity(), package_name);
                    mAppList.add(new GrxAppInfo(package_name, activity_name, label, icon, color));
                }
            }
        }
    }


    private String getResultFromItemList(){
        String resultado ="";
        for(int ind=0;ind<mAppList.size();ind++){
            GrxAppInfo item = (GrxAppInfo) mAppList.get(ind);
            resultado+=item.nombre_app();
            String act = item.nombre_actividad();
            resultado+="=";
            resultado+=String.valueOf(mAppList.get(ind).color_app());
            resultado+=mSeparator;
        }
        return resultado;
    }



    private void iniDragListView(){
        TypedArray a = getActivity().getTheme().obtainStyledAttributes( new int[] {R.attr.complemnt_accent_color});
        int bgcolor = a.getColor(0,0);
        a.recycle();

        Menu menu = new Menu(true,false);
        menu.addItem(new MenuItem.Builder().setWidth( (int) getResources().getDimension(R.dimen.slv_item_bg_btn_width)*2   )
                .setBackground(new ColorDrawable(bgcolor))
                .setText(getString(R.string.grxs_copy_color))
                .setTextColor(GrxPrefsUtils.getContrastTextColor(bgcolor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());
                 menu.addItem(new MenuItem.Builder().setWidth( (int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) *2   )
                      .setBackground(new ColorDrawable(bgcolor))
                      .setText(getString(R.string.grxs_remove))
                      .setTextColor(GrxPrefsUtils.getContrastTextColor(bgcolor))
                      .setDirection(MenuItem.DIRECTION_LEFT)
                      .build());


        ListDragView.setMenu(menu);
        ListDragView.setAdapter(mAdapter);
        ListDragView.setOnListItemLongClickListener(this);
        ListDragView.setOnDragListener(this,mAppList);
        ListDragView.setOnListItemClickListener(this);
        ListDragView.setOnSlideListener(this);
        ListDragView.setOnMenuItemClickListener(this);
        ListDragView.setOnItemDeleteListener(this);
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
        mAppList.remove(position);
        updateChanges();
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {

        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                    default:
                        return Menu.ITEM_NOTHING;
                }



            case MenuItem.DIRECTION_RIGHT:
                saveColorValueToClipboard(itemPosition);
                return Menu.ITEM_SCROLL_BACK;
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onListItemClick(View v, final int position) {
        showColorSelectionDialog(position);
    }


    private boolean IsAppAlreadySelected(String packagename){
        boolean exists=false;
        for(int i = 0; i<mAppList.size(); i++){
            if(packagename.equals(mAppList.get(i).nombre_app())) {
                exists=true;
                break;
            }
        }
        return exists;
    }

    private void refreshAddButtonState() {
        if (mMaxNumOfApps == 0) {
            vAddButton.setClickable(true);
            vAddButton.setAlpha((float) 1.0);
            return;
        }
        if(mAppList!=null){
            if(mAppList.size()>=mMaxNumOfApps) {
                vAddButton.setClickable(false);
                vAddButton.setAlpha((float) 0.5);
            }else{
                vAddButton.setClickable(true);
                vAddButton.setAlpha((float) 1.0);
            }
        }
    }

    @Override
    public void onGrxAppSel(DlgGrxAppSelection dialog, String packagename){
        if(IsAppAlreadySelected(packagename)) {
            Toast.makeText(getActivity(), getString(R.string.grxs_already_selected, ""), Toast.LENGTH_LONG).show();
            return;
        }
        else if (!GrxPrefsUtils.isAppInstalled(getActivity(),packagename)) return;

        String activity_name = GrxPrefsUtils.getMainActivityFromPackageName(getActivity(),packagename); if (!GrxPrefsUtils.isAppInstalled(getActivity(),packagename)) return;
        String label = GrxPrefsUtils.getApplicationLabel(getActivity(),packagename); if (!GrxPrefsUtils.isAppInstalled(getActivity(),packagename)) return;
        Drawable icon = GrxPrefsUtils.getApplicationIcon(getActivity(),packagename);
        mAppList.add(new GrxAppInfo(packagename,activity_name,label,icon,mDefColor));
        updateChanges();
        refreshAddButtonState();
    }

    @Override
    public void onGrxColorSet(int color){
        if(mIdItemClicked==-1) return;
        mAppList.get(mIdItemClicked).pon_color_app(color);
        updateChanges();
    }

    private void showAppSelectionDialog(){
       DlgGrxAppSelection dlg = (DlgGrxAppSelection) getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRSELECTAPP);
        if(dlg==null){
            dlg = DlgGrxAppSelection.newInstance(this, Common.TAG_DLGFRGRMULTIPPCOLOR, mKey,mTitle,mShowAllApps, false, true);
            dlg.show(getFragmentManager(),Common.TAG_DLGFRGRSELECTAPP);
        }
    }

    private void showColorSelectionDialog(int position){

        mIdItemClicked = position;
        DlgFrGrxColorPicker dlgFrGrxColorPicker =  (DlgFrGrxColorPicker) getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRXCOLORPICKER);
        if (dlgFrGrxColorPicker==null){
            int color = mDefColor;
            if(mIdItemClicked!=-1){
                color = mAppList.get(position).color_app();
            }
            dlgFrGrxColorPicker= DlgFrGrxColorPicker.newInstance(this, Common.TAG_DLGFRGRMULTIPPCOLOR, mTitle,mKey,
                    color,Common.getColorPickerStyleIndex(Common.userColorPickerStyle),mAlpha,mAuto,false); //
            dlgFrGrxColorPicker.show(getFragmentManager(),Common.TAG_DLGFRGRXCOLORPICKER);
        }
    }


    private void sortItemList(){
        if(mAppList.size()>1 ){
            try {
                Collections.sort(mAppList, new Comparator<GrxAppInfo>() {
                    @Override
                    public int compare(GrxAppInfo A_appinfo, GrxAppInfo appinfo) {
                        try {
                            return String.CASE_INSENSITIVE_ORDER.compare(A_appinfo.etiqueta_app(), appinfo.etiqueta_app());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
            }catch(Exception e) {
                e.printStackTrace();
            }
            updateChanges();
        }
    }

    private void showDeleteItemsDialog(){
        if(mAppList.size()>0){
            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setTitle(getString(R.string.grxs_delete_list));
            ad.setMessage(getString(R.string.grxs_help_delete_all_values));
            ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAppList.clear();
                    updateChanges();
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
        ad.setMessage(getString(R.string.grxs_multiappcolor_help_color));

        ad.show();
    }

    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxPerAppColor.OnGrxMultiAppColorListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxPerAppColor.OnGrxMultiAppColorListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }

    private void setResultAndDoCallback(){
        checkCallback();
        if (mCallBack != null) mCallBack.onGrxMultiAppColorSel(this,mAppList.size(), getResultFromItemList());
        mAppList.clear();
        this.dismiss();
    }

    private void setInfoText(){
            if(mMaxNumOfApps!=0) vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mAppList.size())+ "  "+ getString(R.string.grxs_current_max_choices,mMaxNumOfApps));
            else vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mAppList.size()));
    }


    private void updateChanges(){
            mAdapter.notifyDataSetChanged();
            setInfoText();
    }

    private void saveColorValueToClipboard(int pos){
        int col = mAppList.get(pos).color_app();
        ClipboardManager cbm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("color", ("#" + Integer.toHexString(col).toUpperCase())            );
        cbm.setPrimaryClip(clip);
        Toast.makeText(getActivity(),getString(R.string.grxs_copied_clipboard),Toast.LENGTH_LONG).show();
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxperappcolor_item_multi, null);
                cvh.vimgLogo = (ImageView) convertView.findViewById(R.id.gid_edit_button);
                cvh.vtxtName = (TextView) convertView.findViewById(R.id.gid_edit_item_text);
                cvh.vtxtpaquete = (TextView) convertView.findViewById(R.id.gid_packagename_text);
                cvh.vcolorapp = (ImageView) convertView.findViewById(R.id.gid_app_color);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            GrxAppInfo grxInfoApp = (GrxAppInfo) this.getItem(position);
            cvh.vtxtName.setText(grxInfoApp.etiqueta_app());
            cvh.vimgLogo.setImageDrawable(grxInfoApp.icono_app());
            cvh.vtxtpaquete.setText(grxInfoApp.nombre_app());
            cvh.vcolorapp.setImageDrawable(new CircleColorDrawable(grxInfoApp.color_app()));


            return convertView;
        }

        class CustomViewHolder {
            public ImageView vimgLogo;
            public TextView vtxtName;
            public TextView vtxtpaquete;
            public ImageView vcolorapp;
        }
    };




}
