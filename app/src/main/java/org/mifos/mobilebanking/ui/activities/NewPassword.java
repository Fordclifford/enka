package org.mifos.mobilebanking.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class NewPassword extends AppCompatActivity {
    EditText editText,editText1;
    Button button;
    ScrollView scrollView;
    Intent intent;
    String email, phone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        
        intent = getIntent();
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        editText = findViewById(R.id.et_new_password);
        editText1 = findViewById(R.id.et_cpassword);
        button = findViewById(R.id.btn_submit);
        scrollView = findViewById(R.id.ScrollPassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pass = editText.getText().toString().trim();
                final String cpass = editText1.getText().toString().trim();
                if (pass.equals("")||cpass.equals("")){
                    Snackbar.make(scrollView,"Please input all fields", Snackbar.LENGTH_LONG).show();
                }else if (!pass.equals(cpass)){
                    Snackbar.make(scrollView,"Password does not match",Snackbar.LENGTH_LONG).show();
                }else {
                    submitPassword();
                }
        
            }
        });
    }
    
    
    private void submitPassword() {
        //submit data
        final String pass = editText.getText().toString().trim();
        
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Resetting password...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseURL.CREATE_NEW_PASSWORD,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            
                            
                            if (success.equals("1")) {
                                Toast.makeText(NewPassword.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(NewPassword.this, LoginActivity.class));
                                finish();
                            }else {
                                Toast.makeText(NewPassword.this, "Password reset failed... Please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                            Toast.makeText(NewPassword.this, "An error occurred...Please try again"+e.toString(), Toast.LENGTH_SHORT).show();
                            
                        }
                        
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(NewPassword.this, "No connection to host", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> map = new HashMap<>();
                map.put("password", pass);
                map.put("email",email);
                map.put("phone",phone);
                return map;
            }
        };
        
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        
        
    }
}
