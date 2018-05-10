package latest.pankaj.utility.gauge_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import latest.pankaj.utility.R;

public class GaugeViewExampleActivity extends AppCompatActivity {
    KdGaugeView speedoMeterView;
    EditText editText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gauge_view_example);

        speedoMeterView = (KdGaugeView)findViewById(R.id.speedMeter);
        editText =(EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}