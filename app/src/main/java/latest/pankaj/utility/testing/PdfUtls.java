package latest.pankaj.utility.testing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 07-Mar-18.
 */

public class PdfUtls {

    //compile 'com.itextpdf:itextpdf:5.0.6'
    // https://mvnrepository.com/artifact/com.itextpdf.tool/xmlworker
    //compile group: 'com.itextpdf.tool', name: 'xmlworker', version: '5.4.1'


    public static void urlToPDF(Context context, String pdfFilename, String url) {

        new BackgroundTask(context, pdfFilename, url).execute();
    }

    public static void bitmapToPdf(Activity activity, ArrayList<Bitmap> bitmapList, int height, String folderName, String fileName) {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;


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
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public static void createdPdf(String text, int ALIGN, String fileName, String folderName) {

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName;

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, fileName + ".pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            //   Font paraFont= new Font(Font.NORMAL);

            switch (ALIGN) {

                case 0:
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    break;
                case 1:
                    p1.setAlignment(Paragraph.ALIGN_LEFT);
                    break;
                case 2:
                    p1.setAlignment(Paragraph.ALIGN_RIGHT);
                    break;

                default:
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    break;
            }

            //       p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }


    }

    // Method for opening a pdf file
    public static void viewPdf(Context mContext, String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            mContext.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    private static class BackgroundTask extends AsyncTask<Void, Void, Void> {
        String pdfFilename;
        String url;
        private ProgressDialog dialog;

        public BackgroundTask(Context context, String pdfFilename, String url) {
            dialog = new ProgressDialog(context);
            this.url = url;
            this.pdfFilename = pdfFilename;
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

        @Override
        protected Void doInBackground(Void... params) {
           /* try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            */

            //path for the PDF file to be generated
            String path = Environment.getExternalStorageDirectory() + "/Dir/" + pdfFilename + ".pdf";
            PdfWriter pdfWriter = null;

            //create a new document
            Document document = new Document();

            try {

                //get Instance of the PDFWriter
                pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path));

                //document header attributes
                // document.addAuthor("betterThanZero");
                // document.addCreationDate();
                // document.addProducer();
                // document.addCreator("MySampleCode.com");
                // document.addTitle("Demo for iText XMLWorker");
                document.setPageSize(PageSize.LETTER);

                //open document
                document.open();

                //To convert a HTML file from the filesystem
                //String File_To_Convert = "docs/SamplePDF.html";
                //FileInputStream fis = new FileInputStream(File_To_Convert);

                //URL for HTML page
                URL myWebPage = new URL(url);
                InputStreamReader fis = new InputStreamReader(myWebPage.openStream());


                //get the XMLWorkerHelper Instance
                XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
                //convert to PDF
                worker.parseXHtml(pdfWriter, document, fis);

                //close the document
                document.close();
                //close the writer
                pdfWriter.close();

            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }


            return null;
        }

    }




}
