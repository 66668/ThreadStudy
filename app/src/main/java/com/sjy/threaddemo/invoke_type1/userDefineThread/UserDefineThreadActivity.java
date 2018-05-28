package com.sjy.threaddemo.invoke_type1.userDefineThread;

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
 * 1-Thread的两种方式：new 自定义Thread
 * <p>
 * 自定义Thread的研究
 * 当自定义Thread(Runnable run1)类中有重写的run方法，执行顺序是；先执行参数run1，再执行重写的run方法
 *
 */
public class UserDefineThreadActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;

    //用于测试 自定义Thread(Runnable run)
    private StringBuilder builder = new StringBuilder();//非线程安全

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        title.setText("Thread更多用法详解");

        mList = new ArrayList<>();
        mList.add("构造01--Thread()");
        mList.add("构造02--Thread(String name)");
        mList.add("构造03--Thread(Runnable target)");
        mList.add("构造04--Thread(Runnable target, String name)");
        mList.add("构造05--Thread(ThreadGroup group, Runnable target)");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        tv_content.setText(null);
                        MyThread thread0 = new MyThread();
                        thread0.start();
                        break;
                    case 1:
                        tv_content.setText(null);
                        MyThread thread1 = new MyThread("myName_SJY");
                        thread1.start();
                        break;

                    case 2:
                        tv_content.setText(null);
                        builder = new StringBuilder();
                        MyThread2 thread2 = new MyThread2(new Runnable() {
                            @Override
                            public void run() {
                                String str = "这是自定义线程的run方法，new Thread(Runnable target)先执行";
                                builder.append(str);

                                //
                                Message message = Message.obtain();
                                message.what = Constants.TYPE1;
                                message.obj = str;
                                handler.sendMessage(message);
                            }
                        });
                        thread2.start();
                        break;
                    case 3:
                        tv_content.setText(null);

                        break;
                    case 4:

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
//                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                tv_content.setText("更新主线程UI-->" + builder.toString());
                break;

            case Constants.TYPE2:
                tv_content.setText("更新主线程UI-->" + builder.toString());
//                    Toy
                break;
        }
    }


    /**
     *
     */
    private void type() {

    }

    //自定义Thread
    private class MyThread extends Thread {

        /**
         * 01 new 自定义无参thread,需在该线程中重写run方法。
         */
        public MyThread() {

        }

        /**
         * 02 给自定义的thread线程起一个名称
         *
         * @param name
         */
        public MyThread(String name) {
            super(name);
        }


        @Override
        public void run() {
            super.run();
            String str = "这是自定义线程的run方法，new Thread()/new Thread(String name)+start自动执行" + "-->getName=" + getName();

            Message message = Message.obtain();
            message.what = Constants.TYPE0;
            message.obj = str;
            handler.sendMessage(message);
        }
    }

    /**
     * 自定义Thread
     */
    private class MyThread2 extends Thread {
        /**
         * 03 implement Runnable形式,该Runnable可以自定义
         *
         * @param target
         */
        public MyThread2(Runnable target) {
            super(target);
        }

        /**
         * 04 有名称的线程，使用Runnable形式
         *
         */
        public MyThread2(Runnable target, String name) {
            super(target, name);
        }

        /**
         * 05
         *
         * @param group
         * @param target
         */
        public MyThread2(ThreadGroup group, Runnable target) {
            super(group, target);
        }

        public MyThread2(ThreadGroup group, String name) {
            super(group, name);
        }


        public MyThread2(ThreadGroup group, Runnable target, String name) {
            super(group, target, name);
        }

        public MyThread2(ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
        }

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String str = "这是自定义线程的重写的run方法，后执行（sleep 2000ms）";
            builder.append("\n" + str);
            //
            Message message = Message.obtain();
            message.what = Constants.TYPE2;
            message.obj = str;
            handler.sendMessage(message);
        }
    }
}

