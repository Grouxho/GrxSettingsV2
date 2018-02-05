
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */



package com.grx.settings.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public class BPRulesUtils {


    public static boolean isBPEnabled(String BPRule){


        if(BPRule==null || BPRule.isEmpty()) return true;

        String[] rule = BPRule.split(Pattern.quote("#"));
        if(rule==null || rule.length!=3  ) return true;

        boolean rule_isenabled = (rule[0].toUpperCase().equals("SHOW")) ? true : false;
        // String propValue = GrxPrefsUtils.getBPProperty(rule[1]);
        String[] conditions = rule[2].split(Pattern.quote(","));

        // process conditions

        String mValuesToCheck=",";
        List<String> mValuesToContain = new ArrayList<String>();

        for(int i = 0; i<conditions.length; i++){
            String value = conditions[i];
            if(value.startsWith("(") && value.endsWith(")")) {
                String substring = value.substring(1,value.length()-1);
                if(substring.contains("NULL"))substring=substring.replace("NULL","");
                mValuesToContain.add(substring);
            }else mValuesToCheck+=value+",";
        }
        if(!mValuesToCheck.endsWith(",")) mValuesToCheck += ",";
        if(mValuesToCheck.contains("NULL")) {
            mValuesToCheck=mValuesToCheck.replace("NULL","");
        }else if(mValuesToCheck.equals(",,")) mValuesToCheck=null;


        //process build prop property

        String value = getBPProperty(rule[1]);


        //String value = RootUtils.getProp("grx.prop");
        boolean matched = false;
        if(mValuesToCheck!=null) {
            String pattern = "," + value + ",";
            matched = mValuesToCheck.contains(pattern);
        }

        if(matched)  return rule_isenabled;

        for (int i = 0; i<mValuesToContain.size();i++){
            String tmp = mValuesToContain.get(i);
            if(value.contains(tmp)){
                return rule_isenabled;
            }
        }
        return !rule_isenabled;


    }


    public static String getBPProperty(String property){
        Process p = null;
        String property_value = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", property).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                property_value = line;
            }
            p.destroy();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return property_value;
    }


    public static boolean setBPProperty(String property, String value){
        Process p = null;
        try {
            p = new ProcessBuilder("/system/bin/setprop", property, value).redirectErrorStream(true).start();
            p.destroy();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(getBPProperty(property)!=value) return false;
        else return true;
    }

}
