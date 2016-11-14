package com.example.reubert.appcadeirantes.view;

import android.app.ProgressDialog;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.GPSManager;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RequestHelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_help);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.help_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final ParseUser user = User.getCurrentUser();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        progressDialog.setTitle("Ajuda");
        progressDialog.setMessage("Aguarde enquanto enviamos sua solicitação.");

        Button btnRequestHelp = (Button) findViewById(R.id.btnRequestHelp);
        btnRequestHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                EditText description = (EditText) findViewById(R.id.description);
                Location loc = GPSManager.getInstance(getBaseContext()).getUserLocation();
                int typeHelp = spinner.getSelectedItemPosition();

                Help.createHelp(
                        user, typeHelp, description.getText().toString(),
                        loc.getLatitude(), loc.getLongitude(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();

                        if(e == null){
                            finish();
                        }else{
                            Log.e("createhelp", e.toString());
                            builder.show();
                        }
                    }
                });
            }
        });
    }
}
