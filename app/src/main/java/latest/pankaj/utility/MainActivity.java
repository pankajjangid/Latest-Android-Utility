package latest.pankaj.utility;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView tvTextView;
    PackageManager packageManager;

    RecyclerView recyclerPkgList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTextView = (TextView) findViewById(R.id.tvTextView);
        recyclerPkgList = (RecyclerView) findViewById(R.id.recyclerPkgList);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder,new FragmentPdf(),"FragmentPdf").commit();
        // tvTextView.setText( HardwareUtils.getPhoneStatus());

        //GET Contacts
        /* List<HashMap<String,String>> list = HardwareUtils.getAllContactInfo();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String,String> hashMaps = list.get(i);

            Log.d(TAG, MyStringUtils.hashMap2String(hashMaps) );
        }*/


       // Log.d(TAG, "isDeviceRooted : " + HardwareUtils.isDeviceRooted());
//
       // Log.d(TAG, "IP Address : "+""+NetworkUtils.getIPAddress(true));
       // Log.d(TAG, "App VersionName : "+""+ AppUtils.getAppVersionName());
       // Log.d(TAG, "SystemApp : "+""+ AppUtils.isSystemApp());
       // Log.d(TAG, "VersionCode : "+""+ AppUtils.getAppVersionCode());
       // Log.d(TAG, "AppRoot : "+""+ AppUtils.isAppRoot());
       // Log.d(TAG, "AppRoot : "+""+ AppUtils.());


      //  PdfUtls.urlToPDF(this,"Test","http://www.google.com/");
     //   PdfUtls.createdPdf("Hello my name is panakj \nHello my name is panakj \nHello my name is panakj \n",1);

       /* Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
        Log.d(TAG, "pkgAppsList : "+pkgAppsList.size());*/

    /*    packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);

        List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();

		*//*To filter out System apps*//*
        for(PackageInfo pi : packageList) {
            boolean b = AppUtils.isSystemPackage(pi);
            if(!b) {
                packageList1.add(pi);
            }
        }
        InstallAdapter  installAdapter  = new InstallAdapter(this,packageList1,packageManager);
        recyclerPkgList.setAdapter(installAdapter);
        recyclerPkgList.setLayoutManager(new LinearLayoutManager(this));




        // Set up social fragment
        SocialFragment fragment =  new SocialFragment.Builder()
                // Mandatory
                .setApplicationId("saschpe.alphaplus")
                // Optional
                .setApplicationName("Alpha+ Player")
                .setContactEmailAddress("saschpe@example.com")
                .setFacebookGroup("234305760076265")
                .setGithubProject("saschpe/PlanningPoker")
                .setGooglePlusGroup("116602691405798233571")
                .setTwitterProfile("pankaj0619")
                // Visual customization
                .setHeaderTextColor(R.color.colorAccent)
                .setIconTint(android.R.color.white)
                .build();

        // Attach it
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_placeholder,fragment)
                .commit();*/
    }
}
