package com.sjy.threaddemo.threadpool;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjy.threaddemo.BaseActivity;
import com.sjy.threaddemo.Constants;
import com.sjy.threaddemo.R;
import com.sjy.threaddemo.adapter.InvokeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 特点：Executors类详解：
 * Executors类对ThreadPoolExecutor和 ScheduledThreadPoolExecutor做了封装，通过传递不同的参数以达到创建不同类型线程池的目的
 * <p>
 * 使用说明，其下有6类线程创建方式，还有其他三个辅助方法
 * （1）--(3)ExecutorService类实现 （4)(5)ScheduledExecutorService类实现
 * =========================================6类线程创建=======================================
 * (1)
 * public static ExecutorService newSingleThreadExecutor()
 * public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory)
 * 用于创建只有一个线程的线程池
 * hreadFactory，自定义创建线程时的行为
 * <p>
 * (2)
 * public static ExecutorService newCachedThreadPool()
 * public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory)
 * 用于创建线程数数目可以随着实际情况自动调节的线程池
 * 当线程池有很多任务需要处理时，会不断地创建新线程，当任务处理完毕之后，如果某个线程空闲时间大于60s，则该线程将会被销毁。
 * 因为这种线程池能够自动调节线程数量，所以比较适合执行大量的短期的小任务
 * <p>
 * (3)
 * public static ScheduledExecutorService newSingleThreadScheduledExecutor()
 * public static ExecutorService newFixedThreadPool(int nThreads)
 * public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory)
 * 用于创建一个最大线程数的固定的线程池，多余任务存储在队列中，等待线程池某一个线程完成任务，后续任务补充进去继续使用，总之，保持最大线程数不变
 * threadFactory是线程工厂类，主要用于自定义线程池中创建新线程时的行为
 * (4)
 * public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory)
 * 用于创建只有一个线程的线程池，并且该线程定时周期性地执行给定的任务
 * 说明：线程在周期性地执行任务时如果遇到Exception，则以后将不再周期性地执行任务
 * <p>
 * <p>
 * （5）
 * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
 * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory)
 * 用于创建一个线程池，线程池中得线程能够周期性地执行给定的任务
 * <p>
 * (6)
 * public static ExecutorService newWorkStealingPool(int parallelism)
 * public static ExecutorService newWorkStealingPool()
 * 用于创建ForkJoin框架中用到的ForkJoinPool线程池，参数parallelism用于指定并行数，默认使用当前机器可用的CPU个数作为并行数。
 * =============================================其他方法==============================================
 * (7)
 * public static ExecutorService unconfigurableExecutorService(ExecutorService executor)
 * 用于包装现有的线程池，包装之后的线程池不能修改，相当于final
 * (8)
 * public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor)
 * 用于包装可以周期性执行任务的线程池，包装之后的线程池不能修改，相当于final
 * <p>
 * (9)
 * public static ThreadFactory defaultThreadFactory()
 * 返回默认的工厂方法类，默认的工厂方法为线程池中新创建的线程命名为：pool-[虚拟机中线程池编号]-thread-[线程编号
 * <p>
 * (10)public static ThreadFactory privilegedThreadFactory()
 * <p>
 * 返回用于创建新线程的线程工厂，这些新线程与当前线程具有相同的权限。此工厂创建具有与 defaultThreadFactory() 相同设置的线程，
 * 新线程的 AccessControlContext 和 contextClassLoader 的其他设置与调用此 privilegedThreadFactory 方法的线程相同。可以在 AccessController.doPrivileged(java.security.PrivilegedAction) 操作中创建一个新 privilegedThreadFactory，设置当前线程的访问控制上下文，以便创建具有该操作中保持的所选权限的线程。
 * 注意，虽然运行在此类线程中的任务具有与当前线程相同的访问控制和类加载器，但是它们无需具有相同的 ThreadLocal
 * 或 InheritableThreadLocal 值。如有必要，使用 ThreadPoolExecutor.beforeExecute(java.lang.Thread, java.lang.Runnable)
 * 在 ThreadPoolExecutor 子类中运行任何任务前，可以设置或重置线程局部变量的特定值。
 * 另外，如果必须初始化 worker 线程，以具有与某些其他指定线程相同的 InheritableThreadLocal 设置，
 * 则可以在线程等待和服务创建请求的环境中创建自定义的 ThreadFactory，而不是继承其值。
 * <p>
 * (11)
 * <p>
 * public static Callable<Object> callable(Runnable task)
 * 运行给定的任务并返回 null
 * public static <T> Callable<T> callable(Runnable task,T result)
 * 运行给定的任务并返回给定的结果。这在把需要 Callable 的方法应用到其他无结果的操作时很有用
 * public static Callable<Object> callable(PrivilegedAction<?> action)
 * 运行给定特权的操作并返回其结果 /有访问控制和类加载器设定的工具方法
 */
public class ExecutorServiceActivity extends BaseActivity {
    private List<String> mList;
    private InvokeAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView recyclerView;
    private TextView title, tv_content;
    private ImageView img;
    private StringBuilder builder;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        recyclerView = findViewById(R.id.recyclerView);
        title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        img = findViewById(R.id.img);
        title.setText("线程池--Executors类详解");
        tv_content.setVisibility(View.VISIBLE);

        mList = new ArrayList<>();
        mList.add("01 ExecutorService--Executors.newSingleThreadExecutor()");
        mList.add("02 ExecutorService--Executors.newCachedThreadPool()");
        mList.add("03 ExecutorService--Executors.newFixedThreadPool(int nThreads)");
        mList.add("04 ExecutorService--Executors.newSingleThreadScheduledExecutor()");
        mList.add("05 ExecutorService--Executors.newScheduledThreadPool(int nThreads)");
        mList.add("06 ExecutorService--Executors.newWorkStealingPool()");

        mList.add("07 ExecutorService--Executors.newSingleThreadExecutor(ThreadFactory threadFactory)");
        mList.add("08 ExecutorService--Executors.newCachedThreadPool(ThreadFactory threadFactory)");
        mList.add("09 ExecutorService--Executors.newSingleThreadScheduledExecutor(ThreadFactory threadFactory)");
        mList.add("10 ExecutorService--Executors.newFixedThreadPool(int nThreads, ThreadFactory threadFactory)");
        mList.add("11 ExecutorService--Executors.newScheduledThreadPool(int coreThread, ThreadFactory threadFactory)");
        mList.add("12 ExecutorService--Executors.newWorkStealingPool(int parallelism)");

        mList.add("13 Callable--Executors.callable(Runnable task)");
        mList.add("14 Callable--Executors.callable(Runable task,T result)");

        adapter = new InvokeAdapter(this, mList, new InvokeAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        tv_content.setText(null);
                        startType1();
                        break;
                    case 1:
                        tv_content.setText(null);
                        startType2();
                        break;
                    case 2:
                        tv_content.setText(null);
                        startType3();
                        break;
                    case 3:
                        tv_content.setText(null);
                        startType4();
                        break;
                    case 4:
                        tv_content.setText(null);
                        startType5();
                        break;
                    case 5:
                        tv_content.setText(null);
                        startType6();
                        break;
                    case 6:
                        tv_content.setText(null);
                        startType7();
                        break;
                    case 7:
                        tv_content.setText(null);
                        startType8();
                        break;
                    case 8:
                        tv_content.setText(null);
                        startType9();
                    case 9:
                        tv_content.setText(null);
                        startType10();
                        break;
                    case 10:
                        tv_content.setText(null);
                        startType11();
                        break;
                    case 11:
                        tv_content.setText(null);
                        startType12();
                        break;
                    case 12:
                        tv_content.setText(null);
                        startType13();
                        break;
                    case 13:
                        tv_content.setText(null);
                        startType14();
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
     * ==================================方法1==================================
     */
    private void startType1() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();//单线程池
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "(01）Executors.newSingleThreadExecutor";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法2：==================================
     */
    private void startType2() {
        ExecutorService executorService = Executors.newCachedThreadPool();//缓存线程池
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "(02）Executors.newCachedThreadPool";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法3：==================================
     */
    //启动
    private void startType3() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "(03）Executors.newFixedThreadPool(1)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法4==================================
     */
    //启动
    private void startType4() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "(04）Executors.newSingleThreadScheduledExecutor";
                handler.sendMessage(msg);
            }
        });
    }


    /**
     * ==================================方法5：==================================
     */
    //启动
    private void startType5() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);//定时
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（05）Executors.newScheduledThreadPool(1)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法6：==================================
     */

    //启动
    private void startType6() {
        ExecutorService executorService = Executors.newWorkStealingPool();//
        Executors.unconfigurableExecutorService(executorService);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（06）Executors.newScheduledThreadPool(1)";
                handler.sendMessage(msg);
            }
        });

    }

    /**
     * ==================================方法7：==================================
     */

    //启动
    private void startType7() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());//
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（07）Executors.newSingleThreadExecutor(ThreadFactory threadFactory)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法8：==================================
     */

    //启动
    private void startType8() {
        final ExecutorService executorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());//
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（08）Executors.newCachedThreadPool(ThreadFactory threadFactory)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法9：==================================
     */

    //启动
    private void startType9() {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());//

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（09）Executors.newSingleThreadScheduledExecutor(ThreadFactory threadFactory)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法10：==================================
     */

    //启动
    private void startType10() {
        ExecutorService executorService = Executors.newFixedThreadPool(3, Executors.defaultThreadFactory());//

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（10）Executors.newFixedThreadPool(int nThread, ThreadFactory threadFactory)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法11：==================================
     */

    //启动
    private void startType11() {
        ExecutorService executorService = Executors.newScheduledThreadPool(3, Executors.privilegedThreadFactory());//

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（11）Executors.newScheduledThreadPool(int coreThread, ThreadFactory threadFactory)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法12：==================================
     */

    //启动
    private void startType12() {
        ExecutorService executorService = Executors.newWorkStealingPool(3);//
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = Message.obtain();
                msg.what = Constants.TYPE0;
                msg.obj = "（12）Executors.newWorkStealingPool(int parallelism)";
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * ==================================方法13：==================================
     */

    //启动
    private void startType13() {
        try {
            Callable<Object> callable = Executors.callable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = Message.obtain();
                    msg.what = Constants.TYPE0;
                    msg.obj = "（13）Executors.callable(Runable task)";
                    handler.sendMessage(msg);
                }
            });//

            callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ==================================方法14：==================================
     */

    //启动
    private void startType14() {
        try {
            Callable<String> callable = Executors.callable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = Message.obtain();
                    msg.what = Constants.TYPE0;
                    msg.obj = "（14）Executors.callable(Runable task,T result)";
                    handler.sendMessage(msg);
                }
            }, "123");//

            String result = callable.call();//result执行完就是"123"
            Toast.makeText(ExecutorServiceActivity.this, "callable.call()=" + result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ==================================方法15：==================================
     */

    //启动
    private void startType15() {
        try {


            Callable<String> callable = Executors.callable(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = Message.obtain();
                    msg.what = Constants.TYPE0;
                    msg.obj = "（15）Executors.callable(Runable task,T result)";
                    handler.sendMessage(msg);
                }
            }, "123");//

            String result = callable.call();//result执行完就是"123"
            Toast.makeText(ExecutorServiceActivity.this, "callable.call()=" + result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //=========================================handleMessage===========================================
    @Override
    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.TYPE0:
                tv_content.setText("延迟1s显示异步结果-->" + (String) msg.obj);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
    }


}

