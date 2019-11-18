package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText fname,lname,email,password,phone,idnumber;
    private Button register,clear;
    private String fnametxt,lnametxt,emailtxt,passwordtxt,phonetxt,idnumbertxt;
    private StringRequest request;
    private RequestQueue queue;
    private Alerts alert = new Alerts();
    private Patterns pt = new Patterns();
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //textfields
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.phone);
        idnumber = (EditText) findViewById(R.id.idnumber);

        //buttons
        register = (Button) findViewById(R.id.registerbtn);
        clear = (Button) findViewById(R.id.clearbtn);

        // on register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnametxt = fname.getText().toString();
                lnametxt = lname.getText().toString();
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();
                phonetxt = phone.getText().toString();
                idnumbertxt = idnumber.getText().toString();

                if(fnametxt.isEmpty()||lnametxt.isEmpty()||emailtxt.isEmpty()||passwordtxt.isEmpty()||
                        phonetxt.isEmpty()||idnumbertxt.isEmpty()){
                    String message = "Please enter all details";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else if(!emailtxt.matches(pt.emailpattern)){
                    String message = "Please enter a valid email address";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else if(!fnametxt.matches(pt.namepattern)||!lnametxt.matches(pt.namepattern)){
                    String message = "Please enter a valid name";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else if(idnumbertxt.length()>9||idnumbertxt.length()<7){
                    String message = "Your ID number is invalid. The length should be between 7 and 9 characters";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else if(!passwordtxt.matches(pt.passwordpattern)){
                    String message = "Minimum password length should be atleast 6 characters and a maximum of" +
                            "10 characters. The password should have at least one lowercase letter and at least one uppercase letter";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else{
                    //register
                    adduser(fnametxt,lnametxt,emailtxt,passwordtxt,phonetxt,idnumbertxt);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname.setText("");
                lname.setText("");
                email.setText("");
                password.setText("");
                phone.setText("");
                idnumber.setText("");
            }
        });
    }
    private void adduser(final String fnametxt,final String lnametxt,final String emailtxt,final String passwordtxt,
                         final String phonetxt,final String idnumbertxt){
        url = AppConfig.register;
        queue = new Volley().newRequestQueue(getApplicationContext());
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading.Please wait");
        pd.setCancelable(false);
        pd.show();

        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equalsIgnoreCase("true")){
                    String message = "You have registered successfully";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                else{
                    String message = "A user with this ID Number already exists";
                    Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    t.show();
                    //alert.showDialog(message);
                }
                pd.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                t.show();
                //alert.showDialog(message);
                pd.hide();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("firstname",fnametxt);
                params.put("lastname",lnametxt);
                params.put("email",emailtxt);
                params.put("password",passwordtxt);
                params.put("phone",phonetxt);
                params.put("idnumber",idnumbertxt);
                return params;
            }

        };
        queue.add(request);
    }
}
