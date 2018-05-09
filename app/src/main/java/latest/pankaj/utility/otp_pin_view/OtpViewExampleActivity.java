package latest.pankaj.utility.otp_pin_view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import latest.pankaj.utility.R;

public class OtpViewExampleActivity extends AppCompatActivity implements View.OnClickListener, OtpListener {
    private Button deleteButton, disableKeypadButton, enableKeypadButton;
    private OtpView otpView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpview_layout);
        initializeUi();
        setListeners();
    }

    @Override public void onClick(View v) {
        if (v == deleteButton) {
            otpView.simulateDeletePress();
        } else if (v == disableKeypadButton) {
            otpView.disableKeypad();
        } else if (v == enableKeypadButton) {
            otpView.enableKeypad();
        }
    }

    @Override public void onOtpEntered(String otp) {
        // do Stuff
        Log.d("onOtpEntered=>", otp);
    }

    private void initializeUi() {
        deleteButton = findViewById(R.id.delete);
        disableKeypadButton = findViewById(R.id.disable_keypad);
        enableKeypadButton = findViewById(R.id.enable_keypad);
        otpView = findViewById(R.id.otp_view);
    }

    private void setListeners() {
        enableKeypadButton.setOnClickListener(this);
        disableKeypadButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        otpView.setListener(this);
    }
}