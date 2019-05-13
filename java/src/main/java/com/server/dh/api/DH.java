package com.server.dh.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class DH {
    private static final String pSource = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6BF12FFA06D98A0864D87602733EC86A64521F2B18177B200CBBE117577A615D6C770988C0BAD946E208E24FA074E5AB3143DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF";

    private BigInteger p;

    private BigInteger g;

    private int serverNum;

    /**
     * 初始化p g server_number 以及计算过的server_number
     */
    public HashMap<String, String> init() {
        generateBaseInfo();
        HashMap<String, String> baseData = new HashMap<>();
        baseData.put("p", p.toString());
        baseData.put("g", g.toString());
        baseData.put("server_number", serverNum+"");
        baseData.put("processed_server_number", processServerKey());
        return baseData;
    }

    /**
     * 根据客户端传过来的client_number 计算出key
     * @param clientNumber 客户端传过来的client_number
     * @param serverNumber 上一次请求随机生成的server_number
     * @param p 上一次请求的p
     * @return String
     */
    public String computeShareKey (String clientNumber, String serverNumber, String p) {
        BigInteger BigClientNumber = new BigInteger(clientNumber);
        return BigClientNumber.modPow(new BigInteger(serverNumber + ""), new BigInteger(p)).toString();
    }

    /**
     * 返回已处理的服务端server-number
     */
    private String processServerKey () {
        return g.modPow((new BigInteger(serverNum + "")), p).toString();
    }

    private void generateBaseInfo () {
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

    /**
     * 获取一个范围的大数的随机数
     * @param min 最小值
     * @param max 最大值
     * @return BigInteger
     */
    private BigInteger getBigIntegerRandomRange(BigInteger min, BigInteger max) {
        //先获取一个0-1中间的随机数 然后乘以(max-min) 去除小数位 再加上min
        Random rd = new Random();
        BigDecimal randomNum = new BigDecimal(rd.nextFloat());
        randomNum = randomNum.multiply(new BigDecimal(max.subtract(min).toString()));
        return randomNum.toBigInteger().add(min);
    }
}
