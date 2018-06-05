package cn.dmrf.nuaa.gesturewithtf.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmrf.gesturewithncnn.R;

import java.io.IOException;

import cn.bmob.v3.Bmob;
import cn.dmrf.nuaa.gesturewithtf.JavaBean.GlobalBean;
import cn.dmrf.nuaa.gesturewithtf.Utils.TensorFlowUtil;
import cn.dmrf.nuaa.gesturewithtf.Utils.VerifyPermission;

public class MainActivity extends AppCompatActivity {
    private GlobalBean globalBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        if (Build.VERSION.SDK_INT >= 23) {
            VerifyPermission verifyPermission = new VerifyPermission(MainActivity.this);
            verifyPermission.RequestPermission();
        }

        Bmob.initialize(this, "9dbc988651cd8b0403a4d8e2566459e9");
        globalBean = new GlobalBean(MainActivity.this);
        globalBean.tensorFlowUtil=new TensorFlowUtil(getAssets(),"abc_gesture_cnn.pb");
      //  globalBean.tensorFlowUtil.PredictTest();

        try {
            Init();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void Init() throws IOException {
        globalBean.btnPlayRecord = (Button) findViewById(R.id.btnplayrecord);
        globalBean.btnStopRecord = (Button) findViewById(R.id.btnstoprecord);
        globalBean.tvDist = (TextView) findViewById(R.id.textView1);
        globalBean.tvDist2 = (TextView) findViewById(R.id.textView2);
        globalBean.flag_small = (ImageView) findViewById(R.id.flag_small);
        globalBean.no_network_worning=(TextView)findViewById(R.id.no_network_worning);
        globalBean.gesturePicture=findViewById(R.id.gesturePicture);

        globalBean.Init();
    }
}
