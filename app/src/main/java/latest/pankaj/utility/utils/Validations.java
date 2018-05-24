package latest.pankaj.utility.utils;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kushal on 4/26/2016.
 */
public class Validations {


    /*
    Be between 8 and 40 characters long
    Contain at least one digit.
    Contain at least one lower case character.
    Contain at least one upper case character.
    Contain at least on special character from [ @ # $ % ! . ].
     */
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
    private Matcher matcher;

    public static boolean isValidPassword(EditText etPassword, String error) {
         Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        boolean val = false;
        String password = etPassword.getText().toString().trim();

        Matcher    matcher = pattern.matcher(password);

        if (matcher.matches()) {

            val = true;
        } else {

            etPassword.setError(error);
            etPassword.requestFocus();

        }

        return val;
    }


    // Check 10 Digit Mobile Number From Edtitext

    public static boolean isValidMobile(EditText mobile, String error) {

        boolean val = false;
        String mob = mobile.getText().toString().trim();


        if (mob.length() == 10 && !mob.isEmpty()) {

            val = true;
        } else {

            mobile.setError(error);
            mobile.requestFocus();

        }

        return val;
    }

    // Check PIN From EditText
    public static boolean isValidPin(EditText pin) {

        boolean val = false;
        String postal = pin.getText().toString().trim();


        if (postal.length() == 6 && !postal.isEmpty()) {

            val = true;
        } else {

            pin.setError("Invalid pin code");
            pin.requestFocus();

        }

        return val;
    }

    //Check Valid Email Form EditText
    public static boolean isValidEmail(EditText email, String error) {
        boolean val = false;


        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {

            val = true;

        } else {

            email.setError(error);
            email.requestFocus();
        }

        return val;
    }

    // Check Valid Name From EditText
    public static boolean isValidName(EditText name) {

        boolean val = false;
        String mob = name.getText().toString().trim();


        if (!mob.isEmpty()) {

            val = true;
        } else {

            name.setError("Invalid name");
            name.requestFocus();

        }

        return val;
    }

    // Check EditText Is Filled With Error
    public static boolean isEditTextFilled(EditText name, String ErrorMsg) {

        boolean val = false;
        String mob = name.getText().toString().trim();


        if (!mob.isEmpty()) {

            val = true;
        } else {

            name.setError(ErrorMsg);
            name.requestFocus();

        }

        return val;
    }

    // Check ength Of  EditText With Error
    public static boolean isEditTextMaxLenth(EditText name, int length, String ErrorMsg) {

        boolean val = false;
        int editLength = name.getText().toString().length();


        if (editLength <= length) {

            val = true;
        } else {

            name.setError(ErrorMsg);
            name.requestFocus();

        }

        return val;
    }

    // Check Given String Is Filled
    public static boolean isStringFilled(String value, String ErrorMsg, Context context) {

        boolean val = false;
        if (!value.isEmpty()) {

            val = true;
        } else {
            DialogUtils.showAlertDialog("", ErrorMsg, context);
        }

        return val;
    }

    // Check Double Value Greater Than Zero With Alert
    public static boolean isDoubleNotZero(double value, String ErrorMsg, Context context) {

        boolean val = false;
        if (value > 0.0) {

            val = true;
        } else {
            DialogUtils.showAlertDialog("", ErrorMsg, context);
        }

        return val;
    }

    // Check Given CheckBox Is Checked Or Not With Error Msg
    public static boolean isChecked(CheckBox checkBox, String ErrorMsg) {

        boolean val = false;
        if (checkBox.isChecked()) {

            val = true;
        } else {
            // DialogUtils.showAlertDialog(ErrorMsg, context);
            checkBox.setError(ErrorMsg);
        }

        return val;
    }

    // Check Given EditText Has Error Or Not
    public static boolean isErrorOnEditText(String error, Context mContext) {

        boolean val = false;

        if (error.equals("null")) {

            val = true;
        } else {
            val = false;

        }

        return val;
    }

    // Check Aarray List Is Empty Or Not With Error Msg
    public static boolean isArrayListEmpty(int size, String error, Context mContext) {

        boolean val = false;

        if (size != 0) {

            val = true;
        } else {
            val = false;
            DialogUtils.showAlertDialog("", error, mContext);
        }

        return val;
    }

    // Compare Two EditText Both Values Are Eqaul Or Not
    public static boolean isEditTextStringEqual(EditText editText1, EditText editText2, String ErrorMsg) {
        boolean val = false;
        String string1 = editText1.getText().toString().trim();
        String string2 = editText2.getText().toString().trim();

        if (string1.equals(string2)) {

            val = true;
        } else {

            editText1.setError("");
            editText2.setError(ErrorMsg);

            editText1.requestFocus();

        }

        return val;
    }
}
