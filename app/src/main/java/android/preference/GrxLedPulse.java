
/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */


package android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grx.settings.GrxPreferenceScreen;
import com.grx.settings.R;
import com.grx.settings.prefs_dlgs.DlgFrAppLedPulse;
import com.grx.settings.utils.Common;
import com.grx.settings.utils.GrxPrefsUtils;
import com.qfcolorpicker.CircleColorDrawable;

import java.util.regex.Pattern;


public class GrxLedPulse extends GrxBasePreference implements DlgFrAppLedPulse.AppLedPulseListener  {

    TextView mPulseOnView, mPulseOffView;

    private boolean mShowPulse=true;

    String mColor="#FFFFFFFF";
    String mPulseOn;
    String mPulseOff;

    String mDefValue;

    public GrxLedPulse(Context context, AttributeSet attrs) {
        super(context,attrs);
        initAttributes(context, attrs);
    }

    public GrxLedPulse(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs){

      setWidgetLayoutResource(R.layout.pref_grxappledpulse_widget);
      initStringPrefsCommonAttributes(getContext(),attrs,false,false);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.grxledpulse);
        mShowPulse=ta.getBoolean(R.styleable.grxledpulse_showPulseOptions,true);
        ta.recycle();
      setDefaultValues();
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue,defaultValue);
       if(mStringValue==null || mStringValue.isEmpty()) mStringValue=mDefValue;
       refresValues(mStringValue);

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        if(getIcon()==null){
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.gid_icon_frame);
            if(linearLayout!=null) linearLayout.setVisibility(View.GONE);
        }
        mPulseOffView = (TextView) view.findViewById(R.id.gid_pulse_off);
        mPulseOnView = (TextView) view.findViewById(R.id.gid_pulse_on);
        vWidgetIcon.setVisibility(View.VISIBLE);
        if(!mShowPulse){
            mPulseOffView.setVisibility(View.GONE);
            mPulseOnView.setVisibility(View.GONE);
        }
        return view;
    }

  @Override
  public void showDialog(){
      DlgFrAppLedPulse dlg;
      GrxPreferenceScreen prefsScreen = (GrxPreferenceScreen) getOnPreferenceChangeListener();
      if(prefsScreen!=null){
          dlg = (DlgFrAppLedPulse) prefsScreen.getFragmentManager().findFragmentByTag(Common.TAG_DLGFRAPPLEDPULSE);
          if(dlg==null){
              dlg = DlgFrAppLedPulse.newInstance(this, Common.TAG_PREFSSCREEN_FRAGMENT, myPrefAttrsInfo.getMyKey(),myPrefAttrsInfo.getMyTitle(), true,false,false, mShowPulse,mStringValue);
              dlg.show(prefsScreen.getFragmentManager(),Common.TAG_DLGFRAPPLEDPULSE);
          }
      }

  }


    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        refresPrefView();
    }

 @Override
 public void configStringPreference(String value){
      if(value==null || value.isEmpty()) {
          mStringValue=mDefValue;
      }
    refresValues(mStringValue);
 }

  private void setDefaultValues()  {
      String defval = myPrefAttrsInfo.getMyStringDefValue();
      if(defval==null || defval.isEmpty()){
          mDefValue=(mShowPulse) ? "#FFFFFF;500;500" : "#ffffff";
          mColor="#FFFFFF";
          mPulseOn="500";
          mPulseOff="500";
      }else mDefValue=defval;
      setDefaultValue(mDefValue);


  }


   private void refresValues(String value){
       if(mShowPulse){
           String[] array = value.split(Pattern.quote(";"));
           mColor=array[0];
           mPulseOn=array[1];
           if(mPulseOn.equals("1")) {
               mPulseOn=getContext().getString(R.string.grxs_pulse_always_on);
               mPulseOff="-";
           }else {
               int tonpos = GrxPrefsUtils.getPositionInArray(getContext().getResources().getStringArray(R.array.grxa_ledpulse_ton_values),array[1],5);
               mPulseOn=getContext().getResources().getStringArray(R.array.grxa_ledpulse_ton_options)[tonpos];
               int toffpos = GrxPrefsUtils.getPositionInArray(getContext().getResources().getStringArray(R.array.grxa_ledpulse_toff_values),array[2],4);
               mPulseOff=getContext().getResources().getStringArray(R.array.grxa_ledpulse_toff_options)[toffpos];
           }

       }else mColor=value;

   }

    @Override
    public void resetPreference(){
        saveNewStringValue(mDefValue);
        configStringPreference(mStringValue);
    }


    @Override
    public void onAppLedPulseSet(String value){
      saveNewStringValue(value);
      refresValues(mStringValue);
    }

  private void refresPrefView(){
      CircleColorDrawable colorChoiceDrawable = null;
      Drawable currentDrawable = vWidgetIcon.getDrawable();
      if (currentDrawable!=null && currentDrawable instanceof CircleColorDrawable)
          colorChoiceDrawable = (CircleColorDrawable) currentDrawable;
      if (colorChoiceDrawable==null) {
          colorChoiceDrawable = new CircleColorDrawable(Color.parseColor(mColor));
      }
      vWidgetIcon.setImageDrawable(colorChoiceDrawable);

      if(mShowPulse){
          mPulseOnView.setText(mPulseOn);
          mPulseOffView.setText(mPulseOff);
      }

  }

}
