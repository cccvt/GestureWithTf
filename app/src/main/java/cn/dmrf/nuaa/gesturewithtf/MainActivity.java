package cn.dmrf.nuaa.gesturewithtf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TensorFlowUtil tensorFlowUtil=new TensorFlowUtil();
        tensorFlowUtil.load(getAssets(),"gesture_cnn256.pb");

        findViewById(R.id.beg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tensorFlowUtil.re();
            }
        });
    }
}
