package cn.dmrf.nuaa.gesturewithtf.JavaBean;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.dmrf.gesturewithncnn.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.dmrf.nuaa.gesturewithtf.Activity.MainActivity;
import cn.dmrf.nuaa.gesturewithtf.JniClass.SignalProcess;
import cn.dmrf.nuaa.gesturewithtf.Utils.TensorFlowUtil;
import cn.dmrf.nuaa.gesturewithtf.Thread.InstantPlayThread;
import cn.dmrf.nuaa.gesturewithtf.Thread.InstantRecordThread;
import cn.dmrf.nuaa.gesturewithtf.Utils.FrequencyPlayerUtils;
import cn.dmrf.nuaa.gesturewithtf.Utils.NetWorkUtils;
import cn.dmrf.nuaa.gesturewithtf.Utils.SendDataUtils;


/**
 * Created by dmrf on 18-3-15.
 */

public class GlobalBean {

   /*
   set audio
    */

    public double[] Freqarrary = {17500, 17850, 18200, 18550, 18900, 19250, 19600, 19950, 20300, 20650};        //设置播放频率
    public int encodingBitrate = AudioFormat.ENCODING_PCM_16BIT;// 编码率（默认ENCODING_PCM_16BIT）
    public int channelConfig = AudioFormat.CHANNEL_IN_MONO;        //声道（默认单声道） 单道  MONO单声道，STEREO立体声
    public AudioRecord audioRecord;    //录音对象
    public FrequencyPlayerUtils FPlay;
    public int sampleRateInHz = 44100;//采样率（默认44100，每秒44100个点）
    public int recBufSize = 4400;            //定义录音片长度
    public int numfre = 8;
    public char[] CODE = {'A', 'B', 'C', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'};
    public String[] gesture_name = {"Static", "Push Left", "Push Right", "Click", "Flip", "Circle"};
    public float[] scores = new float[13];
    public int[] len_i = new int[2];

    public TensorFlowUtil tensorFlowUtil;


    /*
    views
     */
    public Button btnPlayRecord;        //开始按钮
    public Button btnStopRecord;        //结束按钮
    //    public Button btnSet;        //结束按钮
    public TextView tvDist;
    public TextView tvDist2;
    public TextView no_network_worning;
    public ImageView flag_small;
//    public CheckBox CkBox_send;

    public int is_in_count = -1;
    public int gesture_length = 1100;

    /*
    variable
     */
    public boolean flag = true;        //播放标志
    public boolean flag1 = false;        //jieshu标志
    public boolean senddataflag = true;   //发送数据标志

    public ArrayList<Double> L_I[];
    public ArrayList<Double> L_Q[];


    public String whoandwhich = "W";
    private int flagnum = 0;

    private Context context;

    public SignalProcess signalProcess;

    private String[] codesstr = {"ncnntest", "static", "push left", "push right", "click", "flip", "grab", "release"};

    private int lstm_predict_count = 0;

    private float dataraw[][][] = new float[8][2200][2];
    private float[][] gesturedata = new float[4][8800];


    private int gif_id[] = {R.drawable.staticgesture, R.drawable.push_left, R.drawable.push_right,
            R.drawable.click, R.drawable.grab, R.drawable.release};
    private int git_num = 0;


    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        //设置圆环角度
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj.toString().equals("predict")) {
                        flag_small.setVisibility(View.GONE);
                        long time = System.currentTimeMillis();
                        final String day = String.valueOf(time);

                        PredictContinousGesture();


                        for (int i = 0; i < 8; i++) {
                            L_I[i].clear();
                            L_Q[i].clear();
                        }

                    } else if (msg.obj.toString().equals("start")) {

                        flag_small.setVisibility(View.VISIBLE);
                        Start();
                    } else if (msg.obj.toString().equals("stop")) {
                        flag_small.setVisibility(View.GONE);

                        // btnPlayRecord.setVisibility(View.VISIBLE);

                        //btnStopRecord.setVisibility(View.GONE);

                        FPlay.colseWaveZ();
                        audioRecord.stop();
                        flag1 = false;

                    } else if (msg.obj.toString().equals("playe")) {
                        Toast.makeText(context, "发生了异常，请联系最帅的人优化代码～", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    tvDist.setText(msg.obj.toString());
                    break;
                case 2:
                    tvDist2.setText(msg.obj.toString());
                    break;
                case 3:
                    if (msg.obj.toString().equals("abc")) {
                        PredictAbcGesture();
                        for (int i = 0; i < 8; i++) {
                            L_I[i].clear();
                            L_Q[i].clear();
                        }
                    }

                    break;
            }
        }
    };
    public ImageView gesturePicture;


    private void PredictAbcGesture() {

        float id[] = new float[4400];
        float qd[] = new float[4400];
        float floatValues[] = new float[8800];


        int ks = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                id[ks] = L_I[i].get(j).floatValue();
                qd[ks] = L_Q[i].get(j).floatValue();
                ks++;
            }
        }

        signalProcess.Normalize(id, qd);

        float dataraw[][][] = new float[8][550][2];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                for (int k = 0; k < 2; k++) {
                    if (k == 0) {
                        dataraw[i][j][k] = id[i * 550 + j];
                    } else {
                        dataraw[i][j][k] = qd[i * 550 + j];
                    }

                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                for (int k = 0; k < 2; k++) {
                    floatValues[k + j * 2 + i * 1100] = dataraw[i][j][k];
                }
            }
        }


        int label = tensorFlowUtil.PredictCnnAbc(floatValues);


        Log.i("TensorflowesturePredict", "end result:" + label);


        if (label == 1) {
            tvDist.setText("靠近" + "-" + flagnum);
        } else if (label == 0) {
            tvDist.setText("静止" + "-" + flagnum);
        } else {
            // tvDist.setText("静止"+"-"+flagnum);
        }


        flagnum++;

//        if (flagnum==30){
//            flagnum=0;
//            StopVoi();
//            PlayVoi();
//
//        }

    }


    private void PredictContinousGesture() {//第一个a为0，第二个a为550

        float id[] = new float[4400];
        float qd[] = new float[4400];
        int ks = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                id[ks] = L_I[i].get(j).floatValue();
                qd[ks] = L_Q[i].get(j).floatValue();
                ks++;
            }
        }

        signalProcess.Normalize(id, qd);


        float dataraw[][][] = new float[8][550][2];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                for (int k = 0; k < 2; k++) {
                    if (k == 0) {
                        dataraw[i][j][k] = id[i * 550 + j];
                    } else {
                        dataraw[i][j][k] = qd[i * 550 + j];
                    }

                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                for (int k = 0; k < 2; k++) {
                    gesturedata[lstm_predict_count][k + j * 2 + i * 1100] = dataraw[i][j][k];

                }
            }
        }


        long inde = tensorFlowUtil.PredictContinous(gesturedata, lstm_predict_count);
        tvDist.setText(gesture_name[(int) inde]);
        lstm_predict_count++;
        lstm_predict_count = lstm_predict_count % 4;


/*        if (senddataflag) {
            SaveData(name, max_index, data_i, data_q);
        }*/


    }


    public GlobalBean(Context context) {
        this.context = context;
    }

    public void Init() throws IOException {


        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        final String day = formatter.format(curDate);
        whoandwhich = whoandwhich + "_" + day;


        L_I = new ArrayList[8];
        L_Q = new ArrayList[8];

        for (int i = 0; i < 8; i++) {
            ArrayList<Double> list1 = new ArrayList<Double>();
            ArrayList<Double> list2 = new ArrayList<Double>();
            L_I[i] = list1;
            L_Q[i] = list2;
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,//从麦克风采集音频
                sampleRateInHz,//采样率，这里的值是sampleRateInHz = 44100即每秒钟采样44100次
                channelConfig,//声道设置，MONO单声道，STEREO立体声，这里用的是立体声
                encodingBitrate,//编码率（默认ENCODING_PCM_16BIT）
                recBufSize);//录音片段的长度，给的是minBufSize=recBufSize = 4400 * 2;


        // btnStopRecord.setVisibility(View.GONE);

        InitListener();


    }

    private void InitListener() {


        btnPlayRecord.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                Glide.with(context).load(gif_id[git_num]).into(gesturePicture);
                git_num++;

            }
        });


        //停止按钮
        btnStopRecord.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                // TODO 自动生成的方法存根
                StopVoi();
            }
        });

    }


    private void PlayVoi() {


        Start();

        new InstantPlayThread(GlobalBean.this).start();        //播放(发射超声波)


        try {
            Thread.sleep(10);    //等待开始播放再录音
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new InstantRecordThread(GlobalBean.this, context).start();        //录音
        //录音播放线程
    }

    private void StopVoi() {
        Stop();
    }

    @SuppressLint("ResourceAsColor")
    public void Stop() {
        flag1 = true;
    }

    public void AddDataToList(ArrayList<Double>[] list, double[] data) {

        int count = -1;
        for (int i = 0; i < 880; i++) {
            if (i % 110 == 0) {
                count++;
            }
            list[count].add(data[i]);
        }
    }


    private void Start() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 550; j++) {
                for (int k = 0; k < 2; k++) {
                    dataraw[i][j][k] = 0;
                }
            }
        }
        lstm_predict_count = 0;

        if (L_I[0] != null) {
            for (int i = 0; i < 8; i++) {
                L_I[i].clear();
                L_Q[i].clear();
            }
        }

        flag = true;

    }


    public void ShowChoiseWhich() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("设置手势种类");
        //    指定下拉列表的显示数据
        final String[] codes = {"ncnntest", "static", "push left", "push right", "click", "flip", "grab", "release"};


        //    设置一个下拉的列表选择项
        builder.setItems(codes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                whoandwhich = codes[which];

            }
        });
        AlertDialog alertDialog = builder.create();
        final Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.show();
    }


    private void SaveData(String name, int max_index, float[] data_i, float[] data_q) {

        DataBean dataBean = new DataBean();

        // 自定义一个字符缓冲区，
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("[ ");
        sb2.append("[ ");
        //遍历int数组，并将int数组中的元素转换成字符串储存到字符缓冲区中去
        for (int i = 0; i < data_i.length; i++) {
            if (i != data_i.length - 1) {
                sb.append(data_i[i] + " ,");
                sb2.append(data_q[i] + " ,");
            } else {
                sb.append(data_i[i] + " ]");
                sb2.append(data_q[i] + " ]");
            }

        }

        dataBean.setI(String.valueOf(sb));
        dataBean.setQ(String.valueOf(sb2));
        dataBean.setPre_label(String.valueOf(max_index));
        dataBean.setFilename(whoandwhich + "_" + name);

        SendDataUtils sendDataUtils = new SendDataUtils(dataBean, context, this);
        sendDataUtils.execute("");
    }


}
