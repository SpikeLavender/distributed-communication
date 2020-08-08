package com.natsumes.demo.service.impl;


import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.service.RmiDemoService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiDemoServiceImpl extends UnicastRemoteObject implements RmiDemoService {

    private static final long serialVersionUID = 7994202176440473376L;

    public RmiDemoServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public ResponseBody sendRequest(RequestBody request) throws RemoteException{
        System.out.println("this is the rmi server, handle " + request.getName());
        ResponseBody responseBody = new ResponseBody();
        responseBody.setId(request.getId());
        responseBody.setRespMsg("handle success");
        return responseBody;
    }
}
