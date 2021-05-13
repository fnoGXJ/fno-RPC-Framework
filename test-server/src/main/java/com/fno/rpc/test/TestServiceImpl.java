package com.fno.rpc.test;

import com.fno.rpc.api.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public String justTest(String msg){
        return msg+", yes";
    }
}
