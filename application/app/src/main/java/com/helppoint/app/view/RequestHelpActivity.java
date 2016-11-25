package com.helppoint.app.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.helppoint.app.appcadeirantes.R;
import com.helppoint.app.manager.GPSManager;
import com.helppoint.app.model.Help;
import com.helppoint.app.model.User;
import com.helppoint.app.wrappers.ProgressDialog;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class RequestHelpActivity extends AppCompatActivity {

    private Button btnRequestHelp;
    private Spinner spinnerHelpTypes;
    private EditText edtObservation;
    private ProgressDialog progressDialog;

    private Help creatingHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_help);
        loadElementsFromXML();
        loadServicesInstances();
        createElementsListeners();
        loadHelpTypesOnSpinner();
    }

    private void loadElementsFromXML(){
        btnRequestHelp = (Button) findViewById(R.id.btnRequestHelp);
        spinnerHelpTypes = (Spinner) findViewById(R.id.spnHelpTypes);
        edtObservation = (EditText) findViewById(R.id.edtObservation);
    }


    private void loadServicesInstances(){
        progressDialog = ProgressDialog.getInstance();
    }


    private void createElementsListeners(){
        btnRequestHelp.setOnClickListener(new RequestHelpButtonHandler());
    }


    private void loadHelpTypesOnSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.help_array, R.layout.spinner_component);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHelpTypes.setAdapter(adapter);
    }


    private void adviceAboutSendingRequest(){
        progressDialog.create(this, "Só mais um pouco, herói", "Estamos enviando sua solicitação...");
    }


    private class RequestHelpButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            adviceAboutSendingRequest();

            Location userLocation = GPSManager.getInstance(getBaseContext()).getUserLocation();
            int typeHelp = spinnerHelpTypes.getSelectedItemPosition();

            creatingHelp = Help.createHelp(
                    User.getCurrentUser(), typeHelp, edtObservation.getText().toString(),
                    userLocation.getLatitude(), userLocation.getLongitude(), new SaveHelp()
            );
        }
    }


    private class SaveHelp implements SaveCallback {
        @Override
        public void done(ParseException e) {
            progressDialog.hide();

            if(e == null){
                Intent intentData = new Intent();
                intentData.putExtra("objectId", creatingHelp.getObjectId());
                setResult(Activity.RESULT_OK, intentData);
                finish();
            }else{
                Log.e("createhelp", e.toString());
            }
        }
    }

}
