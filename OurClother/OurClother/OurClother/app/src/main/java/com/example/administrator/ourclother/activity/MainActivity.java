package com.example.administrator.ourclother.activity;


import com.example.administrator.ourclother.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.example.administrator.ourclother.util.AsyncTaskUtil;

public class MainActivity extends ActionBarActivity {
    private ImageView img1;
    private ImageView img2;
    private int screenWidth;
    private int screenHeight;
    private Drawable userface;
    private Drawable modelface;
    private Button save;
    private Button start;
    private position userpicpos;
    private Bitmap composition;
    private Handler mHandler = new Handler();
    private AsyncTaskUtil mDownloadAsyncTask;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userpicpos = new position();
        img1 = (ImageView) findViewById(R.id.imageView);
        img2 = (ImageView) findViewById(R.id.imageView2);
        save = (Button) findViewById(R.id.button);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;

       userface= Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","source.png").getAbsolutePath());
   //     modelface= Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","dst.png").getAbsolutePath());

  //      userface=this.getResources().getDrawable(R.drawable.source);
        modelface = this.getResources().getDrawable(R.drawable.dst);
        composition=Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8);
        img1.setOnTouchListener(movinguserfaceEventListener);
        img1.setImageDrawable(userface);
       // img2.setImageDrawable(modelface);
   //     img2.setOnTouchListener(movingEventListener);
        save.setOnClickListener(saveEventListener);

        //mDownloadAsyncTask = new AsyncTaskUtil(MainActivity.this, mHandler);
        //mDownloadAsyncTask.execute("http://d3.freep.cn/3tb_150405023837ylfg547926.png","dst.png");//必须传入两个参数——参数1：url；参数2：文件名（可以为null）
         modelface = Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","dst.png").getAbsolutePath());
                    img2.setImageDrawable(modelface);
                    count++;



    }
    private OnClickListener saveEventListener = new OnClickListener() {
      @Override
        public void onClick(View t)
      {
          BitmapDrawable t1,t2;
          t1=(BitmapDrawable)modelface;
          t2=(BitmapDrawable)userface;
          composition=createBitmap(t1.getBitmap(),t2.getBitmap(),userpicpos.getX(),userpicpos.getY());
          img2.setImageBitmap(composition);

      }
    };
    private OnTouchListener movinguserfaceEventListener = new OnTouchListener() {
        int lastX, lastY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    img2.setImageDrawable(modelface);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    img2.setImageDrawable(modelface);
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    // 设置不能出界
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    v.layout(left, top, right, bottom);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    userpicpos.setpos(left-25,top-25);
                    break;
                case MotionEvent.ACTION_UP:
                    BitmapDrawable t1,t2;
                    t1=(BitmapDrawable)modelface;
                    t2=(BitmapDrawable)userface;
                    composition=createBitmap(t1.getBitmap(),t2.getBitmap(),userpicpos.getX(),userpicpos.getY());
                    img2.setImageBitmap(composition);




                    break;
            }
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private Bitmap createBitmap( Bitmap src, Bitmap watermark ,int x,int y) {//src:模特面部 watermark:用户面部
        if( src == null ) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();

//create the new blank bitmap
        Bitmap newb = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas( newb );
//draw src into
        cv.drawBitmap( watermark, x, y, null );//在 x，y坐标开始画入用户
//draw watermark into
        cv.drawBitmap( src, 0, 0, null );//在0,0的坐标开始画入模特
//save all clip
        cv.save( Canvas.ALL_SAVE_FLAG );//保存
//store
        cv.restore();//存储
        return newb;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected  void onResume()
    {
        super.onResume();

    }
}
