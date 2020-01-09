package org.mifos.mobilebanking.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.local.PreferencesHelper;
import org.mifos.mobilebanking.presenters.LoginPresenter;
import org.mifos.mobilebanking.ui.activities.base.BaseActivity;
import org.mifos.mobilebanking.ui.views.LoginView;
import org.mifos.mobilebanking.utils.Constants;
import org.mifos.mobilebanking.utils.Network;
import org.mifos.mobilebanking.utils.Toaster;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Vishwajeet
 * @since 05/06/16
 */

public class LoginActivity extends BaseActivity implements LoginView {

    @Inject
    LoginPresenter loginPresenter;

    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;

    @BindView(R.id.til_username)
    TextInputLayout tilUsername;

    @BindView(R.id.til_password)
    TextInputLayout tilPassword;

    @BindView(R.id.ll_login)
    LinearLayout llLogin;

    @Inject
    PreferencesHelper preferencesHelper;

    private String userName;
    LinearLayout linearLayout;
    TextView reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
    
        if (!preferencesHelper.getPasscode().isEmpty()) {
            startPassCodeActivity();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginPresenter.attachView(this);
        
        linearLayout = findViewById(R.id.LinearLogin);
        reset = findViewById(R.id.ResetTextView);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });
    }
    
    /**
     * Called when Login is user has successfully logged in
     * @param userName Username of the user that successfully logged in!
     */
    @Override
    public void onLoginSuccess(String userName) {
        this.userName = userName;
        loginPresenter.loadClient();
    }

    /**
     * Shows ProgressDialog when called
     */
    @Override
    public void showProgress() {
        showProgressDialog(getString(R.string.progress_message_login));
    }

    /**
     * Hides the progressDialog which is being shown
     */
    @Override
    public void hideProgress() {
        hideProgressDialog();
    }

    /**
     * Starts {@link PassCodeActivity}
     */
    @Override
    public void showPassCodeActivity() {
        showToast(getString(R.string.toast_welcome, userName));
        startPassCodeActivity();
    }

    /**
     * It is called whenever any error occurs while executing a request
     * @param errorMessage Error message that tells the user about the problem.
     */
    @Override
    public void showMessage(String errorMessage) {
        showToast(errorMessage, Toast.LENGTH_LONG);
        llLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUsernameError(String error) {
        Snackbar.make(linearLayout,"Please input Username",Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showPasswordError(String error) {
        Snackbar.make(linearLayout,"Please input Password",Snackbar.LENGTH_LONG).show();
    }

    /**
     * Called when Login Button is clicked, used for logging in the user
     */
    @OnClick(R.id.btn_login)
    public void onLoginClicked() {

        final String username = tilUsername.getEditText().getText().toString();
        final String password = tilPassword.getEditText().getText().toString();

        if (Network.isConnected(this)) {
            loginPresenter.login(username, password);
        } else {
            Toaster.show(llLogin, getString(R.string.no_internet_connection));
        }
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClicked() {
        startActivity(new Intent(LoginActivity.this, CreateClientActivity.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.detachView();
    }

    /**
     * Starts {@link PassCodeActivity} with {@code Constans.INTIAL_LOGIN} as true
     */
    private void startPassCodeActivity() {
        Intent intent = new Intent(LoginActivity.this, PassCodeActivity.class);
        intent.putExtra(Constants.INTIAL_LOGIN, true);
        startActivity(intent);
        finish();
    }
}
