package com.example.reubert.appcadeirantes.utilities;

import android.content.Context;
import android.support.v7.widget.Toolbar;

public class LayoutManager {

    public static void enableTransparentStatusBar(Context activity, Toolbar toolbar){
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }

        toolbar.setPadding(toolbar.getPaddingLeft(), statusBarHeight, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
    }

}
