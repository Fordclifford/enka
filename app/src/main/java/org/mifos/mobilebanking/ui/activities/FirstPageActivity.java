package org.mifos.mobilebanking.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.BaseURL;
import org.mifos.mobilebanking.utils.Constants;


public class FirstPageActivity extends AppCompatActivity {
    PasscodePreferencesHelper passcodePreferencesHelper;
    Handler mHandler;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    Intent intentt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        passcodePreferencesHelper = new PasscodePreferencesHelper(FirstPageActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. */

                    SharedPreferences prefs = getSharedPreferences(BaseURL.MY_TIME, MODE_PRIVATE);
                    Boolean restoredText = prefs.getBoolean("times", false);

                    if (restoredText == false) {
                        Intent mainIntent = new Intent(FirstPageActivity.this, SplashActivity.class);
                        FirstPageActivity.this.startActivity(mainIntent);
                        FirstPageActivity.this.finish();
                    } else {
                        if (!passcodePreferencesHelper.getPassCode().isEmpty()) {
                            intentt = new Intent(FirstPageActivity.this, PassCodeActivity.class);
                            intentt.putExtra(Constants.INTIAL_LOGIN, true);
                        } else {
                            intentt = new Intent(FirstPageActivity.this, LoginActivity.class);
                        }
                        startActivity(intentt);
                        finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }

}