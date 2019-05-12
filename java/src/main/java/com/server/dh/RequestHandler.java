package com.server.dh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import com.server.dh.api.DHController;

public class RequestHandler {
    public static byte[] response(ChannelHandlerContext ctx, Object msg) {
        HttpRequest req = (HttpRequest) msg;

        String uri = req.uri();
        if (uri.length() <= 0) {
            return error();
        }

        //暂时只支持两个路由
        DHController dh = new DHController();
        if (uri.contains("getdhbasedata")) {
            Object baseData = dh.getBaseData();
            return encode(baseData);
        } else if (uri.contains("postdhclientdata")) {
            //先获取请求参数
            Map<String, String> postData = getRequestParams(ctx, req);
            return encode(dh.postClientData(postData));
        } else {
            return error();
        }

    }

    private static byte[] encode(Object object) {
        String data = JSON.toJSONString(object);
        return data.getBytes();
    }


    private static byte[] error() {
        String error = "params error";
        return error.getBytes();
    }

    private static Map<String, String> getRequestParams(ChannelHandlerContext ctx, HttpRequest req) {
        Map<String, String>requestParams=new HashMap<>();
        // 处理get请求
        if (req.method() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            Map<String, List<String>> parame = decoder.parameters();
            Iterator<Map.Entry<String, List<String>>> iterator = parame.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, List<String>> next = iterator.next();
                requestParams.put(next.getKey(), next.getValue().get(0));
            }
        }
        // 处理POST请求
        if (req.method() == HttpMethod.POST) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(false), req);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas(); //
            for(InterfaceHttpData data:postData){
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    requestParams.put(attribute.getName(), attribute.getValue());
                }
            }
        }
        return requestParams;
    }
}