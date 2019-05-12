package com.server.dh.api;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class DHController {

    /**
     * getdhbasedata 接口
     */
    public Object getBaseData() {

        DH dh= new DH();
        Map<String, String> baseData = dh.init();

        // 意此处，你的业务系统里需要将p、g、server_number保存起来，而且每个
        // 不同的客户端和你业务系统协商出来的g和server_number都是不相同的，应该
        // 使用客户端的id或者token之类作为前缀保存
        // 此处demo里，我们使用redis存储第一步协商的临时数据，用于第二步计算使用
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //jedis.auth("xxxx"); 如果Redis服务连接需要密码
        jedis.hmset("test:pgs", baseData);
        jedis.close(); //使用完关闭连接

        //服务端的server_number是不能直接暴露给客户端的 我们给客户端的应该是processed_server_number
        String processedServerNum = baseData.get("processed_server_number");
        baseData.remove("processed_server_number");
        baseData.put("server_number", processedServerNum);
        return baseData;
    }

    /**
     * postdhclientdata 接口
     * 在实际生产环境中不能返回的要保密的 本例子为了方便测试验证 所以返回
     */
    public Object postClientData(Map<String, String> postData) {
        String clientNum = postData.get("client_number");
        if (clientNum == null) {
            return "client_number required";
        }

        // 需要根据客户端传来的id或者token取出上一个接口中协商好的server_number和p
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //jedis.auth("xxxx"); 如果Redis服务连接需要密码
        Map ret = jedis.hgetAll("test:pgs");
        String serverNum = ret.get("server_number").toString();
        String p = ret.get("p").toString();
        jedis.close();

        DH dh = new DH();
        String key = dh.computeShareKey(clientNum, serverNum, p);
        Map<String, String> retData = new HashMap<>();
        retData.put("key", key);
        return retData;
    }
}
