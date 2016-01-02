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
import com.example.administrator.ourclother.util.ClotherStringUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.example.administrator.ourclother.util.AsyncTaskUtil;
import com.example.administrator.ourclother.util.ClotherHttpUtil;
import com.example.administrator.ourclother.util.ClotherUrlUtil;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;
import android.content.ContentResolver;
import android.util.Log;

public class MainActivity2 extends ActionBarActivity {
    private ImageView img1;
    private ImageView img2;
    private int screenWidth;
    private int screenHeight;
    private Drawable userface;
    private Drawable modelface;
    private Bitmap bitmapUser=null;
    private Button addCollect;
    private Button myBody;
    private position userpicpos;
    private Bitmap composition;
    private Handler mHandler = new Handler();
    private AsyncTaskUtil mDownloadAsyncTask;
    private int count=0;
    private String uname;
    private String clothId;
    Bitmap bmp=null;
private com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache memoryCache;
    private com.example.administrator.ourclother.util.imageUtil.ImageFileCache fileCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        userpicpos = new position();
        img1 = (ImageView) findViewById(R.id.imageView);
        img2 = (ImageView) findViewById(R.id.imageView2);
         addCollect= (Button) findViewById(R.id.addCollectBtn);
        myBody=(Button)findViewById(R.id.myBodyLoadBtn);
        SysApplication.getInstance().addActivity(this);
        uname=this.getIntent().getStringExtra("loginId");
        clothId=this.getIntent().getStringExtra("clothId");
        System.out.println(clothId+"          mainactivity2idididid");



        memoryCache = new com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache(this);
        fileCache = new com.example.administrator.ourclother.util.imageUtil.ImageFileCache();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;
       // img1.setImageDrawable(userface);
        myBody.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });
       //userface= Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","source.png").getAbsolutePath());
   //     modelface= Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","dst.png").getAbsolutePath());

  //      userface=this.getResources().getDrawable(R.drawable.source);
      //  modelface = this.getResources().getDrawable(R.drawable.dst);
        composition=Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8);
        img1.setOnTouchListener(movinguserfaceEventListener);


        addCollect.setOnClickListener(saveEventListener);


         //modelface = Drawable.createFromPath(new File(Environment.getExternalStorageDirectory().toString() + "/AsyncTaskDownload/","dst.png").getAbsolutePath());
                    //img2.setImageDrawable(modelface);
                    count++;
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap=null;
                android.os.Message msg = new android.os.Message();
                Bundle bun = new Bundle();

                bitmap=getBitmap("http://d3.freep.cn/3tb_150405023837ylfg547926.png");
                ArrayList list = new ArrayList(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>

                list.add(bitmap);
                bun.putParcelableArrayList("list", list);
                msg.what = 1;
                msg.setData(bun);
                handler.sendMessage(msg);
            }

        }.start();



    }
     private Handler handler;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                System.out.println("handle");
                Bundle ble = msg.getData();


                ArrayList list = ble.getParcelableArrayList("list");

                bmp = (Bitmap) list.get(0);
                img2.setImageBitmap(bmp);
                img1.setImageBitmap(bitmapUser);



            }
        };
    }


    protected void deleteCollect() {
        new Thread(){
            @Override
            public void run() {
                String urlAddCollect = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.ADD_COLLECT;
                java.util.List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                nvps.add(new org.apache.http.message.BasicNameValuePair("loginId", "tom"));
                nvps.add(new org.apache.http.message.BasicNameValuePair("clothId","305"));
                System.out.println(uname+"++++++"+clothId);
                String res= ClotherHttpUtil.getHttpPostResultForUrl(urlAddCollect, nvps);
                android.os.Message m = new android.os.Message();


                System.out.println(res+"　　　　　　　　　　");
                if("-2".equals(res))
                    m.what = ClotherStringUtil.ADDCOLLECT_ALREADY;//0
                else if("-1".equals(res))
                    m.what = ClotherStringUtil.ADDCLOLECT_ERROR;//0
                else
                    m.what = ClotherStringUtil.ADDCOLLECT_SUCCESS;//1




                proHandle.sendMessage(m);
            }
        }.start();
    }
    private Handler proHandle = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);



            switch(msg.what){
                case ClotherStringUtil.ADDCLOLECT_ERROR:
                    Toast toast1=Toast.makeText(getApplicationContext(), "服务器故障", Toast.LENGTH_LONG);
                    toast1.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast1.show();
                    break;
                case ClotherStringUtil.ADDCOLLECT_SUCCESS:
                    Toast toast2=Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_LONG);
                    toast2.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast2.show();
                    break;
                case ClotherStringUtil.ADDCOLLECT_ALREADY:
                    Toast toast3=Toast.makeText(getApplicationContext(), "已收藏过", Toast.LENGTH_LONG);
                    toast3.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast3.show();
                    break;

            }
        }
    };
    private OnClickListener saveEventListener = new OnClickListener() {
      @Override
        public void onClick(View t)
      {
          deleteCollect();



      }
    };
    private OnTouchListener movinguserfaceEventListener = new OnTouchListener() {
        int lastX, lastY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    img2.setImageBitmap(bmp);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    img2.setImageBitmap(bmp);
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

                    //t2=(BitmapDrawable)userface;
                    composition=createBitmap(bmp,bitmapUser,userpicpos.getX(),userpicpos.getY());
                    img2.setImageBitmap(composition);




                    break;
            }
            return true;
        }
    };

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
    protected  void onResume()
    {
        super.onResume();


    }
    public Bitmap getBitmap(String url) {
        // 从内存缓存中获取图片
        Bitmap result=null;
        result= memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result == null) {
                // 从网络获取
                System.out.println("66666666666");
                result = com.example.administrator.ourclother.util.imageUtil.ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                    System.out.println("77777777777777");
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                // 添加到内存缓存
                System.out.println("8888888888888");
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            android.net.Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();

            try{
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;
                bitmapUser = BitmapFactory.decodeStream(cr.openInputStream(uri),null,bitmapOptions);
                img1.setImageBitmap(bitmapUser);
            }catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*@Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.connect:
                android.widget.Toast.makeText(getApplicationContext(), "设置……", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.disconnect:
                Toast.makeText(getApplicationContext(), "切换账号……", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.search:
                Toast.makeText(getApplicationContext(), "退出……", Toast.LENGTH_SHORT).show();
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
                {
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        SysApplication.getInstance().exit();
                        return true;
                    }
                });
                return true;

        }
        return false;
    }*/
}

