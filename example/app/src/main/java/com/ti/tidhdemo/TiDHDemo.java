package com.ti.tidhdemo;

import android.util.Log;

import com.ti.tidh.TiDHClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TiDHDemo {
    private static final String TAG = "DHTest";
    private final OkHttpClient mHttpClient;
    private String mClientKey = "";
    private String mServerKey = "";

    TiDHDemo() {
        mHttpClient = new OkHttpClient.Builder().build();
    }

    String getClientKey() {
        return mClientKey;
    }

    String getServerKey() {
        return mServerKey;
    }

    boolean isKeyMath() {
        return mClientKey.equals(mServerKey);
    }

    /**
     * 演示使用 TiDH 进行计算获取公钥。
     * 演示使用 https://t.ti-node.com 作为服务器,可根据需要搭建自己的服务
     * 参考：https://github.com/ti-dh/ti-dh-php
     * @throws IOException
     * @throws JSONException
     */
     void processPublicKey() throws IOException, JSONException {
        // 获取服务器的p、g和server_number
        Request request = new Request.Builder()
                .get()
                .url("https://t.ti-node.com/dh/getdhbasedata")
                .build();
        Call call = mHttpClient.newCall(request);
        Response res = call.execute();

        JSONObject data = new JSONObject(res.body().string());
        String p = data.getString("p");
        String g = data.getString("g");
        String serverNumber = data.getString("server_number");

        // 根据server-number，client-number和p 计算出公共密钥K
        TiDHClient dhClient = new TiDHClient();
        mClientKey = dhClient.processKey(p, g, serverNumber);

        // 将计算过后的client-number发送给服务器
        FormBody formBody = new FormBody
                .Builder()
                .add("client_number",dhClient.getClientNumber())
                .build();
        request = new Request.Builder()
                .post(formBody)
                .url("https://t.ti-node.com/dh/postdhclientdata")
                .build();
        call = mHttpClient.newCall(request);
        res = call.execute();
        data = new JSONObject(res.body().string());
        mServerKey = data.getString("key");

        Log.d(TAG, "is key match: " + mServerKey.equals(mClientKey));
    }
}
