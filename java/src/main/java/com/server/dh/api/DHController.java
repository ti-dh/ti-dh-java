package com.server.dh.api;

import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DHController {

    private static final String pSource = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";

    private BigInteger p;

    private BigInteger g;

    private int serverNum;

    public DHController() {
        generateBase();
    }

    public Object getBaseData() {
        String processedServerNum = g.modPow((new BigInteger(this.serverNum + "")), p).toString();
        Map<String, String> baseData = new HashMap<>();
        baseData.put("p", p.toString());
        baseData.put("g", g.toString());
        baseData.put("processed_server_number", processedServerNum);
        baseData.put("server_number", this.serverNum + "");

        // 意此处，你的业务系统里需要将p、g、server_number保存起来，而且每个
        // 不同的客户端和你业务系统协商出来的g和server_number都是不相同的，应该
        // 使用客户端的id或者token之类作为前缀保存
        // 此处demo里，我们使用redis存储第一步协商的临时数据，用于第二步计算使用
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //jedis.auth("xxxx"); 如果Redis服务连接需要密码
        jedis.hmset("test:pgs", baseData);
        jedis.close(); //使用完关闭连接

        //服务端的server_number是不能直接暴露给客户端的 我们给客户端的应该是processed_server_number
        baseData.remove("processed_server_number");
        baseData.put("server_number", processedServerNum);
        return baseData;
    }

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

        String key = (new BigInteger(clientNum)).modPow(new BigInteger(serverNum + ""), new BigInteger(p)).toString();
        HashMap<String, String> baseData = new HashMap<>();
        baseData.put("key", key);
        return baseData;
    }

    private void generateBase() {
        //第一步：根据pSource生成服务器当前固定的p
        BigInteger p = new BigInteger(pSource, 16);

        BigInteger tempP;
        BigInteger g;
        BigInteger gFlag;
        while (true) {
            tempP = p.subtract(new BigInteger("1"));
            //取一个2-p中间的随机数
            g = getBigIntegerRandomRange(new BigInteger("2"), tempP);
            gFlag = g.modPow(tempP, p);
            if (gFlag.toString().equals("1")) {
                break;
            }
        }

        Random serverNumRd = new Random();
        this.serverNum = serverNumRd.nextInt(100000) + 100;
        this.g = g;
        this.p = p;
    }

    private BigInteger getBigIntegerRandomRange(BigInteger min, BigInteger max) {
        //先获取一个0-1中间的随机数 然后乘以(max-min) 去除小数位 再加上min
        Random rd = new Random();
        BigDecimal randomNum = new BigDecimal(rd.nextFloat());
        randomNum = randomNum.multiply(new BigDecimal(max.subtract(min).toString()));
        return randomNum.toBigInteger().add(min);
    }
}
