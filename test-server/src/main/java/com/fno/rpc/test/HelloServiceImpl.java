package com.fno.rpc.test;

import com.fno.rpc.api.HelloObject;
import com.fno.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}",object.getMessage());
        return "返回值：id = " + object.getId();
    }
}
