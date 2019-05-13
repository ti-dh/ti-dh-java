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

DH算法服务端的具体实现参考DH类

说明:
**init()函数**

参数: 无

返回:

| 字段 | 类型 | 含义 |
| ------ | ------ | ------ |
| p | String | 服务端计算出来的p |
| g | String | 服务端计算出来的g |
| server_number | String | 服务端【私钥】，请保密，不可以外泄 |
| processed_server_number | String | 处理过的服务端【私钥】，返回给客户端 |

**computeShareKey()函数**

请求参数: 

| 字段 | 类型 | 含义 |
| ------ | ------ | ------ |
| client_number | String | 客户端提交过来client_number |
| server_number | String | 服务端server_number，未经过处理的需要保密的那个 |
| p | String | 服务端计算出来的p |

返回参数:

| 参数 | 类型 | 说明 |
| ------ | ------ | ------ |
| key | String | 协商完成的用于对称加解密的密钥 |


```com.server.client```是客户端的包，里面包含了计算client_number的方法 可以直接运行