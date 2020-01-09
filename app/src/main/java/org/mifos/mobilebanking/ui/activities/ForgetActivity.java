package org.mifos.mobilebanking.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.BaseURL;

import java.util.HashMap;
import java.util.Map;

public class ForgetActivity extends AppCompatActivity {
    EditText et_email,et_phone_number;
    Button btn_send;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        et_phone_number=findViewById(R.id.et_phone_number);
        et_email=findViewById(R.id.et_email);
        relativeLayout = findViewById(R.id.ScrollForget);
        btn_send = findViewById(R.id.btn_client);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                final String email = et_email.getText().toString().trim();
                final String phone = et_phone_number.getText().toString().trim();
                 if (phone.equals("")){
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Please input phone number", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher( email).matches() || email.equals("")) {
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Please input valid email address", Snackbar.LENGTH_LONG);
                    snackbar.show();
        
                } else {
                    sendCode();
                }
                
            }
        });
    }
    
    private void sendCode() {
        //submit data
        final String email = et_email.getText().toString().trim();
        final String phone = et_phone_number.getText().toString().trim();
        
        
        
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Sending verification code...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseURL.CREATE_RESET_CODE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
    
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
    
                            if (success.equals("1")) {
                                Intent intent = new Intent(ForgetActivity.this, ResetActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("phone",phone);
                                startActivity(intent);
                                Toast.makeText(ForgetActivity.this, "Verification code sent... Please check your email or text messages, the code will expire after 10 minutes", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ForgetActivity.this, "Verification code not sent...Please confirm phone and email", Toast.LENGTH_SHORT).show();
                                
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                            Toast.makeText(ForgetActivity.this, "Verification code not sent...Please try again", Toast.LENGTH_SHORT).show();
    
                        }
    
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(ForgetActivity.this, "Verification code not sent...Please try again", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("phone", phone);
                return map;
            }
        };
        
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        
        
    }
    
}
