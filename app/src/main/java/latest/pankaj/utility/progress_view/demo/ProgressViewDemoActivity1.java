package latest.pankaj.utility.progress_view.demo;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import latest.pankaj.utility.R;
import latest.pankaj.utility.progress_view.files.CircleProgressView;
import latest.pankaj.utility.progress_view.files.HorizontalProgressView;

public class ProgressViewDemoActivity1 extends AppCompatActivity {
    private HorizontalProgressView mProgressView20;
    private HorizontalProgressView mProgressView40;
    private HorizontalProgressView mProgressView60;
    private HorizontalProgressView mProgressView80;
    private HorizontalProgressView mProgressView100;

    private CircleProgressView mCircleProgressViewNormal;
    private CircleProgressView mCircleProgressViewFillIn;
    private CircleProgressView mCircleProgressViewFillInArc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view_demo1);
        mProgressView20 = $(R.id.progress20);
        mProgressView40 = $(R.id.progress40);
        mProgressView60 = $(R.id.progress60);
        mProgressView80 = $(R.id.progress80);
        mProgressView100 = $(R.id.progress100);
        mProgressView20.setProgressPosition(HorizontalProgressView.BOTTOM);
        mProgressView80.setProgressPosition(HorizontalProgressView.TOP);

        mCircleProgressViewNormal = $(R.id.circle_progress_normal);
        mCircleProgressViewFillIn = $(R.id.circle_progress_fill_in);
        mCircleProgressViewFillInArc = $(R.id.circle_progress_fill_in_arc);

    }

    public void startWithAnim(View view) {
        mProgressView20.runProgressAnim(1000);
        mProgressView40.runProgressAnim(2000);
        mProgressView60.runProgressAnim(3000);
        mProgressView80.runProgressAnim(4000);
        mProgressView100.runProgressAnim(5000);

        mCircleProgressViewNormal.runProgressAnim(1000);
        mCircleProgressViewFillIn.runProgressAnim(2000);
        mCircleProgressViewFillInArc.runProgressAnim(3000);
    }


    private <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }
}