package com.sjy.threaddemo.invoke_type1.userDefineThread;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 1-Thread的两种方式：new 自定义Thread
 * <p>
 * 自定义Thread的研究
 * （1）当自定义Thread(Runnable run1)类中还有重写的run方法，执行顺序是；先执行参数run1，再执行重写的run方法
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
        mList.add("构造05--Thread(ThreadGroup group, Runnable target, String name, long stackSize)");

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
                        builder = new StringBuilder();

                        MyThread2 thread3 = new MyThread2(new Runnable() {
                            @Override
                            public void run() {
                                String str = "这是自定义线程的run方法，new Thread(Runnable target,String name)先执行";
                                builder.append(str);

                                //
                                Message message = Message.obtain();
                                message.what = Constants.TYPE3;
                                message.obj = str;
                                handler.sendMessage(message);
                            }
                        }, "name_SJY03");
                        thread3.start();
                        break;
                    case 4:
                        tv_content.setText(null);
                        builder = new StringBuilder();
                        //创建10个线程 ，并入线程组中
                        ThreadGroup group = new ThreadGroup("myGroup_SJY");
                        MyRunable3 runable3 = new MyRunable3();
                        for (int i = 0; i < 10; i++) {
                            MyThread3 thread = new MyThread3(group, runable3, "groupName_SJY", 0);
                            builder.append("\n当前线程组=" + thread.getThreadGroup().getName() + "--》" + thread.getThreadGroup().activeCount());
                            thread.start();
                        }
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
                break;

            case Constants.TYPE3:
                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
                break;
            case Constants.TYPE4:
                tv_content.setText("更新主线程UI-->" + builder.toString());
                break;

        }
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
         */
        public MyThread2(Runnable target, String name) {
            super(target, name);
        }


        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String str = "这是自定义线程的重写的run方法，后执行（sleep 2000ms）" + "--》getName=" + getName();
            builder.append("\n" + str);
            //
            Message message = Message.obtain();
            message.what = Constants.TYPE2;
            message.obj = str;
            handler.sendMessage(message);
        }
    }

    /**
     * 自定义Thread
     *
     * ThreadGroup详解
     * 构造 05--08方法，源码显示，最终都调用一个方法，所以使用08演示即可
     */
    private class MyThread3 extends Thread {
        /**
         * 05
         *
         * @param group
         * @param target
         */
        public MyThread3(ThreadGroup group, Runnable target) {
            super(group, target);
        }

        /**
         * 06
         *
         * @param group
         * @param name
         */
        public MyThread3(ThreadGroup group, String name) {
            super(group, name);
        }

        /**
         * 07
         *
         * @param group
         * @param target
         * @param name
         */
        public MyThread3(ThreadGroup group, Runnable target, String name) {
            super(group, target, name);
        }

        /**
         * 08
         *
         * @param group     指定当前线程的线程组
         * @param target    需要指定，或者 在自定义线程中重写run
         * @param name      线程的名称，不指定自动生成
         * @param stackSize 预期堆栈大小，不指定默认为0
         */
        public MyThread3(ThreadGroup group, Runnable target, String name, long stackSize) {
            super(group, target, name, stackSize);
        }
    }

    /**
     *
     */
    private class MyRunable3 implements Runnable {
        @Override
        public void run() {

            int num = (int) ((Math.random())*2000+1000);

            try {
                Thread.sleep(num);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            builder.append("\n当前线程名称=" + Thread.currentThread().getName() + "-->睡眠" + num + "ms");


            //
            Message message = Message.obtain();
            message.what = Constants.TYPE4;
            message.obj = "";
            handler.sendMessage(message);
        }
    }
}