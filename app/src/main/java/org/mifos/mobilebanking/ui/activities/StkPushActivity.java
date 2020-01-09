package org.mifos.mobilebanking.ui.activities;

import android.os.Bundle;

import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.ui.activities.base.BaseActivity;

public class StkPushActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stk_push);
        //replaceFragment(StkPushFragment.newInstance(Constants.LOAN_ID, LoanWithAssociations.getSummary().getTotalOutstanding()), false, R.id.container);
    }
}
