
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.prefssupport;

import android.graphics.drawable.Drawable;

public class GrxAppInfo {

    private String mPackageName;
    private String mLabel;
    private Drawable mIcoApp;
    private String mActividad;
    private int mColor;

    public GrxAppInfo(String packageName, String Actividad, String label, Drawable ico_app, int color){

        mPackageName=packageName;
        mActividad=Actividad;
        mLabel=label;
        mIcoApp=ico_app;
        mColor=color;
    }

    public String nombre_app(){
        return mPackageName;
    }

    public String nombre_actividad(){
        return mActividad;
    }

    public String etiqueta_app(){
        return mLabel;
    }

    public int color_app(){
        return mColor;
    }

    public Drawable icono_app(){
        return mIcoApp;
    }

    public void pon_color_app(int color){
        mColor = color;
    }
}
