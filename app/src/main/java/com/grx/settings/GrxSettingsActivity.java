
/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */


package com.grx.settings;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import com.grx.settings.app_fragments.DlgFrRestore;
import com.grx.settings.app_fragments.GrxHelpFragment;
import com.grx.settings.utils.Common;

import com.grx.settings.utils.GrxPrefsUtils;
import com.grx.settings.utils.RootPrivilegedUtils;
import com.grx.settings.views.GrxFloatingRecents;
import com.sublimenavigationview.OnNavigationMenuEventListener;
import com.sublimenavigationview.SublimeBaseMenuItem;
import com.sublimenavigationview.SublimeGroup;
import com.sublimenavigationview.SublimeGroupHeaderMenuItem;
import com.sublimenavigationview.SublimeMenu;
import com.sublimenavigationview.SublimeNavMenuView;
import com.sublimenavigationview.SublimeNavigationView;
import com.sublimenavigationview.SublimeTextWithBadgeMenuItem;
import com.root.RootUtils;
import com.grx.settings.act.GrxImagePicker;
import com.fab.ObservableScrollView;
import com.grx.settings.app_fragments.GrxInfoFragment;
import com.grx.settings.utils.GrxImageHelper;
import com.grx.settings.app_fragments.DlgFrGrxNavigationUserOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class GrxSettingsActivity extends AppCompatActivity implements
        DlgFrGrxNavigationUserOptions.DlgFrGrxNavigationUserOptionsCallBack,
        GrxPreferenceScreen.onListReady,
        GrxPreferenceScreen.onBackKey,
        GrxPreferenceScreen.onScreenChange,
        DlgFrRestore.OnDlgFrRestoreActionListener,
        GrxInfoFragment.onSlidingTabChanged,
        GrxFloatingRecents.OnGrxFWsetScreenCallback

{


    /*svn*/
    final String SS_KEY_MENU_1 = "ss.key.menu.1";
    final String SS_KEY_MENU_2 = "ss.key.menu.2";
    final String SS_KEY_LAST_ITEM = "ss.key.lastitem";
    final String SS_KEY_CURRENT_MENU = "ss.key.current.menu";

    private SublimeNavigationView mSVN;  //SublimeNavigationView
    private SublimeNavMenuView vSvnMenu; //Sublime Menu View
    FloatingActionButton mFabSvn; //Standard Floating action Buttom in SNV

    //Padding for Expand - Collapse Buttoms - SNV main menu
    private int mPaddinOnVGrButtons;
    private int mPaddinOffVGrButtons;
    private LinearLayout vExpandCollapseButtons;
    private LinearLayout vExpandButton;
    private LinearLayout vCollapseButton;


    private int mCurrentMenu;
    private SublimeMenu mOptionsMenu, mConfigMenu;

    private DrawerLayout mDrawer;
    private boolean mDrawerInLeftPosition;

    private com.fab.FloatingActionButton fab;

    private boolean mChangeScreenAfterDrawerClosed=false;

    public Toolbar mToolbar;


    GrxPreferenceScreen PrefScreenFragment;

    private boolean mExpandCollapseVisible;

    private SublimeBaseMenuItem mCurrentMenuItem;


    private boolean mShowFab;
    private int mFabPosition;
    private boolean mRememberScreen;
    private boolean mGroupsExpanded;
    private boolean mShowExpandCollapseButtons;
    private int mDividerHeight;
    private boolean mShowFloatingRecentsWindow;
    private boolean mHideFloatingAreaWithBack;



    private android.support.v4.widget.DrawerLayout.LayoutParams mDrawerLayoutParams;

    private String mCurrentScreen;
    private String mCurrentSubScreen;

    private int mSnackBarBgColor;
    private int mNumberOfGroups=0;

    private boolean mExitConfirmation = Common.DEF_VAL_EXIT_CONFIRM;

    // sync
    public Map<Integer,String> ResXML;
    public int mNumSyncPrefs=0;
    public int mNumSyncScreens=0;

    //backup restore

    EditText mEditText;

    private DlgFrRestore mRestoreDialog = null;

    public ProgressBar mProgressbar;

    //private  int mTheme;
    private String mThemeS;
    private String mSubScreenIntent=null;
    private String mGrxKeyIntent=null;

    public GrxFloatingRecents mFloatingRecentsWindow;

    Runnable mAnimationRunnable;
    Handler mHandler = new Handler();


    private boolean calledFromTile = false;

    public  static GrxSettingsActivity GrxSettingsActivityInstance = null;

    public getSuBgTask mGetSuBgTask;

    public int mSelectedTool = -1;

    /*************************************************************************************************/
    /********************* ROOT AND SCRIPT OPERATIONS ************************************************/
    /*************************************************************************************************/

    public void testFromTile(String txt){
        showToast(txt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        if(savedInstanceState==null){
            calledFromTile = getIntent().getIntExtra("GrxTileService",0)!=0;
        }



       // RootPrivilegedUtils.checkRootGranted();
        mGetSuBgTask  = new getSuBgTask();
        mGetSuBgTask.setActivity(this);
        mGetSuBgTask.execute();

        mAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                doChangeOfScreenOnDrawerClosed();
            }
        };

        initSharedPreferencesAndValues();
        setUserTheme();

        setRecentsTaskDescriptionIconAndBg();




        setContentView(R.layout.grx_nav_layout);

        mOptionsMenu=null;
        mCurrentMenuItem = null;
        mConfigMenu=null;
        mCurrentMenu=-1;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SS_KEY_MENU_1)) {
                mOptionsMenu = savedInstanceState.getParcelable(SS_KEY_MENU_1);
            }
            if (savedInstanceState.containsKey(SS_KEY_MENU_2)) {
                mConfigMenu = savedInstanceState.getParcelable(SS_KEY_MENU_2);
            }
            if (savedInstanceState.containsKey(SS_KEY_LAST_ITEM)) {
                mCurrentMenuItem = savedInstanceState.getParcelable(SS_KEY_LAST_ITEM);
            }
            if (savedInstanceState.containsKey(SS_KEY_CURRENT_MENU)) {
                mCurrentMenu = savedInstanceState.getInt(SS_KEY_CURRENT_MENU,-1);
            }
        }
        mCurrentScreen="";
        mCurrentSubScreen="";
        int mode = Common.INI_MODE_NORMAL;
        if(savedInstanceState!=null) mode= Common.INI_MODE_INSTANCE;
        else {
            if(getIntent()!=null){
                mCurrentScreen =getIntent().getStringExtra(Common.EXTRA_SCREEN);
                mCurrentSubScreen=getIntent().getStringExtra(Common.EXTRA_SUB_SCREEN);
                if(mCurrentSubScreen==null) mCurrentSubScreen="";
                mSubScreenIntent=mCurrentSubScreen;
                if(mCurrentScreen!=null) mode = Common.INI_MODE_INTENT;
                else mCurrentScreen="";
                mGrxKeyIntent=getIntent().getStringExtra(Common.EXTRA_KEY);
            }
        }

        setDrawerLayoutPosition();
        initToolBar();
        initSublimeNavigationView();
        initSublimeNavigationMenus();
        initUserConfigFAB();
        initMenuNavigation();
        setMainFABListeners();
        readUserConfigOptions();
        updateMainFABVisibility();
        updateMainFABPosition();
        updateSVNGroups();
        updateFABPositionInfo();
        updateThemeTextInfo();
        updateDividerInfo();
        updateColorPickerStyleInfo();
        updateSVNHeader();

        mProgressbar = (ProgressBar) findViewById(R.id.gid_progressbar);


        setCurrentMenuAndScreen(mode);
        updateNavigationButtons();

        if(mCurrentMenuItem!=null && !mCurrentScreen.isEmpty()) {
            mCurrentMenuItem.setChecked(true);
            mCurrentSubScreen="";
            setToolbarTitle(mCurrentMenuItem,mCurrentScreen);
            if(savedInstanceState==null) showPreferencesScreen(mCurrentMenuItem,mCurrentScreen);
            else PrefScreenFragment = (GrxPreferenceScreen) getFragmentManager().findFragmentByTag(Common.TAG_PREFSSCREEN_FRAGMENT);
            if(mode!= Common.INI_MODE_INSTANCE) {
                if(mCurrentMenuItem!=null) showSnack(mCurrentMenuItem.getTitle().toString());
            }
            setSNVMenuHeaderChecked(mCurrentMenuItem.getGroupId(), true);
        }
        if(savedInstanceState==null) GrxPrefsUtils.deleteGrxPreferenceTmpFilesInFolder(Common.CacheDir); //let´s clean existing tmp files

        synchronizePreferences();
        initMenuItemsPositions();
        scrollSVNtoCurrentItem();

        if(mCurrentScreen.isEmpty()) showInfoFragment();

        Common.IsRebootPermissionGranted = RootPrivilegedUtils.getIsRebootPermissionGranted(this);

        GrxSettingsActivityInstance = this;

      /*  if(calledFromTile){
            mCurrentMenu= 1;
            mSVN.switchMenuTo(mConfigMenu);
            showInfoFragment();
            mDrawer.openDrawer(mSVN);
            saveLastScreenValueToSHP();
            showToast("viene del tile");
        }*/



    }




    private void setInitialRecentsScreens(){

        String recents = Common.sp.getString(Common.S_CTRL_RECENTS_SCREENS,"");
        if(!recents.isEmpty()){
            String[] screens = recents.split(Pattern.quote("|"));
            for(int i = 0;i<screens.length;i++){
                int index = screens.length-1 - i;
                int id = getResources().getIdentifier(screens[index], "id", getApplicationContext().getPackageName());
                if(id!=0) {
                    SublimeBaseMenuItem menuItem = mOptionsMenu.getMenuItem(id);
                    if(menuItem!=null) {
                        mFloatingRecentsWindow.addScreen(menuItem.getTitle().toString(), screens[index],id);
                    }
                }
            }
        }
    }

    private void addFloatingRecentScreensWindow(){

        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.recentscreensbg });
        int bgcolor = a.getColor(0, 0);
        a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.recentscreenstextcolor });
        int textcolor = a.getColor(0, 0);
        a.recycle();


        mFloatingRecentsWindow = new GrxFloatingRecents(this, getWindowManager(), bgcolor, textcolor);
        mFloatingRecentsWindow.setGrxFWCallBack(this);
        mFloatingRecentsWindow.setVisibility(Common.sp.getInt("fw_visibility",View.GONE));
        setInitialRecentsScreens();
        if(mCurrentScreen!=null && !mCurrentScreen.isEmpty() && mCurrentMenuItem!=null) {  /// FIX A SUBIR A GITHUB
            mFloatingRecentsWindow.addScreen(mCurrentMenuItem.getTitle().toString(),mCurrentScreen,mCurrentMenuItem.getItemId());
        }
        getWindowManager().addView(mFloatingRecentsWindow, mFloatingRecentsWindow.getLayoutParams());

    }


    /** interface implementation for grxfloatingwindow set screen */

    public void setScreenFromGrxFW(String xml_name, int id){
        if(xml_name==null) return;
        if(xml_name.equals(mCurrentScreen)) {
            mDrawer.closeDrawers();
            return;
        }
        if(mCurrentMenu!=0) {
            mSVN.switchMenuTo(mOptionsMenu);
            mCurrentMenu=0;
        }
        SublimeBaseMenuItem menuItem = mOptionsMenu.getMenuItem(id);
        if(menuItem==null) return;
        else {
            changeOfScreen(menuItem);
            mChangeScreenAfterDrawerClosed=true;
            scrollSVNtoCurrentItem();
            doChangeOfScreenOnDrawerClosed();

        }


    }

    public void backPressedFromGrxFW(){
        onBackPressed();
    }

    /********************************/
    public static String readAssetFile(Context context, String file) {
        InputStream input = null;
        BufferedReader buf = null;
        try {
            StringBuilder s = new StringBuilder();
            input = context.getAssets().open(file);
            buf = new BufferedReader(new InputStreamReader(input));

            String str;
            while ((str = buf.readLine()) != null) {
                s.append(str).append("\n");
            }
            return s.toString().trim();
        } catch (IOException e) {
            Log.e("GRX", "Unable to read " + file);
        } finally {
            try {
                if (input != null) input.close();
                if (buf != null) buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onStop(){
        super.onStop();
    }


    @Override
    protected void onPause(){
        super.onPause();
        if(mShowFloatingRecentsWindow) {
            Common.sp.edit().putInt("fw_visibility",mFloatingRecentsWindow.getVisibility()).commit();
            mFloatingRecentsWindow.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SS_KEY_MENU_1, mOptionsMenu);
        outState.putParcelable(SS_KEY_MENU_2, mConfigMenu);
        outState.putParcelable(SS_KEY_LAST_ITEM, mCurrentMenuItem);
        outState.putInt(SS_KEY_CURRENT_MENU, mCurrentMenu);
        if(mCurrentSubScreen==null) mCurrentSubScreen="";
        if(mGrxKeyIntent==null) mGrxKeyIntent="";
        getIntent().removeExtra(Common.EXTRA_SCREEN);
        getIntent().removeExtra(Common.EXTRA_SUB_SCREEN);
        getIntent().removeExtra(Common.EXTRA_KEY);

    }


    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentMenuItem!=null) setToolbarTitle(mCurrentMenuItem, mCurrentScreen);
        if(mFloatingRecentsWindow!=null) {
            mFloatingRecentsWindow.setVisibility(Common.sp.getInt("fw_visibility",View.GONE));
        }
    }

    @Override
    public void onBackKey(CharSequence Subtitle, boolean navicon){
        setToolBarSubTitle(Subtitle);
    }

    @Override
    public void onScreenChange(String last_sub_screen){
        mCurrentSubScreen = last_sub_screen;
    }



    @Override
    public void SetObservableScrollView(ObservableScrollView observableScrollView){
        if(fab!=null && observableScrollView!=null){
            fab.attachToScrollView(observableScrollView);
            fab.show(true);
        }
    }

    @Override
    public void onListReady(ListView listaprefs){
        if(listaprefs!=null && fab!=null) {
            fab.attachToListView(listaprefs);
            fab.show(true);
        }
    }


    private void showPreferencesScreen(SublimeBaseMenuItem menuItem, String screen_name) {
        String subscreen=(mSubScreenIntent==null) ? mCurrentSubScreen : mSubScreenIntent;
        String key = (mGrxKeyIntent==null) ? "" : mGrxKeyIntent;
        mSubScreenIntent=null;
        mGrxKeyIntent=null;
        PrefScreenFragment = GrxPreferenceScreen.newInstance(mCurrentScreen,subscreen,key,mDividerHeight);
        setToolBarSubTitle("");
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if(!Common.SyncUpMode) {  ///grxgrxgrx
            if(!mDrawerInLeftPosition) fragmentTransaction.setCustomAnimations(R.animator.enter_animation_r,R.animator.exit_animator_r);
            else fragmentTransaction.setCustomAnimations(R.animator.enter_animation,0);
        }

        fragmentTransaction.replace(R.id.gid_content,PrefScreenFragment, Common.TAG_PREFSSCREEN_FRAGMENT).commit();
        if(!Common.SyncUpMode) {
            int id =0;
            String title=null;
            if(mCurrentMenuItem!=null) {
                id=mCurrentMenuItem.getItemId();
                title=mCurrentMenuItem.getTitle().toString();
            }
            if(mFloatingRecentsWindow !=null) mFloatingRecentsWindow.addScreen(title, screen_name, id);
        }
    }


    /***** toolbar **/

    private void setToolBarSubTitle(CharSequence Subtitle){
        if(getSupportActionBar()!=null){
            if(Subtitle!=null) getSupportActionBar().setSubtitle(Subtitle);
            else getSupportActionBar().setSubtitle("");
        }
    }

    private void setToolbarTitle(SublimeBaseMenuItem menuItem, String screen){
        String tmp = "-";
        String title=null;
        if(menuItem!=null) title=menuItem.getTitle().toString();
        if(title!=null && !title.isEmpty()) tmp=title;
        getSupportActionBar().setTitle(tmp);

    }



    //navigation menu auto-scrolling
    private void initMenuItemsPositions(){
        ArrayList<SublimeBaseMenuItem> menuItems = mOptionsMenu.getMenuItems();
        for(int i=0;i<menuItems.size();i++){
            int id = menuItems.get(i).getItemId();
            mOptionsMenu.getMenuItem(id).setPosition(i);
        }
    }



    private void scrollSVNtoCurrentItem(){  ///ffffffffffffffffffffffff
        if(mCurrentMenuItem!=null) {
            int group = mCurrentMenuItem.getGroupId();
            if(group!=0){
                SublimeGroup sublimeGroup = mOptionsMenu.getGroup(group);
                if  (sublimeGroup!=null && sublimeGroup.isCollapsible() && sublimeGroup.isCollapsed()) sublimeGroup.setStateCollapsed(false);
            }

            int pos=mCurrentMenuItem.getPosition();
            vSvnMenu.smoothScrollToPosition(pos);
        }
    }

//ini menu item and screen

    private void setCurrentMenuAndScreen(int mode){

        if(mCurrentMenu==0) mSVN.switchMenuTo(mOptionsMenu);
        else mSVN.switchMenuTo(mConfigMenu);

        switch (mode){
            case Common.INI_MODE_INSTANCE:
                if(mCurrentMenuItem!=null) {
                    int tmp = mCurrentMenuItem.getItemId();
                    mCurrentMenuItem = mOptionsMenu.getMenuItem(tmp);
                    mCurrentScreen = getResources().getResourceEntryName(mCurrentMenuItem.getItemId());
                }
                break;
            case Common.INI_MODE_INTENT:
                mCurrentMenuItem = getMenuItem(mCurrentScreen);
                if(mCurrentMenuItem == null) mCurrentScreen="";
                break;
            case Common.INI_MODE_NORMAL:
                if(mRememberScreen) {
                    mCurrentScreen=readLastScreenSavedValue();
                    if(!mCurrentScreen.isEmpty()) mCurrentMenuItem = getMenuItem(mCurrentScreen);
                }else showInfoFragment();
                break;

            case Common.INI_MODE_FORCED:
                mCurrentScreen="";

                mCurrentMenuItem=null;
                showInfoFragment();
                return;

        }

        SublimeGroup group;
        ArrayList<SublimeGroup> g = mOptionsMenu.getMenuGroups();
        if(g!=null) mNumberOfGroups = g.size();
        if(mCurrentMenuItem!=null) {
            SublimeGroup svng = mOptionsMenu.getGroup(mCurrentMenuItem.getGroupId());
            if(svng!=null){
                if(svng.isCollapsed()) svng.setStateCollapsed(false);
            }
        }

    }

    private SublimeBaseMenuItem getMenuItem(String screen){
        int i = getResources().getIdentifier(screen, "id", getApplicationContext().getPackageName());
        if(i!=0) return mOptionsMenu.getMenuItem(i);
        return null;
    }


    private void setSNVMenuHeaderChecked(int groupid, boolean checked){
        SublimeGroup group = mOptionsMenu.getGroup(groupid);
        if(group==null ) return;
        SublimeGroupHeaderMenuItem headerMenuItem = null;
        int headerid = group.getheaderId();
        try {
            headerMenuItem = (SublimeGroupHeaderMenuItem) mOptionsMenu.getMenuItem(headerid);
        }catch (ClassCastException e){
            return;
        }
        if (headerMenuItem==null) return;
        headerMenuItem.setChecked(checked);

    }


    private void updateDividerHeight(){
        if(PrefScreenFragment!=null) PrefScreenFragment.updateDividerHeight(mDividerHeight);
    }


    private void showFABPostionDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_FAV_POS);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_FAV_POS).commit();
    }


    private void showExitAppDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_EXIT_CONFIRM);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_EXIT_CONFIRM).commit();
    }

    private void showDividerHeightDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_DIV_HEIGHT);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_DIV_HEIGHT).commit();
    }


    private void showThemeSelectionDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_SET_THEME);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_SET_THEME).commit();
    }

    private void showNavigationHeaderDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_SET_BG_PANEL_HEADER);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_SET_BG_PANEL_HEADER).commit();
    }

    private void showColorPickerStyleDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_SET_COLORPICKER_STYLE);
        getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_SET_COLORPICKER_STYLE).commit();
    }


    private void restartApp() {
        finish();  //hay que reiniciar para que en ambas orientaciones pille bien el cambio de posición y las preferencias restauradas
        this.overridePendingTransition(0,R.animator.fadeout);
        startActivity(new Intent(this, GrxSettingsActivity.class));
        Common.buildContextWrapper(this);
        this.overridePendingTransition(R.animator.fadein, 0);

    }

    private void updateUserConfigCheckBox(String userOption, boolean state){

        switch(userOption){
            case "grx_mid_drawer_right":
                Common.sp.edit().putBoolean(Common.S_APPOPT_DRAWER_POS,state).commit();
                restartApp();
                break;

            case "grx_mid_show_fab":
                Common.sp.edit().putBoolean(Common.S_APPOPT_SHOW_FAV,state).commit();
                mShowFab=state;
                updateMainFABVisibility();
                updateRecentsFloatingAreaOptions();
                break;

            case "grx_mid_remember_screen":
                Common.sp.edit().putBoolean(Common.S_APPOPT_REMEMBER_SCREEN,state).commit();
                mRememberScreen=state;
                break;
            case "grx_mid_groups_open":
                Common.sp.edit().putBoolean(Common.S_APPOPT_MENU_GROUPS_ALWAYS_OPEN,state).commit();
                mGroupsExpanded=state;
                updateGroupButtonsUserOptionCheckBox();
                updateNavigationButtons();
                break;
            case "grx_mid_groups_buttons":
                mShowExpandCollapseButtons=state;
                Common.sp.edit().putBoolean(Common.S_APPOPT_SHOW_COLLAPSE_EXPAND_BUTTONS,state).commit();
                updateNavigationButtons();
                break;
            case "grx_mid_exit_confirmation":
                Common.sp.edit().putBoolean(Common.S_APPOPT_EXIT_CONFIRM,state).commit();
                mExitConfirmation=state;
                break;
            case "grx_mid_fw_show": //enable recent screens floating window
                Common.sp.edit().putBoolean(Common.S_APPOPT_FW_ENABLE,state).commit(); //fffffffffffff
                updateRecentsFloatingAreaOptions();
                break;

            /* to do : for now always back key hides floating area or we could have problems with some dialogs */

            case "grx_mid_fw_dismiss_back":
                Common.sp.edit().putBoolean(Common.S_APPOPT_RFW_EXIT_WITH_BACK,state).commit();
                updateRecentsFloatingAreaOptions();
                break;


            case "grx_mid_fw_dismiss_outside":
                Common.sp.edit().putBoolean(Common.S_APPOPT_RFW_EXIT_OUTSIDE,state).commit();
                updateRecentsFloatingAreaOptions();
                break;




            default: break;
        }
    }


    public void showSNVHeaderBgSelection(){
        if(mDrawer.isDrawerVisible(mSVN)) mDrawer.closeDrawer(mSVN);
        String nav_header_file = Common.IconsDir + File.separator+getString(R.string.grxs_nav_header_bg_image_name);
        File f = new File(nav_header_file);
        if(!f.exists()){
            startImagePicker();
        }else {
            showNavigationHeaderDialog();
        }
    }

    public void do_fragment_gallery_image_picker(Intent intent){
        intent.createChooser(intent, getResources().getString(R.string.grxs_selecc_image_usando));
        startActivityForResult(intent,Common.REQ_CODE_GALLERY_IMAGE_PICKER_FROM_FRAGMENT);
    }

    private void startImagePicker(){
        Intent intent = new Intent(this, GrxImagePicker.class);
        int ancho = getResources().getDimensionPixelSize(R.dimen.snv_navigation_max_width);
        int alto = getResources().getDimensionPixelSize(R.dimen.svn_nav_header_height);
        intent = GrxImageHelper.intent_avatar_img(intent, ancho, alto);
        intent.createChooser(intent, getResources().getString(R.string.grxs_selecc_image_usando));
        startActivityForResult(intent,Common.REQ_CODE_GALLERY_IMAGE_PICKER_FROM_GRXAJUSTES);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        boolean error=false;
        if(resultCode== Activity.RESULT_OK) {
            switch (requestCode){
                case Common.REQ_CODE_GALLERY_IMAGE_PICKER_FROM_FRAGMENT:
                    String dest_fragment_tag = data.getStringExtra(Common.TAG_DEST_FRAGMENT_NAME_EXTRA_KEY);
                    DialogFragment dialogFragment = null;
                    if(dest_fragment_tag!=null) {
                        dialogFragment = (DialogFragment) getFragmentManager().findFragmentByTag(dest_fragment_tag);
                        if(dialogFragment!=null) dialogFragment.onActivityResult(requestCode,resultCode,data);
                    }
                    break;
                case Common.REQ_CODE_GALLERY_IMAGE_PICKER_FROM_GRXAJUSTES:
                    String sFile = data.getStringExtra(GrxImagePicker.S_DIR_IMG);
                    if(sFile!=null) {
                        setSNVHeaderImg(sFile);
                    }else showSnack("IMG ERROR!!");
                    break;
                case Common.REQ_CODE_GALLERY_IMAGE_PICKER_JUST_URI:
                case Common.REQ_CODE_GALLERY_IMAGE_PICKER_CROP_CIRCULAR:
                    if(PrefScreenFragment!=null){
                        PrefScreenFragment.onImagePickerResult(data,requestCode);
                    }
                    break;
                default:
                    break;
            }
        }else super.onActivityResult(requestCode,resultCode,data);
    }


    private void setSNVHeaderImg(String header_img){
        Bitmap bitmap = GrxImageHelper.load_bmp_image(header_img);
        String nav_header_file = Common.IconsDir + File.separator+getString(R.string.grxs_nav_header_bg_image_name);
        GrxImageHelper.save_png_from_bitmap(bitmap,nav_header_file);
        GrxPrefsUtils.deleteFileFromStringName(header_img);
        updateSVNHeader();
    }


    private void showInfoFragment(){
        PrefScreenFragment=null;
        mCurrentScreen="";
        GrxInfoFragment info_fragment= new GrxInfoFragment();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.gid_content,info_fragment, Common.TAG_INFOFRAGMENT).commit();
        String title = getResources().getString(R.string.gs_rom_name);
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(title==null ? "?":title);

    }

    private void showHelpFragment(){
        PrefScreenFragment=null;
        mCurrentScreen="";
        GrxHelpFragment grxHelpFragment = new GrxHelpFragment();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(!mDrawerInLeftPosition) fragmentTransaction.setCustomAnimations(R.animator.enter_animation_r,R.animator.exit_animator_r);
        else fragmentTransaction.setCustomAnimations(R.animator.enter_animation,0);
        fragmentTransaction.replace(R.id.gid_content,grxHelpFragment, Common.TAG_INFOFRAGMENT).commit();
        String title = getResources().getString(R.string.gs_rom_name);
        if(getSupportActionBar()!=null) getSupportActionBar().setTitle(title==null ? "?":title);

    }

    private void updateSVNGroups(){
        ArrayList<SublimeGroup> g = mOptionsMenu.getMenuGroups();
        for (int i = 0; i < g.size(); i++) {
            if(g.get(i)!=null){
                g.get(i).setIsCollapsible(!mGroupsExpanded);
            }
        }
    }

    private void updateMainFABPosition(){

        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        switch (mFabPosition){
            case 0:
                p.gravity = Gravity.BOTTOM|Gravity.CENTER;
                break;
            case 1:
                p.gravity = Gravity.BOTTOM|Gravity.LEFT;
                break;
            case 2:
                p.gravity = Gravity.BOTTOM|Gravity.RIGHT;
                break;

        }

        fab.setLayoutParams(p);
    }

    private void updateMainFABVisibility(){
        if(mShowFab) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.INVISIBLE);
        mConfigMenu.getMenuItem(R.id.grx_mid_fab_position).setEnabled(mShowFab);
    }


    private void hideExpandCollapseNavigationButtons(){
        vExpandCollapseButtons.setVisibility(View.INVISIBLE);
        vSvnMenu.setPadding(0,0,0,mPaddinOffVGrButtons);

    }

    private void showExpandColapseNavigationButtons(){
        vExpandCollapseButtons.setVisibility(View.VISIBLE);
        vSvnMenu.setPadding(0,0,0,mPaddinOnVGrButtons);
    }



    private void updateNavigationButtons(){
        mExpandCollapseVisible=false;
        if(mNumberOfGroups!=0) {
            if(!mGroupsExpanded && mShowExpandCollapseButtons) mExpandCollapseVisible=true;
            if(mExpandCollapseVisible){
                if(mCurrentMenu==1) hideExpandCollapseNavigationButtons();
                else showExpandColapseNavigationButtons();
            }else hideExpandCollapseNavigationButtons();
        }else hideExpandCollapseNavigationButtons();
    }


    private void updateRecentsFloatingAreaOptions(){
        mShowFloatingRecentsWindow = Common.sp.getBoolean(Common.S_APPOPT_FW_ENABLE,true);
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_show).setChecked(mShowFloatingRecentsWindow);
        if(!mShowFab){
            mShowFloatingRecentsWindow=false;
        }
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_show).setEnabled(mShowFab);


        mHideFloatingAreaWithBack = Common.sp.getBoolean(Common.S_APPOPT_RFW_EXIT_WITH_BACK,false);
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_dismiss_back).setChecked(mHideFloatingAreaWithBack);
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_dismiss_back).setEnabled(mShowFloatingRecentsWindow);


        boolean hideoutside = Common.sp.getBoolean(Common.S_APPOPT_RFW_EXIT_OUTSIDE,false);
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_dismiss_outside).setChecked(hideoutside);
        mConfigMenu.getMenuItem(R.id.grx_mid_fw_dismiss_outside).setEnabled(mShowFloatingRecentsWindow);

        if(mShowFloatingRecentsWindow) {
            if(mFloatingRecentsWindow==null) addFloatingRecentScreensWindow();
            mFloatingRecentsWindow.setHideOptions(/*hidewithback*/true, hideoutside);
        }else {
            if(mFloatingRecentsWindow!=null){
                getWindowManager().removeViewImmediate(mFloatingRecentsWindow);
                mFloatingRecentsWindow=null;
            }
        }
    }


    private void expandSNVMenuGropus(){
        SublimeGroup group;
        ArrayList<SublimeGroup> g = mOptionsMenu.getMenuGroups();
        for (int i = 0; i < g.size(); i++) {
            if(g.get(i)!=null){
                group=g.get(i);
                if(group.isCollapsed()) g.get(i).setStateCollapsed(false);
            }
        }
    }

    private void collapseSNVMenuGropus(){
        SublimeGroup group;
        if(mOptionsMenu!=null){
            ArrayList<SublimeGroup> g = mOptionsMenu.getMenuGroups();
            for (int i = 0; i < g.size(); i++) {
                if(g.get(i)!=null){
                    group = g.get(i);

                    if(!group.isCollapsed()) g.get(i).setStateCollapsed(true);
                    //g.get(i).setIsCollapsible(!mGroupsExpanded);
                }
            }
        }
    }


    private void removeSNVMenuGroupsBasedOnBuildProperty(){
        SublimeGroup group;
        List<Integer> delete_ids = new ArrayList<>();
        ArrayList<SublimeGroup> g = mOptionsMenu.getMenuGroups();
        for (int i = 0; i < g.size(); i++) {
            if(g.get(i)!=null){
                group=g.get(i);
                if(!group.is_enabled_by_BPRule()){
                    delete_ids.add(group.getGroupId());
                }
            }
        }
        for (int i=0; i<delete_ids.size();i++) mOptionsMenu.removeGroup(delete_ids.get(i));
    }


    private void saveLastScreenValueToSHP(){
        if(!Common.SyncUpMode){
            if(mCurrentScreen==null) mCurrentScreen="";
            Common.sp.edit().putString(Common.S_AUX_LAST_SCREEN,mCurrentScreen).commit();
        }
    }

    private String readLastScreenSavedValue(){
        return Common.sp.getString(Common.S_AUX_LAST_SCREEN,"");
    }




    @Override
    public void onBackPressed() {
        if(mShowFloatingRecentsWindow &&mFloatingRecentsWindow.getVisibility()==View.VISIBLE && mHideFloatingAreaWithBack) {
            hideFloatingWindow();
            return;
        }

        boolean control;
        if (mDrawer.isDrawerOpen(mSVN)) {
            mDrawer.closeDrawers();
        }else{
            if(PrefScreenFragment==null) control=true;
            else control =PrefScreenFragment.processBackPressed();
            if(control){
                if(!mExitConfirmation) super.onBackPressed();
                else showExitAppDialog();
            }
        }
    }

    /***********************************************************************/
    /************************* APP MENUS ************************************/
    /***********************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.three_dots_menu, menu);
        if(!getResources().getBoolean(R.bool.grxb_demo_mode)) menu.removeItem(R.id.menu_buildprop);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_backup) {
            hideFloatingWindow();
            showBackupDialog();
            return true;
        }
        if (id == R.id.menu_restaurar){
            showRestoreDialog();
            return true;
        }
        if (id == R.id.menu_buildprop){
            showBuildPropDemoDialog();
            return true;
        }

        if (id == R.id.reset_all){
            showResetAllPreferencesDialog();
            return true;
        }

        if( id == R.id.tools) {
            mSelectedTool = -1;
            DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_TOOLS);
            getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_TOOLS).commit();
        }

        return super.onOptionsItemSelected(item);
    }


    private void showBuildPropDemoDialog(){

        if(!Common.IsRooted){
            showToast(getResources().getString(R.string.grxs_bp_noroot));
            return;
        }

        final String[] options =this.getResources().getStringArray(R.array.demo_build_prop_options);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        String curr_property_val = RootUtils.getProp(getResources().getString(R.string.gs_bp_property));
        int selected = 0;
        if(curr_property_val!=null && !curr_property_val.isEmpty()){

            for(int i = 0; i<options.length;i++) {
                if(options[i].equals("grx.demo.prop"+" "+curr_property_val)) {
                    selected=i;
                    break;
                }
            }

        }
        adb.setSingleChoiceItems(options, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RootUtils.runCommand("setprop "+options[i]);
                String curr_property_val = RootUtils.getProp(getResources().getString(R.string.gs_bp_property));
                showToast(getResources().getString(R.string.grxs_bp_restart));
                restartApp();
            }
        });
        adb.setTitle(getResources().getString(R.string.grxs_bp_title,curr_property_val));
        adb.create().show();

    }







    /********************************************************************************/
    /************************* COMMON  **********************************************/
    /********************************************************************************/

    private void showInfoDialg(String title, String message){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ab.create().show();
    }



    private int getAppVersion(){
        int app_version= -1;
        try{
            app_version = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return app_version;
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    private void showSnack(String mensaje){
        Snackbar snackbar = Snackbar.make(mToolbar, mensaje, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(mSnackBarBgColor);
        snackbar.show();

    }


    private void setRecentsTaskDescriptionIconAndBg(){
        //lets make nicer how the app in showed in recents.. this code shows recents_icon.png in recents and fix the bg color
        TypedArray a = this.getTheme().obtainStyledAttributes( new int[] {R.attr.colorPrimary});
        int bgcolor =a.getColor(0,0);
        a.recycle();
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getString(R.string.grxs_app_name),
                GrxImageHelper.drawableToBitmap(getDrawable(R.drawable.recents_icon)),bgcolor);
        setTaskDescription(taskDescription);
    }
    /**************************************************************************************************/
    ///////////////////******************** PREFERENCES SYNCHRONIZATION *****************************////
    /**************************************************************************************************/

    public void finishPreferencesSynchronization(){
        final ProgressBar progressBar = mProgressbar;
        // if(!mCurrentScreen.isEmpty() && mCurrentMenuItem!=null) showPreferencesScreen(mCurrentMenuItem,mCurrentScreen);
        Common.SyncUpMode = false;
        if(this.getResources().getBoolean(R.bool.grxb_global_enable_settingsdb)) {
            for (String groupkey : Common.GroupKeysList) {
                GrxPrefsUtils.changePreferenceGroupKeyValue(this,groupkey);
            }
        }
        for (String broadcastaction : Common.BroadCastsList) {
            if(broadcastaction!=null) {
                if(broadcastaction.contains(";")){
                    String[] array = broadcastaction.split(";");
                    GrxPrefsUtils.sendPreferenceBroadCastWithExtra(this, array[0], array[1],false);
                }
                else GrxPrefsUtils.sendPreferenceBroadcaast(this,broadcastaction,true);

            }


        }
        for(String extra : Common.CommonBroadCastList){
            String[] arra = extra.split(";");
            GrxPrefsUtils.sendCommonBroadCastExtraDelayed(this,arra[0],arra[1],true);
        }

        Common.GroupKeysList.clear();
        Common.BroadCastsList.clear();
        Common.CommonBroadCastList.clear();;

        runOnUiThread(new Runnable() {
            public void run() {
                showToast(getString(R.string.grxs_sync_end));
                progressBar.setVisibility(View.GONE);
            }
        });

        setCurrentMenuAndScreen(Common.INI_MODE_FORCED);

    }



    public void synchronizeNextScreen(){
        if(mNumSyncScreens<ResXML.size()){
            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            GrxPreferenceScreen prefsScreen = GrxPreferenceScreen.newInstance(ResXML.get(mNumSyncScreens),"","",0);
            fragmentTransaction.replace(R.id.gid_content,prefsScreen, Common.TAG_PREFSSCREEN_FRAGMENT_SYNC).commit();
        }else finishPreferencesSynchronization();
    }


    public void onPreferenceScreenSinchronized(int num_prefs){
        mNumSyncPrefs+=num_prefs;
        mNumSyncScreens++;
        synchronizeNextScreen();
    }


    private void synchronizePreferences(){
        final ProgressBar progressBar = mProgressbar;
        Common.SyncUpMode=false;
        boolean needsync = Common.sp.getBoolean(Common.S_CTRL_SYNC_NEEDED,true);
        if(needsync){
            if(getNumOfAvailableScreens()!=0){
                if(Common.GroupKeysList!=null) Common.GroupKeysList.clear();
                if(Common.BroadCastsList!=null) Common.BroadCastsList.clear();
                if(Common.CommonBroadCastList!=null) Common.CommonBroadCastList.clear();
                mNumSyncPrefs=0;
                mNumSyncScreens=0;
                Common.SyncUpMode = true;
                Common.sp.edit().putBoolean(Common.S_CTRL_SYNC_NEEDED,false).commit();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        synchronizeNextScreen();
                    }
                });
            }
        }
    }



    private int getNumOfAvailableScreens(){
        SublimeBaseMenuItem menuItem;
        int id;
        String screen_name;
        int id_xml;
        int num_screens=0;

        ArrayList<SublimeBaseMenuItem> options_menu=mOptionsMenu.getMenuItems();
        ResXML = new HashMap<>();

        for(int i=0;i<options_menu.size();i++){
            id=0;
            id_xml=0;
            menuItem = options_menu.get(i);
            if(menuItem!=null) id=menuItem.getItemId();
            if(id!=0){
                try{
                    screen_name = getResources().getResourceEntryName(id);
                    if(screen_name!=null && ( !screen_name.isEmpty() ) ) id_xml = getResources().getIdentifier(screen_name, "xml", getApplicationContext().getPackageName());
                    if(id_xml!=0) {
                        ResXML.put(num_screens,getResources().getResourceEntryName(id_xml));
                        num_screens++;
                    }
                }catch (Exception e){}
            }
        }
        return ResXML.size();
    }


    /********************************************************************************/
    /**************************** PREFERENCES BACKUP *********************************/
    /********************************************************************************/

    private void showBackupDialog(){
        mEditText = null;
        AlertDialog.Builder adb= new AlertDialog.Builder(this);
        adb.setTitle(R.string.grxs_tit_backup);
        View view = getLayoutInflater().inflate(R.layout.dlg_backup,null);
        mEditText = (EditText) view.findViewById(R.id.gid_backup_name);
        TextView info= (TextView) view.findViewById(R.id.gid_backup_info);
        info.setText(getString(R.string.grxs_info_backup));
        mEditText.append("backup_");
        adb.setNegativeButton(R.string.grxs_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String backup_name="";
                if(mEditText!=null) backup_name= mEditText.getText().toString();
                dialog.dismiss();
                showBackupConfirmationDialog(backup_name);
            }
        });
        adb.setView(view);
        adb.create().show();
    }

    private void showBackupConfirmationDialog(String backup_name){

        if(backup_name==null || backup_name.isEmpty()) showSnack(getString(R.string.grxs_no_valid_name));
        else{
            File f = new File(Common.BackupsDir+File.separator+backup_name+"."+ getString(R.string.grxs_backups_files_extension));
            if(f.exists()) showOverWriteBackupDialog(backup_name);
            else showBackupResult(doPreferencesBackUp(backup_name));
        }
    }
    private void showBackupResult(String result){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.grxs_tit_backup);
        ab.setMessage(result);
        ab.setPositiveButton(R.string.grxs_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.create().show();
    }


    private String doPreferencesBackUp(String backup_name){
        mProgressbar.setVisibility(View.VISIBLE);
        Common.sp.edit().putBoolean(Common.S_CTRL_SYNC_NEEDED,true).commit();
        Common.sp.edit().putInt(Common.S_CTRL_APP_VER,getAppVersion()).commit();
        boolean error=false;
        String serror="";
        ObjectOutputStream oos = null;
        FileOutputStream fos;
        File f = new File(Common.BackupsDir+File.separator+backup_name+"."+getString(R.string.grxs_backups_files_extension));
        try{
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
        }catch (Exception e){
            serror=e.toString();
            error=true;
        }
        if(!error){
            try{
                Map<String,?> prefes = Common.sp.getAll();
                if(oos!=null) oos.writeObject(prefes);
            }catch (Exception e){
                error = true;
                serror=e.toString();
            }
        }
        if(!error){
            try{
                if(oos!=null) {
                    oos.flush();
                    oos.close();
                }
            }catch (Exception e){
                error = true;
                serror=e.toString();
            }
        }

        String res;
        if(error) res = "Error: "+serror;
        else res = getString(R.string.grxs_backup_ok)+" :  "+backup_name+"."+getString(R.string.grxs_backups_files_extension);


        if(!error) {

            String ori_icons_dir = Common.IconsDir + File.separator;
            String dest_icons_dir = Common.BackupsDir + File.separator + backup_name + File.separator + getString(R.string.grxs_data_icons_subfolder) + File.separator;
            GrxPrefsUtils.deleteFileOrCreateFolder(dest_icons_dir, ".png");
            GrxPrefsUtils.copyFilesWithExtension(ori_icons_dir, dest_icons_dir, ".png");
            GrxPrefsUtils.fixFolderPermissions(dest_icons_dir, ".png");
            GrxPrefsUtils.deleteFileOrCreateFolder(dest_icons_dir, ".jpg");
            GrxPrefsUtils.copyFilesWithExtension(ori_icons_dir, dest_icons_dir, ".jpg");
            GrxPrefsUtils.fixFolderPermissions(dest_icons_dir, ".jpg");
        }
        mProgressbar.setVisibility(View.GONE);
        return res;

    }

    private void showOverWriteBackupDialog(final String backup_name){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.grxs_tit_sobreescribir_backup);
        ab.setMessage(getString(R.string.grxs_mens_sobreescribir_backup, backup_name+"."+getString(R.string.grxs_backups_files_extension)));
        ab.setPositiveButton(getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showBackupResult(doPreferencesBackUp(backup_name));
            }
        });
        ab.setNegativeButton(getString(R.string.grxs_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.create().show();

    }

    /********************************************************************************/
    /**************************** RESET ALL PREFERENCES DIALG ***********************/
    /********************************************************************************/

    public void showResetAllPreferencesDialog(){
        DlgFrGrxNavigationUserOptions dlg = DlgFrGrxNavigationUserOptions.newInstance(Common.INT_ID_APPDLG_RESET_ALL_PREFERENCES);
            getFragmentManager().beginTransaction().add(dlg, Common.S_APPDLG_RESET_ALL_PREFERENCES).commit();


    }


    /********************************************************************************/
    /**************************** RESTORE PREFERENCES   ****************************/
    /********************************************************************************/

    /*** main dialog */

    public void showRestoreDialog(){
        if(mRestoreDialog!=null) mRestoreDialog.dismiss();
        mRestoreDialog = DlgFrRestore.newInstance(this,getString(R.string.grxs_restore_title), getString(R.string.grxs_restore_message), DlgFrRestore.RESTORE_STATE.ASK_BACKUP);
        mRestoreDialog.show(getFragmentManager(),"DlgFrRestore");

    }

    /* DlgFrRestore interface implementation */

    public void processDlgFrRestoreAction(boolean ok, DlgFrRestore.RESTORE_STATE next_action, String info){
        DlgFrRestore frRestore = (DlgFrRestore) getFragmentManager().findFragmentByTag("DlgFrRestore");
        if(frRestore!=null) {
            getFragmentManager().beginTransaction().remove(frRestore).commit();
        }
        mRestoreDialog=null;
        switch (next_action){
            case CONFIRM_BACKUP_FILE:
                mRestoreDialog = DlgFrRestore.newInstance(this,getString(R.string.grxs_restore_title), info,DlgFrRestore.RESTORE_STATE.CONFIRM_BACKUP_FILE);
                mRestoreDialog.show(getFragmentManager(),"DlgFrRestore");
                break;
            case RESTORING_BACKUP:
                showSnack(info);
                RestorePreferencesTask task = new RestorePreferencesTask(this, info);
                task.execute();
                break;
            case ERROR:
                showSnack(info);
                break;
        }
    }


    /***** Restore AsyncTask **/

    private class RestorePreferencesTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String mRestoreResultText="";
        private boolean mRestoreResult=true;
        private String backup_file_name;
        private Context context;

        private void restorePreferences(){
            int contador=0;
            boolean error=false;
            String serror="";
            ObjectInputStream ois = null;
            FileInputStream fis;
            SharedPreferences sp=Common.sp;
            File f = new File(Common.BackupsDir+File.separator+backup_file_name+"."+getString(R.string.grxs_backups_files_extension));
            try{
                fis = new FileInputStream(f);
                ois = new ObjectInputStream(fis);
            }catch (Exception e){
                serror=e.toString();
                error=true;
            }
            if(!error){
                sp.edit().clear().commit();
                try{
                    Map map= (Map) ois.readObject();
                    Set set = map.entrySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext()){
                        contador++;
                        Map.Entry entrada = (Map.Entry) iterator.next();
                        String clave = (String) entrada.getKey();
                        if( entrada.getValue() instanceof Boolean ){
                            Boolean b = (Boolean) entrada.getValue();
                            sp.edit().putBoolean(clave,b.booleanValue()).commit();
                        }else if(entrada.getValue() instanceof Float){
                            Float flo = (Float) entrada.getValue();
                            sp.edit().putFloat(clave,flo.floatValue()).commit();

                        }else if (entrada.getValue() instanceof Integer){
                            Integer ent = (Integer) entrada.getValue();
                            sp.edit().putInt(clave,ent.intValue()).commit();
                        }else if (entrada.getValue() instanceof Long){
                            Long lo = (Long) entrada.getValue();
                            sp.edit().putLong(clave,lo.longValue()).commit();
                        }else if (entrada.getValue() instanceof String){
                            String str = (String) entrada.getValue();
                            sp.edit().putString(clave,str).commit();
                        }else if (entrada.getValue() instanceof Set){
                            Set s = (Set) entrada.getValue();
                            sp.edit().putStringSet(clave,s).commit();
                        }
                    }
                }catch (Exception e){
                    serror=e.toString();
                    error=true;
                }
            }
            if(ois!=null)
                try{
                    ois.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            String ret;
            if(error) mRestoreResultText = "ERROR: "+serror;
            else mRestoreResultText = getString(R.string.grxs_restore_result_message,backup_file_name+"."+getString(R.string.grxs_backups_files_extension),contador);
            if(!error){
                if(!error){
                    String  ori_icon_folder=Common.BackupsDir+File.separator+backup_file_name+File.separator+getString(R.string.grxs_data_icons_subfolder)+File.separator;
                    String dest_icon_folder=Common.IconsDir+File.separator;
                    GrxPrefsUtils.deleteFileOrCreateFolder(dest_icon_folder, ".png");
                    GrxPrefsUtils.copyFilesWithExtension(ori_icon_folder, dest_icon_folder,".png");
                    GrxPrefsUtils.fixFolderPermissions(dest_icon_folder, ".png");
                    GrxPrefsUtils.deleteFileOrCreateFolder(dest_icon_folder, ".jpg");
                    GrxPrefsUtils.copyFilesWithExtension(ori_icon_folder, dest_icon_folder,".jpg");
                    GrxPrefsUtils.fixFolderPermissions(dest_icon_folder, ".jpg");
                }
            }
            mRestoreResult=!error;
        }

        public RestorePreferencesTask(GrxSettingsActivity activity, String backcupname) {
            dialog = new ProgressDialog(activity);
            dialog.setCancelable(false);
            context = activity;
            backup_file_name=backcupname;
        }

        @Override
        protected void onPreExecute() {
            if(backup_file_name==null || backup_file_name.isEmpty()){
                cancel(true);
            }
            dialog.setMessage(context.getResources().getString(R.string.grxs_restoring_msg, backup_file_name));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if(dialog.isShowing()) dialog.dismiss();

        }

        private void showToast(String msg){
            final String txt = msg;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context,txt, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(!isCancelled()){
                restorePreferences();
                if(mRestoreResult) {
                    //Common.sp.edit().putInt(Common.S_APPOPT_USER_SELECTED_THEME,mTheme).commit(); //lets keep current theme
                    Common.sp.edit().putString(Common.S_APPOPT_USER_SELECTED_THEME_NAME,mThemeS);
                    Common.sp.edit().putBoolean(Common.S_CTRL_SYNC_NEEDED,true).commit();
                    dialog.dismiss();
                    synchronizePreferences();
                }else showToast(mRestoreResultText);
            }
            return null;
        }
    }


    /***********************************************************************************/
    /***************** USER NAVIGATION OPTIONS *****************************************/
    /***********************************************************************************/


    @Override
    public void onNavigationUserOptionSet(int tdialog, int opt){

        switch (tdialog){
            case Common.INT_ID_APPDLG_FAV_POS:
                mFabPosition=opt;
                Common.sp.edit().putInt(Common.S_APPOPT_FAB_POS,opt).commit();
                updateFABPositionInfo();
                updateMainFABPosition();

                break;
            case Common.INT_ID_APPDLG_DIV_HEIGHT:
                mDividerHeight=opt;
                Common.sp.edit().putInt(Common.S_APPOPT_DIV_HEIGHT,opt).commit();
                updateDividerInfo();
                Common.cDividerHeight=mDividerHeight;
                updateDividerHeight();

                break;

            case  Common.INT_ID_APPDLG_EXIT_CONFIRM:
                if(opt!=0) this.finish();
                break;

            case Common.INT_ID_APPDLG_SET_THEME:
                mThemeS=getResources().getStringArray(R.array.grxa_theme_values)[opt];
                Common.sp.edit().putString(Common.S_APPOPT_USER_SELECTED_THEME_NAME,mThemeS).commit();

                /*
                Common.sp.edit().putInt(Common.S_APPOPT_USER_SELECTED_THEME,opt).commit();*/
                restartApp();
                break;

            case Common.INT_ID_APPDLG_SET_COLORPICKER_STYLE:  //GRXGRX
                String cpstyle = Common.sp.getString(Common.S_APPOPT_COLORPICKERSTYLE,getString(R.string.grxs_colorPickerStyle_default));
                cpstyle=Common.getColorPickerStyleString(opt);
                Common.sp.edit().putString(Common.S_APPOPT_COLORPICKERSTYLE,cpstyle).commit();
                Common.userColorPickerStyle=cpstyle;
                updateColorPickerStyleInfo();
                break;

            case Common.INT_ID_APPDLG_SET_BG_PANEL_HEADER:
                switch (opt){
                    case 0:
                        String nav_header_file = Common.IconsDir + File.separator+getString(R.string.grxs_nav_header_bg_image_name);
                        GrxPrefsUtils.deleteFileFromStringName(nav_header_file);
                        updateSVNHeader();
                        break;
                    case 1:  startImagePicker();
                        break;
                }
                break;
            case Common.INT_ID_APPDLG_RESET_ALL_PREFERENCES:
                        Common.sp.edit().clear().commit();
                        restartApp();
                        break;

            case Common.INT_ID_APPDLG_TOOLS:
                    mSelectedTool = opt;
                if(!Common.IsRooted){
                    showToast(getResources().getString(R.string.grxs_noroot_action_possible));
                    return;
                }
                    switch (mSelectedTool) {
                        case 0: // recovery
                            RootPrivilegedUtils.runRebootInRecoveryMode();
                            break;
                        case 1: //restart ui
                            doKillPackage("com.android.systemui");
                            break;
                        case 2: // reboot phone
                            RootPrivilegedUtils.runRebootDeviceCommands();
                            break;
                    }

                    break;

        }
    }

    private void updateSVNHeader(){
        boolean color_bg = true;
        if(getResources().getBoolean(R.bool.grxb_allow_panelheader_changes)){
            String nav_header_file = Common.IconsDir + File.separator+getString(R.string.grxs_nav_header_bg_image_name);
            File f = new File(nav_header_file);
            if(f.exists()){
                upadePanelHeaderBgInfo(getString(R.string.grxs_image));
                Bitmap bitmap = GrxImageHelper.load_bmp_image(nav_header_file);
                if(bitmap!=null){
                    FrameLayout header = (FrameLayout) mSVN.getHeaderView().findViewById(R.id.gid_snv_header_container);
                    if(header!=null) {
                        header.setBackground(new BitmapDrawable(bitmap));
                        color_bg = false;
                    }
                }
            }else upadePanelHeaderBgInfo(getString(R.string.grxs_default));
        }
        if(color_bg){
            FrameLayout header = (FrameLayout) mSVN.getHeaderView().findViewById(R.id.gid_snv_header_container);
            if(header!=null){
                TypedArray a = this.getTheme().obtainStyledAttributes( new int[] {R.attr.svn_nav_header_bg});
                if(header!=null) header.setBackgroundColor(a.getColor(0,0));
                a.recycle();
            }
        }
    }


    private void updateFABPositionInfo(){
        String[] arr= getResources().getStringArray(R.array.grxa_fab_position);
        String tmp = mConfigMenu.getMenuItem(R.id.grx_mid_fab_position).getClass().getSimpleName();
        if(tmp.equals("SublimeTextWithBadgeMenuItem")) {
            SublimeTextWithBadgeMenuItem item = (SublimeTextWithBadgeMenuItem) mConfigMenu.getMenuItem(R.id.grx_mid_fab_position);
            item.setBadgeText(arr[mFabPosition]);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_fab_position).setHint(arr[mFabPosition]);
    }

    private void upadePanelHeaderBgInfo(String text){
        String tmp = mConfigMenu.getMenuItem(R.id.grx_mid_header_svn_back).getClass().getSimpleName();
        if(tmp.equals("SublimeTextWithBadgeMenuItem")) {
            SublimeTextWithBadgeMenuItem item = (SublimeTextWithBadgeMenuItem) mConfigMenu.getMenuItem(R.id.grx_mid_header_svn_back);
            item.setBadgeText(text);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_header_svn_back).setHint(text);
    }

    private void updateThemeTextInfo(){
        if(!getResources().getBoolean(R.bool.grxb_allow_theme_change)) return;
        String[] arr= getResources().getStringArray(R.array.grxa_theme_list);
        mThemeS=mThemeS=Common.sp.getString(Common.S_APPOPT_USER_SELECTED_THEME_NAME,getString(R.string.grxs_default_theme));
        int pos = 0;
        String[] values = getResources().getStringArray(R.array.grxa_theme_values);
        for(int i = 0; i< values.length; i++){
            if(values[i].equals(mThemeS)) {
                pos = i;
                break;
            }
        }
        String tmp = mConfigMenu.getMenuItem(R.id.grx_mid_theme).getClass().getSimpleName();
        if(tmp.equals("SublimeTextWithBadgeMenuItem")) {
            SublimeTextWithBadgeMenuItem item = (SublimeTextWithBadgeMenuItem) mConfigMenu.getMenuItem(R.id.grx_mid_theme);
            item.setBadgeText(arr[pos]);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_theme).setHint(arr[pos]);

    }

    private void updateDividerInfo(){

        String[] arr= getResources().getStringArray(R.array.grxa_divider_height);
        String tmp = mConfigMenu.getMenuItem(R.id.grx_mid_divider_height).getClass().getSimpleName();
        if(tmp.equals("SublimeTextWithBadgeMenuItem")) {
            SublimeTextWithBadgeMenuItem item = (SublimeTextWithBadgeMenuItem) mConfigMenu.getMenuItem(R.id.grx_mid_divider_height);
            item.setBadgeText(arr[mDividerHeight]);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_divider_height).setHint(arr[mDividerHeight]);
    }


    private void updateColorPickerStyleInfo(){
        int index = Common.getColorPickerStyleIndex(Common.userColorPickerStyle);
        String[] arr = getResources().getStringArray(R.array.grxa_colorpickerstyles);
        String tmp = mConfigMenu.getMenuItem(R.id.grx_mid_color_picker_style).getClass().getSimpleName();
        if(tmp.equals("SublimeTextWithBadgeMenuItem")) {
            SublimeTextWithBadgeMenuItem item = (SublimeTextWithBadgeMenuItem) mConfigMenu.getMenuItem(R.id.grx_mid_color_picker_style);
            item.setBadgeText(arr[index]);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_color_picker_style).setHint(arr[index]);
    }

    private void updateGroupButtonsUserOptionCheckBox(){
        if(mGroupsExpanded) mConfigMenu.getMenuItem(R.id.grx_mid_groups_buttons).setEnabled(false);
        else mConfigMenu.getMenuItem(R.id.grx_mid_groups_buttons).setEnabled(true);
    }

    /*****************************************************************************************/
    /************************ INIT MAIN APP ELEMENTS *****************************************/
    /*****************************************************************************************/




    private void initSublimeNavigationView(){
        mSVN = (SublimeNavigationView) findViewById(R.id.gid_snv_view);
        vSvnMenu = mSVN.getMenuView(); //hay que aplicar el padding en SublimeNavMenuView para poder activar o no los botones de grupo y que no se monte
        vExpandCollapseButtons = (LinearLayout) findViewById(R.id.gid_buttons_container);
        mPaddinOnVGrButtons=mSVN.getPaddingBottom(); //truco para no convertir he dejado inicialmente el padding que me interesa y lo consigo con el método getpadding
        vSvnMenu = mSVN.getMenuView(); //hay que aplicar el padding en SublimeNavMenuView para poder activar o no los botones de grupo y que no se monte
        mPaddinOffVGrButtons = vSvnMenu.getPaddingBottom(); //padding inicial a aplicar al menuview del sublime para cuando se desactivan los botones de grupo
        mSVN.setPadding(0,0,0,0); //dejamos la view como debe, con padding 0. En el xml dejé lo que quería.. por vaguería, je je..
        vExpandCollapseButtons.setPadding(0,0,0,0);
        vExpandButton = (LinearLayout) vExpandCollapseButtons.findViewById(R.id.gid_button_expand);
        vCollapseButton = (LinearLayout) vExpandCollapseButtons.findViewById(R.id.gid_button_close);
        vCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseSNVMenuGropus();
            }
        });
        vExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandSNVMenuGropus();
            }
        });




    }




    private void changeOfScreen(SublimeBaseMenuItem menuItem){
        String tmp_screen_name = getResources().getResourceEntryName(menuItem.getItemId());
        if(mCurrentScreen.equals(tmp_screen_name)== false){
            mCurrentScreen=tmp_screen_name;
            int currgroup = 0;
            if(mCurrentMenuItem!=null) {

                mCurrentMenuItem.setChecked(false);
                currgroup =  mCurrentMenuItem.getGroupId();
            }

            /*let´s tint group arrows properly */
            int nexgroup = menuItem.getGroupId();
            if(currgroup!=nexgroup) setSNVMenuHeaderChecked(currgroup, false);
            setSNVMenuHeaderChecked(nexgroup, true);
            /*  check new menu item and change pref screen */
            menuItem.setChecked(true);
            mCurrentMenuItem = menuItem;
            mChangeScreenAfterDrawerClosed=true;
            mDrawer.closeDrawers();
            saveLastScreenValueToSHP();
        }
    }



    private void initMenuNavigation(){

        mSVN.setNavigationMenuEventListener(new OnNavigationMenuEventListener() {

            @Override
            public boolean onNavigationMenuEvent(Event event, SublimeBaseMenuItem menuItem) {
                String userOption;
                boolean state;
                if (mCurrentMenu==0){
                    switch (event) {
                        case GROUP_EXPANDED:
                            break;
                        case GROUP_COLLAPSED:
                            break;
                        default:
                            changeOfScreen(menuItem);
                            break;
                    }
                }
                else {

                    switch (event){

                        case CHECKED:
                            userOption = getResources().getResourceEntryName(menuItem.getItemId());
                            state = true;
                            updateUserConfigCheckBox(userOption, state);
                            break;
                        case UNCHECKED:
                            userOption = getResources().getResourceEntryName(menuItem.getItemId());
                            state = false;
                            updateUserConfigCheckBox(userOption, state);
                            break;
                        default:
                            try{
                                userOption = getResources().getResourceEntryName(menuItem.getItemId());
                            }catch (Resources.NotFoundException e){
                                return true;
                            }

                            switch (userOption){
                                case "grx_mid_theme":
                                    showThemeSelectionDialog();;
                                    break;

                                case "grx_mid_header_svn_back":
                                    showSNVHeaderBgSelection();
                                    break;
                                case "grx_mid_rom_info":
                                    showInfoFragment();
                                    if(mDrawer.isDrawerVisible(mSVN)) mDrawer.closeDrawer(mSVN);
                                    saveLastScreenValueToSHP();
                                    break;
                                case "grx_mid_app_help":
                                    showHelpFragment();
                                    if(mDrawer.isDrawerVisible(mSVN)) mDrawer.closeDrawer(mSVN);
                                    saveLastScreenValueToSHP();
                                    break;

                                case "grx_mid_color_picker_style":
                                    showColorPickerStyleDialog();
                                    break;
                                //grxgrx

                                default:break;
                            }


                            if (userOption.equals("grx_mid_fab_position")) {

                                showFABPostionDialog();
                            }
                            else {
                                if (userOption.equals("grx_mid_divider_height")) {
                                    showDividerHeightDialog();
                                }
                            }
                            break;
                    }
                }

                return true;
            }
        });
    }

    private void initUserConfigFAB(){
        mFabSvn = (FloatingActionButton) mSVN.getHeaderView().findViewById(R.id.gid_user_options_fab);
        mFabSvn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentMenu) {
                    case 0:
                        if(mExpandCollapseVisible) hideExpandCollapseNavigationButtons();
                        mSVN.switchMenuTo(mConfigMenu);
                        mCurrentMenu=1;

                        break;
                    case 1:
                        if(mExpandCollapseVisible) showExpandColapseNavigationButtons();
                        mSVN.switchMenuTo(mOptionsMenu);
                        mCurrentMenu=0;
                        updateSVNGroups();
                        if (mCurrentMenuItem!=null){
                            if(mOptionsMenu.getGroup(mCurrentMenuItem.getGroupId())!=null) mOptionsMenu.getGroup(mCurrentMenuItem.getGroupId()).setStateCollapsed(false);
                        }
                        break;
                }
            }

        });
    }


    private void initSublimeNavigationMenus(){
        //there is a problem in sublime not saving correctly svn state in some circumstances (f.e. changing fonts..) -> unmarshalling problems because of class not found
        //so the menus state is saved in the activity.
        if(mOptionsMenu==null){ //no saved instance state
            mCurrentMenu=0;
            if(!getResources().getBoolean(R.bool.grxb_demo_mode)) {  //if not demo
                mOptionsMenu=mSVN.getMenu(); // clear list
                if(mOptionsMenu!=null) mOptionsMenu.clear();
                mSVN.switchMenuTo(R.menu.rom_navigation_screens); //create rom options menu
            }
            mOptionsMenu = mSVN.getMenu();
            mSVN.switchMenuTo(R.menu.user_options_menu); //create config menu
            mConfigMenu = mSVN.getMenu();
            removeNonAuthorizedUserOptions();
        }else {  //saved instance state -> clear default xml sublime menu
            SublimeMenu tmp = mSVN.getMenu();
            if(tmp!=null) tmp.clear();
        }
        removeSNVMenuGroupsBasedOnBuildProperty();

    }



    private void removeNonAuthorizedUserOptions(){
        if(!getResources().getBoolean(R.bool.grxb_allow_panelheader_changes)){
            mConfigMenu.removeItem(R.id.grx_mid_header_svn_back);
        }

        if(!getResources().getBoolean(R.bool.grxb_allow_user_colorpicker_selection)){
            mConfigMenu.removeItem(R.id.grx_mid_color_picker_style);
        }

        if(!getResources().getBoolean(R.bool.grxb_allow_theme_change)){
            mConfigMenu.removeItem(R.id.grx_mid_theme);
        }
        boolean isdemo = getResources().getBoolean(R.bool.grxb_demo_mode);
        String mTabsLayouts[]=  isdemo ? getResources().getStringArray(R.array.demo_tabs_layouts) : getResources().getStringArray(R.array.rom_tabs_layouts);

        if(mTabsLayouts==null){
            mConfigMenu.removeItem(R.id.grx_mid_rom_info);
        }
    }


    private void initSharedPreferencesAndValues(){
        try {
            Common.sp = getBaseContext().createPackageContext(getPackageName(),CONTEXT_IGNORE_SECURITY).getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            finish();
        }
        Common.IconsDir= Environment.getExternalStorageDirectory().toString() + File.separator + getString(R.string.grxs_data_base_folder)+File.separator+getString(R.string.grxs_data_icons_subfolder);
        Common.CacheDir = getCacheDir().getAbsolutePath();
        GrxPrefsUtils.createFolder(Common.IconsDir);

        Common.BackupsDir = Environment.getExternalStorageDirectory().toString() + File.separator + getString(R.string.grxs_data_base_folder)+File.separator+"backups";
        GrxPrefsUtils.createFolder(Common.BackupsDir);

        int iconsize = getResources().getDimensionPixelSize(R.dimen.icon_size_in_prefs);
        Common.AndroidIconParams = new LinearLayout.LayoutParams(iconsize, iconsize);
        Common.GroupKeysList=new HashSet<>();
        Common.BroadCastsList=new HashSet<>();
        Common.CommonBroadCastList=new HashSet<>();
    }

    private void setDrawerLayoutPosition(){
        boolean mDrawerInOppositePosition =Common.sp.getBoolean(Common.S_APPOPT_DRAWER_POS, Common.DEF_VAL_DRAWER_POS);
        boolean mIsRTL = this.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

        if(!mIsRTL) {
            if(!mDrawerInOppositePosition) mDrawerInLeftPosition = true;
            else mDrawerInLeftPosition = false;
        } else {
            if(mDrawerInOppositePosition) mDrawerInLeftPosition = true;
            else mDrawerInLeftPosition = false;
        }

        if(!mDrawerInLeftPosition) mDrawerLayoutParams = new android.support.v4.widget.DrawerLayout.LayoutParams ((int) getResources().getDimension(R.dimen.ancho_panel), ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        else mDrawerLayoutParams = new android.support.v4.widget.DrawerLayout.LayoutParams ((int) getResources().getDimension(R.dimen.ancho_panel), ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT);
    }


    private void initToolBar(){

        mToolbar = (Toolbar) findViewById(R.id.gid_toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.gid_snv_drawer_layout);


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.grxs_navigation_drawer_open, R.string.grxs_navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                doChangeOfScreenOnDrawerClosed();
            }

        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mDrawer.isDrawerVisible(mSVN) ) {
                    if(!mDrawerInLeftPosition) mDrawer.closeDrawer(Gravity.RIGHT);
                    else mDrawer.closeDrawer(Gravity.LEFT);
                }else {
                    if(!mDrawerInLeftPosition) mDrawer.openDrawer(Gravity.RIGHT);
                    else mDrawer.openDrawer(Gravity.LEFT);
                }
            }

        });


        TypedArray a = this.getTheme().obtainStyledAttributes( new int[] {R.attr.snackbar_bg});
        mSnackBarBgColor=a.getColor(0,0);
        a.recycle();

    }

    private void doChangeOfScreenOnDrawerClosed(){
        if(mChangeScreenAfterDrawerClosed){
            setToolbarTitle(mCurrentMenuItem,mCurrentScreen);
            showPreferencesScreen(mCurrentMenuItem,mCurrentScreen);
            showSnack(mCurrentMenuItem.getTitle().toString());
            mChangeScreenAfterDrawerClosed=false;

        }
    }

    private void setMainFABListeners(){

        fab = (com.fab.FloatingActionButton) findViewById(R.id.gid_main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(mSVN);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mShowFloatingRecentsWindow) {
                    if(mFloatingRecentsWindow.getVisibility()==View.VISIBLE) mFloatingRecentsWindow.setVisibility(View.GONE);
                    else {
                        mFloatingRecentsWindow.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }
        });

    }

    public void hideFloatingWindow(){
        if(mShowFloatingRecentsWindow) mFloatingRecentsWindow.setVisibility(View.GONE);
    }

    private void setUserTheme(){

        mThemeS=Common.sp.getString(Common.S_APPOPT_USER_SELECTED_THEME_NAME,getString(R.string.grxs_default_theme));

        int theme = getResources().getIdentifier(mThemeS,"style",getApplicationContext().getPackageName());

        if(theme!=0) {
            setTheme(theme);
        }

        return;

    }

    private void readUserConfigOptions(){

        mSVN.setLayoutParams(mDrawerLayoutParams);
        // drawer.setLayoutParams(mDrawerLayoutParams);
        mConfigMenu.getMenuItem(R.id.grx_mid_drawer_right).setChecked(Common.sp.getBoolean(Common.S_APPOPT_DRAWER_POS, Common.DEF_VAL_DRAWER_POS));

        mShowFab = Common.sp.getBoolean(Common.S_APPOPT_SHOW_FAV, Common.DEF_VAL_SHOW_FAB);
        mConfigMenu.getMenuItem(R.id.grx_mid_show_fab).setChecked(mShowFab);

        mRememberScreen = Common.sp.getBoolean(Common.S_APPOPT_REMEMBER_SCREEN, getResources().getBoolean(R.bool.grxb_remember_screen_default));
        mConfigMenu.getMenuItem(R.id.grx_mid_remember_screen).setChecked(mRememberScreen);

        mGroupsExpanded=Common.sp.getBoolean(Common.S_APPOPT_MENU_GROUPS_ALWAYS_OPEN, Common.DEF_VAL_GROUPS_ALWAYS_OPEN);
        mConfigMenu.getMenuItem(R.id.grx_mid_groups_open).setChecked(mGroupsExpanded);

        mShowExpandCollapseButtons=Common.sp.getBoolean(Common.S_APPOPT_SHOW_COLLAPSE_EXPAND_BUTTONS, Common.DEF_VAL_SHOW_COL_EXP_BUTTONS );
        mConfigMenu.getMenuItem(R.id.grx_mid_groups_buttons).setChecked(mShowExpandCollapseButtons);

        if(mGroupsExpanded){
            mConfigMenu.getMenuItem(R.id.grx_mid_groups_buttons).setEnabled(false);
        }else mConfigMenu.getMenuItem(R.id.grx_mid_groups_buttons).setEnabled(true);

        mExitConfirmation = Common.sp.getBoolean(Common.S_APPOPT_EXIT_CONFIRM, Common.DEF_VAL_EXIT_CONFIRM);

        mConfigMenu.getMenuItem(R.id.grx_mid_exit_confirmation).setChecked(mExitConfirmation);

        mFabPosition = Common.sp.getInt(Common.S_APPOPT_FAB_POS, Common.DEF_VAL_FAB_POS);

        mDividerHeight =  Common.sp.getInt(Common.S_APPOPT_DIV_HEIGHT, getResources().getInteger(R.integer.grxi_default_list_divider_height));

        Common.cDividerHeight=mDividerHeight;

        Common.allowUserColorPickerStyle=getResources().getBoolean(R.bool.grxb_allow_user_colorpicker_selection);
        Common.userColorPickerStyle=Common.sp.getString(Common.S_APPOPT_COLORPICKERSTYLE,getString(R.string.grxs_colorPickerStyle_default));

        updateRecentsFloatingAreaOptions();

    }


    /************************************* REBOOT AND KILL APP ************************************/


    public void rebootDevice(boolean showdialog){
        if(showdialog){
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setCancelable(true);
            ab.setNegativeButton(getString(R.string.grxs_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            ab.setPositiveButton(getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doReboot();
                }
            }) ;
            ab.setTitle(getString(R.string.grxs_reboot_dialog_title));
            ab.setMessage(getString(R.string.grxs_reboot_dialog_message));
            ab.create().show();

        }else  doReboot();

    }

    public void doReboot(){
        boolean granted = Common.IsRebootPermissionGranted;
        if(granted) RootPrivilegedUtils.rebootDevicePrivileged(this);
        else {
            if(Common.IsRooted ) {
                RootPrivilegedUtils.runRebootDeviceCommands();
            }else showInfoDialg(getString(R.string.grxs_reboot_problem),getString(R.string.grxs_reboot_not_possible));
        }
    }


    public void killPackage(boolean showdialog, String label, String packagename){
        if(!Common.IsRooted) {
            showToast(getString(R.string.grxs_noroot_action_possible));
            showInfoDialg(getString(R.string.grxs_killpackage_dialog_title,label),getString(R.string.grxs_noroot_action_possible));
            return;
        }
        if(showdialog){
            final String packagetokill = packagename;
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setCancelable(true);
            ab.setNegativeButton(getString(R.string.grxs_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            ab.setPositiveButton(getString(R.string.grxs_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doKillPackage(packagetokill);
                }
            }) ;
            ab.setTitle(getString(R.string.grxs_killpackage_dialog_title,label));
            ab.setMessage(getString(R.string.grxs_killpackage_dialog_message,label));
            ab.create().show();
        }else doKillPackage(packagename);
    }


    private void doKillPackage(String packagename){
        String Command = getString(R.string.grxs_kill_command) + " " + packagename;
        RootPrivilegedUtils.runShellCmd(true,Command);

    }


    /*** get root bg task, to avoid the app to crash if phone not rooted */


    public void showRootState(){
        if(!Common.IsRooted) showToast(getString(R.string.grxs_app_not_rooted));
    }

    public class getSuBgTask extends  AsyncTask<Void, Void, Boolean> {
        GrxSettingsActivity mActivity;

        @Override
        protected Boolean doInBackground(Void... voids) {
            RootPrivilegedUtils.checkRootGranted();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isGranted) {
            if(mActivity!=null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRootState();
                    }
                });
            }
        }

        public void setActivity(GrxSettingsActivity activity){
            mActivity = activity;
        }
    };


}
