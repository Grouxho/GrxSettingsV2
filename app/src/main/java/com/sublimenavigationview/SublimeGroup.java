/*
 * Copyright 2015 Vikram Kakkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sublimenavigationview;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Used for grouping menu items together.
 *
 * Created by Vikram.
 */
public class SublimeGroup implements Parcelable {

    public enum CheckableBehavior {NONE, SINGLE, ALL}

    private SublimeMenu mMenu;
    private int mGroupId;
    private boolean mIsCollapsible, mStateCollapsed, mEnabled, mVisible;
    private CheckableBehavior mCheckableBehavior;
    //grx build prop
    private String grxBpRule=null;
    private boolean mGrxIsBPenabled = true;
    private int mHeaderId=-1;




  /*  public SublimeGroup(SublimeMenu menu, int groupId,
                        boolean isCollapsible, boolean stateCollapsed,
                        boolean enabled, boolean visible,
                        CheckableBehavior checkableBehavior) {
        mMenu = menu;
        mGroupId = groupId;
        mIsCollapsible = isCollapsible;
        mStateCollapsed = stateCollapsed;
        mEnabled = enabled;
        mVisible = visible;
        mCheckableBehavior = checkableBehavior;
    }*/

    //grx build prop
    public SublimeGroup(SublimeMenu menu, int groupId,
                        boolean isCollapsible, boolean stateCollapsed,
                        boolean enabled, boolean visible,
                        CheckableBehavior checkableBehavior, String bprule) {
        mMenu = menu;
        mGroupId = groupId;
        mIsCollapsible = isCollapsible;
        mStateCollapsed = stateCollapsed;
        mEnabled = enabled;
        mVisible = visible;
        mCheckableBehavior = checkableBehavior;

        //grx build prop
        grxBpRule = bprule;
        if(grxBpRule!=null && !grxBpRule.isEmpty()) mGrxIsBPenabled  = is_BPEnabled(grxBpRule);

    }

    //grxgrx header id

    public void setHeaderId(int headerid){
        mHeaderId=headerid;
    }

    public int getheaderId(){
        return mHeaderId;
    }


    /**
     * Set this {@link SublimeGroup}'s Parent.
     *
     * @param menu parent menu
     */
    protected void setParentMenu(SublimeMenu menu) {
        mMenu = menu;
    }

    /**
     * Indicates whether this {@link SublimeGroup} is currently enabled/disabled.
     *
     * @return 'true' if this {@link SublimeGroup} is enabled, 'false' otherwise.
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Sets the enabled/disabled state for this {@link SublimeGroup}.
     *
     * @param enabled 'true' if this {@link SublimeGroup} is now 'enabled',
     *                'false' if it isn't.
     * @return this {@link SublimeGroup} for chaining
     */
    public SublimeGroup setEnabled(boolean enabled) {
        mEnabled = enabled;
        mMenu.onGroupEnabledOrDisabled(getGroupId(), enabled);
        return this;
    }

    /**
     * Returns the id assigned to this {@link SublimeGroup}.
     *
     * @return group's id
     */
    public int getGroupId() {
        return mGroupId;
    }

    /**
     * Sets/changes the 'collapsible' behavior of this Group.
     * If 'collapsible' is true, the group remains in its current
     * state, whether 'collapsed' or 'expanded' - a chevron appears
     * on the 'SublimeSubheaderItemView'.
     * If 'collapsible' is set to false:
     * - if group is 'collapsed', the group is expanded
     * and the chevron disappears
     * - if group is 'expanded', the chevron disappears
     *
     * @param collapsible 'true' to indicate that the group is
     *                    now 'collapsible', 'false' otherwise
     * @return this {@link SublimeGroup} for chaining
     */
    public SublimeGroup setIsCollapsible(boolean collapsible) {
        mIsCollapsible = collapsible;
        mMenu.onGroupCollapsibleStatusChanged(this);
        return this;
    }

    /**
     * Indicates if this {@link SublimeGroup} is collapsible.
     * Note that a {@link SublimeGroup} is _not_ collapsible if it does not
     * conatin any visible items.
     *
     * @return 'true' is this {@link SublimeGroup} is collapsible,
     * 'false' otherwise
     */
    public boolean isCollapsible() {
        return mIsCollapsible && mMenu.groupHasVisibleItems(getGroupId());
    }

    /**
     * Sets the collapsed/expanded status of this {@link SublimeGroup}.
     *
     * @param collapsed 'true' to collapse this {@link SublimeGroup}, 'false'
     *                  to expand it.
     * @return this {@link SublimeGroup} for chaining
     */
    public SublimeGroup setStateCollapsed(boolean collapsed) {
        mStateCollapsed = collapsed;
        mMenu.onGroupExpandedOrCollapsed(getGroupId(), collapsed);
        return this;
    }

    /**
     * Indicates whether this {@link SublimeGroup} is currently collapsed.
     *
     * @return 'true' if this {@link SublimeGroup} is collapsed,
     * 'false' if it is expanded
     */
    public boolean isCollapsed() {
        return mStateCollapsed;
    }

    /**
     * Set the checkable behavior for this Group. Following changes
     * will happen:
     * - NONE:     'setCheckable(false)' will be called on all group members.
     * Group members that are currently checked will NOT lose their
     * 'checked' state.
     * - ALL:      'setCheckable(true)' will be called on all group members.
     * Group members that are NOT currently checked will remain in
     * their 'unchecked' state.
     * - SINGLE:   'setChecked(false)' will be called on all group members
     * except the first 'checked' item (if one is found).
     *
     * @param checkableBehavior The {@link SublimeGroup.CheckableBehavior} to set.
     * @return this {@link SublimeGroup} for chaining
     */
    public SublimeGroup setCheckableBehavior(CheckableBehavior checkableBehavior) {
        mCheckableBehavior = checkableBehavior;
        mMenu.onGroupCheckableBehaviorChanged(getGroupId(), checkableBehavior);
        return this;
    }

    /**
     * Returns the {@link SublimeGroup.CheckableBehavior} set for this group.
     *
     * @return currently set {@link SublimeGroup.CheckableBehavior}
     */
    public CheckableBehavior getCheckableBehavior() {
        return mCheckableBehavior;
    }

    /**
     * Returns the visibility status of this group.
     *
     * @return 'true' if the group is visible, 'false' if invisible.
     */
    public boolean isVisible() {
        return mVisible;
    }

    /**
     * Set the visibility status of this group.
     *
     * @param visible 'true' if the group should now be visible, false otherwise
     * @return this {@link SublimeGroup} for chaining
     */
    public SublimeGroup setVisible(boolean visible) {
        mVisible = visible;
        mMenu.onGroupVisibilityChanged(getGroupId(), visible);
        return this;
    }

    //----------------------------------------------------------------//
    //---------------------------Parcelable---------------------------//
    //----------------------------------------------------------------//

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mGroupId);
        dest.writeByte((byte) (mIsCollapsible ? 1 : 0));
        dest.writeByte((byte) (mStateCollapsed ? 1 : 0));
        dest.writeByte((byte) (mEnabled ? 1 : 0));
        dest.writeByte((byte) (mVisible ? 1 : 0));
        dest.writeString(mCheckableBehavior.name());
    }

    protected SublimeGroup(Parcel in) {
        mGroupId = in.readInt();
        mIsCollapsible = in.readByte() != 0;
        mStateCollapsed = in.readByte() != 0;
        mEnabled = in.readByte() != 0;
        mVisible = in.readByte() != 0;
        mCheckableBehavior = CheckableBehavior.valueOf(in.readString());
    }

    public static final Creator<SublimeGroup> CREATOR
            = new Creator<SublimeGroup>() {
        public SublimeGroup createFromParcel(Parcel in) {
            return new SublimeGroup(in);
        }

        public SublimeGroup[] newArray(int size) {
            return new SublimeGroup[size];
        }
    };

    //grx build prop rule

    public static boolean is_BPEnabled(String BPRule){

        String[] rule = BPRule.split(Pattern.quote("#"));
        if(rule==null || rule.length!=3  ) return true;

        boolean rule_isenabled = (rule[0].toUpperCase().equals("ENABLE")) ? true : false;
      // String propValue = Utils.getBPProperty(rule[1]);
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

    //////grxgrx

    public boolean is_enabled_by_BPRule(){return mGrxIsBPenabled;}


}
