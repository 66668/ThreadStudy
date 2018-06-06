package com.sjy.threaddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sjy.threaddemo.adapter.InvokeAdapter;
import com.sjy.threaddemo.asynctask.AsyncTaskActivity;
import com.sjy.threaddemo.intentService.IntentServiceActivity;
import com.sjy.threaddemo.invoke_type1.HandlerAndThreadActivity;
import com.sjy.threaddemo.threadpool.ThreadPoolActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 四种线程方式:
 */
public class InvokeThreadActivity extends Activity {
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
        title.setText("四种方式调用线程");
        tv_content.setVisibility(View.GONE);

        mList = new ArrayList<>();
        mList.add("方式1：Handler+Thread的常规使用");
        mList.add("方式2：IntentService（HandlerThread+Handler的异步封装）");
        mList.add("方式3：Asynctask（Handler+Thread的封装）");
        mList.add("方式4：线程池");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(InvokeThreadActivity.this, HandlerAndThreadActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(InvokeThreadActivity.this, IntentServiceActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(InvokeThreadActivity.this, AsyncTaskActivity.class);
                        startActivity(intent3);
                        break;
                    case 3:
                        Intent intent4 = new Intent(InvokeThreadActivity.this, ThreadPoolActivity.class);
                        startActivity(intent4);
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
     *
     */
    private void type1() {

    }

}

