
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.act;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.grx.settings.R;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxImageHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;




public class GrxImagePicker extends Activity {


    public static final String S_GRX_MODO_CAMARA="grx_modo_camara";
    public static final String S_GRX_REDONDEAR="grx_redondear";
    public static final String S_DIR_IMG = "grx_dir_img";


    public static final int REQ_COD_MODO_IMG=20;
    public static final int REQ_COD_MODO_CAM=30;
    public static final int REQ_COD_MODO_CROP=40;
    public static final int REQ_COD_JUST_GET_URI=50;

    public static final int DEF_TAM_AVATAR=144;

    public static final String S_SPOTLIGHTX = "spotlightX";
    public static final String S_SPOTLIGHTY = "spotlightY";
    public static final String S_SCALE= "scale";
    public static final String S_CROP = "crop";
    public static final String S_SCALE_UP = "scaleUpIfNeeded";
    public static final String S_ASPECTX = "aspectX";
    public static final String S_ASPECTY = "aspectY";
    public static final String S_OUTPUTX = "outputX";
    public static final String S_OUTPUTY = "outputY";
    public static final String S_RETURNDATA = "return-data";
    public static final String S_OUTPUTFORMAT = "outputFormat";
    public static final String S_URI_MODE ="uri_mode";

    public static final String S_OUTPUT_FILE_NAME = "output_file";

    public static final String S_TIPO_IMG="image/*";
    public static final String S_ACT_CROP="com.android.camera.action.CROP";
    public static final String S_TRUE="true";
    public static final String S_FALSE="false";


    private boolean eCrop;

    private Point eAspect;
    private Point eSize;
    private Point eSpotLight;

    private boolean eScale;
    private boolean eScaleUpIfNeeded;

    private boolean eCameraMode;
    private boolean eCircle;

    private String mImageName=null;


    private String mLoaderResultName;
    private String mLoaderError;

    private ProgressBar progressBar;

    String mDestFragmentTag;

    private boolean just_get_uri_mode = false;

    private String output_file_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(!isTaskRoot()) finish();
        if(savedInstanceState == null && getIntent()!=null) {
            ini_progressbar();
            ini_param(getIntent());
            if(eCameraMode) capture_camera_image();
            else start_img_picker();
        }
        else{
            ini_progressbar();
            ini_param(getIntent());
            mLoaderResultName=savedInstanceState.getString("loader_result");
            just_get_uri_mode=savedInstanceState.getBoolean(S_URI_MODE,false);
            output_file_name=savedInstanceState.getString(S_OUTPUT_FILE_NAME,null);
            // finish();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("loader_result",mLoaderResultName);
        outState.putBoolean(S_URI_MODE,just_get_uri_mode);
        outState.putString(S_OUTPUT_FILE_NAME,output_file_name);
        if(mDestFragmentTag!=null) outState.putString(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,mDestFragmentTag);
    }

    private void ini_progressbar(){
        progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        progressBar.setIndeterminate(true);
//        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void ini_param(Intent intent){

        mDestFragmentTag = intent.getStringExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY);

        just_get_uri_mode = intent.getBooleanExtra(S_URI_MODE,false);

        output_file_name = intent.getStringExtra(S_OUTPUT_FILE_NAME);


        eCrop=intent.getBooleanExtra(S_CROP, false);
        eScale=intent.getBooleanExtra(S_SCALE, false);
        eScaleUpIfNeeded=intent.getBooleanExtra(S_SCALE_UP,false);

        if(intent.hasExtra(S_ASPECTY)||intent.hasExtra(S_ASPECTY))
            eAspect = new Point(intent.getIntExtra(S_ASPECTX,0),intent.getIntExtra(S_ASPECTY,0));

        if(intent.hasExtra(S_OUTPUTX)||intent.hasExtra(S_OUTPUTY))
            eSize = new Point(intent.getIntExtra(S_OUTPUTX,0),intent.getIntExtra(S_OUTPUTY,0));

        if(intent.hasExtra(S_SPOTLIGHTX)||intent.hasExtra(S_SPOTLIGHTY))
            eSpotLight =new Point(intent.getIntExtra(S_SPOTLIGHTX,0),intent.getIntExtra(S_SPOTLIGHTY,0));

        eCameraMode=intent.getBooleanExtra(S_GRX_MODO_CAMARA,false);
        eCircle=intent.getBooleanExtra(S_GRX_REDONDEAR,false);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode){
                case REQ_COD_MODO_IMG:
                    new Img_Loader().execute(data.getData());
                    break;

                case REQ_COD_MODO_CAM:
                    Uri u = data.getData();
                    mImageName=uri_to_string_name(u);
                    new Img_Loader().execute(data.getData());
                    break;
                case REQ_COD_MODO_CROP:

                    if(output_file_name==null) {
                        new File(mLoaderResultName).delete();
                        mLoaderResultName += "_tmp";
                    }

                    if(do_circle_img()){
                        Intent intent = new Intent();
                        intent.putExtra(S_DIR_IMG,mLoaderResultName);
                        if(mDestFragmentTag!=null) intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,mDestFragmentTag);
                        setResult(Activity.RESULT_OK,intent);
                        delete_img();
                        finish();
                    }else{
                        delete_tmp_img();
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                    break;

                case REQ_COD_JUST_GET_URI:
                    Intent intent = data;
                    if(mDestFragmentTag!=null) intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,mDestFragmentTag);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                    break;
                default:
                    delte_tmp_and_finish();
                    break;
            }
        } else delte_tmp_and_finish();
    }


    private void delte_tmp_and_finish(){
        delete_tmp_img();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private String uri_to_string_name(Uri uri){

        Cursor cursor = null;
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }

    }


    private void delete_img(){
/*        if(mImageName!=null) {
            File f = new File(mImageName);
            if(f.exists()){
                Toast.makeText(this, "existe", Toast.LENGTH_SHORT).show();
                f.setReadable(true, false);
                f.setWritable(true, false);
                f.delete();
            }
            mImageName=null;
           }*/
    }

    private void delete_tmp_img(){

        if (mLoaderResultName != null && output_file_name==null) {
            new File(mLoaderResultName + "_tmp").delete();
            new File(mLoaderResultName).delete();
        }
        delete_img();
    }


    private void capture_camera_image() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent,REQ_COD_MODO_CAM);
    }


    private File create_temp_img_file(){

        if(media_is_ok()){
            File dir_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if(!dir_fotos.exists()){
                dir_fotos.mkdirs();
            }
            String nombre_fichero = "GRX_"+String.valueOf(System.currentTimeMillis());
            File fichero_temp = new File(dir_fotos, nombre_fichero+".jpg");
            mImageName = fichero_temp.getAbsolutePath();
            return fichero_temp;

        }else{
            File dir_cache = Environment.getDataDirectory();
            String nombre_fichero = "GRX_"+String.valueOf(System.currentTimeMillis());
            File fichero_temp = new File(dir_cache, nombre_fichero+".jpg");
            mImageName = fichero_temp.getAbsolutePath();
            return fichero_temp;
        }
    }


    public static boolean media_is_ok(){
        String estado = Environment.getExternalStorageState();
        if(!estado.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }


    private void start_img_picker(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(S_TIPO_IMG);
        if(just_get_uri_mode) startActivityForResult(Intent.createChooser(intent, getString(R.string.grxs_selecc_image_usando)),REQ_COD_JUST_GET_URI);
        else startActivityForResult(Intent.createChooser(intent, getString(R.string.grxs_selecc_image_usando)),REQ_COD_MODO_IMG);
    }




    @Override
    public void onDestroy() {
        if(progressBar!=null) progressBar.setVisibility(View.GONE);
        super.onDestroy();
    }


    class Img_Loader extends AsyncTask<Uri, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            progressBar.refreshDrawableState();
            mLoaderError=null;
        }

        @Override
        protected String doInBackground(Uri... params) {
            File ftemp;
            InputStream i_s = null;
            FileOutputStream o_s = null;
            if(output_file_name==null) ftemp = new File(getCacheDir() + File.separator+Common.TMP_PREFIX+ String.valueOf(System.currentTimeMillis()));
            else ftemp = new File(output_file_name);
            mLoaderResultName=null;

            try {
                o_s = new FileOutputStream(ftemp);
                if(!eCameraMode) i_s = getContentResolver().openInputStream(params[0]);
                else i_s=getContentResolver().openInputStream(Uri.fromFile(new File(mImageName)));
                final byte[] buff = new byte[1024];
                int ind;
                while((ind = i_s.read(buff)) != -1) {
                    o_s.write(buff, 0, ind);
                }

                ftemp.setReadable(true, false);
                ftemp.setWritable(true, false);
                o_s.close();
                mLoaderResultName = ftemp.getAbsolutePath();
            } catch (Exception e) {
                mLoaderError=e.getMessage();
                mLoaderResultName=null;
            } finally {
                try { i_s.close(); } catch (Exception e) { }
                try { o_s.close(); } catch (Exception e) { }
            }

            return mLoaderResultName;
        }

        @Override
        protected void onPostExecute(String path) {
            progressBar.setVisibility(View.GONE);
            process_img();
        }
    }

    private void process_img(){
        if(!isDestroyed() && mLoaderResultName!=null){


            if(eCrop) crop_img();
            else{
                Intent intent = new Intent();
                intent.putExtra(S_DIR_IMG, mLoaderResultName);
                if(mDestFragmentTag!=null) intent.putExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY,mDestFragmentTag);
                if(do_circle_img()){
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    delete_tmp_img();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                    return;
                }
            }
        }else{

            if(isDestroyed()) {
                delete_tmp_img();
                return;
            }else{
                delete_tmp_img();
                setResult(Activity.RESULT_CANCELED);
                finish();
                return;
            }
        }
    }

    private void crop_img(){

        Intent intent = new Intent(S_ACT_CROP);
        intent.putExtra(S_CROP,S_TRUE);
        intent.putExtra(S_SCALE, eScale);
        intent.putExtra(S_SCALE_UP, eScaleUpIfNeeded);
        intent.putExtra(S_RETURNDATA, false);
        intent.putExtra(S_OUTPUTFORMAT, Bitmap.CompressFormat.PNG.toString());

        if (eAspect != null) {
            intent.putExtra(S_ASPECTX, eAspect.x);
            intent.putExtra(S_ASPECTY, eAspect.y);
        }
        if (eSize != null) {
            intent.putExtra(S_OUTPUTX, eSize.x);
            intent.putExtra(S_OUTPUTY, eSize.y);
        }
        if (eSpotLight != null) {
            intent.putExtra(S_SPOTLIGHTX, eSpotLight.x);
            intent.putExtra(S_SPOTLIGHTY, eSpotLight.y);
        }

        try {
            File fichero_entrada = new File(mLoaderResultName);
            Uri uri = Uri.fromFile(fichero_entrada);
            intent.setDataAndType(uri, S_TIPO_IMG);
            File fichero_salida;
            if(output_file_name==null) fichero_salida = new File(getCacheDir() + File.separator + fichero_entrada.getName() + "_tmp");
            else fichero_salida  = new File(output_file_name);

            fichero_salida.createNewFile();
            fichero_salida.setReadable(true, false);
            fichero_salida.setWritable(true, false);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero_salida));
            startActivityForResult(intent, REQ_COD_MODO_CROP);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            delete_tmp_img();
            finish();
        }

    }

    private boolean do_circle_img(){
        boolean resultado = true;
        if(eCircle){

            Bitmap bmp = GrxImageHelper.load_bmp_image(mLoaderResultName);
            if(bmp==null) {
                resultado=false;
            }
            else{
                bmp = GrxImageHelper.get_circular_bitmap(bmp);
                resultado = GrxImageHelper.save_png_from_bitmap(bmp, mLoaderResultName);
                if(!bmp.isRecycled()) bmp.recycle();
            }
        }

        if(!resultado) Toast.makeText(this,getString(R.string.grxs_error_redondeando),Toast.LENGTH_SHORT).show();
        return resultado;
    }
}
