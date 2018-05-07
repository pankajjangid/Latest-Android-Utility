package latest.pankaj.utility.utils;

/**
 * Created by Pankaj on 31/10/2017.
 */


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import latest.pankaj.utility.MyApp;


/**
 * Created by munix on 07/04/16.
 */
public class System {

    /**
     * Comprueba si una aplicación está instalada
     *
     * @param context
     * @param packageName
     * @return true si el nombre del paquete está instalado en el dispositivo
     */
    public static Boolean isPackageInstalled( Context context, String packageName ) {
        try {
            @SuppressWarnings( "unused" ) ApplicationInfo info = context.getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo( packageName, 0 );
            return true;
        } catch ( PackageManager.NameNotFoundException e ) {
            return false;
        }
    }

    /**
     * Obtiene la primera cuenta de email configurada en el dispositivo. Necesita el permiso GET_ACCOUNTS
     *
     * @return
     */
    public static String getUserSystemEmail() {
        return getUserSystemEmail( MyApp.getApp() );
    }

    /**
     * Obtiene la primera cuenta de email configurada en el dispositivo. Necesita el permiso GET_ACCOUNTS
     *
     * @param mContext
     * @return
     */
    @SuppressWarnings( "MissingPermission" )
    public static String getUserSystemEmail( Context mContext ) {
        String user_email = "";
        try {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get( mContext ).getAccounts();
            for ( Account account : accounts ) {
                if ( emailPattern.matcher( account.name ).matches() ) {
                    user_email = account.name;
                    break;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return user_email;
    }

    public static Boolean isPackageInstalled( String packageName ) {
        return isPackageInstalled( MyApp.getApp(), packageName );
    }

    public static String getLauncherActivity( Context context, String packageName ) {
        Intent intent = new Intent( Intent.ACTION_MAIN, null );
        intent.addCategory( Intent.CATEGORY_LAUNCHER );
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities( intent, 0 );
        for ( ResolveInfo info : infos ) {
            if ( info.activityInfo.packageName.equals( packageName ) ) {
                return info.activityInfo.name;
            }
        }
        return null;
    }

    public static String getLauncherActivity( String packageName ) {
        return getLauncherActivity(  MyApp.getApp(), packageName );
    }

    public static void exit( int code ) {
        java.lang.System.exit( code );
    }

    public static long currentTimeMillis() {
        return java.lang.System.currentTimeMillis();
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getUniqueDeviceId(Context context ) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString( context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID );

        UUID deviceUuid = new UUID( androidId.hashCode(), ( (long) tmDevice.hashCode() << 32 ) | tmSerial
                .hashCode() );
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static String getUniqueDeviceId() {
        return getUniqueDeviceId(  MyApp.getApp() );
    }
}
