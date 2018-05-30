package latest.pankaj.utility.meter_view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import latest.pankaj.utility.R;

public class MeterViewActivity extends AppCompatActivity {

    Meter meter;
    Float progress = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_view);

        meter = findViewById(R.id.meter);


        animate();


    }

    private void animate() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress = progress + 0.1f;
                meter.animate(progress);

                if (progress < 1.0)
                    animate();
            }
        }, 500);
    }


}
