package com.helppoint.app.utilities;

import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

public class LayoutManager {

    public static void normalizePasswordAppearance(EditText passwordEdit){
        passwordEdit.setTypeface(Typeface.DEFAULT);
        passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
    }

}
