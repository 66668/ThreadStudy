package com.sjy.threaddemo.threadpool;

import android.content.Intent;
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
public class ThreadPoolActivity extends BaseActivity {
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
        title.setText("线程池详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("原生调用1：ExecutorService");
        mList.add("原生调用2：");
        mList.add("方式3：act触发广播回调UI");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ThreadPoolActivity.this, ExecutorServiceActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        tv_content.setText(null);
                        startType2();
                        break;
                    case 2:
                        tv_content.setText(null);
                        startType3();
                        break;
                }
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


    }

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
     * ==================================方法1==================================
     */



    /**
     * ==================================方法2：==================================
     */


    private void startType2() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(Constants.TYPE0);
            }
        });

    }


    /**
     * ==================================方法3：act管理广播==================================
     */


    //启动
    private void startType3() {

    }

    //====================================================================================
    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.TYPE0:
                tv_content.setText("这是一种用法，延迟1s显示异步结果");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
    }


}

