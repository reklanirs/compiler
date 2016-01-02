package com.example.administrator.ourclother.activity;


import com.example.administrator.ourclother.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
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
import com.example.administrator.ourclother.util.imageUtil.ImageFileCache;
import com.example.administrator.ourclother.util.imageUtil.ImageGetFromHttp;
import com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;
import android.content.ContentResolver;
import android.util.Log;
import com.example.administrator.ourclother.activity.SuperImageView;
import android.graphics.Point;
import android.graphics.PointF;

public class MainActivity extends Activity {
    private SuperImageView img1;
    private ImageView img2;
    private int screenWidth;
    private int screenHeight;

    private Button save;
    private PointF userpicpos;
    private Bitmap composition;
    private Bitmap bmp;
    private boolean saved;
    private String uname=null;
    private String clothId=null;
    private String shopOrCollect=null;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private Bitmap bitmapUser=null;
    private Button deleteCollect;
    private Button myBody;
    //Bitmap bmp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saved = false;
        userpicpos = new android.graphics.PointF();
        img1 = (SuperImageView) findViewById(R.id.imageView);
        img2 = (ImageView) findViewById(R.id.imageView2);
        save = (Button) findViewById(R.id.save0Btn);
        deleteCollect = (Button) findViewById(R.id.deleteCollectBtn);
        myBody=(Button)findViewById(R.id.myBodyBtn);
        shopOrCollect = this.getIntent().getStringExtra("shopOrCollect");
        uname = this.getIntent().getStringExtra("loginId");
        clothId = this.getIntent().getStringExtra("clothId");
        System.out.println(shopOrCollect+"收到+++");
        if(shopOrCollect.equals("shop"))
        {
            deleteCollect.setText("收藏");
            deleteCollect.setOnClickListener(addCollectListener);
        }
        else {
            deleteCollect.setText("取消收藏");
            deleteCollect.setOnClickListener(deleteCollectListener);
        }
        uname = this.getIntent().getStringExtra("loginId");
        shopOrCollect = this.getIntent().getStringExtra("shopOrCollect");
        clothId = this.getIntent().getStringExtra("clothId");

        memoryCache = new com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache(this);
        fileCache = new ImageFileCache();
        save.setOnClickListener(saveEventListener);
       save.setVisibility(View.INVISIBLE);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;

        myBody.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);


            }
        });
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


        composition=Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8);


    }
    private OnClickListener deleteCollectListener = new OnClickListener() {
        @Override
        public void onClick(View t)
        {
            deleteCollect();



        }
    };
    private OnClickListener addCollectListener = new OnClickListener() {
        @Override
        public void onClick(View t)
        {
            addCollect();




        }
    };
    private Handler handler;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                System.out.println("handle");
                Bundle ble = msg.getData();


                ArrayList list = ble.getParcelableArrayList("list");
                DisplayMetrics dm = getResources().getDisplayMetrics();
                screenWidth = dm.widthPixels;
                screenHeight = dm.heightPixels - 50;

                bmp = (Bitmap) list.get(0);
                bmp.setDensity(dm.densityDpi);
                img2.setImageBitmap(bmp);
                img1.setImageBitmap(bitmapUser);



            }
        };
    }


    protected void deleteCollect() {
        new Thread(){
            @Override
            public void run() {
                System.out.println("deleteCollectStart1"+"_______");
                String urlDeleteCollect = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.DELETE_COLLECT;
                java.util.List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                System.out.println("deleteCollectStart2"+"_______");
                nvps.add(new org.apache.http.message.BasicNameValuePair("loginId", uname));
                nvps.add(new org.apache.http.message.BasicNameValuePair("clothId",clothId));
                String res= ClotherHttpUtil.getHttpPostResultForUrl(urlDeleteCollect, nvps);
                android.os.Message m = new android.os.Message();
                System.out.println("deleteCollectStart3"+"_______");

                System.out.println(res+"　　　　　　　　　　");
                if("-1".equals(res))
                    m.what = ClotherStringUtil.DELETECOLLECT_ERROR;//0
                else if("exception".equals(res))
                    m.what = ClotherStringUtil.INTNERT_ERROR;//20
                else
                    m.what = ClotherStringUtil.DELETECOLLECT_SUCCESS;//1




                proHandleDelete.sendMessage(m);
            }
        }.start();
    }
    private Handler proHandleAdd = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);



            switch(msg.what){
                case ClotherStringUtil.ADDCLOLECT_ERROR:
                    Toast toast1=Toast.makeText(getApplicationContext(), "服务器故障", Toast.LENGTH_LONG);
                    toast1.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast1.show();
                    break;
                case ClotherStringUtil.INTNERT_ERROR:
                    Toast toast2=Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_LONG);
                    toast2.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast2.show();
                    break;
                case ClotherStringUtil.ADDCOLLECT_SUCCESS:
                    Toast toast3=Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_LONG);
                    toast3.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast3.show();
                    deleteCollect.setText("取消收藏");
                    deleteCollect.setOnClickListener(deleteCollectListener);
                    break;
                case ClotherStringUtil.ADDCOLLECT_ALREADY:
                    Toast toast4=Toast.makeText(getApplicationContext(), "已收藏过", Toast.LENGTH_LONG);
                    toast4.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast4.show();
                    deleteCollect.setText("取消收藏");
                    deleteCollect.setOnClickListener(deleteCollectListener);
                    break;

            }
        }
    };
    protected void addCollect() {
        new Thread(){
            @Override
            public void run() {
                String urlAddCollect = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.ADD_COLLECT;
                java.util.List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                nvps.add(new org.apache.http.message.BasicNameValuePair("loginId", uname));
                nvps.add(new org.apache.http.message.BasicNameValuePair("clothId",clothId));
                System.out.println(uname+"++++++"+clothId);
                String res= ClotherHttpUtil.getHttpPostResultForUrl(urlAddCollect, nvps);
                android.os.Message m = new android.os.Message();


                System.out.println(res+"　　　　　　　　　　");
                if("-2".equals(res))
                    m.what = ClotherStringUtil.ADDCOLLECT_ALREADY;//0
                else if("-1".equals(res))
                    m.what = ClotherStringUtil.ADDCLOLECT_ERROR;//0
                else if("exception".equals(res))
                    m.what = ClotherStringUtil.INTNERT_ERROR;//20
                else
                    m.what = ClotherStringUtil.ADDCOLLECT_SUCCESS;//1



                proHandleAdd.sendMessage(m);
            }
        }.start();
    }
    private Handler proHandleDelete = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);



            switch(msg.what){
                case ClotherStringUtil.DELETECOLLECT_ERROR:
                    Toast toast1=Toast.makeText(getApplicationContext(), "服务器故障", Toast.LENGTH_LONG);
                    toast1.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast1.show();
                    break;
                case ClotherStringUtil.INTNERT_ERROR:
                    Toast toast2=Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_LONG);
                    toast2.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast2.show();

                    break;
                case ClotherStringUtil.DELETECOLLECT_SUCCESS:
                    Toast toast3=Toast.makeText(getApplicationContext(), "取消收藏成功", Toast.LENGTH_LONG);
                    toast3.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast3.show();
                    deleteCollect.setText("收藏");
                    deleteCollect.setOnClickListener(addCollectListener);
                    break;

            }
        }
    };
    private OnClickListener saveEventListener = new OnClickListener() {
        @Override
        public void onClick(View t)
        {
            if(!saved) {
                BitmapDrawable t1, t2;
               // t1 = (BitmapDrawable) modelface;
                //t2 = (BitmapDrawable) userface;

                int location1[] = new int[2];
                int location2[] = new int[2];
                img1.getLocationOnScreen(location1);
                img1.getLocationOnScreen(location2);

                userpicpos.set(img1.Center_postion);
                composition = createBitmap(bmp, bitmapUser, userpicpos.x, userpicpos.y, img1.matrix);
                composition = takeScreenShot();
                savePic(composition, Environment.getExternalStorageDirectory().getPath().toString() + "/test/", "1.jpg");
                img2.setImageBitmap(composition);
                saved = true;
                save.setText("Clear");
            }else{
                img2.setImageBitmap(bitmapUser);
                saved = false;
                save.setText("Save");
            }
        }
    };
    public Bitmap takeScreenShot() {
        // View是你需要截图的View
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();


        // 获取状态栏高度
        android.graphics.Rect frame = new android.graphics.Rect();
        getWindow().getDecorView

                ().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // 获取屏幕长和高
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        System.out.println(width);
        System.out.println(height);
        System.out.println(b1.getHeight());
        // 去掉标题栏

        Bitmap b = Bitmap.createBitmap(b1, 30, statusBarHeight+30,
                width-60, height-statusBarHeight-60);

        view.destroyDrawingCache();
        return b;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private Bitmap createBitmap( Bitmap src, Bitmap watermark ,float x,float y, android.graphics.Matrix m) {//src:模特面部 watermark:用户面部
        if( src == null ) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();

//create the new blank bitmap
        Bitmap bm = Bitmap.createBitmap( watermark,0,0,watermark.getWidth(),watermark.getHeight(),m,true);
        Bitmap newb =Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );
        Canvas cv = new Canvas( newb );
//draw src into
        cv.drawBitmap( bm, x, y, null );//在 x，y坐标开始画入用户
//draw watermark into
        cv.drawBitmap( src, 0, 0, null );//在0,0的坐标开始画入模特
//save all clip
        cv.save( Canvas.ALL_SAVE_FLAG );//保存
//store
        cv.restore();//存储
        return newb;
    }
    public void savePic(Bitmap b, String filePath, String fileName)

    {

        File f = new File(filePath);

        if (!f.exists()) {
            f.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath + File.separator + fileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG,90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
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
        save.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

