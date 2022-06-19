package com.kuing.rpcserver;

import com.kuing.rpccommon.service.HelloSerivce;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloSerivce {
    @Override
    public String hello(String name) {
        return "hello, " + name;
    }
}
