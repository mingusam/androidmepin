package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class ForgotPassword extends AppCompatActivity {
    private EditText email;
    private Button getemail;
    private String emailtxt;
    private StringRequest request;
    private RequestQueue queue;
    private Alerts alerts = new Alerts(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        //text
        email = (EditText) findViewById(R.id.email);
        //btn
        getemail = (Button) findViewById(R.id.getpassbtn);
        getemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtxt = email.getText().toString();
                if(emailtxt==""){
                    String message = "Please enter your email address";
                    alerts.showDialog(message);
                }
                else{

                }
            }
        });
    }
}
