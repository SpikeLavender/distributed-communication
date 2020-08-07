package com.natsumes.demo.server;

import com.natsumes.demo.service.RmiDemoService;
import com.natsumes.demo.service.impl.RmiDemoServiceImpl;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException {

        //1.创建实例
        RmiDemoService service = new RmiDemoServiceImpl();

        //2.获取注册表
        LocateRegistry.createRegistry(37256);

        //3.对象的绑定
        // bind 参数1: rmi://ip地址:端口/服务名  参数2: 绑定的对象
        Naming.bind("rmi://127.0.0.1:37256/rmi-server", service);

    }

}
