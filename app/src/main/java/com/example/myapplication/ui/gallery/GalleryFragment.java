package com.example.myapplication.ui.gallery;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.AppConfig;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Patterns;
import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private GalleryViewModel galleryViewModel;
    Patterns pt = new Patterns();
    AppConfig ac = new AppConfig();
    SharedPreferences preferences;
    SessionManager sessionManager;
    RequestQueue queue,queue1;
    StringRequest request,request1;
    TextView phonetf,responsetf,merchantidtf,descriptiontf;
    Button closebtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);

        sessionManager = new SessionManager(getActivity());
        preferences = this.getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        final String emailtxt = preferences.getString("Email","");
        //If email is null
        if(emailtxt ==""){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        View root = inflater.inflate(R.layout.fragment_lipanampesa, container, false);
        final EditText phone = (EditText) root.findViewById(R.id.phoneno);
        final EditText amount = (EditText) root.findViewById(R.id.amount);
        final EditText description = (EditText) root.findViewById(R.id.description);
        final Button push = (Button) root.findViewById(R.id.pushstk);

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phonetxt,amounttxt,descriptiontxt,message;
                phonetxt = phone.getText().toString();
                amounttxt = amount.getText().toString();
                descriptiontxt = description.getText().toString();

                if(phonetxt.isEmpty()||amounttxt.isEmpty()||descriptiontxt.isEmpty()){
                    message = "Please enter all fields";
                    Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    t.show();
                }
                else if((phonetxt.length()>12)||(phonetxt.length()<12)){
                    message = "Enter a valid phonenumber.";
                    Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    t.show();
                }
                else if(!descriptiontxt.matches(pt.namepattern)){
                    message = "Enter a valid description.";
                    Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    t.show();
                }
                else{
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading.Please wait");
                    pd.setCancelable(false);
                    pd.show();

                    queue1 = new Volley().newRequestQueue(getActivity());
                    String url = ac.getshortcode+"?email="+emailtxt;
                    request1 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String shortcode = jsonObject1.getString("shortcode");
                                    final Dialog dialog = new Dialog(getActivity());
                                    dialog.setCancelable(true);
                                    View output = getActivity().getLayoutInflater().inflate(R.layout.view_result,null);
                                    dialog.setContentView(output);
                                    dialog.show();
                                    //get textviews
                                    responsetf = (TextView) output.findViewById(R.id.response);
                                    merchantidtf  = (TextView) output.findViewById(R.id.merchantid);
                                    descriptiontf = (TextView) output.findViewById(R.id.description);
                                    phonetf = (TextView) output.findViewById(R.id.phone);
                                    closebtn = (Button) output.findViewById(R.id.closebtn);
                                    Log.d("TAG",shortcode);
                                    phonetf.setText(phonetxt);
                                    descriptiontf.setText(descriptiontxt);

                                     stkpush(phonetxt,amounttxt,descriptiontxt,shortcode);
                                     closebtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             dialog.dismiss();
                                         }
                                     });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            pd.hide();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errormessage = "Enter a valid description.";
                            Toast t = Toast.makeText(getActivity(),errormessage,Toast.LENGTH_LONG);
                            t.show();
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("email",emailtxt);
                            return params;
                        }
                    };
                    queue1.add(request1);
                }
            }
        });

        return root;
    }
    private void stkpush(final String phone,final String amount,final String description,final String paybill){
        queue = new Volley().newRequestQueue(getActivity());
        String url = ac.stkpush;
        request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    for(int i=0;i<jsonArray.length();i++){
                       JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                       String merchantid = jsonObject1.getString("merchantid");
                       String responsedesc = jsonObject1.getString("responsedesc");
                        Log.d("TAG",merchantid);
                       responsetf.setText(responsedesc);
                       merchantidtf.setText(merchantid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("shortcode",paybill);
                params.put("desc",description);
                params.put("amount",amount);
                params.put("phone",phone);
                return params;
            }

        };
        queue.add(request);
    }

    @Override
    public void onRefresh() {

    }
}