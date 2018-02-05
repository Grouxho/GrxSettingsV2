/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */


package android.preference;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.grx.settings.utils.Common;
import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.act.GrxImagePicker;
import com.grx.settings.utils.GrxImageHelper;

import java.io.File;



public class GrxPickImage extends GrxBasePreference{

    private int mSizeX=0;
    private int mSizeY=0;
    private boolean mCircular=false;
    private boolean mJustUri=true;
    int mIconSize;

    public GrxPickImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }


    public GrxPickImage(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttributes(context,attrs);
    }



    private void initAttributes(Context context, AttributeSet attrs){
        setWidgetLayoutResource(R.layout.widget_icon_accent);
        initStringPrefsCommonAttributes(context,attrs,false, false);
        setDefaultValue(myPrefAttrsInfo.getMyStringDefValue());

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxPreferences);
        mSizeX=ta.getInt(R.styleable.grxPreferences_sizeX,0);
        mSizeY=ta.getInt(R.styleable.grxPreferences_sizeY,0);
        mCircular=ta.getBoolean(R.styleable.grxPreferences_circularImage,false);
        ta.recycle();

        mJustUri = (mSizeX!=0 && mSizeY!=0) ? false : true;
        Resources resources = getContext().getResources();
        mIconSize = resources.getDimensionPixelSize(R.dimen.icon_size_in_prefs);
    }

    @Override
    public void showDialog(){
        if(!myPrefAttrsInfo.isValidKey()) return;
        GrxPreferenceScreen chl = (GrxPreferenceScreen) getOnPreferenceChangeListener();
        if(chl!=null){

            if(mJustUri) {
                Intent intent = new Intent(chl.getActivity(), GrxImagePicker.class);
                intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,myPrefAttrsInfo.getMyKey());
                intent.putExtra(GrxImagePicker.S_URI_MODE,true);
                chl.startImagePicker(intent, Common.REQ_CODE_GALLERY_IMAGE_PICKER_JUST_URI);
            }
            else {
                Intent intent = new Intent(chl.getActivity(), GrxImagePicker.class);
                intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,myPrefAttrsInfo.getMyKey());
                intent = GrxImageHelper.intent_avatar_img(intent, mSizeX, mSizeY,mCircular);
                String output_file_name = Common.IconsDir + File.separator + String.valueOf(System.currentTimeMillis()+".jpg");
                intent.putExtra(GrxImagePicker.S_OUTPUT_FILE_NAME,output_file_name);
                chl.startImagePicker(intent, Common.REQ_CODE_GALLERY_IMAGE_PICKER_CROP_CIRCULAR);
            }
        }
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refreshView();
    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = (View) super.onCreateView(parent);
        vWidgetIcon.setScaleType(ImageView.ScaleType.CENTER);
        vWidgetIcon.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void configStringPreference(String value){
        getImage(mStringValue);
    }


    @Override
    public void resetPreference(){
        setNewImage(myPrefAttrsInfo.getMyStringDefValue());
    }

    public void deletCurrentImgFile(){
          boolean detele_current_img=false;
          if(mStringValue!=null){
              detele_current_img = mStringValue.contains( getContext().getString(R.string.grxs_data_base_folder)+File.separator+getContext().getString(R.string.grxs_data_icons_subfolder) );
          }
          if(detele_current_img) {
              File file = new File(Uri.parse(mStringValue).getPath());
              if(file!=null && file.exists()) file.delete();
          }
    }

    public void setNewImage(String uristr){
        deletCurrentImgFile();
        mStringValue=uristr;
        saveNewStringValue(mStringValue);
        configStringPreference(mStringValue);
    }



    private class ImageLoader extends AsyncTask<Void, Void,Void> {
        @Override
        protected Void doInBackground(Void...params) {
            if(mStringValue!=null && !mStringValue.isEmpty()){
                    mWidgetIcon = GrxImageHelper.get_scaled_drawable_from_uri_string_for_square_container(getContext(),mStringValue,mIconSize);
            }else mWidgetIcon=null;
            return null;
        }
        @Override
        protected void onPostExecute(Void v){
                refreshView();
        }
    }

    private void getImage(String uristr){
        if(Common.SyncUpMode) return;
        mStringValue=uristr;
        if(mStringValue==null){
            mStringValue="";
            mWidgetIcon=null;
            setWidgetIcon(null);
            refreshView();
        }else {
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.execute();
        }
    }

}
