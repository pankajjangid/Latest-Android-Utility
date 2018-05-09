package latest.pankaj.utility.otp_pin_view_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import latest.pankaj.utility.MainActivity;
import latest.pankaj.utility.R;

public class PinViewActivity2  extends AppCompatActivity {
    private Pinview pinview1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_view2);
        pinview1= (Pinview) findViewById(R.id.pinview1);
        pinview1.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                Toast.makeText(PinViewActivity2.this, pinview.getValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
