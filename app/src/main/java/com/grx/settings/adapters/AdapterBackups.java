
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grx.settings.R;

import java.io.File;


public class AdapterBackups extends BaseAdapter {
    Context context;
    File ficheros[];


    public void AdapterBackups(Context ctx, File arr[]){
        context=ctx;
        ficheros=arr;

    }

    @Override
    public int getCount() {
        return ficheros.length;
    }

    @Override
    public Object getItem(int position) {
        return ficheros[position];
    }

    @Override
    public long getItemId(int position) {
        long l = (long) position;
        return l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vista = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,null);
        TextView tv = (TextView) vista.findViewById(android.R.id.text1);
        tv.setText(ficheros[position].getName().replace("."+context.getResources().getString(R.string.grxs_backups_files_extension),""));
        return vista;
    }
}
