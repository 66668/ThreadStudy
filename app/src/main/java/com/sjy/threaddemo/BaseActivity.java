package com.sjy.threaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * 基类
 * 封装 简写设置
 */
public class BaseActivity extends Activity {

    //设置handler软引用，避免handler内存泄漏
    public final Handler handler = new WeakRefHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //===================================handler弱引用--开始=======================================

    public class WeakRefHandler extends  Handler{
        private final WeakReference<BaseActivity> mWeakReference;

        public WeakRefHandler(BaseActivity baseThreadActivity) {
            mWeakReference = new WeakReference<BaseActivity>(baseThreadActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BaseActivity baseAct =mWeakReference.get();

            if (baseAct!=null) {
                //交给该基类自定义的handleMessage方法处理
                baseAct.handleMessage(msg);
            }
        }
    }

    /**
     * 子类复写使用
     * @param msg
     */
    protected void handleMessage(Message msg) {

    }

    //===================================handler弱引用--结束=======================================
}
