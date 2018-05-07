package latest.pankaj.utility.adapter_uitls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import latest.pankaj.utility.R;


/**
 * Created by user on 19-Feb-18.
 */

public class AutoCompleteUtil {

    private static final String TAG = "AutoCompleteUtil";
    public static String ID = "";

    public static void fillAutoComplete(final Context mContext
            , AutoCompleteTextView mAutoCompleteTextView
            , final List<AutoCompletePojo> mainPojoList
            , String DefaultID) {

        ID = "";
        Log.d(TAG, "================ fillAutoComplete START ==============");
        Log.d(TAG, "fillAutoComplete mainPojoList size : " + mainPojoList.size());
        Log.d(TAG, "fillAutoComplete DefaultID : " + DefaultID);

        Log.d(TAG, "================ fillAutoComplete END ==============");

        final List<String> Name = new ArrayList<>();
        final Map<String, String> nameWithIDList = new HashMap<String, String>();
        final Map<String, String> idWithNameList = new HashMap<String, String>();

        for (int i = 0; i < mainPojoList.size(); i++) {
            Name.add(mainPojoList.get(i).getNAME());
            nameWithIDList.put(mainPojoList.get(i).getID(), mainPojoList.get(i).getNAME());
            idWithNameList.put(mainPojoList.get(i).getNAME(), String.valueOf(mainPojoList.get(i).getID()));

            Log.d(TAG, "onResponse: Name " + Name.get(i));
        }


        //  final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_text_custom, Name);
//android.R.layout.simple_list_item_1
        final AutoSuggestAdapter adapter = new AutoSuggestAdapter(mContext, R.layout.spinner_text_custom, Name);

        //   AutoCompleteAdapter adapter  = new AutoCompleteAdapter(mContext,R.layout.spinner_text_custom,Name);
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);


        try {
            if (DefaultID != null && !DefaultID.equals("")) {
                for (int i = 0; i < mainPojoList.size(); i++) {
                    if (DefaultID.equals(mainPojoList.get(i).getID())) {
                        mAutoCompleteTextView.setText(mainPojoList.get(i).getNAME());
                        ID = "" + mainPojoList.get(i).getID();
                        // etPrice.setText(""+list.get(i).getItemPrice());
                        Log.d(TAG, "fillAutoComplete : ID " + ID);
                    }


                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String selection = (String) parent.getItemAtPosition(position);
                for (int i = 0; i < mainPojoList.size(); i++) {
                    if (mainPojoList.get(i).getNAME().equals(selection)) {
                        ID = mainPojoList.get(i).getID();
                        Log.d(TAG, "fillAutoComplete : ID " + ID);
                        break;
                    }
                }
            }
        });


        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ID = "";

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    public static class AutoCompletePojo {

        public String ID;
        public String NAME;
        public String EXTRA_1;

        public String getEXTRA_1() {
            return EXTRA_1;
        }

        public void setEXTRA_1(String EXTRA_1) {
            this.EXTRA_1 = EXTRA_1;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }
    }


}
