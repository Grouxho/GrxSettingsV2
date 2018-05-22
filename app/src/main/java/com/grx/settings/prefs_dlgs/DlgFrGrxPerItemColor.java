
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.prefs_dlgs;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.qfcolorpicker.CircleColorDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class DlgFrGrxPerItemColor extends DialogFragment
        implements AdapterView.OnItemClickListener, DlgFrGrxColorPicker.OnGrxColorPickerListener, AdapterView.OnItemLongClickListener  {

    private int mIdOptionsArr;
    private int mIdValuesArr;
    private int mIdIconsArray;
    private int mIdColorsArray;
    private int mDefColor;

    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;
    private int mIconsTintColor;
    private Qadapter mAdapter;

    private int mIdItemClicked;

   ArrayList<GrxItemInfo> mItemsInfo;

    ListView vList;

    private Runnable RDobleClick;
    private Handler handler;
    private boolean doble_clic_pendiente;
    private Long timeout;
    private int clicks;


    private DlgFrGrxPerItemColor.GrxItemsColorsListener mCallBack;

    public DlgFrGrxPerItemColor(){}

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


    public interface GrxItemsColorsListener{
        void onItemsColorsSelected(String value);
    }


    private void saveCallback(DlgFrGrxPerItemColor.GrxItemsColorsListener callback){
        mCallBack =callback;
    }



    private void checkCallback(){
        if(mCallBack==null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxPerItemColor.GrxItemsColorsListener) prefsScreen.findAndGetCallBack(mKey);
            }else mCallBack=(DlgFrGrxPerItemColor.GrxItemsColorsListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }



    public static DlgFrGrxPerItemColor newInstance(
            DlgFrGrxPerItemColor.GrxItemsColorsListener callback, String HelperFragment,
            String key, String title, String value,
            int id_array_options, int id_array_values, int id_array_icons, int iconstintcolor, int id_array_colors,
            int defcolor, String separtor
    ){


        DlgFrGrxPerItemColor ret = new DlgFrGrxPerItemColor();
        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY,HelperFragment);
        bundle.putString("key",key);
        bundle.putString("tit",title);
        bundle.putString("val",value);
        bundle.putInt("opt_arr_id",id_array_options);
        bundle.putInt("val_array_id",id_array_values);
        bundle.putInt("icons_array_id", id_array_icons );
        bundle.putInt("icons_tintcolor", iconstintcolor);
        bundle.putInt("colors_array_id", id_array_colors);
        bundle.putInt("def_color", defcolor);
        bundle.putString("separator", separtor);
        ret.setArguments(bundle);
        ret.saveCallback(callback);
        return ret;
    }


    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
            if(mItemsInfo!=null) mItemsInfo.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue= getResultFromItemList();
        outState.putString("curr_val", mValue);
        outState.putInt("clicked_id",mIdItemClicked);

    }

    private String getResultFromItemList(){
        String resultado ="";
        for(int ind=0;ind<mItemsInfo.size();ind++) {
            GrxItemInfo item = (GrxItemInfo) mItemsInfo.get(ind);
            resultado += item.get_value();
            resultado += mSeparator;
        }
        return resultado;
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {

        mHelperFragment=getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey=getArguments().getString("key");
        mTitle=getArguments().getString("tit");
        mOriValue=getArguments().getString("val");
        mValue=mOriValue;
        mIdOptionsArr= getArguments().getInt("opt_arr_id");
        mIdValuesArr=getArguments().getInt("val_array_id");
        mIdIconsArray=getArguments().getInt("icons_array_id");
        mIconsTintColor = getArguments().getInt("icons_tintcolor");
        mIdColorsArray = getArguments().getInt("colors_array_id");
        mDefColor = getArguments().getInt("def_color",0xffffffff);
        mSeparator=getArguments().getString("separator");

        mItemsInfo = new ArrayList<GrxItemInfo>();

        mIdItemClicked=-1;

        if (state != null) {
            mValue =  state.getString("curr_val");
            mIdItemClicked = state.getInt("clicked_id");
        }

        initItemsList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        vList = new ListView(getActivity());
        vList.setFocusable(false);
        vList.setFadingEdgeLength(2);
        vList.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.view_minheight_listas_opciones));
        vList.setPadding(8,8,8,8);
        vList.setDividerHeight(Common.cDividerHeight);
        vList.setOnItemClickListener(this);
        vList.setOnItemLongClickListener(this);

        mAdapter = new Qadapter(mItemsInfo);
        vList.setAdapter(mAdapter);
        builder.setView(vList);
        builder.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mValue= getResultFromItemList();
                checkCallback();
                if (mCallBack == null) dismiss();
                mCallBack.onItemsColorsSelected(mValue);
                dismiss();
            }
        });

        builder.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setNeutralButton("?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
                dlg.setTitle(getActivity().getResources().getString(R.string.grxs_help));
                dlg.setMessage(getActivity().getResources().getString(R.string.grxs_help_items_colors));
                dlg.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dlg.show();
            }
        });

         AlertDialog ad = builder.create();
        return ad;

    }


    private void setUpDoubleClick(){
        handler = new Handler();
        timeout = Long.valueOf(ViewConfiguration.getDoubleTapTimeout());
        doble_clic_pendiente=false;

        RDobleClick = new Runnable() {
            @Override
            public void run() {
                if(!doble_clic_pendiente){
                    clickAction();
                }else {
                    if(clicks==0) clickAction();
                    else if(clicks!=2) {
                        clickAction();
                    }else {
                        doubleClickAction();
                    }

                }
            }
        };
    }


    private void clickAction(){
        clicks=0;
        doble_clic_pendiente=false;
        handler.removeCallbacks(RDobleClick);
        DlgFrGrxColorPicker dlgFrGrxColorPicker =  (DlgFrGrxColorPicker) getFragmentManager().findFragmentByTag(Common.TAG_DLGFRGRXCOLORPICKER);
        if (dlgFrGrxColorPicker==null){
            int color = mDefColor;
            if(mIdItemClicked!=-1){
                color = mItemsInfo.get(mIdItemClicked).getColor();
            }
            dlgFrGrxColorPicker= DlgFrGrxColorPicker.newInstance(this, Common.TAG_DLGFRGRITEMSCOLORS, mTitle,mKey,color,Common.getColorPickerStyleIndex(Common.userColorPickerStyle)/*false*/,
                    true,true, false); //
            dlgFrGrxColorPicker.show(getFragmentManager(),Common.TAG_DLGFRGRXCOLORPICKER);
        }
    }

    private void doubleClickAction(){
        clicks=0;
        doble_clic_pendiente=false;
        handler.removeCallbacks(RDobleClick);
        resetItemColor();
    }

    private void resetItemColor(){

        GrxItemInfo grxItemInfo = mItemsInfo.get(mIdItemClicked);
        int def = grxItemInfo.getDefColor();
        int col = grxItemInfo.getColor();
        if(def==col) return;
        AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
        dlg.setTitle(getActivity().getResources().getString(R.string.grxs_reset_values));
        dlg.setMessage(getActivity().getResources().getString(R.string.grxs_reset_message));
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItemsInfo.get(mIdItemClicked).setColor(mItemsInfo.get(mIdItemClicked).getDefColor());
                mAdapter.notifyDataSetChanged();
                mIdItemClicked=-1;
            }
        });
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mIdItemClicked=-1;
            }
        });
        dlg.show();
    }


    @Override
    public void onGrxColorSet(int color){
        if(mIdItemClicked==-1) return;
        mItemsInfo.get(mIdItemClicked).setColor(color);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(handler==null) setUpDoubleClick();
        if(mIdItemClicked!=position) {
            clicks=0;
            handler.removeCallbacks(RDobleClick);
            doble_clic_pendiente = true;
            handler.postDelayed(RDobleClick, timeout);
            mIdItemClicked = position;
            clicks++;
        }else {
            if(RDobleClick==null) clickAction();
            else {
                clicks++;
                if(!doble_clic_pendiente){
                    handler.removeCallbacks(RDobleClick);
                    doble_clic_pendiente=true;
                    handler.postDelayed(RDobleClick,timeout);
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick (AdapterView<?> parent,
                             View view,
                             int position,
                             long id){

        ClipboardManager cbm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("color", (Integer.toHexString(mItemsInfo.get(position).getColor()).toUpperCase()));
        cbm.setPrimaryClip(clip);
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.grxs_copied_clipboard),Toast.LENGTH_LONG).show();
        mIdItemClicked=-1;


        return true;
    }


    private void initItemsList(){

        TypedArray icons_array=null;
        int colors_array[]=null;

        String item[] = new String[2];

        String vals_array[] = getResources().getStringArray(mIdValuesArr);
        String opt_array[] = getResources().getStringArray(mIdOptionsArr);

        if(mIdIconsArray!=0){
            icons_array = getResources().obtainTypedArray(mIdIconsArray);
        }

        if(mIdColorsArray!=0){
            colors_array = getResources().getIntArray(mIdColorsArray);
        }

        for(int i=0;i<vals_array.length;i++){
            mItemsInfo.add(new GrxItemInfo(
                    opt_array[i], vals_array[i],
                    icons_array!=null ? icons_array.getDrawable(i) : null,
                    colors_array!=null ? colors_array[i] : mDefColor,
                    colors_array!=null ? colors_array[i] : mDefColor));
        }

        String[] selected =null;
        if(mValue!=null && !mValue.isEmpty()) {
            selected=mValue.split(Pattern.quote(mSeparator));
        }

        if(selected!=null){
            for(int ii=0; ii<selected.length;ii++){
                item = selected[ii].split(Pattern.quote("/"));
                for(int iii=0;iii<mItemsInfo.size();iii++){
                    if(mItemsInfo.get(iii).get_option_value().equals(item[0])){
                        mItemsInfo.get(iii).setColor(Integer.valueOf(item[1]));
                    }
                }
            }
        }
         if(icons_array!=null) icons_array.recycle();
    }


    private class GrxItemInfo {

        private String mLabel;
        private Drawable mIcon;
        private int mDefaultColor;
        private int mColor;
        private String mOptionValue;
        private String mValue;


        public  GrxItemInfo(String label, String optionvalue, Drawable icon, int defcolor, int color){
            mLabel=label;
            mIcon = icon;
            mDefaultColor=defcolor;
            mOptionValue=optionvalue;
            mColor=color;
            compose_value();
        }

        private void compose_value(){
            mValue = mOptionValue + "/" + String.valueOf(mColor);
        }

        public String getLabel(){
            return mLabel;
        }

        public Drawable getIcon(){
            return mIcon;
        }

        public int getDefColor() {return mDefaultColor;}

        public int getColor() {return  mColor;}

        public void setColor(int color) {
            mColor=color;
            compose_value();
        }

        public String get_value(){ return mValue;}

        public String get_option_value(){return mOptionValue;}

    }



    private class Qadapter extends BaseAdapter {

        List<GrxItemInfo> mList;

        Qadapter(List<GrxItemInfo> qlist) {
            this.mList = qlist;
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dlg_grxperitemcolor_item, null);
                cvh.vIcon = (ImageView) convertView.findViewById(R.id.item_icon);
                cvh.vLabel = (TextView) convertView.findViewById(R.id.item_name);
                cvh.vColor = (ImageView) convertView.findViewById(R.id.gid_item_color);
                cvh.vIconContainer =( LinearLayout) convertView.findViewById(R.id.gid_item_icon_container);
                cvh.vColorContainer = (LinearLayout) convertView.findViewById(R.id.gid_color_container);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            GrxItemInfo item = (GrxItemInfo) this.getItem(position);
            if(item.getIcon()!=null) {
                cvh.vIcon.setImageDrawable(item.getIcon());
                if(mIconsTintColor!=0) cvh.vIcon.setColorFilter(mIconsTintColor);
            }
            else {
                cvh.vIconContainer.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT,4.0f);
                cvh.vColorContainer.setLayoutParams(layoutParams);
            }
            cvh.vLabel.setText(item.getLabel());
            cvh.vColor.setImageDrawable(new CircleColorDrawable(item.getColor()));
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vIcon;
            public TextView vLabel;
            public ImageView vColor;
            public LinearLayout vIconContainer;
            public LinearLayout vColorContainer;
        }
    }



}
