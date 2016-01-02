package com.example.administrator.ourclother.util;

/**
 * Created by Administrator on 2015/4/10.
 */
import java.text.DecimalFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.example.administrator.ourclother.R;

public class AsyncTaskRunnable implements Runnable{

    public static final String TAG = "AsyncTaskRunnable";
    //主线程的activity
    private Context mContext;
    //notification的状态：更新 or 失败 or 成功
    private int mStatus;
    //notification的下载比例
    private float mSize;
    //管理下拉菜单的通知信息
    private NotificationManager mNotificationManager;
    //下拉菜单的通知信息
    private Notification mNotification;
    //下拉菜单的通知信息的view
    private RemoteViews mRemoteViews;
    //下拉菜单的通知信息的种类id
    private static final int NOTIFICATION_ID = 1;

    //设置比例和数据
    public void setDatas(int status , float size) {
        this.mStatus = status;
        this.mSize = size;
    }
    //初始化
    public AsyncTaskRunnable(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //初始化下拉菜单的通知信息
        mNotification = new Notification();
        mNotification.icon = com.example.administrator.ourclother.R.drawable.ic_launcher;//设置下载进度的icon
        mNotification.tickerText = mContext.getResources().getString(R.string.app_name); //设置下载进度的title

        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.down_notification);//对于RemoteView的使用，不懂的需要查找google
        mRemoteViews.setImageViewResource(R.id.id_download_icon, R.drawable.ic_launcher);
    }

    @Override
    public void run() {//通过判断不同的状态：更新中/下载失败/下载成功 更新下拉菜单的通知信息
        switch (mStatus) {
            case AsyncTaskUtil.NOTIFICATION_PROGRESS_FAILED://下载失败
                mNotificationManager.cancel(NOTIFICATION_ID);
                break;

            case AsyncTaskUtil.NOTIFICATION_PROGRESS_SUCCEED://下载成功
                mRemoteViews.setTextViewText(R.id.id_download_textview, "Download completed ! ");
                mRemoteViews.setProgressBar(com.example.administrator.ourclother.R.id.id_download_progressbar, 100, 100, false);
                mNotification.contentView = mRemoteViews;
                mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                mNotificationManager.cancel(NOTIFICATION_ID);
                Toast.makeText(mContext, "Download completed ! ", Toast.LENGTH_SHORT).show();
                break;

            case AsyncTaskUtil.NOTIFICATION_PROGRESS_UPDATE://更新中
                DecimalFormat format = new DecimalFormat("0.00");//数字格式转换
                String progress = format.format(mSize);
                Log.d(TAG, "the progress of the download " + progress);
                mRemoteViews.setTextViewText(R.id.id_download_textview, "Download completed : " + progress + " %");
                mRemoteViews.setProgressBar(R.id.id_download_progressbar, 100, (int)mSize, false);
                mNotification.contentView = mRemoteViews;
                mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                break;
        }
    }

}
