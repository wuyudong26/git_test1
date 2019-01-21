package com.example.wuyd2.myapplication;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yuyuhang on 15/12/7.
 */
public class App extends Application {
//    private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");

    public static Context mContext;
    private  File[] files;
    private List<String> list = new ArrayList<>();
    private String url;
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        AppCache.setContext(this);
        mContext = this;
//        files=MyLog.getFils();
//            程序崩溃后，再次第一次打开时将日志上传至服务器
//        if(null!=files){
//            url="http://202.99.114.136:10002/uploadfile";
//            for (int i=0;i<files.length;i++){
//                list.add(files[i].getPath());
//            }
//            upload (list);
//            upLoadFile(url, list, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                   Log.e("333","e: "+ e.getMessage());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String xx = response.body().string();
//                    Log.d("333",xx);
//                    //上传成功后，然后删除本地日志
////                    deleteFiles();
//                    //然后将崩溃日志标记位重置为“false”
//                    CrashPreferences.saveKeyCrash("false");
//                }
//            });

//        }
    }
    public void upload (final List<String> list){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
            try {
                doFilesPost(list);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            }
        }).start();
    }
    public void deleteFiles(){
        for(int i=0;i<files.length;i++){
            if (files[i].isFile()){
                File file = new File(files[i].getPath());
//                MyLog.d("files[i].getPath()==="+files[i].getPath());
                file.delete();
            }
        }
    }



    public String doFilesPost(List<String> fileNames) {
        // 超时设置
        //        BasicHttpParams httpParams = new BasicHttpParams();
        //        HttpConnectionParams.setConnectionTimeout(httpParams, HttpRequestTime.REQUEST_TIMEOUT);
        //        HttpConnectionParams.setSoTimeout(httpParams, HttpRequestTime.SO_TIMEOUT);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // 发起POST请求
            HttpPost httpPost = new HttpPost("http://202.99.114.136:10002/uploadfile");
            MultipartEntity reqEntity = new MultipartEntity();
            for (int i = 0; i < files.length; i++) {
                Log.d("333","fileNames.get(i)==="+fileNames.get(i));
                FileBody file = new FileBody(new File(fileNames.get(i)));
                reqEntity.addPart("logFile", file);
            }
//            FileBody file = new FileBody(new File(fileNames));
//            reqEntity.addPart("logFile", file);
            reqEntity.addPart("phonenumber", new StringBody("phonenumber"));
            reqEntity.addPart("uploadOperatingSystem", new StringBody("android"));
            reqEntity.addPart("uploadChannel", new StringBody("IM"));
            reqEntity.addPart("province", new StringBody("province"));

            httpPost.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(httpPost);
            // 获取响应码
            int responseCode = response.getStatusLine().getStatusCode();
            Log.d("333", "responseCode====" + responseCode);
            if (responseCode == 200) {
                HttpEntity resEntity = response.getEntity();
                if (null == resEntity) {
                    System.out.println("resEntity is null");
                    return null;
                }
                Log.d("333",EntityUtils.toString(resEntity, HTTP.UTF_8));
                return EntityUtils.toString(resEntity, HTTP.UTF_8);
            }
        } catch (Exception e) {
            System.out.println(" e.printStackTrace();" + e);

        } finally {
            // 关闭连接，释放资源
            httpClient.getConnectionManager().shutdown();
        }

        return null;
    }
//    /**
//     * 通过上传的文件的完整路径生成RequestBody
//     * @param fileNames 完整的文件路径
//     * @return
//     */
//    private static RequestBody getRequestBody(List<String> fileNames) {
//        //创建MultipartBody.Builder，用于添加请求的数据
//        MultipartBody.Builder builder = new MultipartBody.Builder();
//        for (int i = 0; i < fileNames.size(); i++) { //对文件进行遍历
//            File file = new File(fileNames.get(i)); //生成文件
//            builder.addFormDataPart( //给Builder添加上传的文件
//                    "fileName",  //请求的名字
//                    file.getName(), //文件的文字，服务器端用来解析的
//                    RequestBody.create(MediaType.parse("application/octet-stream"), file) //创建RequestBody，把上传的文件放入
//            );
//        }
//        builder.addFormDataPart("phonenumber","IMwuyudong")
//                .addFormDataPart("uploadOperatingSystem","android")
//                .addFormDataPart("uploadChannel","IM")
//                .addFormDataPart("province","206");
//        return builder.build(); //根据Builder创建请求
//    }
//
//    /**
//     * 获得Request实例
//     * @param url
//     * @param fileNames 完整的文件路径
//     * @return
//     */
//    private static Request getRequest(String url, List<String> fileNames) {
//        Request.Builder builder = new Request.Builder();
//        builder.url(url)
//                .post(getRequestBody(fileNames));
//        return builder.build();
//    }
//
//    /**
//     * 根据url，发送异步Post请求
//     * @param url 提交到服务器的地址
//     * @param fileNames 完整的上传的文件的路径名
//     * @param callback OkHttp的回调接口
//     */
//    public static void upLoadFile(String url, List<String> fileNames, Callback callback){
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Call call = okHttpClient.newCall(getRequest(url,fileNames)) ;
//        call.enqueue(callback);
//    }
//    public static  String sendFromDataPostRequest(String url, File file,String typeName)throws IOException {
//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
//        MultipartBody body = new MultipartBody.Builder()
//                .setType(FROM_DATA)
//                .addFormDataPart(typeName,"2.png",fileBody)
//                .build();
//        Request request = new Request.Builder()
//                .post(body)
//                .url(url)
//                .build();
//
//        return getClient().newCall(request).execute().body().string();}
//

//    public static String doFilesPost(String fileNames) throws Exception
//    {
//        // 超时设置
//        //        BasicHttpParams httpParams = new BasicHttpParams();
//        //        HttpConnectionParams.setConnectionTimeout(httpParams, HttpRequestTime.REQUEST_TIMEOUT);
//        //        HttpConnectionParams.setSoTimeout(httpParams, HttpRequestTime.SO_TIMEOUT);
//        HttpClient httpClient = new DefaultHttpClient();
//        try
//        {
//            // 发起POST请求
//            HttpPost httpPost = new HttpPost("http://202.99.114.136:10002/uploadfile");
//            String[] filenames = fileNames.split(";");
//            MultipartEntity reqEntity = new MultipartEntity();
//            /*for (int i = 0; i < filenames.length; i++)
//            {
//                String fileName = filenames[i];
//
//            }*/
//
//            FileBody file = new FileBody(new File(fileNames));
//            reqEntity.addPart("logFile", file);
//
//            reqEntity.addPart("phonenumber", new StringBody(DSMGlobal.USER_ID));
//            reqEntity.addPart("uploadOperatingSystem", new StringBody("android"));
//            reqEntity.addPart("uploadChannel", new StringBody("DSM"));
//            reqEntity.addPart("province", new StringBody(DSMGlobal.CARRIER_ID));
//
//            httpPost.setEntity(reqEntity);
//            HttpResponse response = httpClient.execute(httpPost);
//            // 获取响应码
//            int responseCode = response.getStatusLine().getStatusCode();
//            if (responseCode == 200)
//            {
//                HttpEntity resEntity = response.getEntity();
//                if (null == resEntity)
//                {
//                    System.out.println("resEntity is null");
//                    return null;
//                }
//                return EntityUtils.toString(resEntity, HTTP.UTF_8);
//            }
//        }
//        catch (Exception e)
//        {
//            System.out.println(" e.printStackTrace();"+ e);
//
//        }
//        finally
//        {
//            // 关闭连接，释放资源
//            httpClient.getConnectionManager().shutdown();
//        }
//
//        return null;
//    }
}
