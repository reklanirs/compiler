package com.example.administrator.ourclother.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;
import com.example.administrator.ourclother.util.ClotherHttpUtil;
import com.example.administrator.ourclother.util.ClotherStringUtil;
import com.example.administrator.ourclother.util.ClotherUrlUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import com.example.administrator.ourclother.R;

public class LogActivity extends ActionBarActivity {
    private Button m_login;
    private Button m_register;

    private EditText m_username;
    private EditText m_password;

    private String uname;
    private String pwd;
    private ProgressDialog prgDialog;
    private String res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);//去除标题栏
        setContentView(R.layout.activity_log);
        System.out.println("log出现！！！！！！！！！！！！");
        SysApplication.getInstance().addActivity(this);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelayout);
        m_login = (Button)findViewById(R.id.okButton);
        m_register = (Button)findViewById(R.id.regButton);

        m_username = (EditText)findViewById(R.id.userNameText);
        m_password = (EditText)findViewById(R.id.passWordText);

       // m_login.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/a.ttf"));字体设置不成功
       // m_register.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/a.ttf"));
        setImageButtonListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences share = getSharedPreferences(ClotherStringUtil.USER_DATA_PROVIDE, PreferenceActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();

        boolean name = share.getBoolean(ClotherStringUtil.IS_USER_NAME, false);
        boolean pwd = share.getBoolean(ClotherStringUtil.IS_PASSWORD, false);
        if(name)
            editor.putString(ClotherStringUtil.USERNAME, m_username.getText().toString());
        else
            editor.remove(ClotherStringUtil.USERNAME);
        if(pwd)
            editor.putString(ClotherStringUtil.PASSWORD, m_password.getText().toString());
        else
            editor.remove(ClotherStringUtil.PASSWORD);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }
    /**
     * 添加监听器
     */
    private void setImageButtonListener() {

        // 登陆
        m_login.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                uname = m_username.getText().toString().trim();
                pwd = m_password.getText().toString().trim();
                System.out.println(uname+"   ");
                System.out.println(pwd+"   ");
                if("".equals(uname)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
                    builder.setIcon(R.drawable.alert_wanring)
                            .setTitle(R.string.login_account_null)
                            .setMessage(R.string.login_account_null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                // 点击确定按钮
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                    return ;
                }
                if("".equals(pwd)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);
                    builder.setIcon(R.drawable.alert_wanring)
                            .setTitle(R.string.login_password_null)
                            .setMessage(R.string.login_password_null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                // 点击确定按钮
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return ;
                }

                // 显示登陆对话框
                prgDialog = new ProgressDialog(LogActivity.this);
               // prgDialog.setIcon(R.drawable.progress);
                prgDialog.setTitle("请稍等");
                prgDialog.setMessage("正在登陆，请稍等...");
                prgDialog.setCancelable(false);
                prgDialog.setIndeterminate(true);
                prgDialog.show();

                login();

            }

        });

        // 注册
        m_register.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LogActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void login() {
        new Thread(){
            @Override
            public void run() {


                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("loginId", uname));
                nvps.add(new BasicNameValuePair("password", pwd));
                System.out.println(nvps);
                String url = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.LOGIN_URL;

                res = ClotherHttpUtil.getHttpPostResultForUrl(url,nvps);

                Message m = new Message();
                System.out.println("+++++++++++++++");
                System.out.println(res);
                System.out.println("---------------");
                if("-1".equals(res))
                    m.what = ClotherStringUtil.LOGIN_ERROR;//2
                else if("-2".equals(res))
                m.what = ClotherStringUtil.SERVER_ERROR;//0
                else if("exception".equals(res))
                    m.what = ClotherStringUtil.INTNERT_ERROR;//0
                else
                    m.what = ClotherStringUtil.LOGIN_SUCCESS;//1


                proHandle.sendMessage(m);

            }
        }.start();
    }

    private Handler proHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(LogActivity.this);

            prgDialog.dismiss();
            switch(msg.what){
                case ClotherStringUtil.LOGIN_ERROR:
                    builder.setIcon(R.drawable.alert_error)
                            .setTitle("错误")
                            .setMessage("用户名或密码错误，请确认")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                // 点击确定按钮
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    break;
                case ClotherStringUtil.SERVER_ERROR:
                builder.setIcon(R.drawable.alert_error)
                        .setTitle("错误")
                        .setMessage("未连接到网络或服务器已关闭，请确认")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            // 点击确定按钮
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
                case ClotherStringUtil.INTNERT_ERROR:
                    builder.setIcon(R.drawable.alert_error)
                            .setTitle("错误")
                            .setMessage("未连接到网络，请确认")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                // 点击确定按钮
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    break;
                case ClotherStringUtil.LOGIN_SUCCESS:
                    builder.setIcon(R.drawable.alert_ok)
                            .setTitle("登陆成功")
                            .setMessage("恭喜您，登陆成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                // 点击确定按钮
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(LogActivity.this, CenterActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("loginId", uname);

                                    intent.putExtras( bundle);

                                    startActivity(intent);
                                }
                            }).show();
                    break;
            }
        }
    };
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

}
