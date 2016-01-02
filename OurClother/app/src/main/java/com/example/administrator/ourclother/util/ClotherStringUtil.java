package com.example.administrator.ourclother.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.administrator.ourclother.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/2/25.
 */
public class ClotherStringUtil {

    /**
     * 内容提供者列名
     */
    public static final String USER_DATA_PROVIDE = "com.lixa.provide.USERS";

    /**
     * 列名约束 constraint
     */
    public static final String TEXT_NOT_NULL = "text not null";
    public static final String TEXT_DEFAULT_NULL = "text default null";
    public static final String INTEGER_NOT_NULL = "integer not null";
    public static final String INTEGER_DEFAULT_NULL = "integer default null";

    /**
     * 菜单类型
     */
    public static final String HOT_ORDER_MENU = "recai";
    public static final String COOL_ORDER_MENU = "liangcai";
    public static final String NOOLDE_ORDER_MENU = "mianshi";

    /**
     * Handler 消息内容列表
     */
    public static final int LOGIN_ERROR = 0; // 登陆失败

    public static final int INTNERT_ERROR = 20; // 网络异常
    public static final int LOGIN_SUCCESS = 1; // 登陆成功
    public static final int ADDCLOLECT_ERROR = -1; //收藏失败，服务器错误
    public static final int ADDCOLLECT_SUCCESS =1; // 登陆成功
    public static final int ADDCOLLECT_ALREADY = -2; // 已收藏
    public static final int DELETECOLLECT_ERROR =0; // 取消收藏失败
    public static final int DELETECOLLECT_SUCCESS = 1; // 取消收藏成功
    public static final int SERVER_ERROR = 2; // 服务器异常
    public static final int SERVER_NO_DATA = 3; // 服务器无数据
    public static final int DATA_DETAIL = 4; // 有数据，提示详细信息

    public static final int ERROR = 6; // 错误操作
    public static final int OK = 7; // 操作正常
    public static final int NEW_ORDER_FINASH = 8; // 新增更新完成
    public static final int UPDATE_ORDER_FINASH = 9; // 修改更新完成
    public static final int DELETE_ORDER_FINASH = 10; // 删除更新完成
    public static final int ERROR_ORDER_FINASH = 11; // 错误更新完成

    public static final int BASE_MODIFY_OK = 14; // 信息修改成功
    public static final int PASSWORD_MODIFY_OK = 15; // 信息修改成功
    public static final int EMAIL_EXISTS = 16; // 邮箱已经存在
    public static final int BASE_ERROR = 17; // 基本信息修改错误
    public static final int PASSWORD_ERROR = 18; // 密码修改错误
    public static final int PASSWORD_OLD_REEOR = 19; // 原密码错误

    /**
     * 类型名称
     */
    public static final String[] orderTypeName = {"热菜", "凉菜", "面食"};

    /**
     * 类型值
     */
    public static final String[] orderTypeValue = {"recai", "liangcai", "mianshi"};

    /**
     * 是否保持用户名 key
     */
    public static final String IS_USER_NAME = "is_save_uname";

    /**
     * 是否保持密码 key
     */
    public static final String IS_PASSWORD = "is_save_pwd";

    /**
     * 用户名  key
     */
    public static final String USERNAME = "username";

    /**
     * 密码 key
     */
    public static final String PASSWORD = "password";


    /**
     * 验证邮箱是否合法
     * @param umail
     * @return
     */
    public static boolean emailRule(String umail) {
        boolean result = false;
        String reg = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
        if(umail.matches(reg)){
            result = true;
        }else{
            result = false;
        }

        return result;
    }

    /**
     * 返回 format 格式的时间字符串
     * 时间格式为 yyyy-MM-dd HH:mm:ss
     * yyyy 返回4位年份
     * MM 返回2位月份
     * dd 返回2位日
     * 时间类同
     * @return 相应日期类型的字符串
     */
    public static String getCurrentDate(String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date()).toString();
    }

    /**
     * 通过URL返回网络的BitMap图片
     * @param url
     * @return Bitmap
     */
    public static Bitmap getBitMapForStringURL(String urlString) {

        URL url = null;
        Bitmap bitmap = null;

        try {
            url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 进度对话框
     * @param context
     * @param title
     * @param message
     * @param canCelable
     * @param indeterminate
     */
    public static ProgressDialog createProgressDialog(Context context, String title,
                                                      String message, boolean canCelable, boolean indeterminate) {
        ProgressDialog p = new ProgressDialog(context);
        p.setIcon(R.drawable.progress);
        p.setTitle(title);
        p.setMessage(message);
        p.setCancelable(canCelable);
        p.setIndeterminate(indeterminate);
        return p;
    }

    /**
     * 传递登陆数据，添加数据到Intent中 Bundle->Intent 数据key都为data
     * 结合 getDataFromIntent(Intent) 使用
     * @param intent
     * @param str id,loginid,password,nikename,phone,email,gender,create_at
     */
    public static void putDataIntoIntent(Intent intent, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        intent.putExtra("data", bundle);
    }

    /**
     * 传递登陆数据，从Intent中获取Bundle数据
     * 结合 putDataIntoIntent(Intent, String) 使用
     * @param intent
     * @return String id,loginid,password,nikename,phone,email,gender,create_at
     */
    public static String getDataFromIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra("data");
        String res = bundle.getString("data");;
        return res;
    }

    /**
     * 判断手机号码是否合法
     * @param trim
     * @return
     */
    public static boolean phoneNumberRule(String phone) {
        boolean result = false;
        long min = 13000000000L;
        long max = 18999999999L;
        long data = 0;
        try {
            data = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            result = false;
            e.printStackTrace();
        }
        if(phone.length()!=11)
            result = false;
        else if(data < min || data > max)
            result = false;
        else
            result = true;
        return result;
    }
}
