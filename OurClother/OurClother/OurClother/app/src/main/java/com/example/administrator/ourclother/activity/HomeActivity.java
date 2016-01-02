package com.example.administrator.ourclother.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

import com.example.administrator.ourclother.R;


public class HomeActivity extends Activity implements View.OnClickListener {
    private Button shop;
    private Button collect;
    private Button center;
    private ViewPager viewPager;
    private String uname;
    private int[] images;//图片ID数组
    private int currentPage = 0;//当前展示的页码
    private ScheduledExecutorService scheduledExecutorService;
    //当创建一个新的Handler实例时它会绑定到当前线程和消息的队列中,开始分发数据。
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {//方法重写，用于处理接收后的数据
            viewPager.setCurrentItem(currentPage);// 切换当前显示的图片
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = (ViewPager) findViewById(R.id.viewpager2);
        viewPager.setOnClickListener(this);
        //初始化图片资源
        images = new int[]{R.drawable.one, R.drawable.two, R.drawable.home, R.drawable.three, R.drawable.four};

        //-----初始化PagerAdapter------
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return images.length;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object o) {
                //container.removeViewAt(position);
            }

            //用来初始化ViewPager
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView im = new ImageView(HomeActivity.this);
                im.setImageResource(images[position]);
                container.addView(im);
                return im;
            }
        };
        viewPager.setAdapter(adapter);
        findAllViewById();
        shop.setOnClickListener(this);
        collect.setOnClickListener(this);
        center.setOnClickListener(this);
        Intent intent=new Intent();
        uname=this.getIntent().getStringExtra("loginId");
    }


    @Override
    public void onClick(View v) {
        //页面图片被点击
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
         bundle.putString("loginId", uname);

         intent.putExtras( bundle);
        intent.setClass(HomeActivity.this, ShopMap.class);
        switch (v.getId()) {
            case R.id.shopBtn:
                intent.setClass(HomeActivity.this, ShopMap.class);
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.collectBtn:
                intent.setClass(HomeActivity.this, MyCollectList.class);
                HomeActivity.this.startActivity(intent);
                break;
            case R.id.centerBtn:
                intent.setClass(HomeActivity.this, BodyDatactivity.class);
                HomeActivity.this.startActivity(intent);
                break;
        }
    }
        //用于处理自动切换
        @Override
        protected void onStart () {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();//初始化一个对象，为单线程池
            // 当Activity显示出来后，每两秒钟切换一次图片显示
            scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
            super.onStart();
        }

        @Override
        protected void onStop () {
            // 当Activity不可见的时候停止切换
            scheduledExecutorService.shutdown();
            super.onStop();
        }

        private class ScrollTask implements Runnable {

            public void run() {
                synchronized (viewPager) {
                    currentPage = (currentPage + 1) % images.length;
                    handler.obtainMessage().sendToTarget(); // 发送消息，通过Handler切换图片
                }
            }
        }
        /**
         * 实例化组件
         */

    private void findAllViewById() {

        shop = (Button) findViewById(R.id.shopBtn);
        collect = (Button) findViewById(R.id.collectBtn);
        center = (Button) findViewById(R.id.centerBtn);


    }

}

