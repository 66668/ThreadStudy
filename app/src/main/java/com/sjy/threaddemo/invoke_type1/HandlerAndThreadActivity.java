package com.sjy.threaddemo.invoke_type1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.Constants;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;
import com.sjy.threaddemo.invoke_type1.userDefineThread.UserDefineThreadActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 方式1 handler+thread
 * 解释：
 * 开启线程后，子线程独自运行，不影响主线程的的运行，但是，当子线程运行完，需要将结果返回给UI线程，让UI线程更新ui，需要使用handler，这是android独有的方式
 * <p>
 * handler的原生写法，需要再次封装成软饮用/弱引用：当子线程运行过程中，当前Act销毁，子线程结果返回给销毁的Act，会抛出异常
 * 使用弱引用后，销毁的act不会强制处理消息队列信息，避免异常
 * 弱引用的handler,最好写在baseAct中，方便其他act使用
 */
public class HandlerAndThreadActivity extends BaseActivity implements Runnable {

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
        mList.add("2-Thread的两种方式：implement 自定义Runnable");
        mList.add("特殊Thread：HandlerThread");
        mList.add("更多 自定义Thread");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        tv_content.setText(null);
                        type0();
                        break;

                    case 1:
                        tv_content.setText(null);
                        type1();
                        break;

                    case 2:
                        tv_content.setText(null);
                        type2();
                        break;
                    case 3:
                        tv_content.setText(null);
                        type3();
                        break;

                    case 5:

                        Intent intent = new Intent(HandlerAndThreadActivity.this, UserDefineThreadActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

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
            case Constants.TYPE2:
                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                break;

        }
    }

    /**
     * 1-Thread的两种方式：new Thread
     * run执行完之后，未与UI线程交互
     */

    private void type0() {

        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时操作
                long time0 = System.currentTimeMillis();
                long total = 0;
                for (int i = 0; i < 100000; i++) {
                    total = i + total;
                }
                long time1 = System.currentTimeMillis();
                Message message = Message.obtain();
                message.what = Constants.TYPE0;
                message.obj = "100000累加耗时：" + (time1 - time0) + "ms" + "-->结果=" + total;
                handler.sendMessage(message);
            }
        });
        thread0.start();
    }

    /**
     * 2-Thread的两种方式：implement Runnable
     */
    private void type1() {
        Thread thread1 = new Thread(this);
        thread1.start();
    }

    @Override
    public void run() {
        //
        long time0 = System.currentTimeMillis();
        long result = 100000;
        while (result <= 0) {
            result = result - 1;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time1 = System.currentTimeMillis();

        //
        Message message = Message.obtain();
        message.what = Constants.TYPE1;
        message.obj = "100000累减1至0 耗时(睡眠1000ms)：" + (time1 - time0) + "ms";
        handler.sendMessage(message);

    }

    /**
     * 1-Thread的两种方式：new 自定义Thread
     */
    private void type2() {
        MyThread2 thread1 = new MyThread2("name1");
        thread1.start();
        Toast.makeText(this, thread1.getName() + "--" + thread1.getId(), Toast.LENGTH_LONG).show();
    }

    //自定义Thread
    private class MyThread2 extends Thread {

        public MyThread2(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();

            //累加操作
            int total = 0;
            long time0 = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                total = i + total;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long time1 = System.currentTimeMillis();

            //返回
            Message message = Message.obtain();
            message.what = Constants.TYPE2;
            message.obj = "100累加（每次等待10ms） 耗时：" + (time1 - time0) + "ms" + "--线程名称：" + getName() + "--线程id:" + getId();
            handler.sendMessage(message);
        }
    }

    /**
     * 2-Thread的两种方式：implement 自定义Runnable
     */
    private void type3() {
        MyThread2 thread1 = new MyThread2("name1");
        thread1.start();
        Toast.makeText(this, thread1.getName() + "--" + thread1.getId(), Toast.LENGTH_LONG).show();
    }



}

