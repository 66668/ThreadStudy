package com.sjy.threaddemo.invoke_type1.threadHandler;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.Constants;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ThreadHandler的研究
 */
public class ThreadHandlerActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        title.setText("Handler+Thread");

        mList = new ArrayList<>();
        mList.add("1-Thread的两种方式：new Thread");
        mList.add("2-Thread的两种方式：implement Runnable");
        mList.add("1-Thread的两种方式：new 自定义Thread");
        mList.add("2-Thread的两种方式：implement 自定义Runnablee");
        mList.add("特殊Thread：HandlerThread");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:

                        break;


                }
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.TYPE0:
                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                break;

            case Constants.TYPE1:
                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                break;
        }
    }


    /**
     * 1-Thread的两种方式：new 自定义Thread
     */
    private void type2() {

    }

    //自定义Thread
    private class MyThread2 extends Thread {

        public MyThread2(String name) {
            super(name);
        }

        public MyThread2(ThreadGroup group, String name) {
            super(group, name);
        }

        public MyThread2(Runnable target, String name) {
            super(target, name);
        }

        public MyThread2(ThreadGroup group, Runnable target, String name) {
            super(group, target, name);
        }

        public MyThread2(ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
        }

        public MyThread2() {
        }

        public MyThread2(Runnable target) {
            super(target);
        }

        public MyThread2(ThreadGroup group, Runnable target) {
            super(group, target);
        }
    }
}

