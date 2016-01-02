package com.example.administrator.ourclother.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.view.Menu;
import android.view.MenuItem;
import com.example.administrator.ourclother.R;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;
/**
 * Created by Administrator on 2015/4/2.
 */
public class CenterActivity extends TabActivity  implements CompoundButton.OnCheckedChangeListener{
    private TabHost mTabHost;
    private Intent mAIntent;
    private Intent mBIntent;
    private Intent mCIntent;
    private Intent mDIntent;
    private String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        System.out.println("center出现！！！！！！！！！！！！");
        SysApplication.getInstance().addActivity(this);
        uname=this.getIntent().getStringExtra("loginId");
        Bundle bundle = new Bundle();
        bundle.putString("loginId", uname);

        this.mAIntent = new Intent(this, HomeActivity.class);
        mAIntent.putExtras( bundle);
        this.mCIntent = new Intent(this, MyCollectList.class);
        mCIntent.putExtras( bundle);
        this.mDIntent = new Intent(this, BodyDatactivity.class);
        mDIntent.putExtras( bundle);

        ((RadioButton) findViewById(R.id.homeBtn))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.collectBtn))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.personalBtn))
                .setOnCheckedChangeListener(this);
        mTabHost = getTabHost();
        mTabHost.addTab(buildTabSpec("A_TAB",R.string.home_1,
                R.drawable.center2, this.mAIntent));
        mTabHost.addTab(buildTabSpec("C_TAB",R.string.home_3,
                R.drawable.collected2,this.mCIntent));
        mTabHost.addTab(buildTabSpec("D_TAB",R.string.home_4,
                R.drawable.me2,this.mDIntent));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.homeBtn:
                    mTabHost.setCurrentTabByTag("A_TAB");
                    break;
                case R.id.collectBtn:
                    mTabHost.setCurrentTabByTag("C_TAB");
                    break;
                case R.id.personalBtn:
                    mTabHost.setCurrentTabByTag("D_TAB");
                    break;
            }
        }
    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
                                         final Intent content) {
        return mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
                getResources().getDrawable(resIcon)).setContent(content);
    }
     @Override
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
        }
}
