package com.sjy.threaddemo.invoke_type1.intentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.Constants;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 特点：
 * <p>
 * 它本质是一种特殊的Service,继承自Service并且本身就是一个抽象类
 * 它可以用于在后台执行耗时的异步任务，当任务完成后会自动停止
 * 它拥有较高的优先级，不易被系统杀死（继承自Service的缘故），因此比较适合执行一些高优先级的异步任务
 * 它内部通过HandlerThread和Handler实现异步操作
 * 创建IntentService时，只需实现onHandleIntent和 空构造方法这两步骤即可，onHandleIntent为异步方法，可以执行耗时操作
 * 使用步骤：
 * 1:自定义的ItnentService重写onHandleIntent方法
 * 2：在Manifest.xml注册自定义的ItnentService
 * 3: startService(intent);启动服务
 */
public class IntentServiceActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private ImageView img;
    private StringBuilder builder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        img = findViewById(R.id.img);
        title.setText("IntentSerice详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("方式1：自定义回调UI");
        mList.add("方式2：LocalBroadcastManager触发广播回调UI");
        mList.add("方式3：act触发广播回调UI");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        startType1();
                        break;
                    case 1:
                        if (!isStop) {
                            startType2();
                            mList.remove(1);
                            mList.add("方式2：关闭IntentService");
                            adapter.setmList(mList);
                            isStop = true;
                        } else {
                            stopType2();
                            mList.remove(1);
                            mList.add("方式2：LocalBroadcastManager触发广播回调UI");
                            adapter.setmList(mList);
                            isStop = false;
                        }
                        break;
                    case 2:
                        startType3();
                        break;
                }
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        initType2();
        initType3();

    }

    /**
     * ==================================方法1：模拟下载图片==================================
     */
    private void startType1() {
        time = 0;
        Intent intent = new Intent(this, MyIntentService1.class);

        for (int i = 0; i < url.length; i++) {
            intent.putExtra(MyIntentService1.DOWNLOAD_URL, url[i]);
            intent.putExtra(MyIntentService1.FLAG, i);
            startService(intent);
        }

        //启动回调
        MyIntentService1.setCallback(new MyIntentService1.UpdataUICallBack() {
            @Override
            public void updataUI(Message msg) {
                handler.sendMessageDelayed(msg, time * 1000);
                time++;
            }
        });
    }

    int time = 0;//计数使用

    //图片地址集合
    private String url[] = {
            "http://ww1.sinaimg.cn/large/0065oQSqly1frrifts8l5j30j60ojq6u.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frmuto5qlzj30ia0notd8.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frjd77dt8zj30k80q2aga.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frqscr5o00j30k80qzafc.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frrifts8l5j30j60ojq6u.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frmuto5qlzj30ia0notd8.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frjd77dt8zj30k80q2aga.jpg"
    };


    /**
     * ==================================方法2：LocalBroadcastManager管理广播==================================
     */

    private LocalBroadcastManager mLocalBroadCastManager;
    private MyBroadCastReceiver myBroadCastReceiver;
    private boolean isStop = false;

    //初始化
    private void initType2() {
        mLocalBroadCastManager = LocalBroadcastManager.getInstance(this);
        myBroadCastReceiver = new MyBroadCastReceiver();
        //添加最全的action过滤，只处理过滤的action,没添加不处理
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.type.service");
        intentFilter.addAction("action.type.thread");
        intentFilter.addAction("type3");
        //注册广播
        mLocalBroadCastManager.registerReceiver(myBroadCastReceiver, intentFilter);
    }

    //启动
    private void startType2() {
        tv_content.setText(null);
        builder = new StringBuilder();
        //
        Intent intent = new Intent(this, MyIntentService2.class);
        //二选一，设置onHandleIntent的2个处理情况
//        intent.putExtra("type",Constants.TYPE0);
        intent.putExtra("type", Constants.TYPE1);
        //启动服务
        startService(intent);
    }

    //关闭（对于循环异步，没有作用）
    private void stopType2() {
        Intent stopIntent = new Intent(this, MyIntentService2.class);
        //二选一，设置onHandleIntent的2个处理情况
//        stopIntent.putExtra("type",Constants.TYPE0);
        stopIntent.putExtra("type", Constants.TYPE1);
        stopService(stopIntent);
    }

    //处理返回结果
    public class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "action.type.service":
                    builder.append(intent.getStringExtra("result"));
                    tv_content.setText(builder.toString());
                    break;
                case "action.type.thread":
                    builder.append(intent.getStringExtra("result"));
                    tv_content.setText(builder.toString());
                    break;
                case "type3":
                    builder.append(intent.getStringExtra("result"));
                    tv_content.setText("方法3："+builder.toString());
                    break;
            }
        }
    }

    /**
     * ==================================方法3：act管理广播==================================
     */

    private MyBroadCastReceiver myBroadCastReceiver3;

    private void initType3() {
        //添加最全的action过滤，只处理过滤的action,没添加不处理
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("type3");
        myBroadCastReceiver3 = new MyBroadCastReceiver();
        this.registerReceiver(myBroadCastReceiver3, intentFilter);
    }

    //启动
    private void startType3() {
        tv_content.setText(null);
        builder = new StringBuilder();

        Intent intent = new Intent(this, MyIntentService3.class);
        //二选一，设置onHandleIntent的2个处理情况
        intent.putExtra("type",Constants.TYPE0);
//        intent.putExtra("type", Constants.TYPE1);
        //启动服务
        startService(intent);
    }

    //====================================================================================
    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.TYPE0:
                img.setImageBitmap((Bitmap) msg.obj);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
        mLocalBroadCastManager.unregisterReceiver(myBroadCastReceiver);
    }


}

