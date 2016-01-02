package com.example.administrator.ourclother.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import com.example.administrator.ourclother.R;
import com.example.administrator.ourclother.util.ClotherHttpUtil;
import com.example.administrator.ourclother.util.ClotherUrlUtil;

import android.os.Message;
import android.app.ProgressDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private EditText email;
    private EditText gender;
    private Button register;



    private String res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /**
         * 实例化组件
         */
        findAllViewById();

        /**
         * 注册事件
         */

        register.setOnClickListener(new RegisterViewListener());
    }
    class RegisterViewListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final String uname = username.getText().toString();
            final String upwd = password.getText().toString();
            final String umail = email.getText().toString();
            final String ugender = gender.getText().toString();

            if("".equals(uname.trim())){ // 用户名为空！

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setIcon(R.drawable.alert_wanring)
                        .setTitle(R.string.login_account_null)
                        .setMessage(R.string.login_account_null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            // 点击确定按钮
                            public void onClick(DialogInterface dialog, int which) {
                                username.setText("");
                                password.setText("");
                                email.setText("");
                            }
                        }).show();
                return ;

            }
            if("".equals(upwd)){
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setIcon(R.drawable.alert_wanring)
                        .setTitle(R.string.login_password_null)
                        .setMessage(R.string.login_password_null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            // 点击确定按钮
                            public void onClick(DialogInterface dialog, int which) {
                                password.setText("");
                                email.setText("");
                            }
                        }).show();
                return ;
            }
            if("".equals(umail.trim())){
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setIcon(R.drawable.alert_wanring)
                        .setTitle(R.string.login_email_null)
                        .setMessage(R.string.login_email_null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            // 点击确定按钮
                            public void onClick(DialogInterface dialog, int which) {
                                email.setText("");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    // 点击取消按钮
                    public void onClick(DialogInterface dialog, int which) {
                        email.setText("");
                    }
                }).show();
                return ;
            }




            new Thread(){
                @Override
                public void run() {
                    /**
                     * 1  验证用户是否存在，不存在，注册
                     * 2  注册成功，返回账号和密码显示
                     * 3  登录
                     */

                    String registerString = "loginId=" + uname + "&password=" + upwd + "&email=" + umail ;
                   //String url = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.REGISTER_URL + registerString;
                    //url=url+"&gender=" + "boy";

                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("loginId", uname));
                    nvps.add(new BasicNameValuePair("password", upwd));
                    nvps.add(new BasicNameValuePair("email", umail));
                    nvps.add(new BasicNameValuePair("gender", ugender));
                    String url = ClotherHttpUtil.BASE_URL + ClotherUrlUtil.REGISTER_URL;

                    res = ClotherHttpUtil.getHttpPostResultForUrl(url,nvps);
                    System.out.println(res);
                    handler.sendEmptyMessage(1);


                }
            }.start();


        }

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
    /**
     * 实例化组件
     */
    private void findAllViewById() {

        register= (Button)findViewById(R.id.btn_reg);

        username = (EditText)findViewById(R.id.text_username);
        password = (EditText)findViewById(R.id.text_password);
        email = (EditText)findViewById(R.id.text_email);
        gender = (EditText)findViewById(R.id.text_gender);


    }
    protected void showRegisterMesg(String res) {
        if("0".equals(res)){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setIcon(R.drawable.alert_error)
                    .setTitle("注册失败")
                    .setMessage("注册失败，请稍后再试！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        // 点击确定按钮
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return ;
        }
        if("1".equals(res)){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setIcon(R.drawable.alert_add)
                    .setTitle("注册成功")
                    .setMessage("恭喜您，注册成功，请登陆！")
                    .show();
            return ;
        }
        if("2".equals(res)){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setIcon(R.drawable.alert_error)
                    .setTitle("邮箱已存在")
                    .setMessage("邮箱已存在，请使用其它邮箱！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        // 点击确定按钮
                        public void onClick(DialogInterface dialog, int which) {
                            email.setText("");
                        }
                    }).show();
            return ;
        }
        if("3".equals(res)){
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setIcon(R.drawable.alert_error)
                    .setTitle("登陆账号已存在")
                    .setMessage("登陆账号已存在，请使用其它账号！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        // 点击确定按钮
                        public void onClick(DialogInterface dialog, int which) {
                            username.setText("");
                        }
                    }).show();
            return ;
        }
    }

    private Handler handler;

    {
        handler = new Handler() {
            public void dispatchMessage(Message msg) {
               // proDlg.dismiss();

                System.out.print("handle");
                showRegisterMesg(res);
            }

            ;
        };
    }


}
