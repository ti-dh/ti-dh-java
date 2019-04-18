package com.ti.tidh;

import java.math.BigInteger;
import java.util.Random;

public class TiDHClient {
    private final int mClientNum;
    private BigInteger mP;
    private BigInteger mG;
    private BigInteger mServerNumber;
    private BigInteger mProcessedClientNum;
    private BigInteger mPublicKey;

    public TiDHClient() {
        mClientNum = new Random().nextInt(99999 - 10000) + 10000;
    }

    /**
     * 获取 client number. 用于发送给服务端.
     * 如果未调用 processKey 将返回空字符串.
     * @return client number.
     */
    public String  getClientNumber() {
        if (mProcessedClientNum == null)
            return "";
        return  mProcessedClientNum.toString();
    }

    /**
     * 返回公钥字符串.
     * 如果未调用 processKey 将返回空字符串
     * @return public key
     */
    public String getPublicKey() {
        if (mPublicKey == null)
            return "";
        return mPublicKey.toString();
    }

    /**
     * 通过服务端获取的 p, g 和server number计算公钥 K.
     * @param p 通过服务端获取的 p
     * @param g 通过服务端获取的 g
     * @param serverNum 通过服务端获取的 server number
     * @return 公钥字符串
     */
    public String processKey(String p, String g, String serverNum) {
        mP = new BigInteger(p);
        mG = new BigInteger(g);
        mServerNumber = new BigInteger(serverNum);
        mProcessedClientNum = mG.modPow(new BigInteger(String.valueOf(mClientNum)), mP);
        // 计算公钥 K
        mPublicKey = mServerNumber.modPow(new BigInteger(String.valueOf(mClientNum)), mP);
        return mPublicKey.toString();
    }
}
