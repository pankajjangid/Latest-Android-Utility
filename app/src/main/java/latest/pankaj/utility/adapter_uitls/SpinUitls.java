package latest.pankaj.utility.adapter_uitls;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import latest.pankaj.utility.R;


/**
 * Created by user on 21-Mar-18.
 */

public class SpinUitls {
    private static final String TAG = "SpinUitls";
    public static String SpinID = "";

    public static void setSpinner(Context mContext, Spinner spinner, final List<SpinPojo> spinPojosList, String defaultValueToSet) {

        List<String> itemStringList = new ArrayList<>();
        for (int i = 0; i < spinPojosList.size(); i++) {
            itemStringList.add(spinPojosList.get(i).getName());

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_text_custom, itemStringList);
        spinner.setAdapter(adapter);
        ;

        if (defaultValueToSet.equals("")) {
            for (int i = 0; i < spinPojosList.size(); i++) {
                if (defaultValueToSet.equals(spinPojosList.get(i).getID())) {
                    spinner.setSelection(i);
                    SpinID = spinPojosList.get(i).getID();
                    break;
                }
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                SpinID = spinPojosList.get(position).getID();

                Log.d(TAG, "onItemSelected: " + SpinID);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public static class SpinPojo {

        public String ID;
        public String Name;

        public SpinPojo() {
        }


        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }

}
