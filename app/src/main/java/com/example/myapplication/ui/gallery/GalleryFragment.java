package com.example.myapplication.ui.gallery;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.AppConfig;
import com.example.myapplication.Patterns;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    Patterns pt = new Patterns();
    AppConfig ac = new AppConfig();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_lipanampesa, container, false);
        final EditText phone = (EditText) root.findViewById(R.id.phoneno);
        final EditText amount = (EditText) root.findViewById(R.id.amount);
        final EditText description = (EditText) root.findViewById(R.id.description);
        final Button push = (Button) root.findViewById(R.id.pushstk);

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonetxt,amounttxt,descriptiontxt,message;
                phonetxt = phone.getText().toString();
                amounttxt = amount.getText().toString();
                descriptiontxt = description.getText().toString();
                int amount = Integer.parseInt(amounttxt);

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
                else if((amount<50)||(amount>99000)){
                    message = "Enter a valid amount.";
                    Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    t.show();
                }
                else if(!descriptiontxt.matches(pt.namepattern)){
                    message = "Enter a valid description.";
                    Toast t = Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
                    t.show();
                }
                else{

                }
            }
        });

        return root;
    }
    private void stkpush(final String phone,final String amount,final String description,final String paybill,final String date){
        RequestQueue queue;
        queue = new Volley().newRequestQueue(getActivity());
        String url = ac.stkpush;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                params.put("date",date);
                params.put("phone",phone);
                return params;
            }

        };
    }
}