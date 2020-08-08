package com.natsumes.demo.service;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;


public interface RpcDemoService {
    String sendRpcRequest(String msg);

    ResponseBody sendRpcRequest(RequestBody requestBody);
}
