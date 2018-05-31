package com.sjy.threaddemo.asynctask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 源码解析AsyncTask：
 *（1） AsyncTask内部new ThreadPoolExecutor()，初始化一个静态的线程池
 *（2）接着实例化了AsyncTask内部类SerialExecutor，该类实现了Executor接口中的execute方法，用于串行执行任务
 *（3）AsyncTask的构造，分别初始化了WorkerRunnable（实未 Callback接口）的call方法和FutureTask类，FutureTask的任务执行完成或任务取消的时候会执行FutureTask的done方法
 * (4)接着，UI就可以执行execute（）方法（里头复杂但好理解），最后将结果封装到一个内部类中，用handler处理
 *
 * 使用AsyncTask常见问题：
 * 开启线程后，未结束，此时用户又一次，甚至多次开启线程，导致多次请求。
 * 解决方式：将线程写为静态static。（静态过多容易导致手机内存不足，适量即可）
 * 当用户开启线程后，退出界面，多次进入。由于线程持有Activity的变量的实例，导致Activity无法被回收，从而导致内存泄漏
 * 解决方式：采用弱引用的方式，将线程与Activity进行解耦
 */
public class AsyncTaskActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private StringBuilder builder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        title.setText("AsyncTask详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("方式1：原生用法");
        mList.add("方式2：封装用法");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        startType0();
                        break;
                    case 1:

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
     * ==================================方法1：模拟下载图片==================================
     */
    private void startType0() {

    }


}

