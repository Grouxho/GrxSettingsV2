package com.grx.settings.prefs_dlgs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;
import com.root.RootUtils;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.regex.Pattern;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

public class DlgFrGrxPerItemSingleSelection extends DialogFragment
        implements SlideAndDragListView.OnDragListener, SlideAndDragListView.OnListItemLongClickListener, SlideAndDragListView.OnListItemClickListener {

    private PerItemSingleSelectionDialogListener mCallBack = null;

    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;
    private int mIdSpinnerValuesArray;
    private int mIdSpinnerOptionsArray;
    private boolean mShortOutOption;
    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;

    private int mItemClicked = -1;

    private int mIconsTintColor=0;

    private ArrayList<ItemInfo> mItemList;
    private SlideAndDragListView mDragList;

    public DlgFrGrxPerItemSingleSelection(){}



    public interface PerItemSingleSelectionDialogListener{
        void onPerItemSingleSelectionSet(String value);
    }



    private void saveCallback(DlgFrGrxPerItemSingleSelection.PerItemSingleSelectionDialogListener callback){
        mCallBack =callback;
    }

    public static DlgFrGrxPerItemSingleSelection newInstance(DlgFrGrxPerItemSingleSelection.PerItemSingleSelectionDialogListener callback,String HelperFragment, String key, String title, String value,
                                                             int id_array_options, int id_array_values, int id_array_icons, int id_array_spinneroptios, int id_array_spinnervalues,
                                                             int iconstintcolor, String separtor, boolean shortout
    ) {


        DlgFrGrxPerItemSingleSelection ret = new DlgFrGrxPerItemSingleSelection();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY, HelperFragment);
        bundle.putString("key", key);
        bundle.putString("tit", title);
        bundle.putString("val", value);
        bundle.putInt("opt_arr_id",id_array_options);
        bundle.putInt("val_array_id",id_array_values);
        bundle.putInt("icons_array_id", id_array_icons );
        bundle.putInt("icons_tintcolor", iconstintcolor);
        bundle.putString("separator", separtor);
        bundle.putInt("spinner_options_array_id",id_array_spinneroptios);
        bundle.putInt("spinner_values_array_id",id_array_spinnervalues);
        bundle.putBoolean("shortout",shortout);
        ret.setArguments(bundle);
        ret.saveCallback(callback);
        return ret;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue= getReturnValue();
        outState.putString("curr_val", mValue);
    }

    /************  DIALOG, VIEW, INSTANCE ************************/

    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxPerItemSingleSelection.PerItemSingleSelectionDialogListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxPerItemSingleSelection.PerItemSingleSelectionDialogListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle state) {

        mHelperFragment = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey = getArguments().getString("key");
        mTitle = getArguments().getString("tit");
        mOriValue=getArguments().getString("val");
        mValue=mOriValue;
        mIdOptionsArr = getArguments().getInt("opt_arr_id");
        mIdValuesArr = getArguments().getInt("val_array_id");
        mIdIconsArray = getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mSeparator = getArguments().getString("separator");
        mIdSpinnerOptionsArray = getArguments().getInt("spinner_options_array_id");
        mIdSpinnerValuesArray = getArguments().getInt("spinner_values_array_id");
        mShortOutOption = getArguments().getBoolean("shortout");

        if (state != null) {
            mValue = state.getString("curr_val");
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

        initItemsList();
        ini_drag_list();
        AlertDialog ad = builder.create();
        return ad;

    }


    private void setResultAndDoCallback(){
        checkCallback();
        if(mCallBack==null ) return;
        mValue= getReturnValue();
        if(mOriValue==null) mOriValue="";
        if(mValue.equals(mOriValue)) return;
        mCallBack.onPerItemSingleSelectionSet(mValue);

    }
    private View getDialogView(){

        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_peritemsingleselection, null);
        mDragList = (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);

        if(mShortOutOption) {
            view.findViewById(R.id.gid_list_title ).setVisibility(View.VISIBLE);
        }

       return view;
    }


    private String getReturnValue(){
        String value = "";

        for(int i=0; i< mItemList.size();i++){
            value+=mItemList.get(i).getValue();
            value+=mSeparator;
        }
       return value;
    }

    private void initItemsList(){

        mItemList = new ArrayList<>();

        TypedArray icons_array=null;
        String vals_array[] = getResources().getStringArray(mIdValuesArr);
        String opt_array[] = getResources().getStringArray(mIdOptionsArr);
        String spinner_vals[]=getResources().getStringArray(mIdSpinnerValuesArray);

        if(mIdIconsArray!=0){
            icons_array = getResources().obtainTypedArray(mIdIconsArray);
        }

        String values[];
        if(mValue==null || mValue.isEmpty()) {
            mValue=generateDefaultValue(vals_array,spinner_vals);
        }

        values=mValue.split(Pattern.quote(mSeparator));

        for(int i=0;i<values.length;i++){
            String[] array = values[i].split(Pattern.quote(";"));
            int pos = GrxPrefsUtils.getPositionInStringArray(vals_array,array[0]);
            int posspinner = GrxPrefsUtils.getPositionInStringArray(spinner_vals,array[1]);
            Drawable drawable=null;
            if(icons_array!=null) drawable = icons_array.getDrawable(pos);
            mItemList.add(new ItemInfo(values[i],opt_array[pos], vals_array[pos],spinner_vals[posspinner],drawable));
        }
        if(icons_array!=null) icons_array.recycle();

    }


    private void ini_drag_list(){
        Menu menu = new Menu(false,false);
        menu.addItem(new MenuItem.Builder().setWidth( 0 )
                .setBackground(getResources().getDrawable(R.drawable.ic_delete))
                .setText(" ")
                .setTextColor(Color.GRAY)
                .setTextSize(1)
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .build());

        mDragList.setMenu(menu);

        mDragList.setDividerHeight(Common.cDividerHeight);
        mDragList.setVerticalScrollBarEnabled(true);
        mDragList.setAdapter(mAdapter);
        mDragList.setOnDragListener(this,mItemList);
        mDragList.setOnListItemLongClickListener(this);
        mDragList.setOnListItemClickListener(this);
        //mAdapter.notifyDataSetChanged();
    }

    private String generateDefaultValue(String[] vals_array, String[] spinner_values_array){
        String returnval = "";
        for(int i=0;i<vals_array.length;i++){
            returnval+=vals_array[i];
            returnval+=";";
            returnval+=spinner_values_array[0];
            returnval+=mSeparator;
        }

        return returnval;
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
        mItemClicked=position;
        final String curval = mItemList.get(position).getSpinnerValue();
        int selected = GrxPrefsUtils.getPositionInStringArray(getResources().getStringArray(mIdSpinnerValuesArray),mItemList.get(position).getSpinnerValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.grxs_select_option);
        builder.setSingleChoiceItems(getResources().getStringArray(mIdSpinnerOptionsArray),
                selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String new_value = getResources().getStringArray(mIdSpinnerValuesArray)[i];
                if(!curval.equals(new_value)) {
                    mItemList.get(mItemClicked).setSpinnervalue(new_value);
                    mAdapter.notifyDataSetChanged();
                }
                mItemClicked=-1;
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItemClicked=-1;
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItemClicked=-1;
                  dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mItemClicked=-1;
            }
        });
        builder.create().show();

    }


    @Override
    public void onListItemLongClick(View view, int position) {
    }


    private class ItemInfo {
        private String mValue="";
        private String mOptionValue;
        private String mOptionText;
        private String mSpinnerValue;
        private Drawable mDrawable=null;


        public ItemInfo(String value, String optiontext, String optionval, String spinnerval, Drawable drawable){
            if(value==null || value.isEmpty() ) return;
            mValue = value;
            mOptionValue=optionval;
            mOptionText=optiontext;
            mSpinnerValue=spinnerval;
            mDrawable = drawable;


        }
        public String getText(){
            return mOptionText;
        }

        public String getValue(){
            return mValue;
        }

        public String getSpinnerValue(){return mSpinnerValue;}

        public Drawable getmDrawable(){
            return mDrawable;
        }

        public void setSpinnervalue(String value){
            mSpinnerValue=value;
            mValue=mOptionValue+";"+value;
        }
    }


    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_dlg_peritem_single_sel, null);
                cvh.vCtxt = (TextView) convertView.findViewById(R.id.gid_item_text);
                cvh.vIcon =(ImageView)   convertView.findViewById(R.id.gid_item_img);
                cvh.vValue = (TextView) convertView.findViewById(R.id.gid_item_value);

                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            DlgFrGrxPerItemSingleSelection.ItemInfo grxInfoItem = (DlgFrGrxPerItemSingleSelection.ItemInfo) this.getItem(position);
            cvh.vCtxt.setText(grxInfoItem.getText());
             int pos =  GrxPrefsUtils.getPositionInStringArray(getResources().getStringArray(mIdSpinnerValuesArray),
                           grxInfoItem.getSpinnerValue());
            cvh.vValue.setText(getResources().getStringArray(mIdSpinnerOptionsArray)[pos]);

            if(mIdIconsArray==0) cvh.vIcon.setVisibility(View.GONE);
            else {
                cvh.vIcon.setImageDrawable(grxInfoItem.getmDrawable());
                if(mIconsTintColor!=0)cvh.vIcon.setColorFilter(mIconsTintColor);
            }


            // convertView.setMinimumHeight(minheight);
            return convertView;
        }

        class CustomViewHolder {
            public TextView vCtxt;
            public ImageView vIcon;
            public TextView vValue;

        }
    };

    }
