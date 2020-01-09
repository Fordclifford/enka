package org.mifos.mobilebanking.ui.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.mobilebanking.ui.activities.HomeActivity;
import org.mifos.mobilebanking.utils.gcm.Constant;
import org.mifos.mobilebanking.utils.gcm.SharedPreference;
import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.ui.activities.HomeActivity;
import org.mifos.mobilebanking.utils.Constants;
import org.mifos.mobilebanking.utils.FcmVolley;
import org.mifos.mobilebanking.utils.gcm.Constant;
import org.mifos.mobilebanking.utils.gcm.SharedPreference;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StkPushFragment extends Fragment  {
    private String accountId;
    private Double loanBal;
    View rootView;
    private  static final String urlAdress="https://41.90.111.246:3000/tech/api/mpesa/stkpush";
    private  static final String transStatus="https://41.90.111.246:3000/tech/api/mpesa/stkpushstatus";
    private  static final String confirmUrl="https://techsavanna.net:8181/enka/api/confirmstk.php";
    String link="https://41.90.111.246:3000/tech/api/auth/signin";

    TextView accountidtxt;
    EditText amountedt,phoneedt;
    Button makepaymentbtn;

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private ProgressDialog progressDialog;
    public String accessToken;
    public JSONObject ApiResponse;
    ProgressDialog progress;


    public StkPushFragment() {
        // Required empty public constructor

    }

    public static StkPushFragment newInstance(String accountId, double loanBal){
        StkPushFragment transferFragment = new StkPushFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ACCOUNT_ID, accountId);
        args.putDouble(Constants.LOANBAL, loanBal);
        // args.putString(Constants.TRANSFER_TYPE, transferType);
        transferFragment.setArguments(args);
        return transferFragment;
    }


    //SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            accountId = getArguments().getString(Constants.ACCOUNT_ID);
            loanBal = getArguments().getDouble(Constants.LOANBAL);
            // transferType = getArguments().getString(Constants.TRANSFER_TYPE);

        }
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handleSSLHandshake();
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_stk_push, container, false);
        amountedt=rootView.findViewById(R.id.amount);
        phoneedt=rootView.findViewById(R.id.phoneno);
        accountidtxt=rootView.findViewById(R.id.accounttxt);
        makepaymentbtn=rootView.findViewById(R.id.paymentbtn);

        // accountidtxt.setText((int) accountId);
        accountidtxt.setText(getString(R.string.accounttobepaidfor)+" "+ String.valueOf(accountId));


        //////////////////////////////////////////////////


        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String Phonenumber = prefs.getString("phonenoIndividual", null);

        String Phonenumber2 = prefs.getString("phonenoIndividual", getString(R.string.enterphoneno1));
        System.out.println("Phone no "+Phonenumber);

        phoneedt.setText(Phonenumber2);
        amountedt.setText(""+loanBal.intValue());
        //////////////////////////////////////////////////////////////////


        makepaymentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    payLoan(accountId);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Unknown Error occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
        return rootView;
    }

    private void payLoan(final String accountId) throws JSONException {
        // String link="http://207.180.239.173:9091/tech/api/auth/signin";

        final String username="cliffordmasi@gmail.com";
        final String password="adminpassword123";


        progress= new ProgressDialog(getActivity());
        progress.setMessage("Sending...");
        progress.setMessage("Please check your phone...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("usernameOrEmail", username);
        jsonParam.put("password", password);

        String resp = getToken(link,jsonParam);
        System.out.println(resp);
    }


    public JSONObject sendRequest(String url, JSONObject jsonBody, final String header){

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        // Toast.makeText(getActivity(), "SSent", Toast.LENGTH_SHORT).show();
                        progress.setMessage("Confirm your pin within 20 seconds...");

                        System.out.println("Response for token" + response.toString());


                        Thread background = new Thread() {
                            public void run() {

                                try {
                                    sleep(20 * 1000);
                                    JSONObject jsonParam1 = new JSONObject();
                                    jsonParam1.put("tenant", 2);
                                    jsonParam1.put("posid", "nil");
                                    jsonParam1.put("checkoutRequestId", response.getString("CheckoutRequestID"));
                                    JSONObject respons = confirmRequest(transStatus, jsonParam1, accessToken);

                                } catch (InterruptedException e) {
                                    Toast.makeText(getActivity(), "Error occurred...Kindly check the inputs", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    Toast.makeText(getActivity(), "Error occurred...Kindly check the inputs", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                    e.printStackTrace();
                                }


                            }
                        };
                        background.start();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Sending failed...Please try again", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer "+header);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        mQueue.add(jsonObjectRequest);
        return ApiResponse;
    }
    public JSONObject confirmRequest(String url, JSONObject jsonBody, final String header){

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        progress.setMessage("Please wait...");

                        try {

                            int finalResponse=response.getInt("ResultCode");
                            if(finalResponse==0){
                                JSONObject jsonParam1 = new JSONObject();
                                jsonParam1.put("CheckoutRequestID", response.getString("CheckoutRequestID"));
                                JSONObject respons = processRequest(confirmUrl, jsonParam1);
                                progress.setMessage("Processing Payment...");
                            }else{
                                Toast.makeText(getActivity(), "Could not process payment", Toast.LENGTH_LONG).show();
                                progress.dismiss();

                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Error occurred...Kindly check the inputs", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                            e.printStackTrace();
                        }

                        //Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Sending failed...Please try again", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
                progress.dismiss();
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer "+header);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        mQueue.add(jsonObjectRequest);
        return ApiResponse;
    }


    public JSONObject processRequest(String url, JSONObject jsonBody){

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Toast.makeText(getActivity(), "confirmation request sent", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "please wait...", Toast.LENGTH_LONG).show();



                        try {
                            int finalResponse=response.getInt("success");
                            if(finalResponse==1){
                                Toast.makeText(getActivity(), "Payment confirmed successfully", Toast.LENGTH_LONG).show();
                                progress.dismiss();
                                startActivity(new Intent(getActivity(),HomeActivity.class));
                                getActivity().finish();
                            }else{
                                Toast.makeText(getActivity(), "Processing Request Timeout!", Toast.LENGTH_LONG).show();
                                progress.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "An error occured,We are procesing your payment", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finish();
                            e.printStackTrace();
                        }




                        //Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Sending failed...Please try again", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
                progress.dismiss();
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                // params.put("Authorization", "Bearer "+header);
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        mQueue.add(jsonObjectRequest);
        return ApiResponse;
    }

    public String getToken(String url, JSONObject jsonBody){

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Toast.makeText(getActivity(), "Sent", Toast.LENGTH_SHORT).show();
                        try {
                            accessToken=response.getString("accessToken");

                            final String amount=amountedt.getText().toString();
                            final String phone=phoneedt.getText().toString();
                            String strLastFourDi = phone.length() >= 9 ? phone.substring(phone.length() - 9) : "";
                            JSONObject jsonParam1 = new JSONObject();


                            jsonParam1.put("phone", "254" + strLastFourDi);
                            jsonParam1.put("amount", amount);
                            jsonParam1.put("tenant", 2);
                            jsonParam1.put("accountReference",   accountId);
                            jsonParam1.put("transactionDesc", "Loan Repayment");
                            JSONObject respons = sendRequest(urlAdress, jsonParam1, accessToken);
                            System.out.println("response" + respons);

                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Server error occured", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                            e.printStackTrace();
                        }



                        //Log.d("TAG", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Sending failed...Please try again", Toast.LENGTH_SHORT).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        mQueue.add(jsonObjectRequest);
        return accessToken;
    }

    public void sendToken(final String view) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Registering Device...");
                progressDialog.show();
//

                final String token = SharedPreference.getInstance(getContext()).getDeviceToken();
                final String email = view;

                System.out.println("Token update" + token + " " + email);
//
                if (token == null) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Token not generated", Toast.LENGTH_LONG).show();
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_REGISTER_DEVICE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("token", token);
                        return params;
                    }
                };
                FcmVolley.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }

        });
    }





}