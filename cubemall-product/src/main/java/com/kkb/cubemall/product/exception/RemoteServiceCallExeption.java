package com.kkb.cubemall.product.exception;

public class RemoteServiceCallExeption extends Exception {
    RemoteServiceCallExeption(){};

    public RemoteServiceCallExeption(String message){
        super(message);
    }
}
