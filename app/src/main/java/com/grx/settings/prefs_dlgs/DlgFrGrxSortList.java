
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class DlgFrGrxSortList extends DialogFragment implements SlideAndDragListView.OnDragListener, SlideAndDragListView.OnListItemLongClickListener {

    private OnSortedList mCallback;
    private String mTitle;
    private String mValue;
    private String mSeparator;
    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;
    private boolean mShowSortIcon;

    private int mIconsTintColor;

    private ArrayList<GrxInfoItem> mItemList;
    private SlideAndDragListView mDragList;
    private float tam_txt;
    private int minheight;
    private String mHelperFragment;
    private String mKey;

    public DlgFrGrxSortList(){}


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

    public interface OnSortedList {
        void saveSortedList(String mValue);
    }

    public static DlgFrGrxSortList newInstance(OnSortedList callback, String HelperFragment, String key, String title, String value, String separator,
                                               int id_array_options, int id_array_values, int id_array_icons, int iconstintcolor, boolean show_icon_sort ){
        DlgFrGrxSortList ret = new DlgFrGrxSortList();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putString("sep",separator);
        bundle.putInt("opt_arr_id",id_array_options);
        bundle.putInt("val_array_id",id_array_values);
        bundle.putInt("icons_tintcolor", iconstintcolor);
        bundle.putInt("icons_array_id", id_array_icons );
        bundle.putBoolean("show_sort_icon",show_icon_sort);
        ret.setArguments(bundle);
        ret.saveCallback(callback);
        return ret;

    }


    private void saveCallback(OnSortedList callback){
        mCallback=callback;
    }


    private View getSortListView(){
        View view = getActivity().getLayoutInflater().inflate(R.layout.dlg_grxsortlist, null);
        mDragList = (SlideAndDragListView) view.findViewById(R.id.gid_slv_listview);
        TextView vTxthelp = (TextView) view.findViewById(R.id.gid_help_sort_button);
        vTxthelp.setText(R.string.grxs_help_sort_long_press);
        tam_txt = getResources().getDimension(R.dimen.textsize_listas_opciones);
        minheight = getResources().getDimensionPixelSize(R.dimen.view_minheight_listas_opciones);
        mDragList.setVerticalScrollBarEnabled(true);
        mDragList.setDividerHeight(Common.cDividerHeight);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue= getReturnValue();
        outState.putString("val", mValue);
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

        mHelperFragment=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey=getArguments().getString("key");
        mTitle=getArguments().getString("tit");
        mValue=getArguments().getString("val");
        mSeparator = getArguments().getString("sep");
        mIdOptionsArr = getArguments().getInt("opt_arr_id");
        mIdValuesArr = getArguments().getInt("val_array_id");
        mIdIconsArray = getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mShowSortIcon = getArguments().getBoolean("show_sort_icon");

        if (state != null) {
            mValue=state.getString("val");
            if(mCallback==null){
                if(mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)){
                    GrxPreferenceScreen prefsScreen =(GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                    mCallback=(OnSortedList) prefsScreen.findAndGetCallBack(mKey);
                }else mCallback=(OnSortedList) getFragmentManager().findFragmentByTag(mHelperFragment);
            }
        }

        if(mItemList!=null) mItemList.clear();
        else mItemList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setView(getSortListView());
        builder.setNegativeButton(R.string.grxs_cancel,null);
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mCallback != null) mCallback.saveSortedList(getReturnValue());
                mItemList.clear();
                dismiss();
            }
        });

        initItemsList();
        ini_drag_list();
        return builder.create();
    }


    private void initItemsList(){
        TypedArray icons_array=null;
        String vals_array[] = getResources().getStringArray(mIdValuesArr);
        String opt_array[] = getResources().getStringArray(mIdOptionsArr);
        if(mIdIconsArray!=0){
            icons_array = getResources().obtainTypedArray(mIdIconsArray);
        }

        String values[];
        if(mValue==null || mValue.isEmpty()) {
            values=vals_array;
            //bbbb
        }
        else values=mValue.split(Pattern.quote(mSeparator));;

        mItemList.clear();
        for(int i=0;i<values.length;i++){
            int pos = GrxPrefsUtils.getPositionInStringArray(vals_array,values[i]);
            Drawable drawable=null;
            if(icons_array!=null) drawable = icons_array.getDrawable(pos);
            mItemList.add(
                    new GrxInfoItem(opt_array[pos], vals_array[pos],drawable)
            );
        }
        if(icons_array!=null) icons_array.recycle();
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
    public void onListItemLongClick(View view, int position) {
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
        mDragList.setAdapter(mAdapter);
        mDragList.setOnDragListener(this,mItemList);
        mDragList.setOnListItemLongClickListener(this);
        //mAdapter.notifyDataSetChanged();
    }


    private String getReturnValue(){
        String tmp="";
        for(int i=0;i<mItemList.size();i++){
            tmp+=mItemList.get(i).getValue();
            tmp+=mSeparator;
        }
        return tmp;
    }


    private class GrxInfoItem {

        private String text;
        private String value;
        private Drawable Icono;

        public GrxInfoItem(String texto, String valor, Drawable icono){
            text =texto;
            value =valor;
            Icono=icono;
        }
        public String getTexto(){
            return text;
        }

        public String getValue(){
            return value;
        }

        public Drawable getIcono(){
            return Icono;
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxsortlist_item, null);
                cvh.vCtxt = (TextView) convertView.findViewById(R.id.gid_text);
                cvh.vIcon =(ImageView)   convertView.findViewById(R.id.gid_icon);
                cvh.vIcon2 =(ImageView)   convertView.findViewById(R.id.gid_icon2);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            GrxInfoItem grxInfoItem = (GrxInfoItem) this.getItem(position);
            cvh.vCtxt.setText(grxInfoItem.getTexto());
            cvh.vCtxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,tam_txt);
            Drawable ic = grxInfoItem.getIcono();
            if(ic!=null){
                cvh.vIcon2.setImageDrawable(ic);
                if(mShowSortIcon) cvh.vIcon.setImageResource(R.drawable.ic_grabber);
                else cvh.vIcon.setImageDrawable(ic);
            }else{
                if(mShowSortIcon){
                    cvh.vIcon.setImageResource(R.drawable.ic_grabber);
                    cvh.vIcon2.setImageResource(R.drawable.ic_grabber);

                }else{
                    cvh.vIcon.setVisibility(View.GONE);
                    cvh.vIcon2.setVisibility(View.GONE);
                }
            }
            if(ic!=null && mIconsTintColor!=0){

                if(!mShowSortIcon) cvh.vIcon.setColorFilter(mIconsTintColor);
                cvh.vIcon2.setColorFilter(mIconsTintColor);
           }

           // convertView.setMinimumHeight(minheight);
            return convertView;
        }

        class CustomViewHolder {
            public TextView vCtxt;
            public ImageView vIcon;
            public ImageView vIcon2;
        }
    };

}
