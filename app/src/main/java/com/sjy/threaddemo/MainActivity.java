package com.sjy.threaddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sjy.threaddemo.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private List<String> mList;
    private MainAdapter adapter;
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
        title.setText("首页");
        tv_content.setVisibility(View.GONE);

        mList = new ArrayList<>();
        mList.add("四种线程调用");
        mList.add("线程同步");

        adapter = new MainAdapter(this, mList, new MainAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, InvokeThreadActivity.class);
                        startActivity(intent);

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
}

