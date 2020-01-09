package org.mifos.mobilebanking.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.SSLCertificateSocketFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.mobilebanking.MifosSelfServiceApp;
import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.BaseURL;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class CreateClientActivity extends AppCompatActivity {
    BaseURL baseURL;
    public static final String MyPREFERENCES = "basicinfo";
    
    Button button;
    
    
    EditText etdatebirth;
    SharedPreferences sharedpreferences;
    
    EditText etphonenumber;
    
    EditText etlastname;
    
    EditText etmiddlename;
    
    ScrollView scrollView;
    
    private static final int REQUEST_IMEI = 1;
    
    EditText etfirstname;
    public static final String firstnameacc = "firstnameaccKey";
    public static final String lastnameacc = "lastnameaccKey";
    public static final String middlenameacc = "middlenameaccKey";
    public static final String phonenumberacc = "phonenumberaccKey";
    public static final String accountIdacc = "accountIdaccKey";
    public static final String imeiNumber = "imeiNumber";
    
    TelephonyManager telephonyManager;
    String imei;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);
    
        etdatebirth = findViewById(R.id.et_date_birth);
        etphonenumber = findViewById(R.id.et_phone_number);
        etlastname = findViewById(R.id.et_last_name);
        etmiddlename = findViewById(R.id.et_middle_name);
        etfirstname = findViewById(R.id.et_first_name);
        scrollView = findViewById(R.id.ScrollRegister);
    
        button = findViewById(R.id.btn_RegisterClient);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("");
                String firstname = etfirstname.getText().toString();
                String middlename = etmiddlename.getText().toString();
                String idno = etdatebirth.getText().toString();
                String lastname = etlastname.getText().toString();
                String phonenumber = etphonenumber.getText().toString();
            
                System.out.println("phone"+phonenumber);
            
                if (firstname.equals("")||middlename.equals("")||idno.equals("")||phonenumber.equals("")){
                    Snackbar.make(scrollView,"Please input all fields",Snackbar.LENGTH_LONG).show();
                }else {
                    createClient();
                }
            
            
            }
        });
    
//        imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //Toast.makeText(this, imei, Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(CreateClientActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateClientActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_IMEI);
        } else {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT > 26) {
                if (telephonyManager!=null){
                    imei = telephonyManager.getImei();
                }
            }
            else
            {
                imei=telephonyManager.getDeviceId();
            }

            System.out.println("imei"+imei);




        }
        }
    
    
    private void createClient() {
    
        final String firstname = etfirstname.getText().toString();
        final String idno = etdatebirth.getText().toString();
        final String lastname = etlastname.getText().toString();
        final String phonenumber = etphonenumber.getText().toString();
    
        final ProgressDialog progressDialog = new ProgressDialog(CreateClientActivity.this);
        progressDialog.setMessage("Submitting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("firstname", firstname);
        params.put("mobileNo", phonenumber);
        params.put("lastname", lastname);
        params.put("externalId", idno);
        params.put("imei", imei);
        JSONObject nn = new JSONObject(params);
        
        System.out.println("Jsonn" + nn);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, baseURL.CREATE_CLIENT_URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        // display response
                        Log.d("ooo Response", response.toString());
                        
                        try {
                            
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String success = jsonObject.getString("success");
                            final String message = jsonObject.getString("message");
                            System.out.println("message" + message);
                            
                            if (success.equals("1")) {
                                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                System.out.println("Account No" + message);
                                editor.putString(firstnameacc, firstname);
                                editor.putString(lastnameacc, lastname);
                                // editor.putString(middlenameacc, lastname);
                                
                                editor.putString(phonenumberacc, phonenumber);
                                editor.putString(accountIdacc, message);
                                editor.putString(imeiNumber, imei);
                                editor.commit();
                                
                                
                                Intent intent = new Intent(CreateClientActivity.this, RegistrationActivity.class);
                                startActivity(intent);
                                
                                
                            } else if (success.equals("2")) {
                                Toast.makeText(CreateClientActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else if (success.equals("0")) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateClientActivity.this);
                                        alertDialogBuilder.setMessage(message);
                                        alertDialogBuilder.setPositiveButton("yes",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        Intent intent = new Intent(CreateClientActivity.this, ForgetActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                        
                                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                        
                                        
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateClientActivity.this, "An error occurred... Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        
                        
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateClientActivity.this, "An error occurred... Please try again", Toast.LENGTH_SHORT).show();
    
                        // Log.e(TAG, "On ErrorResponse: " + error.getMessage());
                        VolleyLog.e("Error: ", error.getMessage());
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            
                            System.out.println("Error:" + jsonError);
                        }
                    }
                }) {
            
            
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

// add it to the RequestQueue
        MifosSelfServiceApp.getInstance().addToRequestQueue(getRequest);
        
    }
    
    public void sendPost(final String firstname, final String middlename, final String lastname, final String phonenumber, final String datebirth, final String imei) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urls = new URL(baseURL.CREATE_CLIENT_URL);
                    HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
                    
                    if (conn instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                        httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                        httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
                    }
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("firstname", firstname);
                    jsonParam.put("mobileNo", phonenumber);
                    jsonParam.put("lastname", lastname);
                    jsonParam.put("externalId", datebirth);
                    jsonParam.put("externalId", datebirth);
                    
                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());
                    
                    os.flush();
                    os.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    System.out.println("Output from Server .... \n");
                    
                    String accountId = br.readLine();
                    // System.out.println("jsonnn" + accountId);
//                    if (accountId != null) {
//                        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        System.out.println("Account No" + accountId);
//                        editor.putString(firstnameacc, firstname);
//                        editor.putString(lastnameacc, lastname);
//                        // editor.putString(middlenameacc, lastname);
//
//                        editor.putString(phonenumberacc, phonenumber);
//                        editor.putString(accountIdacc, accountId);
//                        editor.commit();
//
//
//                        Intent intent = new Intent(CreateClientActivity.this, RegistrationActivity.class);
//                        startActivity(intent);
//
//                    }
//                    else if (accountId == null) {
//                        System.out.println("jsonnn" + accountId);
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateClientActivity.this);
//                                alertDialogBuilder.setMessage("Your mobile number/ID number is already registered. " +
//                                        "Do you wish to recover account?");
//                                alertDialogBuilder.setPositiveButton("yes",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface arg0, int arg1) {
//                                                Intent intent = new Intent(CreateClientActivity.this, ConfirmClientActivity.class);
//                                                intent.putExtra("mobile_number", phonenumber);
//                                                intent.putExtra("id", datebirth);
//                                                startActivity(intent);
//                                            }
//                                        });
//
//                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
//                                    }
//                                });
//
//                                AlertDialog alertDialog = alertDialogBuilder.create();
//                                alertDialog.show();
//
//
//                            }
//                        });
                    
                    //}
                    Log.i("MSG new", conn.getInputStream().toString());
                    
                    
                    conn.disconnect();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        thread.start();
    }
}
