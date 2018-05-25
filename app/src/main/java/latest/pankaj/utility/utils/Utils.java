package latest.pankaj.utility.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.System;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import latest.pankaj.utility.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Pankaj on 25/10/2017.
 */

public class Utils {

    private static final String TAG = "Utils";


    public static void compreesImageByWidth(File imgFileOrig) throws IOException {
        // we'll start with the original picture already open to a file
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
// original measurements
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        final int destWidth = 1000;//or the width you need

        if (origWidth > destWidth) {
            // picture is wider than we want it, we calculate its target height
            int destHeight = origHeight / (origWidth / destWidth);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
            // we save the file, at least until we have made use of it
            File f = new File(imgFileOrig.getAbsolutePath());
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            // remember close de FileOutput
            fo.close();
        }
    }

    public static int getDuplicateCountInArrayList(ArrayList<String> l) {
        int cnt = 0;
        HashSet<String> h = new HashSet<String>(l);

        for (String token : h) {
            if (Collections.frequency(l, token) > 1)
                cnt++;
        }

        return cnt;
    }

    public static void downloadFile(Context mContext, String url, String fileName) {

        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));

// This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

// When downloading music and videos they will be listed in the player
// (Seems to be available since Honeycomb only)
        r.allowScanningByMediaScanner();

// Notify user when download is completed
// (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

// Start download
        DownloadManager dm = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    //Open downloaded folder
    public static void openDownloadedFolder(Context mContext, String folderName) {
        //First check if SD Card is present or not
        if (new CheckForSDCard().isSDCardPresent()) {

            //Get Download Directory File
            File apkStorage = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + folderName);

            //If file is not present then display Toast
            if (!apkStorage.exists())
                Toast.makeText(mContext, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();

            else {

                //If directory is present Open Folder

                /** Note: Directory will open only if there is a app to open directory like File Manager, etc.  **/

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/" + folderName);
                intent.setDataAndType(uri, "file/*");
                mContext.startActivity(Intent.createChooser(intent, "Open Download Folder"));
            }

        } else
            Toast.makeText(mContext, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

    }


    public static String getDateTimeStamp() {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        return s.format(new Date());
    }

    //Check if internet is present or not
    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

//     compile 'org.apache.commons:commons-lang3:3.6'



    public static void openSettingPermission(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public static void createandDisplayPdf(String text, Bitmap[] bitmaparr) {

        Document doc = new Document();
        Rectangle one = new Rectangle(bitmaparr[0].getHeight(), bitmaparr[0].getHeight());
        doc.setPageSize(one);
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "newFile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            for (int i = 0; i < bitmaparr.length; i++) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmaparr[i].compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                Image image = Image.getInstance(imageInByte);

                image.setAbsolutePosition(10f, 10f);
                //    image.scaleAbsolute(600, 800);
                image.setAlignment(Element.ALIGN_MIDDLE);

                //Rectangle pagesize = new Rectangle(image.getScaledWidth(), image.getScaledHeight());

                // doc.setPageSize(pagesize);
                //  image.scalePercent(80);


                //     image.scaleToFit(PageSize.A4);

                float indentation = 0;

                /*float scaler = ((doc.getPageSize().getWidth() - doc.leftMargin()
                        - doc.rightMargin() - indentation) / image.getWidth()) * 100;

                image.scalePercent(scaler);
                image.scaleAbsolute( bitmaparr[i].getWidth(),  bitmaparr[i].getHeight());*/

                doc.add(image);
            }


            //     Paragraph p1 = new Paragraph(text);
            //   Font paraFont= new Font(Font.COURIER);
            //       p1.setAlignment(Paragraph.ALIGN_CENTER);
            //   p1.setFont(paraFont);

            //add paragraph to document
            //     doc.add(p1);

        } catch (DocumentException | IOException de) {

            de.printStackTrace();
        } finally {
            doc.close();
        }

        //  viewPdf("newFile.pdf", "Dir");
    }

    /*     // Method for opening a pdf file
        private void viewPdf(String file, String directory) {

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
            Uri path = Uri.fromFile(pdfFile);

            // Setting the intent for pdf reader
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(TableActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
            }
        }
    */
    public static Bitmap[] splitBitmap(Bitmap picture, int cout, int w) {


        Bitmap[] imgs = new Bitmap[cout];
        imgs[0] = Bitmap.createBitmap(picture, 0, 0, w, picture.getHeight() / 4);
        imgs[1] = Bitmap.createBitmap(picture, 0, imgs[0].getHeight(), w, picture.getHeight() / 4);
        imgs[2] = Bitmap.createBitmap(picture, 0, imgs[1].getHeight(), w, picture.getHeight() / 4);
        imgs[3] = Bitmap.createBitmap(picture, 0, imgs[2].getHeight(), w, picture.getHeight() / 4);

        return imgs;


    }

    public static Bitmap drawText(String text, int textWidth, int color, int textAlignment) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(35);


        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight() + 20, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        switch (textAlignment) {

            case 0:
                paint.setTextAlign(Paint.Align.CENTER);

                break;
            case 1:
                paint.setTextAlign(Paint.Align.LEFT);

                break;

            case 2:
                paint.setTextAlign(Paint.Align.RIGHT);

                break;
        }
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


    public static Bitmap getListViewViewScreenshot(ListView p_ListView) {
        ListView listview = p_ListView;
        ListAdapter adapter = listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        for (int i = 0; i < itemscount; i++) {
            View childView = adapter.getView(i, null, listview);
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bmps.add(childView.getDrawingCache());
            allitemsheight += childView.getMeasuredHeight();
        }
        Bitmap bigbitmap = Bitmap.createBitmap(listview.getMeasuredWidth(), allitemsheight,
                Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }
        //  storeImage(bigbitmap, "Test.jpg");
        return bigbitmap;
    }

    public static Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }

    // Cobine Multi Image Into One
    public static Bitmap combineBitmapIntoOne(ArrayList<Bitmap> bitmap) {
        int w = 0, h = 0;
        for (int i = 0; i < bitmap.size(); i++) {
            if (i < bitmap.size() - 1) {
                w = bitmap.get(i).getWidth() > bitmap.get(i + 1).getWidth() ? bitmap.get(i).getWidth() : bitmap.get(i + 1).getWidth();
            }
            h += bitmap.get(i).getHeight();
        }

        Bitmap temp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        int top = 0;
        for (int i = 0; i < bitmap.size(); i++) {
            Log.d("HTML", "Combine: " + i + "/" + bitmap.size() + 1);

            top = (i == 0 ? 0 : top + bitmap.get(i - 1).getHeight());
            canvas.drawBitmap(bitmap.get(i), 0f, top, null);
        }
        return temp;

    }


    public static Bitmap mergeBitmapOverlay(Bitmap bmp1, Bitmap bmp2, int w, int h) {
        Bitmap bmOverlay = Bitmap.createBitmap(w, h, bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        //canvas.drawBitmap(bmp2, 0, 0, null);
        //canvas.drawBitmap(bmp3, 0, 0, null);
        return bmOverlay;
    }

    public static Bitmap mergeBitmapHorizonal(Bitmap fr, Bitmap sc) {

        Bitmap comboBitmap;

        int width, height;

        width = fr.getWidth() + sc.getWidth();
        height = fr.getHeight();

        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(comboBitmap);


        comboImage.drawBitmap(fr, 0f, 0f, null);
        comboImage.drawBitmap(sc, fr.getWidth(), 0f, null);
        return comboBitmap;

    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "games");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + java.lang.System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public static RequestBody parseStringData(String data) {
        RequestBody parseName = RequestBody.create(MediaType.parse("text/plain"), data);
        return parseName;
    }

    public static String getCurrendate() {

        Calendar c = Calendar.getInstance();
        java.lang.System.out.println("Current time => " + c.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");

        return df.format(c.getTime());
    }

    public static String getCurrenTime() {

        String time = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());

        return time;
    }

    // To Change String TO Date Object
    public static Date stringToDate(String stringDate) {

        Date selectedDate = null;
        try {
            selectedDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                    .parse(stringDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate;
    }

    // To Get Number In Decimal Format
    public static String getDecimalFormat(double number) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(number);
    }    // To Get Number In Decimal Format

    public static String getDecimalFormat(double number, String afterDecimalPoint) {

        DecimalFormat decimalFormat = new DecimalFormat("0." + afterDecimalPoint);

        return decimalFormat.format(number);
    }

    public static String getRealDate(String fulldate) {

        String mStringDate = fulldate;
        String oldFormat = "yyyy-MM-dd'T'hh:mm:ss";
        String newFormat = "dd/MMM/yyyy";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mStringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        formatedDate = timeFormat.format(myDate);

        return formatedDate;
    }

    public static void clearViewFouces(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    editText.clearFocus();
                }
                return false;
            }
        });

    }

    public static String getString(EditText editText) {
        return editText.getText().toString().trim();

    }

    public static String getStringFromTV(TextView textView) {
        return textView.getText().toString().trim();

    }

    public static void goNextClass(Context context, Class className) {

        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    //ArraryList To Comma Seprated String
    public static String toCSV(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        for (String str : arrayList) {
            sb.append(str).append(","); //separating contents using semi colon
        }

        String strfromArrayList = sb.toString();

        return strfromArrayList;

    }

    public static boolean checkImageResource(Context ctx, ImageView imageView, int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }
            Log.d("imageviewImage", "" + constantState);
            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        Log.d("Image", "" + imageView.getDrawable().getConstantState());

        return result;
    }

    //Comapare Imageview and Drawable

    public static void showAlertBox(Context context, String msg) {

        // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(context);

        }

        // set title
        //  alertDialogBuilder.setTitle("Your Title");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(true)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // show it
        alertDialog.show();
    }

    public static void showToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public static void showAlertBoxwithIntent(final Context context, String msg, final Class class1) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        //  alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        // dialog.cancel();
                        Intent intent = new Intent(context, class1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    //method for click images or get from gallary
    public static void selectImage(final Activity activity) {
        final CharSequence[] option1 = {"Take Photo", "Choose from Gallery", "Take selfie", "Select Multiple", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle("Add Photo!");
        builder.setItems(option1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (option1[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(intent, 101);
                } else if (option1[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*"); // intent.setType("video/*"); to select videos to upload
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), 201);
                } else if (option1[item].equals("Take selfie")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT,0);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);

                    activity.startActivityForResult(intent, 301);


                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //method for get Image URI
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //method for get Image PATH
    public static String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // Method to show alert dialog
    public static void showAlert(String message, Context context) {

        AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(context);

        }
        alertDialogBuilder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    public static void captureImage(Bitmap bitmap, Intent data, ImageView imageView, String imagepath) {
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            /*Uri tempUri = MyUtil.getImageUri(activity.getApplicationContext(), bitmap);
            imagepath = MyUtil.getPath(tempUri, activity);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void imageFromGallary(Intent data, String imagepath, Activity activity, ImageView imageView) {

        Uri selectedImageUri = data.getData();
        imagepath = Utils.getPath(selectedImageUri, activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // down sizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 4;
        final Bitmap bitmap = BitmapFactory.decodeFile(imagepath, options);
        imageView.setImageBitmap(bitmap);
    }

    public static void showSnackBar(final View coordinatorLayout, String msg) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

// Changing message text color
        snackbar.setActionTextColor(Color.RED);

// Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void hideKeyboardForFocusedView(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view != null) {
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
    // Create First Word Capital

    @SuppressLint("SimpleDateFormat")
    public static String convertJsonDate(String jsondate) {
        jsondate = jsondate.replace("/Date(", "").replace(")/", "");
        long time = Long.parseLong(jsondate);
        Date d = new Date(time);

        //  return new SimpleDateFormat("MM/dd/yyyy").format(d).toString();
        return new SimpleDateFormat("dd/MM/yyyy").format(d).toString();
    }

    /**
     * Disable soft keyboard from appearing, use in conjunction with android:windowSoftInputMode="stateAlwaysHidden|adjustNothing"
     *
     * @param editText
     */
    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }




    /**
     * Convert the bitmap into image and save it into the sdcard.
     *
     * @param imageData -Bitmap image.
     * @param filename  -Name of the image.
     * @return
     */
    public static boolean storeImage(Context context, Bitmap imageData, String filename, String foldeName) {
        // get path to external storage (SD card)
        File sdIconStorageDir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + foldeName + "/");
        // create storage directories, if they don't exist
        if (!sdIconStorageDir.exists())
            sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir.toString() + File.separator + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            Toast.makeText(context, "Image Saved at----" + filePath, Toast.LENGTH_LONG).show();
            // choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            DialogUtils.openImageFromUriDialog(context, new File(filePath));

            bos.flush();
            bos.close();
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * this utility method helps to save images in internal memory. Since the image is saved in the internal memory, the image is private
     *
     * @param bitmap    image in bitmap form
     * @param name      name of the file
     * @param type      format of the image. only png and jpg formats are allowed. parameter should be of following format. "png" or "jpg"
     * @param path      path of the folder in which image to be saved. sample path format: /folder1/folder2/folder3/
     * @param quality   quality of the image
     * @param isPrivate
     * @return path in which image is saved
     */
    public static String saveImage(Context mcontext, Bitmap bitmap, String name, String type, String path, int quality, boolean isPrivate) {
        File file_path = null;
        File dir2 = null;

        String a = File.separator;


        if (path == null || path.isEmpty() || path.length() <= 3 || !path.contains("/")) {
            Log.e("Utils Plus", "Folder path seems to be incorrect. Please correct the path");

        }
        if (type == null || type.isEmpty()) {

            Log.e("Utils Plus", "Image type can not be null or empty.");
        }
        if (name == null || name.isEmpty()) {

            Log.e("Utils Plus", "Image name can not be null or empty.");
        }
        if (quality < 0 || quality > 100) {

            Log.e("Utils Plus", "Quality must be greater than 0 and less than 100");
        }

        if (type != null) {
            if (!type.equalsIgnoreCase("PNG") && !type.equalsIgnoreCase("JPG")) {
                Log.e("Utils Plus", "format not supported except png and jpg");
            }
        }

        if (type.equalsIgnoreCase("PNG")) {
            name = name + ".png";
        } else if (type.equalsIgnoreCase("JPG")) {
            name = name + ".jpg";
        }

        if (!isPrivate) {
            File rootDir = null;
            path = path.replaceAll("//*", "/");
            String[] folders = path.split("/");
            for (int i = 0; i < folders.length; i++) {
                if (i == 0) {
                    rootDir = mcontext.getDir(folders[i], Context.MODE_APPEND); //Creating an internal dir;
                    if (!rootDir.exists()) {
                        rootDir.mkdirs();
                    }
                } else {
                    rootDir = new File(rootDir, folders[i]);
                    rootDir.mkdir();

                }

            }
            return rootDir.getAbsolutePath();

        }
        ContextWrapper cw = new ContextWrapper(mcontext);
        File dir = cw.getFilesDir();
        dir2 = new File(dir, path);
        dir2.mkdirs();

        file_path = new File(dir2, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file_path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            if (type.equalsIgnoreCase("PNG")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
            } else if (type.equalsIgnoreCase("JPG")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return dir2.getAbsolutePath();
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

    /**
     * Utility method which will help you to show notification in the status bar.
     *
     * @param title      title of the push notification
     * @param body       content to be displayed in the notification
     * @param small_icon small icon which will be showed in the notification. should be of following format:R.drawable.imageName
     * @param large_icon Large icon which will be showed in the notification. should be of following format:R.drawable.imageName
     * @param class_name The  activity which will open on clicking notification. Parameter format: activity_name.class
     * @param autoCancel true or false. if set to true, notification will be disappeared after clicking it otherwise it will remain in the status bar
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displaySimplePushNotification(Context mcontext, String title, String body, int small_icon, int large_icon, Class<?> class_name, boolean autoCancel) {
        NotificationManager notificationManager;
        Notification notification;
        boolean autoCancelValue = autoCancel;
        Intent intent = new Intent(mcontext, class_name);
        PendingIntent pIntent = PendingIntent.getActivity(mcontext, (int) System.currentTimeMillis(), intent, 0);

        notification = new Notification.Builder(mcontext)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(autoCancelValue)
                .setSmallIcon(small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mcontext.getResources(), large_icon))
                .build();

        notificationManager =
                (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);

        int maximum = 9999, minimum = 0;
        Random rn = new Random();
        int n = maximum - minimum + 1;
        int i = rn.nextInt() % n;
        notificationManager.notify(minimum + i, notification);

    }

    /**
     * converts image of the form "drawable" to "bitmap"
     *
     * @param drawable drawable resource
     * @return bitmap obtained from drawable
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * this utility method is used to decrypt the encrypted information.
     *
     * @param decryptKey secret key which is required  to decrypt the information
     * @param value      information which is to be decrypted
     * @return decrypted information
     */
    public String decryptIt(String decryptKey, String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(decryptKey.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            return decrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * this utility method is used to encrypt the information
     *
     * @param encryptKey secret key which is used to encrypt the information
     * @param value      information to be encrypted
     * @return encrypted information
     */
    public String encryptIt(String encryptKey, String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(encryptKey.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

}
