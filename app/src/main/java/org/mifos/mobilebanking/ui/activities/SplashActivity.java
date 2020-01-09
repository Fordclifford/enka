package org.mifos.mobilebanking.ui.activities;

/*
 * Created by saksham on 01/June/2018
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.ui.activities.base.BaseActivity;
import org.mifos.mobilebanking.utils.Constants;

import static org.mifos.mobilebanking.api.BaseURL.MY_TIME;

public class SplashActivity extends BaseActivity {

    PasscodePreferencesHelper passcodePreferencesHelper;
    Intent intent;
    TextView mTosTextView,networktxt;
    CheckBox mTosCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //  getActivityComponent().inject(this);
        setContentView(R.layout.activity_splash);

        WebView browser = (WebView) findViewById(R.id.webview);
        mTosTextView = (TextView) findViewById(R.id.tosTextView);
        mTosCheckBox = (CheckBox) findViewById(R.id.tosCheckBox);
        networktxt=findViewById(R.id.networktxt);

        ProgressBar progressBar=findViewById(R.id.progressBar);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            progressBar.setVisibility(View.GONE);
            networktxt.setVisibility(View.GONE);
            browser.loadUrl("https://server-ke365.com/uipb2ccallback/terms/index.php");

            mTosTextView.setText(Html.fromHtml(getString(R.string.TOSInfo)));
            mTosTextView.setMovementMethod(LinkMovementMethod.getInstance());

            mTosCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences.Editor editor = getSharedPreferences(MY_TIME, MODE_PRIVATE).edit();
                    editor.putBoolean("times", true);
                    editor.apply();
                    passcodePreferencesHelper = new PasscodePreferencesHelper(SplashActivity.this);
                    if (!passcodePreferencesHelper.getPassCode().isEmpty()) {
                        intent = new Intent(SplashActivity.this, PassCodeActivity.class);
                        intent.putExtra(Constants.INTIAL_LOGIN, true);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();

                }
            });
        }else {
            browser.setVisibility(View.GONE);
            mTosTextView.setVisibility(View.GONE);
            mTosCheckBox.setVisibility(View.GONE);
        }



    }

}
