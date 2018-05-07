package latest.pankaj.utility.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;

/**
 * Created by user on 08-Mar-18.
 */

public class ShareUtils {

    public static void  openDialer(Context mContext,String number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+number));
        mContext.startActivity(intent);
    }

    public static void openWhatsApp(Context mContext,String number) {

     //   number= number.replace("+", "").replace(" ", "");
        boolean isWhatsappInstalled = whatsappInstalledOrNot(mContext,"com.whatsapp");
        if (isWhatsappInstalled) {

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
           sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");//phone number without "+" prefix
           // sendIntent.putExtra("jid", number + "@s.whatsapp.net");//phone number without "+" prefix

            mContext.startActivity(sendIntent);
        } else {
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(mContext, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            mContext.startActivity(goToMarket);
        }
    }

    public static void openOnMap(Context mContext,String address) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:0,0?q=%s",
                        URLEncoder.encode(address)))));
    }

    public static void openOnMap(Context mContext,String lat, String lng) {
        mContext.startActivity( new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:%s,%s", lat, lng))));
    }

    public static boolean whatsappInstalledOrNot(Context mContext,String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }



    public static void shareImageFile(Context mContext, File f) {
        Uri uri = Uri.parse("file://" + f.getAbsolutePath());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("image/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(Intent.createChooser(share, "Share image File"));
    }

    public static void shareBitmap(Context context, Bitmap bitmap) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    /*compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();*/
        i.putExtra(Intent.EXTRA_STREAM, bitmap);
        try {
            context.startActivity(Intent.createChooser(i, "My Profile ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }
    }

}
