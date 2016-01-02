package com.example.administrator.ourclother.util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import  java.io.InputStream;
import org.apache.http.params.HttpConnectionParams;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2015/2/11.
 */
public class ClotherHttpUtil {
    /**
     * 请求对应的基础URL
     */
    public static final String BASE_URL = "http://223.3.73.40:8080/web-cloth/";
    public static final String CLOTH_BASE_URL="http://223.3.73.40/";
    //  public static final String BASE_URL = "http://192.168.42.1:8080/web-cloth/";
    //  public static final String CLOTH_BASE_URL="http://192.168.42.1/";

    public  static  InputStream getImageViewInputStream(String url0 ) throws IOException{


        InputStream inputStream=null;
        URL clothUrl=new URL(url0);

            HttpURLConnection httpURLConnection=(HttpURLConnection)clothUrl.openConnection();

            httpURLConnection.setConnectTimeout(1000000);

            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setDoInput(true);

            int response_code=httpURLConnection.getResponseCode();

            if(response_code==200){
                inputStream=httpURLConnection.getInputStream();
                System.out.print("获取图片链接成功");
            }else
            {
                System.out.println("get image: " + response_code  );
                System.out.print("获取图片链接失败");
            }


    return inputStream;

    }

    /**
     * 通过URL获取HttpGet请求
     * @param url
     * @return HttpGet
     */
    private static HttpGet getHttpGet(String url){

        HttpGet httpGet = new HttpGet(url);
        return httpGet;
    }

    /**
     * 通过URL获取HttpPost请求
     * @param url
     * @return HttpPost
     */
    private static HttpPost getHttpPost(String url){

        System.out.print("1111111111");
        HttpPost httpPost = new HttpPost(url);

        return httpPost;

    }

    /**
     * 通过 HttpGet请求获取HttpResponse对象
     * @param httpGet
     * @return HttpResponse
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static HttpResponse getHttpResponse(HttpGet httpGet) throws ClientProtocolException, IOException{
        HttpResponse response = new DefaultHttpClient().execute(httpGet);
        return response;
    }

    /**
     * 通过HttpPost获取HttpPonse对象
     //
     * @return httpPost
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static HttpResponse getHttpResponse(HttpPost httpPost) throws ClientProtocolException, IOException{




        HttpResponse response = new DefaultHttpClient().execute(httpPost);

        return response;
    }

    /**
     * 将URL打包成HttpPost请求，发送，得到查询结果 网络异常 返回 "exception"
     * @param url
     * @return resultString
     */
    public static String getHttpPostResultForUrl(String url,List nvps){

        System.out.println("url==="+url);
        HttpPost httpPost = new HttpPost(url);
        String resultString = null;

        try {//getbytes可能会不存在这种字符集，所以异常处理
            UrlEncodedFormEntity urlEntity =  new UrlEncodedFormEntity(nvps, "UTF-8");
            httpPost.setEntity(urlEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            //HttpResponse response = ClotherHttpUtil.getHttpResponse(httpPost);
            HttpResponse response = new DefaultHttpClient().execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200)
            {

                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
                resultString = java.net.URLDecoder.decode(resultString,"utf-8");
                //java.net.URLEncoder.encode(resultString);

            }
            else
            resultString= "-1";
            System.out.println("Login form get: " + response.getStatusLine()  );
        } catch (ClientProtocolException e) {
            resultString = "exception";
            e.printStackTrace();
        } catch (IOException e) {
            resultString = "exception";
            e.printStackTrace();
        }
       System.out.print(resultString);
        System.out.print("222222222");
        return resultString;
    }

    /**
     * 发送Post请求，得到查询结果 网络异常 返回 "exception"

     * @return resultString
     */
    public static String getHttpPostResultForRequest(HttpPost httpPost){
        String resultString = null;

        try {
            HttpResponse response = ClotherHttpUtil.getHttpResponse(httpPost);

            if(response.getStatusLine().getStatusCode() == 200)
                resultString = EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            resultString = "exception";
            e.printStackTrace();
        } catch (IOException e) {
            resultString = "exception";
            e.printStackTrace();
        }

        return resultString;
    }

    /**
     * 将URL打包成HttpGet请求，发送，得到查询结果 网络异常 返回 "exception"
     * @param url
     * @return resultString
     */
    public static String getHttpGetResultForUrl(String url){


        HttpGet httpGet = ClotherHttpUtil.getHttpGet(url);
        String resultString = null;

        try {
            HttpResponse response = ClotherHttpUtil.getHttpResponse(httpGet);
            if(response.getStatusLine().getStatusCode() == 200)
                resultString = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            resultString = "exception";
            e.printStackTrace();
        } catch (IOException e) {
            resultString = "exception";
            e.printStackTrace();
        }

        return resultString;
    }
    public static Bitmap getHttpPostResultForClothUrl(String clothUrl) {
        Bitmap bitmap=null;
        try {

            InputStream inputStream = ClotherHttpUtil.getImageViewInputStream(clothUrl);
            //readAsFile(inputStream, new File(Environment.getExternalStorageDirectory()+"/"+"test.png"));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            System.out.print("error");

        }catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }
    public static void readAsFile(InputStream inSream, File file) throws Exception{
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = -1;
        while( (len = inSream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inSream.close();
    }

    /**
     * 发送Get请求，得到查询结果 网络异常 返回 "exception"

     * @return resultString
     */
    public static String getHttpGetResultForRequest(HttpGet httpGet){
        String resultString = null;
        try {
            HttpResponse response = ClotherHttpUtil.getHttpResponse(httpGet);
            if(response.getStatusLine().getStatusCode() == 200)
                resultString = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            resultString = "exception";
            e.printStackTrace();
        } catch (IOException e) {
            resultString = "exception";
            e.printStackTrace();
        }

        return resultString;
    }
    /** 方法名：parseJson()
     *  功能：解析多个json
     *  参数：param-->jsonstr
     *  Atthor: timelessjava
     *  date: 2012-12-19
     * */
    public static List<String> parseClothJson(String jsonstr) {
        String clothId="2";
        String clothDESCRIB=null;
        String clothPICTURE=null;
        String clothMAINPIC=null;
        String str;
        List<String> cloth=new ArrayList<String>();

        jsonstr=removeBOM(jsonstr);
        try {

            org.json.JSONArray clotharray = new JSONObject(jsonstr).getJSONArray("cloths");
            for(int i=0; i<clotharray.length();i++) {
                JSONObject clothInfo = clotharray.getJSONObject(i);
                clothId = clothInfo.getString("clothID" );

                clothDESCRIB = java.net.URLDecoder.decode(clothInfo.getString( "clothDESCRIB"),"utf-8");
                clothPICTURE = java.net.URLDecoder.decode(clothInfo.getString("clothPICTURE") ,"utf-8");
                clothMAINPIC = java.net.URLDecoder.decode(clothInfo.getString("clothMAINPIC"),"utf-8");
                System.out.println(clothDESCRIB+"99999999999999999999999999                      ");
                str = "clothID:"+clothId+"clothDESCRIB:" +clothDESCRIB+"clothPICTURE:"+clothPICTURE+"clothMAINPIC"+clothMAINPIC;
                System. out.println(str);
                cloth.add(clothMAINPIC);
                cloth.add(clothDESCRIB);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }catch(java.io.UnsupportedEncodingException e)
        {

        }
        return  cloth;
    }
    public static final String removeBOM(String data) {
        if (android.text.TextUtils.isEmpty(data)) {
            return data;
        }

        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

}
