package com.kuing.dubbohello.provider;

import com.kuing.dubbohello.api.GreetingService;
import org.apache.dubbo.common.extension.ExtensionLoader;

public class GreetingServiceImpl implements GreetingService {
    @Override
    public String sayHi(String name) {
        return "hi," + name;
    }
}
