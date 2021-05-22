package com.fno.rpc.test.socket.serviceImpl;

import com.fno.rpc.annotation.Service;
import com.fno.rpc.api.TestService;

@Service
public class TestServiceImpl implements TestService {
    @Override
    public String justTest(String msg) {
        return msg + ", yes";
    }
}
