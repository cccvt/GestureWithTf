package cn.dmrf.nuaa.gesturewithtf;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Trace;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.ArrayList;

public class TensorFlowUtil {

    private TensorFlowInferenceInterface inferenceInterface;

    private String inputName = "input";
    private String outputName = "softmax";
    private String outputNameint = "output";
    private String[] outputNames;
    private float[] floatValues;
    private float[] outputs;
    private long[] outputint;
    private int classes = 13;
    private int w = 550;
    private int h = 8;
    private int c = 2;
    private int batch = 1;
    private boolean logStats = true;
    private AssetManager assetManager;
    private String target = "H";

    public TensorFlowUtil() {
    }

    @SuppressLint("LongLogTag")
    public void load(AssetManager assetManager, String model) {
        try {
            this.assetManager = assetManager;
            inferenceInterface = new TensorFlowInferenceInterface(assetManager, model);
            outputNames = new String[]{outputName, outputNameint};
            floatValues = new float[w * h * c];

            outputs = new float[classes];
            outputint = new long[1];


        } catch (Exception e) {
            System.out.println("模型加载失败");
        }
    }

    @SuppressLint("LongLogTag")
    public void re() {
        try {
            InputStream is = assetManager.open(target + "_I.txt");
            ArrayList<Float> id = readTextFromFile(is);
            InputStream is2 = assetManager.open(target + "_Q.txt");
            ArrayList<Float> qd = readTextFromFile(is2);

            float dataraw[][][] = new float[8][550][2];

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 550; j++) {
                    for (int k = 0; k < 2; k++) {
                        if (k == 0) {
                            dataraw[i][j][k] = id.get(i * 550 + j);
                        } else {
                            dataraw[i][j][k] = qd.get(i * 550 + j);
                        }

                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 550; j++) {
                    for (int k = 0; k < 2; k++) {
                        floatValues[k  + j * 2 + i * 1100] = dataraw[i][j][k];

                    }
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < 13; i++) {
            outputs[i] = -1;
        }
        //S1. 把图片的像素数据喂给TensorFlow
        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, floatValues, batch, h, w, c);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        //S2. 运行TensorFlow
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();

        // Copy the output Tensor back into the output array.
        //S3. 捕捉输出
        Trace.beginSection("fetch");
        inferenceInterface.fetch(outputNameint, outputint);
        Trace.endSection();
        String log = "\n" + target + ":\n";
//        for (int i = 0; i < 13; i++) {
//            log = log + i + ":" + outputs[i]+"\n";
//
//        }
        Log.i("TensorflowesturePredict", "result:" + outputint[0]);

    }

    /**
     * 按行读取txt
     *
     * @param is
     * @return
     * @throws Exception
     */
    private ArrayList<Float> readTextFromFile(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        ArrayList<Float> data = new ArrayList<>();
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            data.add(Float.parseFloat(str));
        }


        return data;
    }
}
