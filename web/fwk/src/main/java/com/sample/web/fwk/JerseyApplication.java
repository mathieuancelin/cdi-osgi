package com.sample.web.fwk;

import java.util.Set;
import javax.ws.rs.core.Application;

public class JerseyApplication extends Application {

    public static ThreadLocal<Set<Class<?>>> classes = new ThreadLocal<Set<Class<?>>>();

    @Override
    public Set<Class<?>> getClasses() {
        return classes.get();
    }
}
