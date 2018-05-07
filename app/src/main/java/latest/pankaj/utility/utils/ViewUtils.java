package latest.pankaj.utility.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Pankaj on 25/10/2017.
 */

public class ViewUtils {

    private static final String TAG = "ViewUtils";
    /***
     * Show live character counter for the number of characters typed in the parameter {@link EditText}
     *
     * @param editTextView Characters to count from
     * @param textCounterView {@link TextView} to show live character count in
     * @param maxCharCount Max characters that can be typed in into the parameter edittext
     * @param countdown if true, only the remaining of the max character count will be displayed. If false,
     * current character count as well as max character count will be displayed in the UI.
     ****/
    public static void setLiveCharCounter(EditText editTextView, final TextView textCounterView, final int maxCharCount, final boolean countdown) {

        if (editTextView == null) {
            throw new NullPointerException("View to count text characters on cannot be null");
        }

        if (textCounterView == null) {
            throw new NullPointerException("View to display count cannot be null");
        }

        // initialize the TextView initial state
        if (countdown) {
            textCounterView.setText(String.valueOf(maxCharCount));
        } else {
            textCounterView.setText(String.valueOf("0 / " + maxCharCount));
        }

        // initialize the edittext
        setMaxLength(editTextView, maxCharCount);

        editTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (countdown) {
                    // show only the remaining number of characters
                    int charsLeft = maxCharCount - s.length();

                    if (charsLeft >= 0) {
                        textCounterView.setText(String.valueOf(charsLeft));
                    }
                } else {
                    // show number of chars / maxChars in the UI
                    textCounterView.setText(s.length() + " / " + maxCharCount);
                }

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    /***
     * Set max text length for textview
     ****/
    public static void setMaxLength(TextView textView, int maxLength) {

        if (textView == null) {
            throw new NullPointerException("TextView cannot be null");
        }

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        textView.setFilters(fArray);
    }

    /**
     * Aparece una vista con una animación de fade durante una duración
     *
     * @param v
     * @param duration
     */
    public static void appear( View v, int duration ) {
        if ( v.getVisibility() != View.VISIBLE ) {
            ObjectAnimator.ofFloat( v, "alpha", 0, 1 ).setDuration( duration ).start();
            v.setVisibility( View.VISIBLE );
        }
    }

    /**
     * Desaparece una vista con una animación de fade durante una duración
     *
     * @param v
     * @param duration
     */
    public static void disappear( View v, int duration ) {
        if ( v.getVisibility() == View.VISIBLE ) {
            Animation fadeInAnimation = AnimationUtils.loadAnimation( v.getContext(), android.R.anim.fade_out );
            fadeInAnimation.setDuration( duration );
            v.startAnimation( fadeInAnimation );
            v.setVisibility( View.GONE );
        }
    }

    /**
     * Convierte un valor en PX a DP
     *
     * @param px
     * @param context
     * @return
     */
    public static float convertPixelsToDp( float px, Context context ) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ( (float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT );
        return dp;
    }

    /**
     * Convierte un valor en DP a PX
     *
     * @param mContext
     * @param dipValue
     * @return
     */
    public static int getPixels( Context mContext, int dipValue ) {
        if ( mContext != null && dipValue > 0 ) {
            Resources r = mContext.getResources();
            int px = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics() );
            return px;
        } else
            return 0;
    }
    /**
     * Obtiene un drawable por su nombre representado en un String
     *
     * @param context
     * @param name nombre del recurso drawable
     * @return
     */
    public static int getDrawableByString( Context context, String name ) {
        return getResourceByString( context, "drawable", name );
    }

    /**
     * Obtiene un recurso por su nombre en string y su tipo en string. El "mipmap" "ic_launcher"
     *
     * @param context
     * @param resourceType
     * @param name
     * @return
     */
    public static int getResourceByString( Context context, String resourceType, String name ) {
        int resource = 0;

        try {
            resource = context.getResources()
                    .getIdentifier( name, resourceType, context.getPackageName() );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return resource;
    }


    /**
     * Recurse parents children and children children views
     * @param view View
     * @return List of views
     */
    public static ArrayList<View> getAllChildrenInView(View view) {
        ArrayList<View> result = null;
        try {
            if (!(view instanceof ViewGroup)) {
                ArrayList<View> viewArrayList = new ArrayList<>();
                viewArrayList.add(view);
                return viewArrayList;
            }
            result = new ArrayList<>();
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {

                View child = viewGroup.getChildAt(i);

                ArrayList<View> viewArrayList = new ArrayList<>();
                viewArrayList.add(view);
                viewArrayList.addAll(getAllChildrenInView(child));

                result.addAll(viewArrayList);
            }
        } catch (Exception e) {
            Log.d(TAG, "getAllChildrenInView: "+e);        }
        return result;
    }

}
