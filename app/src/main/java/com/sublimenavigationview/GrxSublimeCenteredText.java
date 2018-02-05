package com.sublimenavigationview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;

import com.grx.settings.R;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

public class GrxSublimeCenteredText extends SublimeBaseMenuItem {

    private static final String TAG = GrxSublimeCenteredText.class.getSimpleName();

    public GrxSublimeCenteredText(SublimeMenu menu, int group, int id,
                               CharSequence title, CharSequence hint,
                               boolean valueProvidedAsync, boolean showsIconSpace) {
        super(menu, group, id, title, hint, ItemType.CENTERED, valueProvidedAsync, false);


    }

    public GrxSublimeCenteredText(int group, int id,
                               CharSequence title, CharSequence hint,
                               int iconResId,
                               boolean valueProvidedAsync, boolean showsIconSpace,
                               int flags) {
        super(group, id, title, hint, iconResId, ItemType.CENTERED,
                valueProvidedAsync, false, flags);
    }

    @Override
    public boolean invoke() {
        return invoke(OnNavigationMenuEventListener.Event.CLICKED, this);
    }
}
