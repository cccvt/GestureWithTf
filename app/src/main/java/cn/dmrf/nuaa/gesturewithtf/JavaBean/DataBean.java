package cn.dmrf.nuaa.gesturewithtf.JavaBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by dmrf on 18-3-14.
 */

public class DataBean extends BmobObject {

    private String I;
    private String Q;

    public String getPre_label() {
        return Pre_label;
    }

    public void setPre_label(String pre_label) {
        Pre_label = pre_label;
    }

    private String Pre_label;


    private String filename;


    public String getI() {
        return I;
    }

    public void setI(String i) {
        I = i;
    }

    public String getQ() {
        return Q;
    }

    public void setQ(String q) {
        Q = q;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



}
