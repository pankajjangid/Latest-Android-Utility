package latest.pankaj.utility.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import latest.pankaj.utility.MyApp;


/**
 * Created by Pankaj on 25/10/2017.
 */

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";
    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断设备是否root
     *
     * @return the boolean{@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/",
                "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备系统版本号
     *
     * @return 设备系统版本号
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取设备AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        return Settings.Secure.getString(MyApp.getApp().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    public static String getMacAddress() {
        String macAddress = getMacAddressByWifiInfo();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByNetworkInterface();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (!"02:00:00:00:00:00".equals(macAddress)) {
            return macAddress;
        }
        return "please open wifi";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @return MAC地址
     */
    @SuppressLint("HardwareIds")
    private static String getMacAddressByWifiInfo() {
        try {
            @SuppressLint("WifiManagerLeak")
            WifiManager wifi = (WifiManager) MyApp.getApp().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) return info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
     *
     * @return MAC地址
     */
    private static String getMacAddressByNetworkInterface() {
        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nis) {
                if (!ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02x:", b));
                    }
                    return res1.deleteCharAt(res1.length() - 1).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备MAC地址
     *
     * @return MAC地址
     */
    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    if (result.successMsg != null) {
                        return result.successMsg;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取设备厂商
     * <p>如Xiaomi</p>
     *
     * @return 设备厂商
     */

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备型号
     * <p>如MI2SC</p>
     *
     * @return 设备型号
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 关机
     * <p>需要root权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void shutdown(Context context) {
        ShellUtils.execCmd("reboot -p", true);
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 重启
     * <p>需要root权限或者系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     */
    public static void reboot() {
        ShellUtils.execCmd("reboot", true);
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        MyApp.getApp().sendBroadcast(intent);
    }

    /**
     * 重启
     * <p>需系统权限 {@code <android:sharedUserId="android.uid.system"/>}</p>
     *
     * @param reason 传递给内核来请求特殊的引导模式，如"recovery"
     */
    public static void reboot(final String reason) {
        PowerManager mPowerManager = (PowerManager) MyApp.getApp().getSystemService(Context.POWER_SERVICE);
        try {
            mPowerManager.reboot(reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启到recovery
     * <p>需要root权限</p>
     */
    public static void reboot2Recovery() {
        ShellUtils.execCmd("reboot recovery", true);
    }

    /**
     * 重启到bootloader
     * <p>需要root权限</p>
     */
    public static void reboot2Bootloader() {
        ShellUtils.execCmd("reboot bootloader", true);
    }

    /**
     * 获取IMSI码
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_PHONE_STATE"/>}</p>
     *
     * @return IMSI码
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getIMSI() {
        TelephonyManager tm = (TelephonyManager) MyApp.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getSubscriberId() : null;
    }

    /**
     * 判断sim卡是否准备好
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSimCardReady() {
        TelephonyManager tm = (TelephonyManager) MyApp.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * 获取Sim卡运营商名称
     * <p>中国移动、如中国联通、中国电信</p>
     *
     * @return sim卡运营商名称
     */
    public static String getSimOperatorName() {
        TelephonyManager tm = (TelephonyManager) MyApp.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getSimOperatorName() : null;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getPhoneStatus() {
        TelephonyManager tm = (TelephonyManager) MyApp.getApp()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String str = "";
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "PhoneType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n";
        return str;
    }

    /**
     * 跳至拨号界面
     *
     * @param phoneNumber 电话号码
     */
    public static void dial(final String phoneNumber) {
        MyApp.getApp().startActivity(IntentUtils.getDialIntent(phoneNumber));
    }

    /**
     * 跳至发送短信界面
     *
     * @param phoneNumber 接收号码
     * @param content     短信内容
     */
    public static void sendSms(final String phoneNumber, final String content) {
        MyApp.getApp().startActivity(IntentUtils.getSendSmsIntent(phoneNumber, content));
    }

    /**
     * 发送短信
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.SEND_SMS"/>}</p>
     *
     * @param phoneNumber 接收号码
     * @param content     短信内容
     */
    public static void sendSmsSilent(final String phoneNumber, final String content) {
        if (MyStringUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(MyApp.getApp(), 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    /**
     * 获取手机联系人
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>}</p>
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.READ_CONTACTS"/>}</p>
     *
     * @return 联系人链表
     */
    public static List<HashMap<String, String>> getAllContactInfo() {
        SystemClock.sleep(3000);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        // 1.获取内容解析者
        ContentResolver resolver = MyApp.getApp().getContentResolver();
        // 2.获取内容提供者的地址:com.android.contacts
        // raw_contacts表的地址 :raw_contacts
        // view_data表的地址 : data
        // 3.生成查询地址
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri date_uri = Uri.parse("content://com.android.contacts/data");
        // 4.查询操作,先查询raw_contacts,查询contact_id
        // projection : 查询的字段
        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"}, null, null, null);
        try {
            // 5.解析cursor
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // 6.获取查询的数据
                    String contact_id = cursor.getString(0);
                    // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
                    // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
                    // 判断contact_id是否为空
                    if (!MyStringUtils.isEmpty(contact_id)) {//null   ""
                        // 7.根据contact_id查询view_data表中的数据
                        // selection : 查询条件
                        // selectionArgs :查询条件的参数
                        // sortOrder : 排序
                        // 空指针: 1.null.方法 2.参数为null
                        Cursor c = resolver.query(date_uri, new String[]{"data1",
                                        "mimetype"}, "raw_contact_id=?",
                                new String[]{contact_id}, null);
                        HashMap<String, String> map = new HashMap<String, String>();
                        // 8.解析c
                        if (c != null) {
                            while (c.moveToNext()) {
                                // 9.获取数据
                                String data1 = c.getString(0);
                                String mimetype = c.getString(1);
                                // 10.根据类型去判断获取的data1数据并保存
                                if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                                    // 电话
                                    map.put("phone", data1);
                                } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                                    // 姓名
                                    map.put("name", data1);
                                }
                            }
                        }
                        // 11.添加到集合中数据
                        list.add(map);
                        // 12.关闭cursor
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }
        } finally {
            // 12.关闭cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public static void getAllSMS() {
        // 1.获取短信
        // 1.1获取内容解析者
        ContentResolver resolver = MyApp.getApp().getContentResolver();
        // 1.2获取内容提供者地址   sms,sms表的地址:null  不写
        // 1.3获取查询路径
        Uri uri = Uri.parse("content://sms");
        // 1.4.查询操作
        // projection : 查询的字段
        // selection : 查询的条件
        // selectionArgs : 查询条件的参数
        // sortOrder : 排序
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        // 设置最大进度
        int count = cursor.getCount();//获取短信的个数
        // 2.备份短信
        // 2.1获取xml序列器
        XmlSerializer xmlSerializer = Xml.newSerializer();
        try {
            // 2.2设置xml文件保存的路径
            // os : 保存的位置
            // encoding : 编码格式
            xmlSerializer.setOutput(new FileOutputStream(new File("/mnt/sdcard/backupsms.xml")), "utf-8");
            // 2.3设置头信息
            // standalone : 是否独立保存
            xmlSerializer.startDocument("utf-8", true);
            // 2.4设置根标签
            xmlSerializer.startTag(null, "smss");
            // 1.5.解析cursor
            while (cursor.moveToNext()) {
                SystemClock.sleep(1000);
                // 2.5设置短信的标签
                xmlSerializer.startTag(null, "sms");
                // 2.6设置文本内容的标签
                xmlSerializer.startTag(null, "address");
                String address = cursor.getString(0);
                // 2.7设置文本内容
                xmlSerializer.text(address);
                xmlSerializer.endTag(null, "address");
                xmlSerializer.startTag(null, "date");
                String date = cursor.getString(1);
                xmlSerializer.text(date);
                xmlSerializer.endTag(null, "date");
                xmlSerializer.startTag(null, "type");
                String type = cursor.getString(2);
                xmlSerializer.text(type);
                xmlSerializer.endTag(null, "type");
                xmlSerializer.startTag(null, "body");
                String body = cursor.getString(3);
                xmlSerializer.text(body);
                xmlSerializer.endTag(null, "body");
                xmlSerializer.endTag(null, "sms");
                Log.d(TAG,  "address:" + address + "   date:" + date + "  type:" + type + "  body:" + body);
            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
            // 2.8将数据刷新到文件中
            xmlSerializer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * returns device id of the device. User should grant "android.permission.READ_PHONE_STATE" permission
     *
     * @return
     */
    public String getDeviceID(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

    }

    /**
     * returns the IMEI Code of the device. proper permission to be granted inorder to make this method work.
     *
     * @return IMEI code of the device.
     */
    public String getIMEICode(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}
