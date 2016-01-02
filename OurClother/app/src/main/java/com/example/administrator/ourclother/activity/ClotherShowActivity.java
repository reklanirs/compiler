
package com.example.administrator.ourclother.activity;

        import android.content.ContentResolver;
        import android.content.Intent;
        import android.database.Cursor;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.ImageView;
        import android.os.Handler;
        import android.os.Message;
        import android.graphics.Bitmap;
        import android.widget.Button;
        import android.view.View;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;

        import java.util.ArrayList;
        import java.util.List;
        import com.example.administrator.ourclother.util.ClotherUrlUtil;

        import com.example.administrator.ourclother.R;
        import com.example.administrator.ourclother.util.ClotherHttpUtil;
        import android.graphics.BitmapFactory;
        import com.example.administrator.ourclother.util.AsyncTaskUtil;
        import com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache;
import com.example.administrator.ourclother.util.imageUtil.ImageFileCache;
import com.example.administrator.ourclother.util.imageUtil.ImageGetFromHttp;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;

public class ClotherShowActivity extends ActionBarActivity {
    private Handler mHandler = new Handler();
    private String clothUrl=null;
    private String clothId=null;
    private String uname=null;
    private String shopOrCollect=null;
    private ImageView image;
    private String url=null;
    Bitmap bitmap = null;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private Button tryCloth;
    private static int IMAGE_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clother_show);
        uname = this.getIntent().getStringExtra("loginId");
        shopOrCollect = this.getIntent().getStringExtra("shopOrCollect");
        System.out.println(shopOrCollect+"收藏++++");
        clothId = this.getIntent().getStringExtra("clothId");

        tryCloth = (Button) findViewById(R.id.tryBtn);
        image = (ImageView) findViewById(R.id.clother);
        memoryCache = new ImageMemoryCache(this);
        fileCache = new ImageFileCache();
        //image.setImageResource(R.drawable.myshow);
        SysApplication.getInstance().addActivity(this);
        tryCloth.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_CODE);
            }
        });
        new Thread() {
            @Override
            public void run() {

                String urlCloth = com.example.administrator.ourclother.util.ClotherHttpUtil.BASE_URL + ClotherUrlUtil.CLOTH_URL;

                Message msg = new Message();
                Bundle bun = new Bundle();
                List<org.apache.http.NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new org.apache.http.message.BasicNameValuePair("clothId", clothId));
                String json = ClotherHttpUtil.getHttpPostResultForUrl(urlCloth, nvps);
                List<String> cloth = new ArrayList<String>();
                cloth = ClotherHttpUtil.parseClothJson(json);
                String clothUrl = cloth.get(0);
                clothUrl=clothUrl.substring(3);
                System.out.print(clothUrl + "                                ");
                String clothDescribe = cloth.get(1);

                System.out.println(clothUrl+"    ");
                url= ClotherHttpUtil.CLOTH_BASE_URL.concat(clothUrl);

                bitmap = getBitmap(url);

                System.out.println("**********");

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
            public void handleMessage(Message msg) {
                System.out.println("handle");
                Bundle ble = msg.getData();


                ArrayList list = ble.getParcelableArrayList("list");
                Bitmap bmp = null;
                bmp = (Bitmap) list.get(0);
                image.setImageBitmap(bmp);


            }
        };
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
                result = com.example.administrator.ourclother.util.imageUtil.ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }
    @Override
    protected  void onResume()
    {
        super.onResume();

        image.setImageBitmap(bitmap);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bm = null;
        ContentResolver cr = this.getContentResolver();
        if(resultCode != RESULT_OK) {

            System.out.println("ActivityResult resultCode error");
            return;
        }else if(requestCode  == IMAGE_CODE){


            try{
                android.net.Uri uri = data.getData();
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                bitmapOptions.inSampleSize = 4;
//                bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,bitmapOptions);
                bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                //显得到bitmap图片
                image.setImageBitmap(bitmap);
            }catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
  //      super.onActivityResult(requestCode, resultCode, data);
    }
}