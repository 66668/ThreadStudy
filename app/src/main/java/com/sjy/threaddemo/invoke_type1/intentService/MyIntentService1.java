package com.sjy.threaddemo.invoke_type1.intentService;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sjy.threaddemo.Constants;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 自定义 IntentService 只需要一个构造 +重写的onHandleIntent方法就可以使用
 * 本实例 使用接口回调
 */
public class MyIntentService1 extends IntentService {
    public static final String DOWNLOAD_URL = "download_url";
    public static final String FLAG = "flag";
    public static UpdataUICallBack callback;

    /**
     * TODO 需要定义空参的构造，不然报错（）
     */
    public MyIntentService1() {
        this("name1");
    }

    /**
     * @param name TODO super(name)报错 如下不报错，什么鬼
     */
    public MyIntentService1(String name) {
        super("name1");
    }


    public static void setCallback(UpdataUICallBack callbacka) {
        callback = callbacka;
    }

    /**
     * 重写 可用于实现耗时操作
     *
     * @param intent Activity传递过来的Intent,数据封装在intent中
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //在子线程中进行网络请求
        if (intent != null) {
            Bitmap bitmap = downloadUrlBitmap(intent.getStringExtra(DOWNLOAD_URL));
            Message msg1 = new Message();
            msg1.what = Constants.TYPE0;
            msg1.obj = bitmap;

            //通知主线程去更新UI
            if (callback != null) {
                callback.updataUI(msg1);
            }
        }
    }

    public interface UpdataUICallBack {
        void updataUI(Message msg);
    }

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

    //========================service的其他重写方法（测试玩）=======================

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
        Log.d("SJY", "setIntentRedelivery");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SJY", "onCreate");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("SJY", "onStart");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d("SJY", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SJY", "onDestroy");
        Toast.makeText(this, "IntentService执行完下载图片自动销毁了", Toast.LENGTH_LONG).show();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SJY", "onBind");
        return super.onBind(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("SJY", "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("SJY", "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("SJY", "onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("SJY", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("SJY", "onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("SJY", "onTaskRemoved");
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(fd, writer, args);
        Log.d("SJY", "dump");
    }
}
