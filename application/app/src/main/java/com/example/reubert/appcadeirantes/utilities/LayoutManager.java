package com.example.reubert.appcadeirantes.utilities;

import android.app.Activity;
import android.support.v7.widget.Toolbar;

public class LayoutManager {

    public void enableTransparentStatusBar(Activity activity){
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }


    }

}
