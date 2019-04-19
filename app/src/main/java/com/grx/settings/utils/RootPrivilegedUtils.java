
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.Log;

import com.root.RootOperations;
import com.root.RootUtils;

import java.io.DataOutputStream;
import java.io.IOException;



public class RootPrivilegedUtils {

    public static boolean getIsRebootPermissionGranted(Context context){
        int result = context.checkCallingOrSelfPermission(Manifest.permission.REBOOT);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkRootGranted(){
        Common.IsRootChecked = true;
        Common.IsRooted= RootUtils.rootAccess();
    }


    public static boolean getIsDeviceRooted(){
        if(!Common.IsRootChecked) RootPrivilegedUtils.checkRootGranted();
        return Common.IsRooted;
    }


    public static void rebootDevicePrivileged(Context context){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        powerManager.reboot(null);
    }

    public static void runRebootDeviceCommands(){
        RootUtils.runCommand("su");
        RootUtils.runCommand("reboot");
    }

    public static void runRebootInRecoveryMode(){
        RootUtils.runCommand("su");
        RootUtils.runCommand("reboot recovery");
    }

    public static boolean runShellCmd(boolean runAsRoot, String cmd) {

        String shell = runAsRoot ? "su" : "sh";

        int exitCode = 255;
        Process p;
        try {

            p = Runtime.getRuntime().exec(shell);
            DataOutputStream os = new DataOutputStream(p.getOutputStream());

            os.writeBytes(cmd + "\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            exitCode = p.waitFor();

        } catch (IOException e1) {
            Log.e("Exception", e1.toString());
        } catch (InterruptedException e) {
            Log.e("Exception", e.toString());
        }
        return (exitCode != 255);
    }


    public static boolean runScriptArray(Context context, int arrayid){
        boolean returnvalue = true;
        String[] commands = context.getResources().getStringArray(arrayid);
        for(String cmd : commands){
            returnvalue =runShellCmd(true,cmd);
        }
        return returnvalue;
    }


    public static boolean runFileScript(Context context, String scriptfile, String... arguments){
        boolean returnvalue = true;

        String script = RootOperations.readAssetFile(context,scriptfile);
        if(script!=null && !script.isEmpty()){
            if(arguments!=null) RootUtils.runScript(script,arguments);
            else RootUtils.runScript(script);
        }else returnvalue=false;
        return returnvalue;
    }

}

