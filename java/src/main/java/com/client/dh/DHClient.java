package com.client.dh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class DHClient {

    private static int mClientNum = (new Random()).nextInt(89999) + 10000;

    public static void main(String[] args) throws Exception {
        processPublicKey();
    }

    private static void processPublicKey() throws IOException, JSONException {
        OkHttpClient myHttpClient = new OkHttpClient.Builder().build();
        String mClientKey = "";
        String mServerKey = "";
        Request request = new Request.Builder()
                .get()
                .url("http://127.0.0.1:8877/dh/getdhbasedata")
                .build();
        Call call = myHttpClient.newCall(request);
        Response res = call.execute();
        JSONObject data = JSON.parseObject(res.body().string());

        // 1、第一步，获取服务器的p、g和server_number
        String p = data.getString("p");

        String g = data.getString("g");

        String serverNumber = data.getString("server_number");

        // 2、第二步，根据服务器获取到的数据计算出client-number
        String processClientNumber = processNum(g, mClientNum + "", p);

        FormBody formBody = new FormBody
                .Builder()
                .add("client_number", processClientNumber + "")
                .build();
        request = new Request.Builder()
                .post(formBody)
                .url("http://127.0.0.1:8877/dh/postdhclientdata")
                .build();
        // 3、第三步，将计算过后的client-number发送给服务器
        call = myHttpClient.newCall(request);
        res = call.execute();

        data = JSON.parseObject(res.body().string());

        // 4、第四步，根据server-number，client-number和p 计算出公共密钥K
        mServerKey = data.getString("key");

        //请注意：👆返回的key便是用于参与对称加解密的密钥，正式环境中使用无论如何都是不能在公网上来回传递的，这里之所以显示出来就是为了演示服务端和客户端计算出来的对称密钥是一样的！
        //正式环境里，客户端给服务端返回client_number就可以了 服务端跟客户端各自可以算出来的密钥进行加密了
        System.out.println("mServerKey: " + mServerKey);
        mClientKey = processNum(serverNumber, mClientNum + "", p);
        System.out.println("mClientKey: " + mClientKey);
        System.out.println("Test is key match: " + mServerKey.equals(mClientKey));
    }

    private static String processNum(String g, String e, String p) {
        BigInteger mP = new BigInteger(p);
        BigInteger mG = new BigInteger(g);
        return mG.modPow(new BigInteger(e), mP).toString();
    }
}
