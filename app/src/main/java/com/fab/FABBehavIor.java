/* 
 * Grouxho - espdroids.com - 2018	

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 */
 
package com.fab;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

public class FABBehavIor extends CoordinatorLayout.Behavior<FloatingActionButton> {
public FABBehavIor() {
}

public FABBehavIor(Context context, AttributeSet attrs) {
super(context, attrs);
}

@Override
public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
return dependency instanceof Snackbar.SnackbarLayout;
}

@Override
public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
child.setTranslationY(translationY);
return true;
  }
}