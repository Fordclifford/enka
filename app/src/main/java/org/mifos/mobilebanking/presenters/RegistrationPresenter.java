package org.mifos.mobilebanking.presenters;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.mifos.mobilebanking.api.BaseURL;
import org.mifos.mobilebanking.api.DataManager;
import org.mifos.mobilebanking.injection.ApplicationContext;
import org.mifos.mobilebanking.models.register.RegisterPayload;
import org.mifos.mobilebanking.presenters.base.BasePresenter;
import org.mifos.mobilebanking.ui.views.RegistrationView;
import org.mifos.mobilebanking.utils.MFErrorParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * Created by dilpreet on 31/7/17.
 */

public class RegistrationPresenter extends BasePresenter<RegistrationView> {

    private DataManager dataManager;
    private CompositeDisposable compositeDisposables;

    /**
     * Initialises the RecentTransactionsPresenter by automatically injecting an instance of
     * {@link DataManager} and {@link Context}.
     *
     * @param dataManager DataManager class that provides access to the data
     *                    via the API.
     * @param context     Context of the view attached to the presenter. In this case
     *                    it is that of an {@link android.support.v7.app.AppCompatActivity}
     */
    @Inject
    public RegistrationPresenter(DataManager dataManager, @ApplicationContext Context context) {
        super(context);
        this.dataManager = dataManager;
        compositeDisposables = new CompositeDisposable();
    }

    @Override
    public void attachView(RegistrationView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposables.clear();
    }

    public void registerUser(final RegisterPayload registerPayload) {
        checkViewAttached();
        getMvpView().showProgress();
        compositeDisposables.add(dataManager.registerUser(registerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().hideProgress();
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getMvpView().hideProgress();
                        getMvpView().showRegisteredSuccessfully();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                

                                try {
                                    URL urls = new URL(BaseURL.AUTH);
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


                                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                                    // os.writeBytes(accountid);
                                    os.writeBytes(registerPayload.getAccountNumber().toString());

                                    os.flush();
                                    os.close();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    System.out.println("Output from Server .... \n");

                                    String accountId=br.readLine();
                                    System.out.println("Output from Server 2222  "+accountId);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        thread.start();
                    }
                }));
    }
}
