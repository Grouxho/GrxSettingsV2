
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.grx.settings.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

import static android.util.TypedValue.applyDimension;





public class GrxPrefsUtils {

    GrxPrefsUtils(){}



    public static void deleteFileFromStringName(String filename){
        File f = new File(filename);
        if(f.exists()){
            f.setReadable(true, false);
            f.setWritable(true, false);
            f.delete();
        }
    }

   /*
    UTILIDADES GESTIÓN FICHEROS
     */



    public static void deleteAllFilesInFolderWithGivenExtension(String foler, String extension){
        File dir = new File(foler);
        if(dir.exists()&&dir.isDirectory()){
            File files[]=dir.listFiles();
            if(files.length!=0){
                for(int ind=0;ind<files.length;ind++){
                    if(files[ind].getName().contains(extension)) files[ind].delete();
                }

            }
        }
    }


    public static void deleteGrxPreferenceTmpFilesInFolder(String folder){
        File dir = new File(folder);
        if(dir.exists()&&dir.isDirectory()){
            File files[]=dir.listFiles();
            if(files.length!=0){
                for(int ind=0;ind<files.length;ind++){
                    if(files[ind].getName().contains(Common.TMP_PREFIX)) files[ind].delete();
                }

            }
        }
    }

    public static void copyFilesWithExtension(String from_folder, String to_folder, String extension){

        if(to_folder.equals(from_folder)) return;
        File ori_dir = new File(from_folder);
        if(ori_dir.exists() && ori_dir.isDirectory()){
            File ori_files[]=ori_dir.listFiles();
            if(ori_files.length!=0){
                for(int ind=0;ind<ori_files.length;ind++){
                    if(ori_files[ind].getName().contains(extension)){
                        File dest_file = new File(to_folder+File.separator+ori_files[ind].getName());
                        copyFiles(ori_files[ind],dest_file);
                    }
                }
            }
        }
    }

    public static void copyFiles(File ori_file, File dest_file){

        try {
            FileInputStream i_s = new FileInputStream(ori_file);
            FileOutputStream o_s = new FileOutputStream(dest_file);
            FileChannel inChannel = i_s.getChannel();
            FileChannel outChannel = o_s.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            i_s.close();
            o_s.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void show_toast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void fileCopyFromTo(String ori_file_name, String dest_file_name){
        if(ori_file_name==null || dest_file_name == null) {
            return;
        }

        File file_in = new File(ori_file_name);
        File file_out = new File(dest_file_name);
        if(file_in.exists()) GrxPrefsUtils.copyFiles(file_in,file_out);
    }

    public static void deleteFile(File f){
        if(f!=null) f.delete();
    }




    public static void deleteFileOrCreateFolder(String dest_folder, String extension){
        File f = new File(dest_folder);
        if(f.exists() && f.isDirectory()) {
            File ori_files[]=f.listFiles();
            if(ori_files.length!=0) {
                for (int ind = 0; ind < ori_files.length; ind++) {
                    if (ori_files[ind].getName().contains(extension))
                        deleteFile(ori_files[ind]);

                }
            }

        }else f.mkdirs();
    }

    public static void createFolder(String folder){
        File f = new File(folder);
        if(!f.exists()) {
            f.mkdirs();
            f.setReadable(true,false);
            f.setWritable(true,false);
            try{
                Runtime.getRuntime().exec("chmod 777 " + folder);
            }catch (IOException e){}


        }
    }

    public static void fixFolderPermissions(String folder, String extension){
        File f = new File(folder);
        if(f.exists() && f.isDirectory()) {
            File ori_files[]=f.listFiles();
            if(ori_files.length!=0) {
                for (int ind = 0; ind < ori_files.length; ind++) {
                    if (ori_files[ind].getName().contains(extension))
                        ori_files[ind].setWritable(true,false);
                    ori_files[ind].setReadable(true,false);


                }
            }

        }
    }


    public static String getFormattedStringFromArrayResId(Context context, int array_id, String separator){
        if(array_id==0) return "";

        String array[] = context.getResources().getStringArray(array_id);
        String output = "";
        for(int i=0;i<array.length;i++){
            output+=array[i];
            output+=separator;
        }
        return output;
    }


    public static int getPositionInStringArray(String[] values, String key){
        int pos = 0;
        for(int i=0; i<values.length;i++){
            if(values[i].equals(key)){
                pos=i;
                break;
            }
        }
        return pos;
    }


    public static String getActivityLabelFromIntent(Context context, Intent intent){
        String string;
        string = intent.getStringExtra(Common.EXTRA_URI_LABEL);
        if(string==null) {
            try{
                ComponentName c_n = intent.getComponent();
                if(c_n!=null) {
                    ActivityInfo a_i = context.getPackageManager().getActivityInfo(c_n, 0);
                    if(a_i!=null) string = a_i.loadLabel(context.getPackageManager()).toString();
                }

            }catch (Exception e){}
        }

        if(string==null) string = "?";
        return string;
    }




    public static String getGrxLabelFromPackagenameActivityname(Context context, String nombre_paquete, String activity_name){

        String label="-";
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(nombre_paquete,activity_name ));

        ResolveInfo ri = context.getPackageManager().resolveActivity(intent,0);
        if(ri!=null){
            label=ri.loadLabel(context.getPackageManager()).toString();
        }else label="";


        ApplicationInfo aitemp;
        String app_label="";
        try {
            aitemp=context.getPackageManager().getApplicationInfo(ri.activityInfo.packageName,0);
            app_label=context.getPackageManager().getApplicationLabel(aitemp).toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(app_label.equals(label)) label=activity_name;
        else {
            label=label;
        }
        if(label.contains(".")) label= GrxPrefsUtils.getFormattedlabelForListViews(label);
        return label;

    }

    public static String getFormattedlabelForListViews(String label){
        return ".." + label.substring(Math.max(0, label.length() - Common.ACTIVITY_LABEL_MAX_CHARS));
    }


    public static Drawable getIconFromResolveinfo(Context context, Intent intent){
        Drawable drawable = null;
        ResolveInfo ri = context.getPackageManager().resolveActivity(intent,0);
        if(ri!=null){
            try{
                drawable=ri.loadIcon(context.getPackageManager());
            }catch (Exception e){}
        }
        return drawable;
    }


    public static Drawable getIconFromIntent(Context context, Intent intent){

        Drawable drawable=null;
        try {
            ComponentName c_n = intent.getComponent();
            if(c_n!=null) {
                ActivityInfo a_i = context.getPackageManager().getActivityInfo(c_n, 0);
                if(a_i!=null) drawable = a_i.loadIcon(context.getPackageManager());
            }
        }catch (Exception e){}
        if(drawable==null){
            ResolveInfo ri = context.getPackageManager().resolveActivity(intent,0);
            if(ri!=null){
                try{
                    drawable=ri.loadIcon(context.getPackageManager());
                }catch (Exception e){}
            }
        }

        //if(drawable==null) drawable=context.getDrawable(R.drawable.ic_no_encontrada);
        return drawable;
    }


    public static Drawable getIconFromPackageActivityString(Context context, String pkgactivity){
        Drawable drawable = null ;
        if (pkgactivity != null) {
            String[] arr = pkgactivity.split("/");
            String packagename = arr[0];
            String activity = arr[1];
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packagename, activity));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null) {
                drawable = resolveInfo.activityInfo.loadIcon(context.getPackageManager());
            }
        }
        return drawable;
    }


    public static String getLabelFromPackageActivityString(Context context, String pkgactivity){
        String label = null ;
        if (pkgactivity != null) {
            String[] arr = pkgactivity.split("/");
            String packagename = arr[0];
            String activity = arr[1];
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packagename, activity));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveInfo != null) {
                label = resolveInfo.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        if(label==null) label=context.getResources().getString(R.string.grxs_is_not_installed, pkgactivity);
        return label;
    }

    public static Drawable getDrawableFromPath(Context contex, String path){

        Drawable tmp=null;
        try {
            File f_i = new File(path);
            if(f_i.exists() && f_i.canRead() ){
                FileInputStream i_s = new FileInputStream(f_i);
                tmp = new BitmapDrawable(contex.getResources(), BitmapFactory.decodeStream(i_s));
                i_s.close();
            }
        }catch (Exception e){
            Toast.makeText(contex,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        return tmp;
    }

    public static void animateTextviewMarquee(TextView textView){
        textView.setTextIsSelectable(true);
        textView.setSingleLine(true);
        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setMarqueeRepeatLimit(1);
    }

    public static void animateTextviewMarqueeForever(TextView textView){
        textView.setTextIsSelectable(true);
        textView.setSingleLine(true);
        textView.setHorizontallyScrolling(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);



    }



    public static String getDrawableNameFromGrxIntent(Intent intent){

        return intent.getStringExtra(Common.EXTRA_URI_DRAWABLE_NAME);
    }


    public static Drawable getDrawableFromGrxIntent(Context context, Intent intent){
        Drawable drawable = null;
        String icon_name = intent.getStringExtra(Common.EXTRA_URI_ICON);
        if(icon_name != null){
            drawable = GrxPrefsUtils.getDrawableFromPath(context, icon_name);
        }
        if(drawable==null){
            String ndrw = intent.getStringExtra(Common.EXTRA_URI_DRAWABLE_NAME);
            if(ndrw!=null) {
                int idimg = context.getResources().getIdentifier(ndrw,"drawable",context.getPackageName());
                if(idimg!=0) drawable=context.getResources().getDrawable(idimg);
            }
        }
        if(drawable==null){
            if(intent.getIntExtra(Common.EXTRA_URI_TYPE,-1)!=Common.ID_ACCESS_CUSTOM)
                drawable= GrxPrefsUtils.getIconFromIntent(context, intent);
        }
        return drawable;
    }


    public static void deleteGrxIconFileFromIntent(Intent intent){
        String file_name=null;
        if(intent!=null) file_name = intent.getStringExtra(Common.EXTRA_URI_ICON);
        if(file_name!=null){
            File f_ico = new File(file_name);
            if(f_ico!=null && f_ico.exists()) {
                f_ico.delete();
            }
        }
    }

    public static void deleteGrxIconFileFromIntent(Intent intent, String starts_with){
        String file_name=null;
        if(intent!=null) file_name = intent.getStringExtra(Common.EXTRA_URI_ICON);
        if(file_name!=null){
            File f_ico = new File(file_name);
            if(f_ico!=null && f_ico.exists() && f_ico.getName().startsWith(starts_with)) {
                f_ico.delete();
            }
        }
    }

    public static void deleteGrxIconFileFromUriString(String uri){
        String file_name=null;
        Intent intent = null;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {}
        if(intent!=null) file_name = intent.getStringExtra(Common.EXTRA_URI_ICON);
        if(file_name!=null){
            File f_ico = new File(file_name);
            if(f_ico!=null && f_ico.exists()) {
                f_ico.delete();
            }
        }
    }

    public static void deleteGrxIconFileFromUriStringWithPrefix(String uri, String starts_with){
        String file_name=null;
        Intent intent = null;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {}
        if(intent!=null) file_name = intent.getStringExtra(Common.EXTRA_URI_ICON);
        if(file_name!=null){
            File f_ico = new File(file_name);
            if(f_ico!=null && f_ico.exists() && f_ico.getName().startsWith(starts_with)) {
                f_ico.delete();
            }
        }
    }

    public static void setReadWriteFilePermissions(String file_name){
        if(file_name==null || file_name.isEmpty()) return;
        try {
            File file = new File(file_name);
            if(file.exists()){

                file.setReadable(true,false);
                file.setWritable(true,false);

                try{
                    Runtime.getRuntime().exec("chmod 777 " + file_name);
                }catch (IOException e){

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getArrayIdFromArrayName(Context context, String array_name){
        if(array_name!=null && !array_name.isEmpty()){
            String[] aux=array_name.split("/");
            return context.getResources().getIdentifier(aux[1], "array", context.getPackageName() );
        }else return 0;
    }


    public static String getFileNameFromGrxIntent(Intent intent){
        String file_name = null;
        if(intent!=null){
            file_name=intent.getStringExtra(Common.EXTRA_URI_ICON);
        }
        return file_name;

    }

    public static String getFilenameFromGrxUriString(String uri){
        String file_name = null;
        Intent intent;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {
            return file_name;
        }
        if(intent!=null){
            file_name=intent.getStringExtra(Common.EXTRA_URI_ICON);
        }
        return file_name;
    }

    public static String getShortFileNameFromGrxUriString(String uri){

        String file_name = null;
        Intent intent;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {
            return file_name;
        }
        if(intent!=null){
            file_name=intent.getStringExtra(Common.EXTRA_URI_ICON);
        }

        if(file_name!=null) {
            File file = new File(file_name);
            if(file.exists()) return file.getName();
        }
        return file_name;

    }

    public static String getShortFileNameFromString(String filename){
        String file_name = null;
        if(filename!=null) {
            File file = new File(filename);
            if(file.exists()) return file.getName();
        }
        return file_name;

    }

    public static String changeExtraValueInUriString(String uri, String extra_key, String new_value){
        Intent intent;
        try {
            intent = Intent.parseUri(uri, 0);
        }catch (URISyntaxException e) {
            return null;
        }

        if(intent!=null) {
            intent.putExtra(extra_key, new_value);
            return intent.toUri(0);
        }
        return null;
    }

    public static boolean renameGrxTmpFile(String tmp_name){

        String n_f=tmp_name.replace("grxtmp","");
        File f_t = new File(tmp_name);
        if(f_t.exists()){
            File f_f = new File(n_f);
            f_t.renameTo(f_f);
            f_f.setReadable(true,false);
            f_f.setWritable(true,false);
            return true;
        }return false;
    }

    public static int getContrastTextColor(int bgcolor){
        int textcolor;
        double luminance = ColorUtils.calculateLuminance(bgcolor);
        if(luminance>(double) 0.5) textcolor = 0xff222222;
        else textcolor = 0xffffffff;
        return textcolor;
    }

    public static Drawable getApplicationIcon(Context context, String packagename){
        Drawable drw = null;
        try {
            drw= context.getPackageManager().getApplicationInfo(packagename,0).loadIcon(context.getPackageManager());
        }catch (Exception e){}
        return drw;
    }





    public static boolean isAppInstalled(Context context, String packagename){
        if(packagename==null || packagename.isEmpty()) return false;
        boolean t = false;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packagename,0);
            if(pi!= null) t= true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }


    public static boolean isActivityInstalled(Context context, String packagename, String activityname){
        boolean t = false;
        try {
            ComponentName componentName = new ComponentName(packagename, activityname);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, 0);
            if(resolveInfo!=null) t=true;
        }catch (Exception e){}
        return t;
    }


    public static String getMainActivityFromPackageName(Context context, String packageName){
        String className=null;
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if(launchIntent!=null) className = launchIntent.getComponent().getClassName();
        }catch (Exception e){

        }
        return className;
    }


    public static boolean isPackageActivityInstalled(Context context, String packageactivity){
        boolean installed = false;
        String [] array = packageactivity.split(Pattern.quote("/"));
        if(array!=null && array.length==2){
            installed = isActivityInstalled(context,array[0], array[1]);
        }else{
            installed = isAppInstalled(context, packageactivity);
        }
        return installed;
    }

    public static String getApplicationLabel(Context context, String packagename) {
        String name;
        if(packagename==null || packagename.isEmpty()) name ="";
        else {
            try {
                name = context.getPackageManager().getApplicationInfo(packagename, 0).loadLabel(context.getPackageManager()).toString();
            } catch (Exception e) {
                e.printStackTrace();
                name=context.getResources().getString(R.string.grxs_is_not_installed, packagename);
            }
        }
        return name;
    }

    public static void changePreferenceGroupKeyValue(Context context, String group_key){
        int i = Settings.System.getInt(context.getContentResolver(),group_key,0);
        if (i<32) i++;
        else i=1;
        try {
            Settings.System.putInt(context.getContentResolver(),group_key,i);
        }catch (Exception e){
            Log.d("GrxSettings", e.toString());
        }

    }

    public static void sendPreferenceBroadCastWithExtra(final Context context, final String action, String extra,  boolean delayed){

        final String extraval = extra;
        if(!delayed){
            Intent intent = new Intent();
            try {
                intent.setAction(action);
                if(extraval!=null && !extraval.isEmpty()) intent.putExtra("extravalue", extraval);
                context.sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }else {
            Runnable BC = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    try{
                        intent.setAction(action);
                        if(extraval!=null && !extraval.isEmpty()) intent.putExtra("extravalue", extraval);
                        context.sendBroadcast(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Handler handler = new Handler();
            handler.removeCallbacks(BC);
            handler.postDelayed(BC,Long.valueOf(400));
        }
    }


    public static void sendPreferenceBroadcaast(final Context context, final String action, boolean delayed){
        if(!delayed){
            Intent intent = new Intent();
            try {
                intent.setAction(action);
                context.sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }else {
            Runnable BC = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    try{
                        intent.setAction(action);
                        context.sendBroadcast(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Handler handler = new Handler();
            handler.removeCallbacks(BC);
            handler.postDelayed(BC,Long.valueOf(400));
        }
    }



    public static void sendCommonBroadCastExtraDelayed(final Context context, final String extra, final String extravalue, boolean delayed){
        final String action = context.getResources().getString(R.string.grx_common_extra_broadcast);
        if(action==null || action.isEmpty() || extra==null || extra.isEmpty()) return;
        if(!delayed){
            Intent intent = new Intent();
            try {
                intent.setAction(action);
                intent.putExtra(extra,extravalue);
                context.sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }else {
            Runnable BC = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    try{
                        intent.setAction(action);
                        intent.putExtra(extra,extravalue);
                        context.sendBroadcast(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Handler handler = new Handler();
            handler.removeCallbacks(BC);
            handler.postDelayed(BC,Long.valueOf(200));
        }
    }

    public static String getHexString(int color, boolean showAlpha) {
        int base = showAlpha ? 0xFFFFFFFF : 0xFFFFFF;
        String format = showAlpha ? "#%08X" : "#%06X";
        return String.format(format, (base & color)).toUpperCase();
    }

    public static int getCurrentAccentColor(Context context){
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
    public static int dip_to_pixels(Context context, int dips) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dips, metrics);
    }



    /************* settings system, global and secure support ***/

    public static int getIntValueFromSettingsSystem(Context context, String key, int defval, boolean syncsettings){
        int returnval = defval;
        try {
            returnval = Settings.System.getInt(context.getContentResolver(), key);
            if(syncsettings) {
                if(returnval!= defval){
                    Settings.System.putInt(context.getContentResolver(), key, defval);
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            if(syncsettings) Settings.System.putInt(context.getContentResolver(), key, defval);
        }
        return returnval;
    }


    public static int getIntValueFromSettingsSecure(Context context, String key, int defval, boolean syncsettings){
        int returnval = defval;
        try {
            returnval = Settings.Secure.getInt(context.getContentResolver(), key);
            if(syncsettings) {
                if(returnval!= defval){
                    Settings.Secure.putInt(context.getContentResolver(), key, defval);
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            if(syncsettings) Settings.Secure.putInt(context.getContentResolver(), key, defval);
        }
        return returnval;
    }


    public static int getIntValueFromSettingsGlobal(Context context, String key, int defval, boolean syncsettings){
        int returnval = defval;
        try {
            returnval = Settings.Global.getInt(context.getContentResolver(), key);
            if(syncsettings) {
                if(returnval!= defval){
                    Settings.Global.putInt(context.getContentResolver(), key, defval);
                }
            }
        } catch (Settings.SettingNotFoundException e) {
            if(syncsettings) Settings.Global.putInt(context.getContentResolver(), key, defval);
        }
        return returnval;
    }

    public static String getStringValueFromSettingsSystem(Context context, String key, String defval, boolean syncsettings){
        String def = (defval == null) ? "" : defval;
        String  returnval=null;

        try {
            returnval = Settings.System.getString(context.getContentResolver(), key);
            if(returnval==null) returnval=def;
            if(syncsettings) {
                if(!returnval.equals(defval)){
                    Settings.System.putString(context.getContentResolver(), key, returnval);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(returnval==null) return def;
        else return returnval;

    }

    public static String getStringValueFromSettingsSecure(Context context, String key, String defval, boolean syncsettings){
        String def = (defval == null) ? "" : defval;
        String  returnval=null;

        try {
            returnval = Settings.Secure.getString(context.getContentResolver(), key);
            if(returnval==null) returnval=def;
            if(syncsettings) {
                if(!returnval.equals(defval)){
                    Settings.Secure.putString(context.getContentResolver(), key, returnval);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(returnval==null) return def;
        else return returnval;

    }

    public static String getStringValueFromSettingsGlobal(Context context, String key, String defval, boolean syncsettings){
        String def = (defval == null) ? "" : defval;
        String  returnval=null;

        try {
            returnval = Settings.Global.getString(context.getContentResolver(), key);
            if(returnval==null) returnval=def;
            if(syncsettings) {
                if(!returnval.equals(defval)){
                    Settings.Global.putString(context.getContentResolver(), key, returnval);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(returnval==null) return def;
        else return returnval;

    }


    public static int getPositionInArray(String[] array, String value, int defpos){
        if(value==null || value.isEmpty()) return defpos;
        int i=0;
        for(i = 0; i<array.length;i++){
            if(array[i].equals(value)) break;
        }
        return i;
    }

}
