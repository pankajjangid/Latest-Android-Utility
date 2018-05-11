package latest.pankaj.utility.time_line_view.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import latest.pankaj.utility.R;
import latest.pankaj.utility.time_line_view.example.model.Orientation;


/**
 * Created by HP-HP on 07-06-2016.
 */
public class TimeLineMainActivity extends AppCompatActivity {

    public final static String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";
    public final static String EXTRA_WITH_LINE_PADDING = "EXTRA_WITH_LINE_PADDING";

    @BindView(R.id.verticalTimeLineButton)
    Button mVerticalTimeLineButton;
    @BindView(R.id.verticalTimeLineButtonWPadding)
    Button mVerticalTimeLineButtonWPadding;
    @BindView(R.id.horizontalTimeLineButton)
    Button mHorizontalTimeLineButton;
    @BindView(R.id.horizontalTimeLineButtonWPadding)
    Button mHorizontalTimeLineButtonWPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_main);

        ButterKnife.bind(this);

        mVerticalTimeLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.VERTICAL, false);
            }
        });

        mHorizontalTimeLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.HORIZONTAL, false);
            }
        });

        mVerticalTimeLineButtonWPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.VERTICAL, true);
            }
        });

        mHorizontalTimeLineButtonWPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(Orientation.HORIZONTAL, true);
            }
        });

    }

    private void onButtonClick(Orientation orientation, boolean withLinePadding) {
        Intent intent = new Intent(this, TimeLineActivity.class);
        intent.putExtra(EXTRA_ORIENTATION, orientation);
        intent.putExtra(EXTRA_WITH_LINE_PADDING, withLinePadding);
        startActivity(intent);
    }

}
