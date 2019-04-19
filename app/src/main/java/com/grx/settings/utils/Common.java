
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;

import com.grx.settings.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;


public class Common {

    Common(){}


    //config menu

    public static final String S_APPOPT_DRAWER_POS= "grx_drawer_pos";
    public static final boolean DEF_VAL_DRAWER_POS = false;
    public static final String S_APPOPT_FAB_POS= "grx_fab_pos";
    public static final int DEF_VAL_FAB_POS = 0; //CENTER
    public static final String S_APPOPT_DIV_HEIGHT= "grx_div_height";
    public static final String S_APPOPT_SHOW_FAV= "grx_show_fab";
    public static final boolean DEF_VAL_SHOW_FAB = true;
    public static final String S_APPOPT_REMEMBER_SCREEN= "grx_remenber_screen";

    public static final String S_APPOPT_MENU_GROUPS_ALWAYS_OPEN= "grx_men_groups_open";
    public static final boolean DEF_VAL_GROUPS_ALWAYS_OPEN = false;
    public static final String S_APPOPT_SHOW_COLLAPSE_EXPAND_BUTTONS= "grx_expand_collapser_buttons";
    public static final boolean DEF_VAL_SHOW_COL_EXP_BUTTONS = true;
    public static final String S_APPOPT_EXIT_CONFIRM= "grx_exit_confirm";
    public static final boolean DEF_VAL_EXIT_CONFIRM = true;
    public static final String S_APPOPT_USER_SELECTED_THEME = "grx_user_selected_theme";
    public static final String S_APPOPT_USER_SELECTED_THEME_NAME = "grx_user_selected_theme_name";
    public static final String S_APPOPT_COLORPICKERSTYLE = "grx_colorpickerstyle";

    public static final String S_APPOPT_FW_ENABLE = "grx_enable_fw";
    public static final String S_APPOPT_RFW_EXIT_WITH_BACK = "grx_fw_exit_back";
    public static final String S_APPOPT_RFW_EXIT_OUTSIDE = "grx_fw_exit_outside";
    //sync control key

    public static final String S_CTRL_SYNC_NEEDED = "grx_sync_needed";
    public static final String S_CTRL_APP_VER= "grx_app_version";
    public static final String S_CTRL_RECENTS_SCREENS= "grx_recents_screens";


    //Themes

    public static final int INT_ID_THEME_BASE_LIGHT = 0;
    public static final int INT_ID_THEME_BASE_DARK = 1;
    public static final int INT_ID_THEME_GREEN_LIGHT = 2;
    public static final int INT_ID_THEME_GREEN_DARK = 3;
    public static final int INT_ID_THEME_RED_LIGHT = 4;
    public static final int INT_ID_THEME_RED_DARK = 5;
    public static final int INT_ID_THEME_ORANGE_LIGHT = 6;
    public static final int INT_ID_THEME_ORANGE_DARK = 7;
    public static final int INT_ID_THEME_PURPLE_LIGHT = 8;
    public static final int INT_ID_THEME_PURPLE_DARK = 9;
    public static final int INT_ID_THEME_BROWN_LIGHT = 10;
    public static final int INT_ID_THEME_YELLOW_DARK = 11;
    public static final int INT_ID_THEME_GREEN_ORANGE_LIGHT = 12;

    //aux

    public static final String S_AUX_LAST_SCREEN= "grx_last_screen";


    //dlgs

    public static final String S_DLG_T_KEY= "t_dialog";
    public static final String S_APPDLG_FAV_POS= "dlg_fav_pos";
    public static final int INT_ID_APPDLG_FAV_POS = 0;
    public static final String S_APPDLG_DIV_HEIGHT= "dlg_div_height";
    public static final int INT_ID_APPDLG_DIV_HEIGHT = 1;
    public static final String S_APPDLG_EXIT_CONFIRM = "dlg_exit_confirm";
    public static final int INT_ID_APPDLG_EXIT_CONFIRM = 2;
    public static final String S_APPDLG_SET_THEME= "dlg_set_theme";
    public static final int INT_ID_APPDLG_SET_THEME = 3;
    public static final String S_APPDLG_SET_BG_PANEL_HEADER= "dlg_set_panel_header_bg";
    public static final int INT_ID_APPDLG_SET_BG_PANEL_HEADER = 4;
    public static final String S_APPDLG_SET_COLORPICKER_STYLE = "dlg_colorpicker_style";
    public static final int INT_ID_APPDLG_SET_COLORPICKER_STYLE = 5;

    public static final int INT_ID_APPDLG_RESET_ALL_PREFERENCES = 6;
    public static final String S_APPDLG_RESET_ALL_PREFERENCES = "dlg_reset_allprefs";

    public static final int INT_ID_APPDLG_TOOLS = 7;
    public static final String S_APPDLG_TOOLS = "dlg_tools";



    //App start modes

    public static final int INI_MODE_NORMAL = 0;
    public static final int INI_MODE_INSTANCE = 1;
    public static final int INI_MODE_INTENT = 2;
    public static final int INI_MODE_FORCED = 3;


    //Intent Extras



    public static final String EXTRA_SCREEN = "GrxScreen";
    public static final String EXTRA_SUB_SCREEN = "GrxSubScreen";
    public static final String EXTRA_KEY = "GrxKey";
    public static final String EXTRA_MODE = "GrxMode";
    public static final String EXTRA_DIV_HEIGHT = "GrxDividerHeight";

    public static final String EXTRA_SUB_SCREEN_PREFSSCREEN = "GrxSubScreenPrefsScreen";

    public static final String TAG_PREFSSCREEN_FRAGMENT= "GrxPreferenceScreen";
    public static final String TAG_PREFSSCREEN_FRAGMENT_SYNC= "GrxPrefsScreen_sync";
    public static final String TAG_FRAGMENTHELPER_NAME_EXTRA_KEY= "GrxHelperFragment";

    public static final int REQ_CODE_GALLERY_IMAGE_PICKER_JUST_URI = 97;
    public static final int REQ_CODE_GALLERY_IMAGE_PICKER_CROP_CIRCULAR = 98;
    public static final int REQ_CODE_GALLERY_IMAGE_PICKER_FROM_FRAGMENT = 99;
    public static final int REQ_CODE_GALLERY_IMAGE_PICKER_FROM_GRXAJUSTES = 100;

    public static final int REQ_CODE_GET_SHORTCUT = 101;

    public static final String TAG_DEST_FRAGMENT_NAME_EXTRA_KEY= "GrxDestFragment";
    public static final String TAG_DLGFRGRXCOLORPICKER = "DlgFrGrxColorPicker";
    public static final String TAG_DLGFRGRCOLORPALETTE = "DlgFrColorPalette";
    public static final String TAG_DLGFRGRSELECTAPP = "DlgGrxAppSelection";
    public static final String TAG_DLGFRGRMULTISELECTAPP = "DlgGrxMultipleAppSelection";
    public static final String TAG_DLGFRGRITEMSCOLORS = "DlgFrGrxPerItemColor";
    public static final String TAG_DLGFRGRWIDGETS ="DlgFrGrxMultipleWidgets";
    public static final String TAG_DLGFRGREDITTEXT = "DlgFrEditText";
    public static final String TAG_DLGFRGRACCESS = "DlgFrGrxAccess";
    public static final String TAG_DLGFRGRMULTIACCESS = "DlgFrGrxMultiAccess";
    public static final String TAG_DLGFRGRMULTIPPCOLOR = "DlgFrGrxPerAppColor";
    public static final String TAG_DLGFRGRMULTIVALUES = "DlgFrSelectSortItems";
    public static final String TAG_DLGFRGRMULTISELECT = "DlgFrMultiSelect";
    public static final String TAG_DLGFRGRDATEPICKER = "DlgFrDatePicker";
    public static final String TAG_DLGFRGRTIMEPICKER = "DlgFrTimePicker";
    public static final String TAG_INFOFRAGMENT = "GrxInfoFragment";
    public static final String TAG_DLGFRAPPLEDPULSE = "DlgFrAppLedPulse";
    public static final String TAG_DLGFRPERAPPAPPLEDPULSE = "DlgFrGrxPerAppLedPulse";
    public static final String TAG_DLGFRGRPERITEMSINGLESELECTION = "DlgFrGrxPerItemSingleSelection";


    public static final int ID_ACCESS_SHORCUT = 0;
    public static final int ID_ACCESS_APPS = 1;
    public static final int ID_ACCESS_ACTIVITIES = 2;
    public static final int ID_ACCESS_CUSTOM = 3;

    public static final String EXTRA_URI_ICON = "grx_icon";
    public static final String EXTRA_URI_TYPE = "grx_type";
    public static final String EXTRA_URI_LABEL = "grx_label";
    public static final String EXTRA_URI_DRAWABLE_NAME = "grx_drawable";
    public static final String EXTRA_URI_VALUE = "grx_value";

    public static final String TMP_PREFIX = "grxTMP_";

    public static final int ACTIVITY_LABEL_MAX_CHARS = 25;


    //customizable info tabs

    public static String INFO_ATTR_ULR = "grxURL";
    public static String INFO_ATTR_ROUND_ICON = "grxCircular";
    public static String INFO_ATTR_ANIMATE_TEXT = "grxAnimateText";


    //shared global values

    public static int cDividerHeight;
    public static SharedPreferences sp;
    public static String IconsDir;
    public static String CacheDir;
    public static String BackupsDir;

    public static LinearLayout.LayoutParams AndroidIconParams;

    public static boolean SyncUpMode=false;
    public static HashSet<String> GroupKeysList=null;
    public static HashSet<String> CommonBroadCastList=null;

    public static HashSet<String> BroadCastsList=null;

    public static boolean IsRebootPermissionGranted =false;
    public static boolean IsRootChecked=false;
    public static boolean IsRooted=false;

    public static String userColorPickerStyle;
    public static boolean allowUserColorPickerStyle;


    public static final String SETTINGS_PREF_SYSTEM="system";
    public static final String SETTINGS_PREF_SECURE="secure";
    public static final String SETTINGS_PREF_GLOBAL="global";

    public static final String SETTINGS_PREF_SYSTEM_CLEAN_fROM_PATH="/system/";
    public static final String SETTINGS_PREF_SECURE_CLEAN_fROM_PATH="/secure/";
    public static final String SETTINGS_PREF_GLOBAL_CLEAN_fROM_PATH="/global/";


    public static int getColorPickerStyleIndex(String type){
        int pickerStyle=1;
        switch (type) {
            case "flower":
                pickerStyle = 0;
                break;
            case "circle":
                pickerStyle = 1;
                break;
            case "square":
                pickerStyle = 2;
                break;
            default:
                pickerStyle = 1;
                break;
        }
        return pickerStyle;
    }

    public static String getColorPickerStyleString(int index){
        String style = "circle";
        switch (index){
            case 0:
                style="flower";
                break;
            case 1:
                style="circle";
                break;
            case 2:
                style="square";
                break;
        }
        return style;
    }

    public static Context mContextWrapper = null;

    /** a little help for preferences to access to non estandar color references when configuration changes or app  restart.
     PreferenceFragment could be initialized faster than the activity set the user selected theme, so preferences could get a wrong color.
     */

    public static void buildContextWrapper(Context context){
        Common.mContextWrapper = null;
        String themename = Common.sp.getString(Common.S_APPOPT_USER_SELECTED_THEME_NAME, context.getString(R.string.grxs_default_theme));
        if(themename==null || themename.isEmpty()) return;
        int themeid = context.getResources().getIdentifier(themename,"style",  context.getPackageName());
        Resources.Theme helpertheme = context.getResources().newTheme();
        helpertheme.applyStyle(themeid,true);
        Common.mContextWrapper = new ContextThemeWrapper(context, 0);
        Common.mContextWrapper.getTheme().setTo(helpertheme);
    }
}