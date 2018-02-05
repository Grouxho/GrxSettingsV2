
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
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefssupport.GrxCustomOptionInfo;
import com.grx.settings.utils.GrxPrefsUtils;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;
import com.grx.settings.utils.Common;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class DlgFrSelectSortItems extends DialogFragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {


    private ArrayList<GrxCustomOptionInfo> mSelectedItemsList;
    private ArrayList<GrxCustomOptionInfo> mAvailableItemsList;

    private SlideAndDragListView DragListView;
    private ListView SelectItemsList;

    private LinearLayout vDeleteButton;
    private LinearLayout vBackButton;
    private LinearLayout vAddButton;
    private LinearLayout vHelpButton;
    private LinearLayout vSeparator;

    private int mIconsTintColor;

    TextView vTxtSelectedInfo;
    String SelectedItemsInfo;

    private GrxMultiValueListener mCallBack;
    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;
    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;
    private int mMaxNumOfAccesses;

    private int mCurrentView=0;
    private Button mNegativeButton;

    public interface GrxMultiValueListener{
        void GrxSetMultiValue(String value);
    }

    public DlgFrSelectSortItems(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void save_callback(DlgFrSelectSortItems.GrxMultiValueListener callback){
        mCallBack =callback;
    }

    public static DlgFrSelectSortItems newInstance(DlgFrSelectSortItems.GrxMultiValueListener callback, String HelperFragment, String key, String title, String value,
                                                   int id_array_options, int id_array_values, int id_array_icons, int iconstintcolor,String separtor, int maxitems
    ){


        DlgFrSelectSortItems ret = new DlgFrSelectSortItems();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putInt("opt_arr_id",id_array_options);
        bundle.putInt("val_array_id",id_array_values);
        bundle.putInt("icons_tintcolor", iconstintcolor);
        bundle.putInt("icons_array_id", id_array_icons );
        bundle.putString("separator", separtor);
        bundle.putInt("max_items", maxitems);
        ret.setArguments(bundle);
        ret.save_callback(callback);
        return ret;

    }



    /************  DIALOG, VIEW, INSTANCE ************************/

    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrSelectSortItems.GrxMultiValueListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrSelectSortItems.GrxMultiValueListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }

    private View getDialogView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxmultiplewidgets_grxselectsortitems, null);
        DragListView = (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);
        SelectItemsList=(ListView)view.findViewById(R.id.gid_listview);
        vSeparator=(LinearLayout) view.findViewById(R.id.gid_separator);

        vHelpButton = (LinearLayout) view.findViewById(R.id.gid_help_button);
        vHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelp();
            }
        });
        vTxtSelectedInfo = (TextView) view.findViewById(R.id.gid_items_selected);
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

        SelectItemsList.setAdapter(mAdapter2);

        SelectItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAvailableItemsList.get(position).is_selected()) unSelectItem(position);
                else {
                    if(mMaxNumOfAccesses!=0){
                        if(mSelectedItemsList.size()>=mMaxNumOfAccesses) {
                            if(!mAvailableItemsList.get(position).is_selected()) {
                                Toast.makeText(getActivity(),getString(R.string.grxs_max_choices_warning),Toast.LENGTH_SHORT).show();
                            }

                        }else addSelectedItem(position);
                    }else addSelectedItem(position);
                }
            }
        });
        return view;
    }

    private void unSelectItem(int pos){
        String value = mAvailableItemsList.get(pos).get_value();
        for(int i=0;i<mSelectedItemsList.size();i++){
            if(mSelectedItemsList.get(i).get_value().equals(value)){
                mSelectedItemsList.remove(i);
                break;
            }
        }
        updateAvailableOptions();
        notifyChanges();
    }

    private void addSelectedItem(int position){
        if(!mAvailableItemsList.get(position).is_selected()) {
            mSelectedItemsList.add(new GrxCustomOptionInfo(mAvailableItemsList.get(position).get_title(),mAvailableItemsList.get(position).get_value(),
                    mAvailableItemsList.get(position).get_icon()));
            notifyChanges();
            updateAvailableOptions();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("curr_val", getStringValue());
        outState.putInt("current_view",mCurrentView);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

        mSelectedItemsList = new ArrayList<>();
        mAvailableItemsList = new ArrayList<>();

        mHelperFragment=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey=getArguments().getString("key");
        mTitle=getArguments().getString("tit");
        mOriValue=getArguments().getString("val");
        mValue=mOriValue;
        mIdOptionsArr= getArguments().getInt("opt_arr_id");
        mIdValuesArr=getArguments().getInt("val_array_id");
        mIdIconsArray=getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mSeparator=getArguments().getString("separator");
        mMaxNumOfAccesses = getArguments().getInt("max_items");

        mCurrentView=0;
        if (state != null) {
            mValue =  state.getString("curr_val");
            mCurrentView=state.getInt("current_view");

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(getDialogView());
        builder.setNegativeButton(R.string.grxs_cancel, null);
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultAndDoCallback();
            }
        });

        iniSelectedAndAvailableItems(mValue);
        setSummary();
        updateAddbutton();
        iniDragAndDropListview();
        if (mCurrentView==0) showSelectedItems();
        else showSelectableItems();
        final AlertDialog ad = builder.create();
        ad.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mNegativeButton = ad.getButton(DialogInterface.BUTTON_NEGATIVE);
                if(mCurrentView!=0) mNegativeButton.setText(getString(R.string.grxs_back));
                else mNegativeButton.setText(getString(R.string.grxs_cancel));
                mNegativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCurrentView==0) dismiss();
                        else showSelectedItems();
                    }
                });
            }
        });
        return ad;

    }

    private String getStringValue(){
        String value = "";
        for(int i=0;i<mSelectedItemsList.size();i++){
            value+=mSelectedItemsList.get(i).get_value();
            value+=mSeparator;
        }
        return value;
    }

    private  void setResultAndDoCallback() {
        checkCallback();
        if (mCallBack == null) return;
        mValue = getStringValue();
        if (mOriValue == null) mOriValue = "";
        if (mValue.equals(mOriValue)) dismiss();
        mCallBack.GrxSetMultiValue(mValue);
        mSelectedItemsList.clear();
        mAvailableItemsList.clear();
        this.dismiss();
    }

    private void updateAvailableOptions(){

        for(int iii=0;iii<mAvailableItemsList.size();iii++) mAvailableItemsList.get(iii).set_selected(false);
        for(int ii=0;ii<mSelectedItemsList.size();ii++){
            for(int iii=0;iii<mAvailableItemsList.size();iii++){
                if(mSelectedItemsList.get(ii).get_value().equals(mAvailableItemsList.get(iii).get_value())) mAvailableItemsList.get(iii).set_selected(true);
            }
        }
        mAdapter2.notifyDataSetChanged();
        int no = mAvailableItemsList.size();
        int sel= mSelectedItemsList.size();
    }

    private void iniSelectedAndAvailableItems(String valor){

        mSelectedItemsList.clear();
        mAvailableItemsList.clear();

        TypedArray icons_array=null;
        String vals_array[] = getResources().getStringArray(mIdValuesArr);
        String opt_array[] = getResources().getStringArray(mIdOptionsArr);
        if(mIdIconsArray!=0){
            icons_array = getResources().obtainTypedArray(mIdIconsArray);
        }

        for(int i=0;i<vals_array.length;i++){
            Drawable drwtmp = null;
            if(icons_array!=null) {
                drwtmp = icons_array.getDrawable(i);
            }
            mAvailableItemsList.add(new GrxCustomOptionInfo(opt_array[i], vals_array[i], drwtmp));
        }

        if(icons_array!=null) icons_array.recycle();

        String[] selected =null;

        if(valor!=null && !valor.isEmpty()) selected=valor.split(Pattern.quote(mSeparator));
        if(selected!=null){
            for(int ind=0;ind<selected.length;ind++){
                for(int i=0;i<mAvailableItemsList.size();i++){
                    if(mAvailableItemsList.get(i).get_value().equals(selected[ind])){
                        mSelectedItemsList.add(new GrxCustomOptionInfo(mAvailableItemsList.get(i).get_title(),mAvailableItemsList.get(i).get_value(), mAvailableItemsList.get(i).get_icon()));
                    }
                }
            }
        }

     updateAvailableOptions();
    }


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
        DragListView.setOnDragListener(this,mSelectedItemsList);
        DragListView.setOnListItemClickListener(this);
        DragListView.setOnSlideListener(this);
        DragListView.setOnMenuItemClickListener(this);
        DragListView.setOnItemDeleteListener(this);

    }

    private void showSelectableItems(){
        mCurrentView=1;
        this.setCancelable(false);
        if(mNegativeButton!=null) mNegativeButton.setText(getString(R.string.grxs_back));
        DragListView.setVisibility(View.GONE);
        vSeparator.setVisibility(View.INVISIBLE);
        SelectItemsList.setVisibility(View.VISIBLE);
        vBackButton.setVisibility(View.VISIBLE);
        vAddButton.setVisibility(View.GONE);
        vDeleteButton.setVisibility(View.GONE);
    }

    private void showHelp(){

        AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
        ad.setTitle(getString(R.string.grxs_help));
        ad.setMessage(getString(R.string.grxs_select_sort_help));
        ad.show();
    }

    private void clearAllSelectedItems(){
        if(mSelectedItemsList.size()>0){
            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setTitle(getString(R.string.grxs_delete_list));
            ad.setMessage(getString(R.string.grxs_help_delete_all_values));
            ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSelectedItemsList.clear();
                    notifyChanges();
                    updateAvailableOptions();
                    updateAddbutton();
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


    private void updateAddbutton(){

        if(true ) return; // for now list selection edit is allowed
        int max;
        if(mMaxNumOfAccesses==0) max=mAvailableItemsList.size();
        else max = mMaxNumOfAccesses;
        if(mSelectedItemsList.size()<max){
            vAddButton.setClickable(true);
            vAddButton.setAlpha((float) 1.0);
        }else{
            vAddButton.setClickable(false);
            vAddButton.setAlpha((float) 0.4);
        }

    }

    private void showSelectedItems(){
        mCurrentView=0;
        this.setCancelable(true);
        if(mNegativeButton!=null) mNegativeButton.setText(getString(R.string.grxs_cancel));
        DragListView.setVisibility(View.VISIBLE);
        SelectItemsList.setVisibility(View.GONE);
        vBackButton.setVisibility(View.GONE);
        vAddButton.setVisibility(View.VISIBLE);
        vDeleteButton.setVisibility(View.VISIBLE);
        updateAddbutton();
    }


    private void setSummary(){

        if(mMaxNumOfAccesses!=0) SelectedItemsInfo = getString(R.string.grxs_num_items_selected,mSelectedItemsList.size())+ "  "+ getString(R.string.grxs_current_max_choices, mMaxNumOfAccesses);
        else SelectedItemsInfo= String.valueOf(mSelectedItemsList.size() )+"/"+String.valueOf(mAvailableItemsList.size())+" "+getString(R.string.grxs_items_selected);
        vTxtSelectedInfo.setText(SelectedItemsInfo);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mSelectedItemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSelectedItemsList.get(position);
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

            GrxCustomOptionInfo item = (GrxCustomOptionInfo) this.getItem(position);
            cvh.vTxt.setText(item.get_title());
            cvh.vIcono.setImageDrawable(item.get_icon());
            if(mIconsTintColor!=0) cvh.vIcono.setColorFilter(mIconsTintColor);
            cvh.vImgGrabber.setImageDrawable(getResources().getDrawable(R.drawable.ic_grabber));
            //if(mSelectedItemsList.size()<2) cvh.vImgGrabber.setVisibility(View.INVISIBLE);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vImgGrabber;
            public TextView vTxt;
            public ImageView vIcono;
        }
    };


    private BaseAdapter mAdapter2 = new BaseAdapter() { //grx
        @Override
        public int getCount() {
            return mAvailableItemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAvailableItemsList.get(position);
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

            GrxCustomOptionInfo item = (GrxCustomOptionInfo) this.getItem(position);
            cvh.vTxt.setText(item.get_title());
            cvh.vTxt.setGravity(Gravity.CENTER);
            Drawable icon = item.get_icon();
            if(icon==null) {
                cvh.vIcono.setVisibility(View.GONE);
                cvh.vImgGrabber.setVisibility(View.GONE);
            }
            else {
                cvh.vIcono.setImageDrawable(item.get_icon());
                if(mIconsTintColor!=0) cvh.vIcono.setColorFilter(mIconsTintColor);
                cvh.vImgGrabber.setImageDrawable(item.get_icon());
            }



            //if(mSelectedItemsList.size()<2) cvh.vImgGrabber.setVisibility(View.INVISIBLE);

            if(item.is_selected()) {
                convertView.setAlpha((float)0.5);

            }else{
                convertView.setAlpha((float)1);
            }
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
        mSelectedItemsList.remove(position);
        notifyChanges();
        updateAvailableOptions();
        updateAddbutton();
    }

    private void notifyChanges(){
        mAdapter.notifyDataSetChanged();
        setSummary();
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
        mSelectedItemsList.clear();
        mAvailableItemsList.clear();
    }
}
