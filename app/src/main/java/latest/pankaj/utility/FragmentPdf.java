package latest.pankaj.utility;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bachors.img2ascii.Img2Ascii;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import latest.pankaj.utility.testing.PdfUtls;
import latest.pankaj.utility.utils.RealPathUtil;
import latest.pankaj.utility.utils.Utils;

import static android.app.Activity.RESULT_OK;
import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPdf extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    Context mContext;
    View mView;
    EditText etPdfText;
    Button btnClick,btnImage;
    RadioGroup rgAlign;
    int align=0;

    ArrayList<Bitmap> bitmapList = new ArrayList<>();

    private final static String[] CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String imagepath = "";
    int numberOfImagesToSelect = 5;
    CharSequence[] imageOptions;

    private static final String TAG = "FragmentPdf";
    public FragmentPdf() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_pdf, container, false);
        findControls();
        return mView;
    }

    private void findControls() {
        mContext=getActivity();

        etPdfText = mView.findViewById(R.id.etPdfText);
        btnClick= mView.findViewById(R.id.btnClick);
        btnImage= mView.findViewById(R.id.btnImage);
        rgAlign= mView.findViewById(R.id.rgAlign);
        btnImage.setOnClickListener(this);
        btnClick.setOnClickListener(this);
        rgAlign.setOnCheckedChangeListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btnClick:

              /*  if (!etPdfText.getText().toString().equals(""))
                PdfUtls.createdPdf(etPdfText.getText().toString(),align,"122","PDF");
                else
                    new BitmapToPdfTask(getActivity(),bitmapList,"PDF","Image").execute();*/
                createBitmapToAscii();
                break;
            case R.id.btnImage:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
               /*         permissionFragmentHelper
                                .setForceAccepting(false) // default is false. its here so you know that it exists.
                                .request(CAMERA_PERMISSIONS);*/
                    } else {
                        openImageDialog();
                    }

                } else {
                    openImageDialog();

                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
       // int id = radioGroup.getCheckedRadioButtonId();


        if (checkedId==R.id.rbCenter){
            align=0;
        }
        else if (checkedId==R.id.rbLeft){
            align=1;
        }
        else if (checkedId==R.id.rbRight){
            align=2;
        }

        Log.d(TAG, "onCheckedChanged: "+align);


    }


    //    compile 'com.github.darsh2:MultipleImageSelect:3474549'
    private void openImageDialog() {

        final CharSequence[] option1 = {"Take Photo", "Choose from Gallery","Select Document", "Cancel"};
        imageOptions = option1;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        //builder.setTitle("Add Photo!");
        builder.setItems(imageOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (option1[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 101);
                } else if (imageOptions[item].equals("Choose from Gallery")) {

                    Intent intent = new Intent(mContext, AlbumSelectActivity.class);
                    //set limit on number of images that can be selected, default is 10
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, numberOfImagesToSelect);
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                   /* Intent i = new Intent(Ac.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, 200);*/
                } else if (imageOptions[item].equals("Select Document")) {


                    Intent intent = new Intent();
                    //sets the select file to all types of files
                    intent.setType("*/*");
                    //allows to select data and return it
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //starts new activity to select file and return data
                    startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), 102);


                } else if (imageOptions[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     //   permissionFragmentHelper.onActivityForResult(requestCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == 101 ) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this

                try {
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true);
                    Uri tempUri = Utils.getImageUri(mContext.getApplicationContext(), resized);
                    imagepath = Utils.getPath(tempUri, getActivity());
                    Log.d("Camera", "" + imagepath);
                //    imgPic.setImageBitmap(resized);
                    String filename = imagepath.substring(imagepath.lastIndexOf("/") + 1);

                  //  tvImage.setText("" + filename);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE && data != null) {
                //The array list has the image paths of the selected images
                ArrayList<Image> imagesPathList = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                for (int i = 0; i < imagesPathList.size(); i++) {
                    File image = new File(imagesPathList.get(i).path);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true);

                    bitmapList.add(bitmap);

                    Log.d(TAG, "imagepath : " + imagesPathList.get(i).path);
                }

            }else  if (requestCode == 102) {

                Uri uri = data.getData();


                if (uri != null) {
                    if (uri.toString().contains("content://com.android.externalstorage")) {
                        //  HashSet<String>  getMountedMMC =   getExternalMounts();
                        String[] getMountedMMC = getExternalStorageDirectories();
                        int len = uri.getPath().indexOf(':');

                        imagepath = getMountedMMC[0] + "/" + uri.getPath().substring(len + 1);


                        Log.d(TAG, "FilePath : " + imagepath);
                    } else {
                        imagepath = RealPathUtil.getPath(mContext, uri);
                    }
                }

                String extension = imagepath.substring(imagepath.lastIndexOf("."));
                String fileName = imagepath.substring(imagepath.lastIndexOf("/")).replace("/", "");
                Log.d("onActivityResult", "onActivityResult: " + imagepath);
                Log.d("onActivityResult", "onActivityResult: " + extension);
                Log.d("onActivityResult", "fileName: " + fileName);

               // tvFile.setText("" + fileName);
                Log.d(TAG, "FilePath : " + imagepath);
                Log.d(TAG, "extension : " + extension);

                String content = readFromFile(new File(imagepath),fileName);

                Log.d(TAG, "onActivityResult: "+content);

            }
        }

    }

    private String readFromFile(File file,String fileName) {
        //Read text from file
        StringBuilder text = new StringBuilder();
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        PdfUtls.createdPdf(text.toString(),align,fileName,"PDF");



        return text.toString();
    }


    public  class BitmapToPdfTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        Activity activity;
        String folderName;
        String fileName;
        ArrayList<Bitmap> bitmapList;


        private ProgressDialog dialog;

        public BitmapToPdfTask(Activity activity, ArrayList<Bitmap> bitmapList, String folderName, String fileName) {
            dialog = new ProgressDialog(activity);

            this.bitmapList = bitmapList;
            this.activity = activity;
            this.folderName = folderName;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... params) {

            try {
       /*         WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics displaymetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                float hight = displaymetrics.heightPixels;
                float width = displaymetrics.widthPixels;
*/

                PdfDocument document = new PdfDocument();
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);

                paint.setColor(Color.parseColor("#ffffff"));
                for (int i = 0; i < bitmapList.size(); i++) {

                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmapList.get(i).getWidth(), bitmapList.get(i).getHeight(), i + 1).create();


                    PdfDocument.Page page = document.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    canvas.drawPaint(paint);
                    Bitmap bitmap = Bitmap.createScaledBitmap(bitmapList.get(i), bitmapList.get(i).getWidth(), bitmapList.get(i).getHeight(), true);
                    canvas.drawBitmap(bitmap, 0, 0, null);

                    document.finishPage(page);

                }


                // write the document content
                String targetPdf = Environment.getExternalStorageDirectory() + "/" + folderName + "/" + fileName + ".pdf";
                File filePath = new File(targetPdf);
                try {
                    document.writeTo(new FileOutputStream(filePath));

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                // close the document
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    public void createBitmapToAscii(){

// bitmap
     //   Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.image);
// Bitmap image = BitmapFactory.decodeFile(filename);

// quality
        int quality = 3; // 1 - 3

// convert image(bitmap) to ascii(string)
        Img2Ascii image2ascii = new Img2Ascii();
        String ascii = image2ascii.convert(bitmapList.get(0), quality);

        Log.d(TAG, "createBitmapToAscii: "+ascii);

// textView.setTypeface(monospaceFont);
// textView.setText(ascii);
    }


    /* returns external storage paths (directory of external memory card) as array of Strings */
    public String[] getExternalStorageDirectories() {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = mContext.getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                } else {
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if (addPath) {
                    results.add(path);
                }
            }
        }

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            StringBuilder output = new StringBuilder();
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output.append(new String(buffer));
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.toString().trim().isEmpty()) {
                String devicePoints[] = output.toString().split("\n");
                for (String voldPoint : devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        }

        String[] storageDirectories = new String[results.size()];
        for (int i = 0; i < results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;
    }
}
