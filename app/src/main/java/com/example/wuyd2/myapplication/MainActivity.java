package com.example.wuyd2.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private Button button;
private Button button2;
private Button button3;
private Button button4;
private Button button5;
private Button button6;
    private  File[] files;
    private List<String> list = new ArrayList<>();
    private String url;
    private DoughnutProgress doughnutProgress;
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                     doughnutProgress.setVisibility(View.VISIBLE);
                break;
                case 2:
                    doughnutProgress.setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);

        files=MyLog.getFils();
        doughnutProgress=findViewById(R.id.DoughnutProgress);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (doughnutProgress.getVisibility() ==View.VISIBLE){
            return;
        }
        if(doughnutProgress.getVisibility()==View.GONE){
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                for(int i=0;i<100000;i++){
            MyLog.d("1234567890123456");}

            break;
        case R.id.button2:
           String XXX=null;
           XXX.toString();
            break;
        case R.id.button3:
            startActivity(new Intent(MainActivity.this,Main3Activity.class));
            break;
        case R.id.button4:
            if(null!=files){
                url="http://202.99.114.136:10002/uploadfile";
                for (int i=0;i<files.length;i++){
                    list.add(files[i].getPath());
                }
                handler.sendEmptyMessage(1);
                upload (list);
            }
//            startActivity(new Intent(MainActivity.this,Main4Activity.class));
            break;
        case R.id.button5:
            startActivity(new Intent(MainActivity.this,Main5Activity.class));
            break;
        case R.id.button6:
            startActivity(new Intent(MainActivity.this,Main6Activity.class));
            break;
        }
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
                handler.removeMessages(1);
                handler.sendEmptyMessage(2);
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
}
