package com.sample.web.fwk;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class JerseyApplication extends Application {

    public JerseyApplication() {
        System.out.println("jersey appppppp");
    }


    @Override
    public Set<Class<?>> getClasses() {
        System.out.println("classssssssessss");
        Set<Class<?>> result = new HashSet<Class<?>>();
        new Throwable().printStackTrace();
        return result;
    }
}
