# ti-dh-java

Java实现的DH算法，用于安卓客户端

## 目录

```
- android 安卓端项目源码
- example 安卓测试用例
- release 发布的 aar 包和测试用例 apk
```

## 使用

```
// 根据server-number，client-number和p 计算出公共密钥K
TiDHClient dhClient = new TiDHClient();
String clientKey = dhClient.processKey(p, g, serverNumber);
// 将计算过后的 client-number
dhClient.getClientNumber()
```