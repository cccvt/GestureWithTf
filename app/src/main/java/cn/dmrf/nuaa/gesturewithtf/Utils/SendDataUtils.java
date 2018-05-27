package cn.dmrf.nuaa.gesturewithtf.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;



import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.dmrf.nuaa.gesturewithtf.JavaBean.DataBean;
import cn.dmrf.nuaa.gesturewithtf.JavaBean.GlobalBean;

/**
 * Created by dmrf on 18-3-14.
 */

public class SendDataUtils extends AsyncTask<String, Void, Void> {

    private DataBean dataBean;
    private Context context;
    private GlobalBean globalBean;

    public SendDataUtils(DataBean dataBean, Context context, GlobalBean globalBean) {
        this.dataBean = dataBean;
        this.context = context;
        this.globalBean=globalBean;
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (dataBean!=null){
            dataBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                       // Toast.makeText(context,"添加数据成功",Toast.LENGTH_SHORT).show();
                        if (globalBean.is_in_count!=-1){
                            globalBean.is_in_count++;
                        }
                    }else{
                        Toast.makeText(context,"数据创建失败"+s, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return null;
    }



}
