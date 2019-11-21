package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    private StringRequest request;
    public Volley v;
    private EditText email;
    private EditText password;
    private Button login,register,forgotpassword;
    private Patterns pt = new Patterns();
    private String TAG = "LOGIN";
    private String emailtxt,passwordtxt;
    private Alerts alert = new Alerts(this);
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.loginbtn);
        register = (Button) findViewById(R.id.registerbtn);
        forgotpassword = (Button) findViewById(R.id.forgotbtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();

                if(emailtxt.isEmpty()||passwordtxt.isEmpty()){
                    String message = "Please Enter all details";
                    //Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                   // t.show();
                    alert.showDialog(message);
                }
                else if(!emailtxt.matches(pt.emailpattern)){
                    String message = "Please enter a valid email address";
                    //Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    //t.show();
                    alert.showDialog(message);
                }
                else if(!passwordtxt.matches(pt.passwordpattern)){
                    String message = "Minimum password length should be atleast 6 characters and a maximum of" +
                            "10 characters. The password should have at least one lowercase letter and at least one uppercase letter";
                    //Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                   // t.show();
                    alert.showDialog(message);
                }
                else{
                    //log in
                    loginuser(emailtxt,passwordtxt);
                }

            }
        });

    }
    private void loginuser(final String emailtxt,final String passwordtxt){
        url = AppConfig.login;
        queue = new Volley().newRequestQueue(getApplicationContext());
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading.Please wait");
        pd.setCancelable(false);
        pd.show();
        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("true")) {
                    Log.d(TAG,"Login successfull");
                    SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Email",emailtxt);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                    startActivity(intent);
                }
                else{
                    String message = "Invalid email/password.Please try again";
                    //Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                    //t.show();
                    alert.showDialog(message);
                }
                pd.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                //Toast t = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                //t.show();
                alert.showDialog(message);
                pd.hide();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",emailtxt);
                params.put("password",passwordtxt);
                return params;
            }
        };
        queue.add(request);
    }
}
