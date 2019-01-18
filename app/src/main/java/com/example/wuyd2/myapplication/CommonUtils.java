/**
 *
 */
package com.example.wuyd2.myapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


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
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Administrator
 */
public class CommonUtils {
    /**
     * 日志标签
     */
    private final static String TAG = "CommonUtils";

    /**
     * 实例
     */
    public static CommonUtils instance;

    /**
     * 开始页
     */
    public static CommonUtils getInstance() {
        if (null == instance) {
            return new CommonUtils();
        } else {
            return instance;
        }
    }

    public static String doFilesPost(String fileNames) {
        // 超时设置
        //        BasicHttpParams httpParams = new BasicHttpParams();
        //        HttpConnectionParams.setConnectionTimeout(httpParams, HttpRequestTime.REQUEST_TIMEOUT);
        //        HttpConnectionParams.setSoTimeout(httpParams, HttpRequestTime.SO_TIMEOUT);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            // 发起POST请求
            HttpPost httpPost = new HttpPost("http://202.99.114.136:10002/uploadfile");
            MultipartEntity reqEntity = new MultipartEntity();
//            for (int i = 0; i < 5; i++) {
//                Log.d("333","fileNames.get(i)==="+fileNames.get(i));
//                FileBody file = new FileBody(new File(fileNames.get(i)));
//                reqEntity.addPart(file.getFilename(), file);
//            }
            FileBody file = new FileBody(new File(fileNames));
            reqEntity.addPart(file.getFilename(), file);
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

}
