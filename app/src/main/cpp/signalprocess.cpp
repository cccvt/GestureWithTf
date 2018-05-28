#include <jni.h>
#include <string>
#include <fcntl.h>
#include "mycic/MyCic.h"
#include "sup/support.cpp"


static void mean(float *data, int len, float &mean, float &max, float &min) {


    float sum = data[0];
    max = data[0];
    min = data[0];

    for (int i = 1; i < 4400; ++i) {
        sum += data[i];
        if (data[i] > max)max = data[i];
        if (data[i] < min)min = data[i];
    }

}


static void normalize(float *data, int len) {


    float m = 0.0;
    float mx = 0.0;
    float mn = 0.0;
    mean(data, len, m, mx, mn);
    for (int i = 0; i < 4400; ++i) {
        data[i] = (data[i] - m) / (mx - mn);
    }


}


newdemo *demo_data = NULL;

extern "C"
JNIEXPORT void
Java_cn_dmrf_nuaa_gesturewithtf_JniClass_SignalProcess_DemoNew(
        JNIEnv *env,
        jobject /* this */
) {
    if (demo_data != NULL)
        delete demo_data;
    demo_data = new newdemo();
}

extern "C"
JNIEXPORT jdouble
Java_cn_dmrf_nuaa_gesturewithtf_JniClass_SignalProcess_DemoL(
        JNIEnv *env,
        jobject /* this */,
        jshortArray BUFF,
        jdoubleArray REDist,
        jdoubleArray tII,
        jdoubleArray tQQ
) {
    jshort *Buff = (env)->GetShortArrayElements(BUFF, 0);
    jdouble *O_dist = (env)->GetDoubleArrayElements(REDist, 0);
    jdouble *tempII = (env)->GetDoubleArrayElements(tII, 0);
    jdouble *tempQQ = (env)->GetDoubleArrayElements(tQQ, 0);
    double RE = -1;


    short *coL = new short[6600];
    if (demo_data->now > 3) {//过滤每次录音前0.3
        memcpy(coL, demo_data->lastRecordL, 2200 * sizeof(short));        //上一切片
        memcpy(coL + 2200, Buff, 4400 * sizeof(short));        //当前切片
    }
    memcpy(demo_data->lastRecordL, Buff + 2200,
           2200 * sizeof(short));   //为下一窗口保留  recBufSize/2 - LastLength = 2200

    if (demo_data->now > 4) {
        demo(coL, demo_data->now, demo_data->lastL, demo_data->levdIL, demo_data->levdQL, O_dist,
             tempII, tempQQ);

        RE = 1;
    }
//    demo_data->now++;         //后调用的负责加一

    (env)->ReleaseDoubleArrayElements(tQQ, tempQQ, 0);
    (env)->ReleaseDoubleArrayElements(tII, tempII, 0);
    (env)->ReleaseShortArrayElements(BUFF, Buff, 0);
    (env)->ReleaseDoubleArrayElements(REDist, O_dist, 0);

    return RE;

}

extern "C"
JNIEXPORT jdouble
Java_cn_dmrf_nuaa_gesturewithtf_JniClass_SignalProcess_DemoR(
        JNIEnv *env,
        jobject /* this */,
        jshortArray BUFF,
        jdoubleArray REDist,
        jdoubleArray tII,
        jdoubleArray tQQ
) {
    jshort *Buff = (env)->GetShortArrayElements(BUFF, 0);
    jdouble *O_dist = (env)->GetDoubleArrayElements(REDist, 0);
    jdouble *tempII = (env)->GetDoubleArrayElements(tII, 0);
    jdouble *tempQQ = (env)->GetDoubleArrayElements(tQQ, 0);

    double RE = -1;

    short *coR = new short[6600];
    if (demo_data->now > 3) {//过滤每次录音前0.3
        memcpy(coR, demo_data->lastRecordR, 2200 * sizeof(short));        //上一切片
        memcpy(coR + 2200, Buff, 4400 * sizeof(short));        //当前切片
    }
    memcpy(demo_data->lastRecordR, Buff + 2200,
           2200 * sizeof(short));   //为下一窗口保留  recBufSize/2 - LastLength = 2200

    if (demo_data->now > 4) {

        demo(coR, demo_data->now, demo_data->lastR, demo_data->levdIR, demo_data->levdQR, O_dist,
             tempII, tempQQ);

        RE = 1;
    }
    demo_data->now++;         //后调用的负责加一

    (env)->ReleaseDoubleArrayElements(tQQ, tempQQ, 0);
    (env)->ReleaseDoubleArrayElements(tII, tempII, 0);
    (env)->ReleaseShortArrayElements(BUFF, Buff, 0);
    (env)->ReleaseDoubleArrayElements(REDist, O_dist, 0);

    return RE;
}


extern "C"
JNIEXPORT void
Java_cn_dmrf_nuaa_gesturewithtf_JniClass_SignalProcess_Normalize(JNIEnv *env,
                                                                 jobject /* this */,
                                                                 jfloatArray I,
                                                                 jfloatArray Q) {

    jfloat *data_i = (env)->GetFloatArrayElements(I, 0);
    jfloat *data_q = (env)->GetFloatArrayElements(Q, 0);


    normalize(data_i, 4400);
    normalize(data_q, 4400);


    (env)->ReleaseFloatArrayElements(I, data_i, 0);
    (env)->ReleaseFloatArrayElements(Q, data_q, 0);

}

