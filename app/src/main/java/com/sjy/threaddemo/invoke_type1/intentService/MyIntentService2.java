package com.sjy.threaddemo.invoke_type1.intentService;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * 自定义 IntentService 只需要一个构造 +重写的onHandleIntent方法就可以使用
 * 本实例 使用接口回调
 */
public class MyIntentService2 extends IntentService {
    private LocalBroadcastManager localBroadcastManager;//发送广播的管理类
    private int num = 20;
    private int num2 = 19;

    public MyIntentService2() {
        this("name2");
    }

    public MyIntentService2(String name) {
        super("name2");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        sendBackMessage("启动服务", "action.type.service");
    }

    //处理异步
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        switch (intent.getIntExtra("type",0)) {
            case 0:
                while (num2 > 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendBackMessage(num2 + "action.type.service\n", "action.type.service");
                    num2 -= 2;
                }
                break;
            case 1:
                while (num > 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendBackMessage(num + "action.type.thread\n", "action.type.thread");
                    num -= 2;
                }
                break;

        }

    }

    private void sendBackMessage(String str, String action) {
        Intent intent1 = new Intent(action);
        intent1.putExtra("result", str);
        localBroadcastManager.sendBroadcast(intent1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "IntentService执行完自动销毁了", Toast.LENGTH_LONG).show();
    }
}
