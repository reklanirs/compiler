
package com.example.administrator.ourclother.activity;

        import android.content.Intent;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.ImageView;
        import android.os.Handler;
        import android.os.Message;
        import android.graphics.Bitmap;
        import android.widget.Button;
        import android.view.View;
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

public class ClotherShowActivity extends ActionBarActivity {
    private Handler mHandler = new Handler();
    String clothUrl=null;
    String clothId=null;
    String uname=null;
    ImageView image;
    private Button tryCloth;
    private AsyncTaskUtil mDownloadAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clother_show);
        uname=this.getIntent().getStringExtra("loginId");
        clothId=this.getIntent().getStringExtra("clothId");
        tryCloth=(Button)findViewById(R.id.tryBtn);
        image=(ImageView)findViewById(R.id.clother);
        image.setImageResource(R.drawable.myshow);
        mDownloadAsyncTask = new AsyncTaskUtil(ClotherShowActivity.this, mHandler);
        mDownloadAsyncTask.execute("http://d3.freep.cn/3tb_150405023837ylfg547926.png","dst.png");//必须传入两个参数——参数1：url；参数2：文件名（可以为null）
        System.out.println("download complete++++++++++++++");
        tryCloth.setOnClickListener(new android.view.View.OnClickListener(){

            @Override
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setClass(ClotherShowActivity.this,MainActivity.class);
                startActivity(intent);
                image.setImageBitmap(null);


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clother_show, menu);
        return true;
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
}
