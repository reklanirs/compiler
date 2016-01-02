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

public class MyCollectList extends ListActivity{
    private SimpleAdapter listitemadapter;
    ArrayList<HashMap<String,Object>> listitem;
    private  ListView mListView;
    private   View mView;
    private String uname;
    private Bitmap[] bitmaps;
    String[] collectSingle=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycollectlist);
        mView = LayoutInflater.from(this).inflate(R.layout.list_item, null);
        mListView=(ListView)findViewById(R.id.list_item);
        uname=this.getIntent().getStringExtra("loginId");



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
                collectSingle=collectSet.split(",");
                for(int i=0;i<collectSingle.length;i++){
                    System.out.print(collectSingle[i]+"colcol+++++++");
                    List<org.apache.http.NameValuePair> nvps2 = new ArrayList<NameValuePair>();
                    nvps2.add(new org.apache.http.message.BasicNameValuePair("clothId", collectSingle[i]));
                    String json = ClotherHttpUtil.getHttpPostResultForUrl(urlCloth, nvps2);
                    List<String> cloth=new ArrayList<String>();
                    cloth=ClotherHttpUtil.parseClothJson(json);
                    String clothUrl=cloth.get(0);
                    System.out.print(clothUrl+"                                ");
                    String clothDescribe=cloth.get(1);
                    clothDescribeList.add(clothDescribe);
                    clothUrl = ClotherHttpUtil.CLOTH_BASE_URL + clothUrl;
                    Bitmap bitmap = ClotherHttpUtil.getHttpPostResultForClothUrl(clothUrl);
                    bitmapList.add(bitmap);
                    System.out.println("**********");
                }
                ArrayList list = new ArrayList(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>

                list.add(bitmapList);
                list.add(clothDescribeList);

                bun.putParcelableArrayList("list",list);

                msg.what = 1;
                msg.setData(bun);
                handler.sendMessage(msg);

            }

        }.start();

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
        String clothId=collectSingle[position];
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
                ArrayList list = ble.getParcelableArrayList("list");
                List<Bitmap> bmp=(ArrayList)ble.get("list");
                bmp= (List<Bitmap>) list.get(0);
                List<String> describeList=new ArrayList<String>();
                describeList=(List<String>)list.get(1);
                initialList(bmp,describeList);

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
            System.out.println("OOOOOOOOOO");

            list.add(map);
        }
        return list;
    }
}
