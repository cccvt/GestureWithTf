package cn.dmrf.nuaa.gesturewithtf.JniClass;

public class SignalProcess {
    public native void DemoNew();

    public native int DemoL(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);

    public native int DemoR(short[] Record, double[] DIST, double[] tempII, double[] tempQQ);


    public native  void Normalize(float[] i,float[] q);

    static {
        System.loadLibrary("signalprocess");
    }

}
