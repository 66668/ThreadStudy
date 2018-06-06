package com.sjy.threaddemo.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * #源码解析AsyncTask：
 *（1） AsyncTask内部new ThreadPoolExecutor()，初始化一个静态的线程池
 *（2）接着实例化了AsyncTask内部类SerialExecutor，该类实现了Executor接口中的execute方法，用于串行执行任务
 *（3）AsyncTask的构造，分别初始化了WorkerRunnable（实未 Callback接口）的call方法和FutureTask类，FutureTask的任务执行完成或任务取消的时候会执行FutureTask的done方法
 * (4)接着，UI就可以执行execute（）方法（里头复杂但好理解），最后将结果封装到一个内部类中，用handler处理
 *
 * #使用AsyncTask常见问题：
 * 开启线程后，未结束，此时用户又一次，甚至多次开启线程，导致多次请求。
 * 解决方式：将线程写为静态static。（静态过多容易导致手机内存不足，适量即可）
 * 当用户开启线程后，退出界面，多次进入。由于线程持有Activity的变量的实例，导致Activity无法被回收，从而导致内存泄漏
 * 解决方式：采用弱引用的方式，将线程与Activity进行解耦
 *
 * #AsyncTask优点：
 *为了方便我们在后台线程中执行操作，然后将结果发送给主线程，从而在主线程中进行UI更新等操作
 *
 *
 */
public class AsyncTaskActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private ImageView img;
    private StringBuilder builder;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        img = findViewById(R.id.img);
        progressbar = findViewById(R.id.progressbar);
        title.setText("AsyncTask详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("方式1：原生用法execute无参");
        mList.add("方式2：原生用法execute有参");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:

                        if(!isStop){
                            startType0();
                            mList.remove(0);
                            mList.add(0, "取消");
                            adapter.setmList(mList);
                            isStop =true;
                        }else{

                            //TODO 取消后，需要等待异步执行完才执行取消方法
                            asyncTask0.cancel(true);
                            mList.remove(0);
                            mList.add(0, "方式1：原生用法execute无参");
                            adapter.setmList(mList);
                            isStop =false;
                        }
                        break;
                    case 1:
                        startType1();
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
     * ==================================方法1：原生用法execute,进度条==================================
     */
    private boolean isStop = false;
    private  AsyncTask asyncTask0;
    /**
     * 原生asyncTask用法及工作顺序
     * 无参触发异步(强引用)
     */
    private void startType0() {
        progressbar.setVisibility(View.VISIBLE);
        //初始化及回调处理
        asyncTask0 = new AsyncTask() {

            /**
             *01 主线程中运行，通常用来设置进度条
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * 02 耗时异步
             * 任务可能要分解为好多步骤，每完成一步我们就可以通过调用AsyncTask的publishProgress(Object…)将阶段性的处理结果发布出去，
             * 阶段性处理结果是Object类型。当调用了publishProgress方法后，处理结果会被传递到UI线程中，并在UI线程中回调onProgressUpdate方法
             * @param objects
             * @return
             */
            @Override
            protected Object doInBackground(Object[] objects) {
                for(int i = 0;i<100;i++){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
                return null;
            }

            /**
             * 03 主线程中运行如果在doInBackground中多次调用了publishProgress方法，那么主线程就会多次回调onProgressUpdate方法
             * @param values
             */
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                progressbar.setProgress((Integer) values[0]);
            }


            /**
             *  04 主线程运行 doInBackground执行完就可以将结果传递过来，可以进行UI更新等操作
             * @param o
             */
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }

            /**
             * 05 取消下载的操作
             * @param o
             */
            @Override
            protected void onCancelled(Object o) {
                super.onCancelled(o);
                progressbar.setProgress(0);

            }

            /**
             * 05 取消下载的操作
             */
            @Override
            protected void onCancelled() {
                super.onCancelled();
                progressbar.setProgress(0);

            }
        };

        //触发
        asyncTask0.execute();
    }

    /**
     * ============================================模拟下载图片，并实时回调显示==================================================
     * 原生asyncTask用法及工作顺序
     * 有参触发异步(强引用)
     */
    private void startType1() {
        builder = new StringBuilder();
        //初始化及回调处理
        AsyncTask asyncTask = new AsyncTask() {

            /**
             * 01
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                builder.append("onPreExecute\n");
                tv_content.setText(builder.toString());
            }

            /**
             * 02 耗时异步
             * @param objects
             * @return
             */
            @Override
            protected Object doInBackground(Object[] objects) {
                for(Object url :objects){
                    Bitmap bitmap = downloadUrlBitmap((String) url);
                    publishProgress(bitmap);//触发onProgressUpdate
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }

            /**
             * 03
             * @param values
             */
            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                builder.append("-->onProgressUpdate\n");
                tv_content.setText(builder.toString());
                //显示图
                img.setImageBitmap((Bitmap) values[0]);
            }


            /**
             *  04
             * @param o
             */
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                builder.append("-->onPostExecute（Object）\n");
                tv_content.setText(builder.toString());
                Toast.makeText(AsyncTaskActivity.this,"onPostExecute加载完成",Toast.LENGTH_LONG).show();
            }

            /**
             * 05
             * @param o
             */
            @Override
            protected void onCancelled(Object o) {
                super.onCancelled(o);

                builder.append("-->onPostExecute（Object）\n");
                tv_content.setText(builder.toString());
                Toast.makeText(AsyncTaskActivity.this,"onCancelled(o)",Toast.LENGTH_LONG).show();
            }

            /**
             * 05
             */
            @Override
            protected void onCancelled() {
                super.onCancelled();
                builder.append("-->onCancelled（）\n");
                tv_content.setText(builder.toString());
                Toast.makeText(AsyncTaskActivity.this,"onCancelled()",Toast.LENGTH_LONG).show();

            }
        };

        //触发
        asyncTask.execute(url);
    }

    //图片地址集合
    private String url[] = {
            "http://ww1.sinaimg.cn/large/0065oQSqly1frmuto5qlzj30ia0notd8.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frjd77dt8zj30k80q2aga.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frsllc19gfj30k80tfah5.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frqscr5o00j30k80qzafc.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frrifts8l5j30j60ojq6u.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frmuto5qlzj30ia0notd8.jpg",
            "http://ww1.sinaimg.cn/large/0065oQSqly1frjd77dt8zj30k80q2aga.jpg"
    };

    private Bitmap downloadUrlBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    /**
     *
     */


}

