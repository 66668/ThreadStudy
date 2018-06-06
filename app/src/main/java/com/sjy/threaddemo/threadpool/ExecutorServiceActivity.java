package com.sjy.threaddemo.threadpool;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 特点：
 * <p>
 */
public class ExecutorServiceActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private ImageView img;
    private StringBuilder builder;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        img = findViewById(R.id.img);
        title.setText("线程池--ExecutorService详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("01 ExecutorService--newSingleThreadExecutor");
        mList.add("02 ExecutorService--newCachedThreadPool");
        mList.add("03 ExecutorService--newSingleThreadScheduledExecutor");
        mList.add("04 ExecutorService--newFixedThreadPool(1)");
        mList.add("04 ExecutorService--newScheduledThreadPool(1)");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        tv_content.setText(null);
                        startType1();
                        break;
                    case 1:
                        tv_content.setText(null);
                        startType2();
                        break;
                    case 2:
                        tv_content.setText(null);
                        startType3();
                        break;
                    case 3:
                        tv_content.setText(null);
                        startType4();
                        break;
                    case 4:
                        tv_content.setText(null);
                        startType5();
                        break;
                }
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


    }
    /**
     * ==================================方法1==================================
     */
    private void startType1() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();//单线程池
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "newSingleThreadExecutor";
                handler.sendMessage(msg);
            }
        });
    }
    /**
     * ==================================方法2：==================================
     */
    private void startType2() {
        ExecutorService executorService = Executors.newCachedThreadPool();//缓存线程池
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "newCachedThreadPool";
                handler.sendMessage(msg);
            }
        });
    }
    /**
     * ==================================方法3==================================
     */
    //启动
    private void startType3() {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "newSingleThreadScheduledExecutor";
                handler.sendMessage(msg);
            }
        });
    }
    /**
     * ==================================方法4：==================================
     */
    //启动
    private void startType4() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "newFixedThreadPool(1)";
                handler.sendMessage(msg);
            }
        });
    }
    /**
     * ==================================方法4：==================================
     */
    //启动
    private void startType5() {
        ExecutorService executorService = Executors.newScheduledThreadPool(1);//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "newFixedThreadPool(1)";
                handler.sendMessage(msg);
            }
        });
    }

    //=========================================handleMessage===========================================
    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.TYPE0:
                tv_content.setText("延迟1s显示异步结果-->" + (String) msg.obj);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
    }


}

