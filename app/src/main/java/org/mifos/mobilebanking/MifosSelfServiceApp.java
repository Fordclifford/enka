package org.mifos.mobilebanking;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.mifos.mobile.passcode.utils.ForegroundChecker;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.mifos.mobilebanking.injection.component.ApplicationComponent;
import org.mifos.mobilebanking.injection.component.DaggerApplicationComponent;
import org.mifos.mobilebanking.injection.module.ApplicationModule;
import org.mifos.mobilebanking.utils.LanguageHelper;

import io.fabric.sdk.android.Fabric;

import static com.android.volley.VolleyLog.TAG;

/**
 * @author ishan
 * @since 08/07/16
 */
public class MifosSelfServiceApp extends Application {

    ApplicationComponent applicationComponent;
    private RequestQueue mRequestQueue;

    private static MifosSelfServiceApp instance;

    public static MifosSelfServiceApp get(Context context) {
        return (MifosSelfServiceApp) context.getApplicationContext();
    }
    public static synchronized MifosSelfServiceApp getInstance() {
        return instance;
    }
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        FlowManager.init(new FlowConfig.Builder(this).build());
        ForegroundChecker.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base, "en"));
    }

    public ApplicationComponent component() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }
}
