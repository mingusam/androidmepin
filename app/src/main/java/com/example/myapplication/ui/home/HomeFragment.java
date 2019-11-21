package com.example.myapplication.ui.home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Alerts;
import com.example.myapplication.AppConfig;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Patterns;
import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    final Patterns pt = new Patterns();
    final Alerts alerts = new Alerts(getActivity());
    SessionManager sessionManager;
    SharedPreferences preferences;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_business, container, false);
        Button createbiz = (Button) root.findViewById(R.id.create_biz);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview);

        //get session
        sessionManager = new SessionManager(getActivity());
        preferences = this.getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        final String emailtxt = preferences.getString("Email","");
        //If email is null
        if(emailtxt ==""){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
        createbiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Add Business");
                dialog.setCancelable(true);
                View create = getActivity().getLayoutInflater().inflate(R.layout.create_business,null);
                dialog.setContentView(create);
                dialog.show();

                final EditText bizname = (EditText) create.findViewById(R.id.businessname);
                final EditText bizlocation = (EditText) create.findViewById(R.id.location);
                final EditText shortcode = (EditText) create.findViewById(R.id.shortcode);
                Button addbiz = (Button) create.findViewById(R.id.addbiz);


                addbiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String biztxt,loctxt,codetxt;
                        biztxt = bizname.getText().toString();
                        loctxt = bizlocation.getText().toString();
                        codetxt =shortcode.getText().toString();

                        if(biztxt.isEmpty()||loctxt.isEmpty()||codetxt.isEmpty()){
                            String message = "Enter all details please";
                            Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                            t.show();
                        }
                        else if(!biztxt.matches(pt.namepattern)){
                            String message = "Enter a correct business name";
                            Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                            t.show();
                        }
                        else if(!loctxt.matches(pt.namepattern)){
                            String message = "Enter a correct LOcation";
                            Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                            t.show();
                        }
                        else if((codetxt.length()>7)&&(codetxt.length()<5)){
                            String message ="Enter a valid Paybill Number";
                            Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                            t.show();
                        }
                        else{
                           addBusiness(emailtxt,biztxt,loctxt,codetxt);
                           dialog.dismiss();
                        }

                    }
                });


            }
        });
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
    private void addBusiness(final String email,final String businessname,final String businesslocation,
                             final String shortcode){
        AppConfig ac = new AppConfig();
        String url = ac.addbiz;
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading.Please wait");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = new Volley().newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equalsIgnoreCase("true")){
                    String message = "Business Added Successfully";
                    Toast toast = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    String message = "A business with either that name or shortcode already exists";
                    Toast toast = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    toast.show();
                }
                pd.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errormsg = error.toString();
                Toast toast = Toast.makeText(getActivity(),errormsg,Toast.LENGTH_LONG);
                toast.show();
                pd.hide();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("bizname",businessname);
                params.put("email",email);
                params.put("bizlocation", businesslocation);
                params.put("shortcode",shortcode);
                return params;
            }

        };
        queue.add(request);
    }
}