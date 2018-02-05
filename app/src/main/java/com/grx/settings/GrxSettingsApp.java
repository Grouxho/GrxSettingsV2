package com.grx.settings;

import android.app.Application;
import android.content.Context;


/*
 * Grouxho - espdroids.com - 2018

 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.

 */

public class GrxSettingsApp extends Application {
    private static Context AppContext;

    public static Context getContext() {
        return AppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = this;
    }

}
