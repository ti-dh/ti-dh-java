# ti-dh-java

Java实现的DH算法，用于安卓客户端以及java服务端、客户端

## 目录

```
- java 纯java端项目源码
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
___
## java端项目说明

该项目是maven项目

```com.server.dh```是服务端的包，采用[netty](https://github.com/netty/netty)在8877端口起HttpServer

实际项目中客户直接参考api包里面的DHController来根据你的实际情况使用

API说明:
**getBaseData:**

请求参数: 无

返回参数:

| 参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| p | String | 服务端计算出来的p |
| g | String | 服务端计算出来的g |
| server_number | String | 处理过的服务端【私钥】，返回给客户端 |

说明:服务端返回的server_number是计算过后的 在返回的前一步要缓存一遍原始数据

**postClientData**

请求参数: 

| 参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| client_number | String | 客户端计算出的client_number |

返回参数:

| 参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| key | String | 服务端计算出的密钥 |

说明:在实际生产环境中不能返回的要保密的 本例子为了方便测试验证 所以返回


```com.server.client```是客户端的包，可以直接运行