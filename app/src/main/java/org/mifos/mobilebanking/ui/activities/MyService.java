package org.mifos.mobilebanking.ui.activities;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service Started",Toast.LENGTH_SHORT).show();
        scheduleNext();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this,"Service Running",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this,"Service Stopped",Toast.LENGTH_SHORT).show();
    }

    private void scheduleNext() {
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                startActivity(new Intent(MyService.this, StkPushActivity.class));
                Toast.makeText(getApplicationContext(),"Call to api made",Toast.LENGTH_SHORT).show();
                ha.postDelayed(this, 1000);
            }
        }, 10000);

    }
}
