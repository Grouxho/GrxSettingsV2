
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
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grx.settings.prefssupport.GrxAccessInfo;
import com.grx.settings.utils.GrxPrefsUtils;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class DlgFrGrxMultiAccess extends DialogFragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener, DlgFrGrxAccess.GrxAccesListener {


    private ArrayList<GrxAccessInfo> mItemsList;
    private SlideAndDragListView ListDragView;

    private LinearLayout vDeleteButton;

    private LinearLayout vOpenAccessDialogButton;



    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;

    private int mIconsTintColor;
    private boolean mShowShortCuts;
    private boolean mShowApplications;
    private boolean mShowActivities;
    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private DlgFrGrxMultiAccess.GrxMultiAccessListener mCallBack;
    private boolean mSaveCustomActionsIcons;
    private String mOriValue;
    private String mSeparator;
    private int mMaxNumOfAccesses;
    private LinearLayout vHelpButton;

    private boolean mDeleteTmpFileOnDismiss=true;
    private int mIdItemClicked = -1;


    TextView vTxtSelectedItems;


    public interface GrxMultiAccessListener{
        void GrxSetMultiAccess(String value);
    }

    public DlgFrGrxMultiAccess(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        mDeleteTmpFileOnDismiss = true;
        super.onResume();
    }


    private void save_callback(DlgFrGrxMultiAccess.GrxMultiAccessListener callback){
        mCallBack =callback;
    }

    public static DlgFrGrxMultiAccess newInstance(DlgFrGrxMultiAccess.GrxMultiAccessListener callback,String HelperFragment, String key, String title, String value,
                                                  boolean show_shortcuts, boolean show_apps, boolean show_activities,
                                                  int id_array_options, int id_array_values, int id_array_icons, int iconstintcolor, boolean save_icons, String separtor, int maxitems
    ){


        DlgFrGrxMultiAccess ret = new DlgFrGrxMultiAccess();
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
        bundle.putString("separator", separtor);
        bundle.putInt("max_items", maxitems);
        ret.setArguments(bundle);
        ret.save_callback(callback);
        return ret;

    }


    /************  DIALOG, VIEW, INSTANCE ************************/

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
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxMultiAccess.GrxMultiAccessListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxMultiAccess.GrxMultiAccessListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mDeleteTmpFileOnDismiss=false;
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
        mShowShortCuts = getArguments().getBoolean("show_shortcuts");
        mShowApplications=getArguments().getBoolean("show_aps");
        mShowActivities=getArguments().getBoolean("show_activities");
        mIdOptionsArr= getArguments().getInt("opt_arr_id");
        mIdValuesArr=getArguments().getInt("val_array_id");
        mIdIconsArray=getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mSaveCustomActionsIcons=getArguments().getBoolean("save_icons");
        mSeparator=getArguments().getString("separator");
        mMaxNumOfAccesses = getArguments().getInt("max_items");


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
                mDeleteTmpFileOnDismiss=true;
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultAndDoCallback();
                mDeleteTmpFileOnDismiss=true;
            }
        });

        mItemsList = new ArrayList<>();
        iniAccessesList(mValue);
        showSummary();
        iniDragAndDropList();
        checkAddItemsButtonState();
        AlertDialog ad = builder.create();

        return ad;

    }

    private void showHelp(){

        AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
        ad.setTitle(getString(R.string.grxs_help));
        ad.setMessage(getString(R.string.grxs_select_sort_help));
        ad.show();
    }


    private void checkAddItemsButtonState(){
        if(mMaxNumOfAccesses==0) {
            vOpenAccessDialogButton.setClickable(true);
            vOpenAccessDialogButton.setAlpha((float) 1.0);
            return;
        }

        if(mItemsList!=null){
            if(mItemsList.size()>=mMaxNumOfAccesses) {
                vOpenAccessDialogButton.setClickable(false);
                vOpenAccessDialogButton.setAlpha((float) 0.3);
            }else{
                vOpenAccessDialogButton.setClickable(true);
                vOpenAccessDialogButton.setAlpha((float) 1.0);
            }
        }
    }

    private  View getDialogView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxmultiaccess, null);
        vHelpButton = (LinearLayout) view.findViewById(R.id.gid_help_button);
        vHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelp();
            }
        });
        ListDragView = (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);

        vTxtSelectedItems = (TextView) view.findViewById(R.id.gid_items_selected);
        vOpenAccessDialogButton = (LinearLayout) view.findViewById(R.id.gid_item);

        vOpenAccessDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectAccessDialog();
            }
        });

        vDeleteButton = (LinearLayout) view.findViewById(R.id.gid_delete_button);
        vDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllItems();
            }
        });

        ListDragView.setDividerHeight(Common.cDividerHeight);

        return view;
    }

    private void openSelectAccessDialog(){
        if (getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRACCESS) != null) return;
        mIdItemClicked = -1;
        DlgFrGrxAccess dlg = DlgFrGrxAccess.newInstance(this, Common.TAG_DLGFRGRMULTIACCESS, mKey,getString(R.string.grxs_add_new_item),"",
                mShowShortCuts,mShowApplications,mShowActivities,
                mIdOptionsArr, mIdValuesArr, mIdIconsArray, mIconsTintColor,mSaveCustomActionsIcons,
                true);
        dlg.show(getFragmentManager(),Common.TAG_DLGFRGRACCESS);
    }


    private void showSummary(){

        if(mMaxNumOfAccesses!=0) vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mItemsList.size())+ "  "+ getString(R.string.grxs_current_max_choices,mMaxNumOfAccesses));
        else vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mItemsList.size()));
    }

    /************************* LIST ***************************/


    private String getResultFromItemList(){
        String result ="";
        for(int ind = 0; ind< mItemsList.size();ind ++){
            result+=mItemsList.get(ind).get_uri();
            result+=mSeparator;
        }

        return result;
    }


    private void iniAccessesList(String value){
        mItemsList.clear();
        if(mValue==null || mValue.isEmpty()) {
            return;
        }
        String[] array = mValue.split(Pattern.quote(mSeparator));
        if(array!=null) {
            for (int i= 0; i<array.length; i++){
                mItemsList.add(new GrxAccessInfo(array[i],this.getActivity()));
            }
        }
    }

    private void iniDragAndDropList(){


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

        ListDragView.setMenu(menu);
        ListDragView.setAdapter(mAdapter);
        ListDragView.setOnListItemLongClickListener(this);
        ListDragView.setOnDragListener(this,mItemsList);
        ListDragView.setOnListItemClickListener(this);
        ListDragView.setOnSlideListener(this);
        ListDragView.setOnMenuItemClickListener(this);
        ListDragView.setOnItemDeleteListener(this);

        ListDragView.setDividerHeight(Common.cDividerHeight);
    }



    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mItemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemsList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxmultiaccess_multioption_item, null);
                cvh.vImgGrabber= (ImageView) convertView.findViewById(R.id.gid_icon);
                cvh.vTxt = (TextView) convertView.findViewById(R.id.gid_text);
                cvh.vIcono = (ImageView) convertView.findViewById(R.id.gid_icon2);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            GrxAccessInfo item = (GrxAccessInfo) this.getItem(position);

            cvh.vTxt.setText(item.get_label());
            cvh.vIcono.setImageDrawable(item.get_icon_drawable());
            if(item.get_access_type()==Common.ID_ACCESS_CUSTOM) cvh.vIcono.setColorFilter(mIconsTintColor);
            cvh.vImgGrabber.setImageDrawable(getResources().getDrawable(R.drawable.ic_grabber));
            //if(mItemsList.size()<2) cvh.vImgGrabber.setVisibility(View.INVISIBLE);

            return convertView;
        }

        class CustomViewHolder {
            public ImageView vImgGrabber;
            public TextView vTxt;
            public ImageView vIcono;
        }
    };


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
        if (getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRACCESS) != null) return;
        mIdItemClicked = position;
        DlgFrGrxAccess dlg = DlgFrGrxAccess.newInstance(this, Common.TAG_DLGFRGRMULTIACCESS, mKey,getString(R.string.grxs_update_item),mItemsList.get(position).get_uri(),
                mShowShortCuts,mShowApplications,mShowActivities,
                mIdOptionsArr, mIdValuesArr, mIdIconsArray, mIconsTintColor, mSaveCustomActionsIcons,
                true);
        dlg.show(getFragmentManager(),Common.TAG_DLGFRGRACCESS);
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
        mItemsList.remove(position);
        updateChanges();

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

    private void updateChanges(){
        mAdapter.notifyDataSetChanged();
        showSummary();
        checkAddItemsButtonState();

    }

    private void deleteAllItems(){
        if(mItemsList.size()>0){
            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setTitle(getString(R.string.grxs_delete_list));
            ad.setMessage(getString(R.string.grxs_help_delete_all_values));
            ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mItemsList.clear();
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

    private  void setResultAndDoCallback(){

        check_callback();
        if(mCallBack==null ) return;

        mValue= getResultFromItemList();
        if(mOriValue==null) mOriValue="";
        if(mValue.equals(mOriValue)) return;


        List<String> ori_icon_files_to_delete = new ArrayList<String>();


        String[] arr_ori_uris = mOriValue.split(Pattern.quote(mSeparator));

        /* list of original icons file names ***/

        if(arr_ori_uris!=null) {
            for(int i=0; i<arr_ori_uris.length;i++) {
                String tmp = GrxPrefsUtils.getFilenameFromGrxUriString(arr_ori_uris[i]);
                if(tmp!=null) ori_icon_files_to_delete.add(tmp);
            }
        }



        /*** detect original icons to delete. Copy valid tmp icons. Replace tmp icon names in uris for the callback result ***/

        int size = mItemsList.size();
        for(int ind =0;ind<size;ind++){
            String icon_name = mItemsList.get(ind).get_icon_path();
            if(icon_name!=null){
                if(ori_icon_files_to_delete.contains(icon_name)) ori_icon_files_to_delete.remove(icon_name); //keep this file
                if(icon_name.contains(Common.TMP_PREFIX)) {
                    String shortname = GrxPrefsUtils.getShortFileNameFromString(icon_name);
                    if(shortname!=null) { //just in case the dialog access makes some mistake..
                        String new_icon_name = Common.IconsDir + File.separator + GrxPrefsUtils.getShortFileNameFromString(icon_name).replace(Common.TMP_PREFIX,"");
                        GrxPrefsUtils.fileCopyFromTo(icon_name, new_icon_name); // copy tmp to final icon
                        String new_uri = GrxPrefsUtils.changeExtraValueInUriString(mItemsList.get(ind).get_uri(),Common.EXTRA_URI_ICON,new_icon_name); //new uri value with final icon name
                        mItemsList.get(ind).update_uri(new_uri); //update list value
                    }

                }
            }
        }

        mValue= getResultFromItemList(); //updated value with valid icon names

        /*********** delete icons not more used  **/

        for(int i3 = 0;i3<ori_icon_files_to_delete.size();i3++) {
            GrxPrefsUtils.deleteFileFromStringName(ori_icon_files_to_delete.get(i3));
        }

        mCallBack.GrxSetMultiAccess(mValue);

    }


    @Override
    public void GrxSetAccess(String value){

        if(mIdItemClicked==-1) mItemsList.add(new GrxAccessInfo(value,getActivity()));
        else mItemsList.get(mIdItemClicked).ini_access_info(value,getActivity());
        updateChanges();
    }


    private void deleteTmpFiles(){
        File dir = new File(Common.CacheDir);
        if(dir.exists()&&dir.isDirectory()){
            File ficheros[]=dir.listFiles();
            if(ficheros.length!=0){
                for(int ind=0;ind<ficheros.length;ind++){
                    if(ficheros[ind].getName().contains(Common.TMP_PREFIX)) ficheros[ind].delete();
                    }

            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if(mDeleteTmpFileOnDismiss && isAdded()) {
            deleteTmpFiles();
        }
    }

}
