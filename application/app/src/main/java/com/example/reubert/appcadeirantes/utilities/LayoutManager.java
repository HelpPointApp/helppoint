package com.example.reubert.appcadeirantes.utilities;

import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;

public class LayoutManager {

    public static void normalizePasswordAppearance(TextView passwordEdit){
        passwordEdit.setTypeface(Typeface.DEFAULT);
        passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
    }

}
