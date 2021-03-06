package com.natsumes.demo;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;
import com.natsumes.demo.service.RmiDemoService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

public class RMIClient {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        RmiDemoService service = (RmiDemoService) Naming.lookup("rmi://127.0.0.1:37256/rmi-server");

        RequestBody requestBody = new RequestBody();
        requestBody.setId(UUID.randomUUID().toString());
        requestBody.setName("mo-yu");
        requestBody.setReqMsg("please handle this message");

        ResponseBody responseBody = service.sendRequest(requestBody);

        System.out.println(responseBody);
    }

}
