package com.helppoint.app.wrappers;

import android.content.Context;

public class ProgressDialog {

    private static ProgressDialog instance;
    private android.app.ProgressDialog progressDialog;

    public static ProgressDialog getInstance(){
        if(instance == null){
            instance = new ProgressDialog();
        }
        return instance;
    }

    public void create(Context context, CharSequence title, CharSequence message){
        // Correcao para WindowLeaked
        //if(progressDialog == null){
        progressDialog = new android.app.ProgressDialog(context);
        //}

        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hide(){
        // Correcao para WindowLeaked
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
