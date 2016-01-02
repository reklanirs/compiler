package com.example.administrator.ourclother.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;

import com.example.administrator.ourclother.R;


public class MyShowActivity extends ActionBarActivity {

    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_show);

        image=(ImageView)findViewById(R.id.my_show);
        image.setImageResource(R.drawable.myshow);
        SysApplication.getInstance().addActivity(this);
    }


  /*  @Override
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
    }
*/
}
