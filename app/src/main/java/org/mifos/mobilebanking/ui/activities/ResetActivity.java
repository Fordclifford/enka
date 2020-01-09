package org.mifos.mobilebanking.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.BaseURL;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetActivity extends AppCompatActivity {
    PinView pinView;
    Button button;
    RelativeLayout relativeLayout;
    Intent intent;
    TextView textView, counter;
    String email,phone;
    CountDownTimer countDownTimer;
    long timeLeft = 600000;
    SmsVerifyCatcher smsVerifyCatcher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        
        intent = getIntent();
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        relativeLayout = findViewById(R.id.VerifyRelative);
        pinView = findViewById(R.id.pinView);
        textView = findViewById(R.id.ResendOTP);
        counter = findViewById(R.id.CountDown);
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }
            
            @Override
            public void onFinish() {
                counter.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                
            }
        }.start();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendCODE();
            }
        });
        pinView.setAnimationEnable(true);
        button = findViewById(R.id.SendCodeBTN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTP = pinView.getText().toString();
                if (OTP.equals("")) {
                    pinView.setLineColor(Color.RED);
                } else {
                    verifyUser();
                    
                }
                
            }
        });
    
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                pinView.setText(code);//set code in edit text
                //then you can send verification code to server
                if (!code.equals("")){
                    verifyUser();
                }
            }
        });
    }
    
    private void ResendCODE() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseURL.CREATE_CONFIRM_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")){
                                Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
                            }else if (success.equals("2")){
                                Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
    
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ResetActivity.this, "No connection to host", Toast.LENGTH_SHORT).show();
        
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email",email);
                return map;
            }
        };
        
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        
    }
    
    private void updateTimer() {
        int minutes = (int) timeLeft / 60000;
        int seconds = (int) timeLeft % 60000 / 1000;
        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;
        counter.setText("RESEND CODE IN" + " " + timeLeftText);
        
    }
    
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
    
    
    
    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    private void verifyUser() {
        final String OTP = pinView.getText().toString();
    
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseURL.CREATE_CONFIRM_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
    
                                if (success.equals("1")) {
                                    Intent intent = new Intent(ResetActivity.this, NewPassword.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("phone",phone);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
                                } else if (success.equals("2")) {
                                    Toast.makeText(ResetActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(ResetActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ResetActivity.this, "No connection to host", Toast.LENGTH_SHORT).show();
            
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("code",OTP);
                map.put("email",email);
                return map;
            }
        };
    
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }
}