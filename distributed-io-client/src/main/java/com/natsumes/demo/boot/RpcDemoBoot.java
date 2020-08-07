package com.natsumes.demo.boot;

import com.natsumes.demo.RpcClient;
import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.service.RpcDemoService;

public class RpcDemoBoot {

    //参数定义
    private static final String PROVIDER_NAME_PREFIX = "RpcDemoService#sendRpcRequest#";

    public static void main(String[] args) throws InterruptedException {
        //1.创建代理对象
        RpcDemoService service = (RpcDemoService) RpcClient.createProxy(RpcDemoService.class, PROVIDER_NAME_PREFIX);
        //2.循环给服务器写数据
        while (true) {
            String responseBody = service.sendRpcRequest(PROVIDER_NAME_PREFIX);
            System.out.println(responseBody);
            Thread.sleep(2000);
        }
    }
}
