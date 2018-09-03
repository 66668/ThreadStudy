package com.sjy.threaddemo.invoke_type1.threadHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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
 * ThreadHandler的研究:
 * HandlerThread特点:
 * 1本质是线程，继承于Thread
 * 2HandlerThread内部有自己的Looper对象,可以在当前线程中处理分发消息
 * 3通过获取HandlerThread的looper对象传递给Handler对象，可以在handleMessage方法中执行异步任务
 * <p>
 * 优点：
 * <p>
 * 1.开发中如果多次使用类似new Thread(){}.start()这种方式开启子线程，会创建多个匿名线程，使得程序运行起来越来越慢，
 * 而HandlerThread自带Looper使他可以通过消息机制来多次重复使用当前线程，节省开支。
 * <p>
 * 2.Handler类内部的Looper默认绑定的是UI线程的消息队列，对于非UI线程如果需要使用消息机制，
 * 自己去创建Looper较繁琐，由于HandlerThread内部已经自动创建了Looper，直接使用HandlerThread更方便。
 * <p>
 * Message介绍：
 * 我们最好通过Message.obtain()和Handler.obtatinMessage()来得到一个Message对象
 * （通过这两个方法得到的对象是从对象回收池中得到，也就是说是复用已经处理完的Message对象，而不是重新生成一个新对象）
 * ，如果通过Message的构造方法得到一个Message对象，则这个Message对象是重新生成的（不建议使用这种方法）
 */
public class HandlerThreadActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private StringBuilder builder;
    int result = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        title.setText("HandlerThread");

        mList = new ArrayList<>();
        mList.add("用法1：Handler(Looper looper,Callback callback)执行异步");
        mList.add("用法2：Handler(Looper looper）{handleMessage}执行异步");
        mList.add("用法3：handler.post(runnable)执行异步");
        mList.add("用法4:验证handler的post和重写handleMessage的执行顺序");
        mList.add("用法5：RunOnUiThread(子线程中封装handler消息传递与处理)");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        tv_content.setText(null);

                        builder = new StringBuilder();

                        HandlerThread handlerThread1 = new HandlerThread("name_sjy0");
                        //开启线程
                        handlerThread1.start();
                        //构建异步handler
                        Handler childHandler1 = new Handler(handlerThread1.getLooper(), new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                //在子线程中进行耗时操作,结果通过handler返回给UI
                                builder.append("随机数:" + ((int) (Math.random() * 100 + 100) + "--》" + Thread.currentThread().getName() + "\n"));
                                Message message = Message.obtain();
                                message.what = msg.what;
                                message.obj = builder;
                                handler.sendMessage(message);
                                return false;
                            }
                        });

                        //触发 异步handler
                        //如果要用sendMessageDelayed（msg,mills)方法，msg要和异步handler的handleMessage(msg) msg统一
                        for (int i = 0; i < 5; i++) {
                            childHandler1.sendEmptyMessageDelayed(Constants.TYPE0, 1000 * i);
                        }
                        break;
                    case 1:

                        tv_content.setText(null);

                        builder = new StringBuilder();
                        HandlerThread handlerThread2 = new HandlerThread("name_sjy1");
                        //开启线程
                        handlerThread2.start();
                        //构建异步handler
                        Handler childHandler2 = new Handler(handlerThread2.getLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                //UI线程中更新界面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_content.setText("runOnUiThread方法更新UI，等待后续...");
                                    }
                                });
                                //耗时操作
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message message = Message.obtain();
                                message.what = msg.what;
                                message.obj = "耗时2000ms又更新UI";
                                handler.sendMessage(message);
                            }
                        };
                        //触发 异步handler
                        childHandler2.sendEmptyMessage(Constants.TYPE1);
                        break;
                    case 2:

                        // 1.创建HandlerThread对象，并为子线程指定一个名称
                        HandlerThread handlerThread3 = new HandlerThread("name_sjy2");
                        // 2.调用start()。此时内部run()方法执行，会创建一个绑定当前线程的Looper对象
                        handlerThread3.start();
                        // 3.获取HandlerThread的Looper
                        Looper looper = handlerThread3.getLooper();
                        // 4.使用上面的Looper创建Handler
                        Handler childHandler3 = new Handler(looper);
                        //5.耗时操作
                        childHandler3.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Message message = Message.obtain();
                                message.what = Constants.TYPE2;
                                message.obj = "耗时2000ms更新UI--》" + Thread.currentThread().getName();
                                handler.sendMessage(message);
                            }
                        });

                        //触发 异步handler
                        childHandler3.sendEmptyMessage(Constants.TYPE2);
                        break;
                    case 3:
                        tv_content.setText(null);

                        builder = new StringBuilder();

                        HandlerThread handlerThread4 = new HandlerThread("name_sjy3");
                        handlerThread4.start();
                        Handler childHandler4 = new Handler(handlerThread4.getLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                builder.append("Handler（looper）{handleMessage}后执行--》" + Thread.currentThread().getName() + "\n");
                                Message message = Message.obtain();
                                message.what = Constants.TYPE3;
                                message.obj = "";
                                handler.sendMessage(message);
                            }
                        };

                        //5.耗时操作
                        childHandler4.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                builder.append("handler.post先执行-->" + Thread.currentThread().getName() + "\n");
                                Message message = Message.obtain();
                                message.what = Constants.TYPE3;
                                message.obj = "";
                                handler.sendMessage(message);
                            }
                        });

                        //触发 异步handler
                        childHandler4.sendEmptyMessage(Constants.TYPE3);
                        break;
                    case 4:
                        //开启一个子线程：Thread为例，
                        tv_content.setText("");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //子线程耗时操作，
                                result = 0;
                                for (int i = 1; i < 1000000; i++) {
                                    if (i % 2 == 0) {
                                        result = i / 2 + result;
                                    } else {
                                        result = result - i;
                                    }
                                }
                                //子线程中将结果传递给UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //UI操作即可
                                        tv_content.setText("runOnUiThread更新主线程UI-->" +result);
                                    }
                                });

                            }
                        }).start();
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
                tv_content.setText("更新主线程UI-->" + ((StringBuilder) msg.obj).toString());
                break;

            case Constants.TYPE1:
                tv_content.setText((String) msg.obj);
                break;
            case Constants.TYPE2:
                tv_content.setText("更新主线程UI-->" + (String) msg.obj);
                break;

            case Constants.TYPE3:
                tv_content.setText(builder.toString());
                break;
        }
    }


}

