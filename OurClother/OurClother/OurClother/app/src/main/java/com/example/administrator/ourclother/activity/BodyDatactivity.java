package com.example.administrator.ourclother.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.content.DialogInterface;
import android.app.AlertDialog;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.example.administrator.ourclother.util.ClotherHttpUtil;
import com.example.administrator.ourclother.util.ClotherUrlUtil;
import android.os.Handler;
import android.os.Message;
import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;
import android.content.Intent;


import com.example.administrator.ourclother.R;


public class BodyDatactivity extends ActionBarActivity {

    private String res;
    private String uname;
    private EditText height;
    private EditText weight;
    private EditText chest;
    private EditText waist;
    private EditText shoulder;
    private EditText hip;
    private EditText trouser;
    private Button save;
    private Button match;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodydatactivity);
        findAllViewById();
        save.setOnClickListener(new BodyDataListener());

        match.setOnClickListener(new BodyMatchListener());
        Intent intent=new android.content.Intent();
        uname=this.getIntent().getStringExtra("loginId");

    }

    class BodyMatchListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("loginId", uname);
            intent.putExtras( bundle);
            intent.setClass(BodyDatactivity.this, MyBodyShowActivity.class);
            BodyDatactivity.this.startActivity(intent);
        }
    }


     class BodyDataListener implements android.view.View.OnClickListener {
         @Override
         public void onClick(View v) {
             final String uheight = height.getText().toString();
             final String uweight = weight.getText().toString();
             final String ushoulder = shoulder.getText().toString();
             final String uchest = chest.getText().toString();
             final String uwaist = waist.getText().toString();
             final String uhip = hip.getText().toString();
             final String utrouser = trouser.getText().toString();
             if("".equals(uheight.trim())){ // 身高为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_height_null)
                         .setMessage(R.string.bodyData_height_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(uweight.trim())){ // 体重为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_weight_null)
                         .setMessage(R.string.bodyData_weight_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(ushoulder.trim())){ // 肩宽为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_shoulder_null)
                         .setMessage(R.string.bodyData_shoulder_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(uchest.trim())){ // 胸围为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_chest_null)
                         .setMessage(R.string.bodyData_chest_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(uheight.trim())){ // 腰围为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_waist_null)
                         .setMessage(R.string.bodyData_waist_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(uheight.trim())){ // 臀围为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_hip_null)
                         .setMessage(R.string.bodyData_hip_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             if("".equals(uheight.trim())){ // 裤长为空！

                 android.app.AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
                 builder.setIcon(R.drawable.alert_wanring)
                         .setTitle(R.string.bodyData_trouser_null)
                         .setMessage(R.string.bodyData_trouser_null)
                         .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                             // 点击确定按钮
                             public void onClick(DialogInterface dialog, int which) {
                                 height.setText("");
                                 weight.setText("");
                                 shoulder.setText("");
                                 chest.setText("");
                                 waist.setText("");
                                 hip.setText("");
                                 trouser.setText("");
                             }
                         }).show();
                 return ;

             }
             new Thread(){
                @Override
                public void run() {
                    /**
                     *添加信息
                     */

                   // String registerString = "loginId=" + uname + "&password=" + upwd + "&email=" + umail ;
                   //String url = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.REGISTER_URL + registerString;
                    //url=url+"&gender=" + "boy";

                    List<org.apache.http.NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("loginId", uname));
                    nvps.add(new BasicNameValuePair("height", uheight));
                    nvps.add(new BasicNameValuePair("weight", uweight));
                    nvps.add(new BasicNameValuePair("shoulder", ushoulder));
                    nvps.add(new BasicNameValuePair("chest", uchest));
                    nvps.add(new BasicNameValuePair("waist", uwaist));
                    nvps.add(new BasicNameValuePair("hip", uhip));
                    nvps.add(new BasicNameValuePair("trouser", utrouser));
                    String url = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.ADD_BODYDATA;

                    res = ClotherHttpUtil.getHttpPostResultForUrl(url,nvps);
                    System.out.println(res);
                    handler.sendEmptyMessage(1);


                }
            }.start();

         }
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bodyctivity, menu);
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
    private void findAllViewById() {

        height= (EditText)findViewById(R.id.text_height);

        weight = (EditText)findViewById(R.id.text_weight);
        chest = (EditText)findViewById(R.id.text_chest);
        shoulder = (EditText)findViewById(R.id.text_shoulder);
        waist = (EditText)findViewById(R.id.text_waist);
        hip = (EditText)findViewById(R.id.text_hip);
        trouser = (EditText)findViewById(R.id.text_trouser);
        save=(android.widget.Button)findViewById(R.id.saveBtn);
        match=(Button)findViewById(R.id.matchBtn);

    }
    protected void showRegisterMesg(String res) {
        if ("-1".equals(res)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
            builder.setIcon(R.drawable.alert_error)
                    .setTitle("网络错误")
                    .setMessage("网络错误，添加信息失败，请稍后再试！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        // 点击确定按钮
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return;
        }
        if ("0".equals(res)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BodyDatactivity.this);
            builder.setIcon(R.drawable.alert_add)
                    .setTitle("信息添加成功")
                    .setMessage("恭喜您，信息添加成功！")
                    .show();
            return;
        }

    }
     private Handler handler;

    {
        handler = new Handler() {
            public void dispatchMessage(android.os.Message msg) {
               // proDlg.dismiss();

                System.out.print("handle");
                showRegisterMesg(res);
            }

            ;
        };
    }
}
