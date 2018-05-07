package latest.pankaj.utility;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.support.multidex.MultiDex;

/**
 * Created by Pankaj on 25/10/2017.
 */

public class MyApp extends Application {

    private static MyApp mInstance;

    //For Package Info
    PackageInfo packageInfo;

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;


    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MyApp.sApplication = this;
        MultiDex.install(this);

    }



    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}
