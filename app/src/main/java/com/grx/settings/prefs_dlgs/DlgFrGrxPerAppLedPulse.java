
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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefssupport.GrxAccessInfo;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;
import com.qfcolorpicker.CircleColorDrawable;
import com.sldv.Menu;
import com.sldv.MenuItem;
import com.sldv.SlideAndDragListView;

import java.util.ArrayList;
import java.util.regex.Pattern;



public class DlgFrGrxPerAppLedPulse extends DialogFragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener, DlgFrAppLedPulse.AppLedPulseListener {


    DlgFrGrxPerAppLedPulse.PerAppLedPulseListener mCallBack;

    private String mHelperFragment;
    private String mKey;
    private String mTitle;
    private String mValue;
    private String mOriValue;
    private String mSeparator;
    private int lastposition = 0;
    private int mMaxAllowed;
    private boolean mShowSystemApps;

    public int mNumSelected =0;
    private String mCurrentValue;

    private ArrayList<AppLedPulseInfo> mItemsList;
    private int mIdItemClicked = -1;

    private LinearLayout vHelpButton;
    private LinearLayout vDeleteButton;
    private LinearLayout vAddEditButton;
    private SlideAndDragListView ListDragView;
    TextView vTxtSelectedItems;



    public interface PerAppLedPulseListener {
        void onAppsLedPulseSelected(String apps_selected);
    }


    public DlgFrGrxPerAppLedPulse() {
    }

    public static DlgFrGrxPerAppLedPulse newInstance(DlgFrGrxPerAppLedPulse.PerAppLedPulseListener callback, String HelperFragment, String key, String title, String value,
                                                     String separtor, boolean allapps, int maxnum) {

        DlgFrGrxPerAppLedPulse ret = new DlgFrGrxPerAppLedPulse();

        Bundle bundle = new Bundle();
        bundle.putString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY, HelperFragment);
        bundle.putString("key", key);
        bundle.putString("tit", title);
        bundle.putString("val", value);
        bundle.putString("separator", separtor);
        bundle.putInt("max_items", maxnum);
        bundle.putBoolean("all_apps", allapps);
        ret.setArguments(bundle);
        ret.setCallBack(callback);

        return ret;
    }


    private void setCallBack(DlgFrGrxPerAppLedPulse.PerAppLedPulseListener callback) {
        mCallBack = callback;
    }

    private void checkCallBack() {
        if (mCallBack == null) {
            if (mHelperFragment.equals(Common.TAG_PREFSSCREEN_FRAGMENT)) {
                GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
                if (prefsScreen != null)
                    mCallBack = (DlgFrGrxPerAppLedPulse.PerAppLedPulseListener) prefsScreen.findAndGetCallBack(mKey);
            } else
                mCallBack = (DlgFrGrxPerAppLedPulse.PerAppLedPulseListener) getFragmentManager().findFragmentByTag(mHelperFragment);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mValue= getResultFromItemList();
        outState.putString("curr_val", mValue);
        outState.putInt("clicked_id",mIdItemClicked);
    }


    private int getPositionForPackageName(String packagename){
        int pos = -1;
        for(int i=0;i<mItemsList.size();i++) {
            if(mItemsList.get(i).getPackageName().equals(packagename)) {
                pos=i;
                break;
            }
        }
        return pos;
    }

    public void onAppLedPulseSet(String value){
        AppLedPulseInfo appLedPulseInfo = new AppLedPulseInfo(value, true);
        if(appLedPulseInfo.isAppInstalled()) {
            int pos = getPositionForPackageName(appLedPulseInfo.getPackageName());
            if(pos == -1){
               if(mMaxAllowed==0 || mItemsList.size()<mMaxAllowed) mItemsList.add(appLedPulseInfo);
               else Toast.makeText(getActivity(),getString(R.string.grxs_max_choices_warning),Toast.LENGTH_SHORT);
            }else{
                mItemsList.get(pos).updateThisValue(value);
            }
             mAdapter.notifyDataSetChanged();
            showSummary();
            checkAddItemsButtonState();
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle state) {

        mItemsList=new ArrayList<>();

        mHelperFragment = getArguments().getString(Common.TAG_FRAGMENTHELPER_NAME_EXTRA_KEY);
        mKey = getArguments().getString("key");
        mTitle = getArguments().getString("tit");
        mOriValue = getArguments().getString("val");
        mValue = mOriValue;
        mSeparator = getArguments().getString("separator");
        mMaxAllowed = getArguments().getInt("max_items");
        mShowSystemApps = getArguments().getBoolean("all_apps", false);

        if (state != null) {
            mValue = state.getString("curr_val");
            lastposition = state.getInt("lastpos");
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


        initSelectedItemsList();
        showSummary();
        iniDragAndDropList();
        checkAddItemsButtonState();
        AlertDialog ad = builder.create();
        return ad;

    }




    private View getDialogView(){
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
        vAddEditButton = (LinearLayout) view.findViewById(R.id.gid_item);

        vAddEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewSelection();
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

    private void checkAddItemsButtonState(){
        if(mMaxAllowed==0) {
            vAddEditButton.setClickable(true);
            vAddEditButton.setAlpha((float) 1.0);
            return;
        }

        if(mItemsList!=null){
            if(mItemsList.size()>=mMaxAllowed) {
                vAddEditButton.setClickable(false);
                vAddEditButton.setAlpha((float) 0.3);
            }else{
                vAddEditButton.setClickable(true);
                vAddEditButton.setAlpha((float) 1.0);
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

            mItemsList.remove(position);
            mAdapter.notifyDataSetChanged();
            showSummary();
            checkAddItemsButtonState();

        }

    @Override
    public void onListItemClick(View v, final int position) {
        if (getFragmentManager().findFragmentByTag(Common.TAG_DLGFRAPPLEDPULSE) != null) return;
        mIdItemClicked = position;
        DlgFrAppLedPulse dlg = DlgFrAppLedPulse.newInstance(this, Common.TAG_DLGFRPERAPPAPPLEDPULSE, mKey,mTitle,
                true, true,mShowSystemApps, true,mItemsList.get(position).get_value());
        dlg.show(getFragmentManager(),Common.TAG_DLGFRAPPLEDPULSE);
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_dlgperappledpulse, null);
                cvh.vAppicon= (ImageView) convertView.findViewById(R.id.gid_app_icon);
                cvh.vPulseContainer=(LinearLayout) convertView.findViewById(R.id.gid_pulses_container) ;
                cvh.vLabel = (TextView) convertView.findViewById(R.id.gid_app_label);
                cvh.vPackageName = (TextView) convertView.findViewById(R.id.gid_package_name);
                cvh.vPulseOn = (TextView) convertView.findViewById(R.id.gid_pulse_on);
                cvh.vPulseOff = (TextView) convertView.findViewById(R.id.gid_pulse_off);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            AppLedPulseInfo item = (AppLedPulseInfo) this.getItem(position);
            cvh.vAppicon.setImageDrawable(GrxPrefsUtils.getApplicationIcon(getActivity(),item.getPackageName()));
            cvh.vPackageName.setText(item.getPackageName());
            cvh.vLabel.setText(GrxPrefsUtils.getApplicationLabel(getActivity(),item.getPackageName()));
            cvh.vPulseContainer.setBackgroundColor(item.getColor());
            int textcolor = GrxPrefsUtils.getContrastTextColor(item.getColor());
            cvh.vPulseOff.setTextColor(textcolor);
            cvh.vPulseOff.setText(item.getToff());
            cvh.vPulseOn.setTextColor(textcolor);
            cvh.vPulseOn.setText(item.getTon());
            return convertView;
        }

        class CustomViewHolder {
            public ImageView vAppicon;
            public LinearLayout vPulseContainer;
            public TextView vLabel, vPackageName, vPulseOn, vPulseOff;
        }
    };



    private void showSummary(){

        if(mMaxAllowed!=0) vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mItemsList.size())+ "  "+ getString(R.string.grxs_current_max_choices,mMaxAllowed));
        else vTxtSelectedItems.setText( getString(R.string.grxs_num_items_selected,mItemsList.size()));
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
                    mAdapter.notifyDataSetChanged();
                    showSummary();
                    checkAddItemsButtonState();
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


    private void openNewSelection(){
        if (getFragmentManager().findFragmentByTag(Common.TAG_DLGFRAPPLEDPULSE) != null) return;
        mIdItemClicked = -1;

        DlgFrAppLedPulse dlg = DlgFrAppLedPulse.newInstance(this, Common.TAG_DLGFRPERAPPAPPLEDPULSE, mKey,mTitle,
                true, true,mShowSystemApps, true,"");
        dlg.show(getFragmentManager(),Common.TAG_DLGFRAPPLEDPULSE);


    }

    private void showHelp(){

        AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
        ad.setTitle(getString(R.string.grxs_help));
        ad.setMessage(getString(R.string.grxs_perapppulse_help));
        ad.show();
    }

    private  void setResultAndDoCallback() {

        checkCallBack();
        if (mCallBack == null) return;
        mValue = getResultFromItemList();
        if (mOriValue == null) mOriValue = "";
        if (mValue.equals(mOriValue)) return;
        mCallBack.onAppsLedPulseSelected(mValue);
    }

    private String getResultFromItemList(){
        String result = "";
        for(int i=0;i<mItemsList.size();i++){
            if(mItemsList.get(i).isAppInstalled()) result += mItemsList.get(i).get_value() + mSeparator;
        }

       return result;
    }


    private void initSelectedItemsList(){
        mNumSelected = 0;
        mItemsList.clear();
        if(mValue!=null && !mValue.isEmpty()) {
            String[] array = mValue.split(Pattern.quote(mSeparator));
            if(array!=null){
                for(int i=0;i<array.length;i++){
                    AppLedPulseInfo appLedPulseInfo = new AppLedPulseInfo(array[i],false);
                    if(appLedPulseInfo.isAppInstalled()) mItemsList.add(appLedPulseInfo);
                }
            }
        }
    }




    private class AppLedPulseInfo {
        private boolean isInstalled=false;
        private String mLabel;
        private String mPackageName;
        private Drawable mIcon;
        private int mColor;
        private String mTon, mToff;
        private boolean mWarn=false;

        private String mValue=null;


        public  AppLedPulseInfo(String value, boolean warn){
            mWarn=warn;
            updateThisValue(value);

        }

        private void updateThisValue(String value){
             isInstalled=false;
            if(value!=null && !value.isEmpty()){
                mValue=value;
                String[] array = value.split(Pattern.quote(";"));
                if(array!=null && array.length==4){
                    mPackageName=array[0];
                    if(GrxPrefsUtils.isPackageActivityInstalled(getActivity(), mPackageName)){
                        isInstalled=true;
                        mColor= Color.parseColor(array[1]);
                        mTon=array[2];
                        mToff=array[3];

                        if(mTon!=null & mTon.equals("1")) {
                            mTon = getString(R.string.grxs_pulse_always_on);
                            mToff = "--";
                        }else {
                            String[] array_values = getResources().getStringArray(R.array.grxa_ledpulse_ton_values);
                            int postiion = getPositionInArray(array_values,mTon);
                            mTon = getResources().getStringArray(R.array.grxa_ledpulse_ton_options)[postiion];

                            array_values = getResources().getStringArray(R.array.grxa_ledpulse_toff_values);
                            postiion = getPositionInArray(array_values,mToff);
                            mToff = getResources().getStringArray(R.array.grxa_ledpulse_toff_options)[postiion];

                        }

                        mIcon = GrxPrefsUtils.getApplicationIcon(getActivity(),mPackageName);
                        mLabel=GrxPrefsUtils.getApplicationLabel(getActivity(),mPackageName);

                    }
                }
            }

            if(mWarn && !isInstalled) Toast.makeText(getActivity(), R.string.grxs_bad_pulse_value,Toast.LENGTH_SHORT).show();
        }

        private int getPositionInArray(String[] array, String value){
            int position = 0;
            for(int i=0;i<array.length;i++) {
                if(array[i].equals(value)) {
                    position=i;
                    break;
                }
            }
            return position;
        }

        public String getLabel(){
            return mLabel;
        }

        public Drawable getIcon(){
            return mIcon;
        }

        public int getColor() {return  mColor;}

        public String getPackageName(){return mPackageName;}

        public String getTon(){return mTon;}

        public String getToff(){return mToff;}

        public String get_value(){ return mValue;}

        public void updateValues(String value){}

        public boolean isAppInstalled(){return isInstalled;}

    }


}
