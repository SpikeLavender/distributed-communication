package com.natsumes.demo.service;

import com.natsumes.demo.entity.RequestBody;
import com.natsumes.demo.entity.ResponseBody;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiDemoService extends Remote {

    ResponseBody sendRequest(RequestBody request) throws RemoteException;

}
