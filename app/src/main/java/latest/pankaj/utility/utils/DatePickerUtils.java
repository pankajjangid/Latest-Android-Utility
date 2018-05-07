package latest.pankaj.utility.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by user on 12-Feb-18.
 */

public class DatePickerUtils {


    private static final String TAG = "DatePickerUtils";
    public static int toDay = 0, toMonth = 0, toYear = 0;
    public static int fromYear = 0, fromMonth = 0, fromDay = 0;
    public static String toDate = "";
    public static String fromDate = "";

    public static void selectToDate(final Context mContext, final TextView textView) {

        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //this code will be executed on devices running LOLLIPOP or later
            final Calendar c = Calendar.getInstance();

            if (toDay == 0 && toMonth == 0 && toYear == 0) {
                toYear = c.get(Calendar.YEAR);
                toMonth = c.get(Calendar.MONTH);
                toDay = c.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {

                        @SuppressLint("DefaultLocale")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            String lastDate = "";
                            lastDate = textView.getText().toString();
                            toDay = dayOfMonth;
                            toMonth = monthOfYear;
                            toYear = year;
                            //edtDateTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            //  tvDate.setText(String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + year);
                            textView.setText(String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year);

                            String time = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());
                            Log.d(TAG, "onDateSet: " + time);

                            toDate = String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year
                                    + " "
                                    + time;

                            Log.d(TAG, "toDate : " + toDate);

                        }
                    }, toYear, toMonth, toDay);
            datePickerDialog.setTitle("To Date");
            if (toDay != 0) {
                datePickerDialog.updateDate(toYear, toMonth, toDay);
            }
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //    tvDate.setText("");


                }
            });


            datePickerDialog.show();


        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            final Calendar c = Calendar.getInstance();
            if (toDay == 0 && toMonth == 0 && toYear == 0) {
                toYear = c.get(Calendar.YEAR);
                toMonth = c.get(Calendar.MONTH);
                toDay = c.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {

                        @SuppressLint({"SetTextI18n", "DefaultLocale"})
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            String lastDate = "";
                            lastDate = textView.getText().toString();

                            toDay = dayOfMonth;
                            toMonth = monthOfYear;
                            toYear = year;
                            textView.setText(String.format("%02d", dayOfMonth) + "-" + MONTHS[monthOfYear] + "-" + year);
                            String time = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());
                            Log.d(TAG, "onDateSet: " + time);


                            toDate = String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year
                                    + " "
                                    + time;

                            Log.d(TAG, "toDate : " + toDate);

                        }
                    }, toYear, toMonth, toDay);
            datePickerDialog.setTitle("To Date");
            if (toDay != 0) {
                datePickerDialog.updateDate(toYear, toMonth, toDay);
            }
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //    tvDate.setText("");
                }
            });


            datePickerDialog.show();


        }


    }

    public static void selectFromDate(final Context mContext, final TextView textView) {
        final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //this code will be executed on devices running LOLLIPOP or later
            final Calendar c = Calendar.getInstance();
            if (fromYear == 0 || fromMonth == 0 || fromDay == 0) {

                fromYear = c.get(Calendar.YEAR);
                fromMonth = c.get(Calendar.MONTH);
                fromDay = c.get(Calendar.DAY_OF_MONTH);
            }


            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            fromDay = dayOfMonth;
                            fromMonth = monthOfYear;
                            fromYear = year;
                            //edtDateTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            //     fromDate = tvFromDate.getText().toString();
                            //       fromDate = String.format("%02d", dayOfMonth) + "-" + MONTHS[monthOfYear] + "-" + year;
                            //   showTime();
                            //edtDateTime.setCursorVisible(false);

                            textView.setText(String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year);

                            String time = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());
                            Log.d(TAG, "onDateSet: " + time);

                            fromDate = String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year
                                    + " "
                                    + time;

                            Log.d(TAG, "fromDate : " + fromDate);


                        }
                    }, fromYear, fromMonth, fromDay);
            datePickerDialog.setTitle("from Date");
            if (fromDay != 0) {
                datePickerDialog.updateDate(fromYear, fromMonth, fromDay);
            }
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // tvFromDate.setText("");
                }
            });
            datePickerDialog.show();


        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            final Calendar c = Calendar.getInstance();

            if (fromYear == 0 || fromMonth == 0 || fromDay == 0) {

                fromYear = c.get(Calendar.YEAR);
                fromMonth = c.get(Calendar.MONTH);
                fromDay = c.get(Calendar.DAY_OF_MONTH);
            }


            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            fromDay = dayOfMonth;
                            fromMonth = monthOfYear;
                            fromYear = year;
                            //edtDateTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            textView.setText(String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year);

                            String time = new SimpleDateFormat("HH:mm:ss a").format(Calendar.getInstance().getTime());
                            Log.d(TAG, "onDateSet: " + time);

                            fromDate = String.format("%02d", dayOfMonth) + "/" + MONTHS[monthOfYear] + "/" + year
                                    + " "
                                    + time;

                            Log.d(TAG, "fromDate : " + fromDate);

                            // fromDate = tvFromDate.getText().toString();
                            //  fromDate = String.format("%02d", dayOfMonth) + "-" + MONTHS[monthOfYear] + "-" + year;
                            //   showTime();
                            //edtDateTime.setCursorVisible(false);


                        }
                    }, fromYear, fromMonth, fromDay);
            datePickerDialog.setTitle("from Date");
            if (toDay != 0) {
                datePickerDialog.updateDate(toYear, toMonth, toDay);
            }
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  tvFromDate.setText("");
                }
            });
            datePickerDialog.show();


        }


    }


}
