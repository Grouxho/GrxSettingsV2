
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.grx.settings.act.GrxImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class GrxImageHelper {



    public static Intent intent_avatar_img(Intent intent, int tx, int ty){

        int ax;
        int ay;

        if(tx>=ty){
            ay=1;
            ax=Math.round(tx/ty);
        }else{
            ax=1;
            ay=Math.round(ty/tx);
        }

        return intent.
                putExtra(GrxImagePicker.S_GRX_MODO_CAMARA,false)
                .putExtra(GrxImagePicker.S_GRX_REDONDEAR,false)
           /*     .putExtra(GrxImagePicker.S_ASPECTX,ax)
                .putExtra(GrxImagePicker.S_ASPECTY,ay)*/
                .putExtra(GrxImagePicker.S_ASPECTX,tx)
                .putExtra(GrxImagePicker.S_ASPECTY,ty)
                .putExtra(GrxImagePicker.S_OUTPUTX,tx)
                .putExtra(GrxImagePicker.S_OUTPUTY, ty)
                .putExtra(GrxImagePicker.S_SCALE_UP,true)
                .putExtra(GrxImagePicker.S_CROP,true)
                .putExtra(GrxImagePicker.S_SCALE, false);

    }


    public static Intent intent_avatar_img(Intent intent, int tx, int ty, boolean circular){
        return intent.
                putExtra(GrxImagePicker.S_GRX_MODO_CAMARA,false)
                .putExtra(GrxImagePicker.S_GRX_REDONDEAR,circular)
                .putExtra(GrxImagePicker.S_ASPECTX,tx)
                .putExtra(GrxImagePicker.S_ASPECTY,ty)
                .putExtra(GrxImagePicker.S_OUTPUTX,tx)
                .putExtra(GrxImagePicker.S_OUTPUTY, ty)
                .putExtra(GrxImagePicker.S_SCALE_UP,true)
                .putExtra(GrxImagePicker.S_CROP,true)
                .putExtra(GrxImagePicker.S_SCALE, true);
    }





    public static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }


    public static Intent intent_img_crop_circular(Intent intent){

        return intent.
                putExtra(GrxImagePicker.S_GRX_MODO_CAMARA,false)
                .putExtra(GrxImagePicker.S_GRX_REDONDEAR,true)
                .putExtra(GrxImagePicker.S_ASPECTX,1)
                .putExtra(GrxImagePicker.S_ASPECTY,1)
                .putExtra(GrxImagePicker.S_CROP,true)
                .putExtra(GrxImagePicker.S_SCALE, true);

    }


    public static Bitmap get_circular_bitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap load_bmp_image(String fichero){
        Bitmap bmp = null;
        try {
            File f = new File(fichero);
            bmp=BitmapFactory.decodeStream(new FileInputStream(f));

        }catch (Exception e){

        }
        return bmp;
    }


    public static boolean save_png_from_bitmap(Bitmap bm, String filename){

        boolean resultado = true;
        File f = new File(filename);
        if (f.exists()) f.delete();
        try {
            FileOutputStream o_s = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, o_s);
            o_s.flush();
            o_s.close();
        } catch (Exception e) {
            resultado=false;

            e.printStackTrace();
        }
        return resultado;
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable load_drawable_from_uri_string(Context context, String uri, int sizeX, int sizeY){
        Drawable drawable = null;
        Uri img_uri = Uri.parse(uri);
        if (uri != null) {
            try {

                InputStream inputStream = context.getContentResolver().openInputStream(img_uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap small_bitmap = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false);
                bitmap.recycle();
                drawable = new BitmapDrawable(context.getResources(),small_bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }


    public static Drawable get_scaled_drawable_from_uri_string_for_square_container(Context context, String uri, int max_size){
        Drawable drawable = null;
        Uri img_uri = Uri.parse(uri);
        if (uri != null) {
            try {

                InputStream inputStream = context.getContentResolver().openInputStream(img_uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                int sx = bitmap.getWidth();
                int sy = bitmap.getHeight();

                int fsx = max_size;
                int fsy = max_size;

                if (sy > sx) fsx = (int) ((float) max_size * ((float) sx / (float) sy));
                else if (sx > sy) fsy = (int) ((float) max_size * ((float) sy / (float) sx));

                Bitmap small_bitmap = Bitmap.createScaledBitmap(bitmap, fsx, fsy, false);
                bitmap.recycle();
                drawable = new BitmapDrawable(context.getResources(), small_bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }



    public static Bitmap load_bitmap_from_uri_string(Context context, String uri, int sizeX, int sizeY){
        Bitmap small_bitmap = null;

        Uri img_uri = Uri.parse(uri);
        if (uri != null) {
            try {

                InputStream inputStream = context.getContentResolver().openInputStream(img_uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                small_bitmap = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false);
                bitmap.recycle();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return small_bitmap;
    }

}
