package com.example.administrator.ourclother.activity;

/**
 * Created by Administrator on 2015/3/21.
 */
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.administrator.ourclother.util.CustomViewBinder;
import com.example.administrator.ourclother.R;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.List;
import com.example.administrator.ourclother.R;
import com.example.administrator.ourclother.util.ClotherHttpUtil;
import android.graphics.BitmapFactory;
import com.example.administrator.ourclother.util.ClotherUrlUtil;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import java.util.Map;
import com.example.administrator.ourclother.util.SysApplication;
import com.example.administrator.ourclother.util.imageUtil.ImageMemoryCache;
import com.example.administrator.ourclother.util.imageUtil.ImageFileCache;
import com.example.administrator.ourclother.util.imageUtil.ImageGetFromHttp;

public class MyCollectList extends ListActivity{
    private SimpleAdapter listitemadapter;
    ArrayList<HashMap<String,Object>> listitem;
    private  ListView mListView;
    private   View mView;
    private String uname;

    private Bitmap[] bitmaps;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private ImageView imageView;
    String[] collectSingle=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollectlist);

        mView = LayoutInflater.from(this).inflate(R.layout.list_item, null);
        mListView=(ListView)findViewById(R.id.list_item);
        uname=this.getIntent().getStringExtra("loginId");
        SysApplication.getInstance().addActivity(this);
        System.out.println("List出现！！！！！！！！！！！！");
        memoryCache = new ImageMemoryCache(this);
        fileCache = new ImageFileCache();
        showList();

    }
  public void showList(){
      new Thread() {
          @Override
          public void run() {
              String urlCollect = com.example.administrator.ourclother.util.ClotherHttpUtil.BASE_URL + ClotherUrlUtil.USER_COLLECT;
              String urlCloth = com.example.administrator.ourclother.util.ClotherHttpUtil.BASE_URL + ClotherUrlUtil.CLOTH_URL;

              Message msg = new Message();
              Bundle bun = new Bundle();
              List<Bitmap> bitmapList=new ArrayList<Bitmap>();
              List<String> clothDescribeList=new ArrayList<String>();
              List<org.apache.http.NameValuePair> nvps1 = new ArrayList<NameValuePair>();

              nvps1.add(new org.apache.http.message.BasicNameValuePair("loginId", uname));
              String collectSet= ClotherHttpUtil.getHttpPostResultForUrl(urlCollect, nvps1);

              if(collectSet.equals("exception"))
              {

                  bun.putString("net", "-1");

              }else if(collectSet.equals(""))
              {
                  bun.putString("net","-2");
              }else
              {
                  collectSingle = collectSet.split(",");
                  for (int i = 0; i < collectSingle.length; i++) {
                      System.out.print(collectSingle[i] + "colcol+++++++");
                      List<org.apache.http.NameValuePair> nvps2 = new ArrayList<NameValuePair>();

                      nvps2.add(new org.apache.http.message.BasicNameValuePair("clothId", collectSingle[i]));
                      String json = ClotherHttpUtil.getHttpPostResultForUrl(urlCloth, nvps2);
                      List<String> cloth = new ArrayList<String>();
                      cloth = ClotherHttpUtil.parseClothJson(json);
                      String clothUrl = cloth.get(0);
                      clothUrl=clothUrl.substring(3);
                      System.out.print(clothUrl + "                                ");
                      String clothDescribe = cloth.get(1);
                      clothDescribeList.add(clothDescribe);
                      System.out.println(clothUrl+"    ");
                      String url= ClotherHttpUtil.CLOTH_BASE_URL.concat(clothUrl);
                      //Bitmap bitmap = ClotherHttpUtil.getHttpPostResultForClothUrl(clothUrl);
                      Bitmap bitmap=null;
                      System.out.println(ClotherHttpUtil.CLOTH_BASE_URL+"    ");


                      System.out.println(clothUrl+"      ");
                      bitmap=getBitmap(url);
                      bitmapList.add(bitmap);
                      System.out.println("**********");
                  }

                  bun.putString("net", "1");
              }
              ArrayList list = new ArrayList(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>

              list.add(bitmapList);
              list.add(clothDescribeList);

              bun.putParcelableArrayList("list", list);

              msg.what = 1;
              msg.setData(bun);
              handler.sendMessage(msg);

          }

      }.start();

  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.search:
                Toast.makeText(getApplicationContext(), "退出……", Toast.LENGTH_SHORT).show();
                SysApplication.getInstance().exit();
                return true;

        }
        return false;
    }
    private void initialList(List bitmapList,List describeList) {
        List<HashMap<String, Object>> mListData = getListData(bitmapList,describeList);

        listitemadapter = new SimpleAdapter(this,mListData,R.layout.list_item,new String[] {"ItemTitle", "ItemImage"},
                new int[] {R.id.ItemTitle, R.id.ItemImage});

        listitemadapter.setViewBinder(new CustomViewBinder());
         this.setListAdapter(listitemadapter);



    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("loginId", uname);
        bundle.putString("shopOrCollect","collect");
        String clothId=collectSingle[position];
        System.out.println(clothId+"         mycollect idididid");
        bundle.putString("clothId",clothId);
        intent.putExtras( bundle);

        intent.setClass(MyCollectList.this, ClotherShowActivity.class);
        MyCollectList.this.startActivity(intent);

    }


     private Handler handler;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                System.out.println("handle");
                Bundle ble=msg.getData();
                String netWork=ble.getString("net");


                if(netWork.equals("1")){
                ArrayList list = ble.getParcelableArrayList("list");
                List<Bitmap> bmp=(ArrayList)ble.get("list");
                bmp= (List<Bitmap>) list.get(0);
                List<String> describeList=new ArrayList<String>();
                describeList=(List<String>)list.get(1);
                initialList(bmp,describeList);
                }
                else if(netWork.equals("-1")){
                    Toast toast=Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_LONG);
                    toast.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    Toast toast=Toast.makeText(getApplicationContext(), "还没有收藏", Toast.LENGTH_LONG);
                    toast.setGravity(android.view.Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        };

    }

    public List<HashMap<String, Object>> getListData(List bitmapList,List describeList) {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = null;
        for (int i = 0; i < bitmapList.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("ItemTitle",describeList.get(i));
            map.put("ItemImage",bitmapList.get(i));


            list.add(map);

        }
        return list;
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
                result = ImageGetFromHttp.downloadBitmap(url);
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

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }
}

