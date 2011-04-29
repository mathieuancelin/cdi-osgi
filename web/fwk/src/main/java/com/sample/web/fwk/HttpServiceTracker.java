package com.sample.web.fwk;

import java.util.Properties;
import javax.enterprise.inject.Instance;
import javax.servlet.Servlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServiceTracker extends ServiceTracker {

    private final BundleContext bc;
    private HttpService httpService = null;
    private ServiceRegistration reg;
    private final String contextRoot;
    private final Instance<Object> instances;

    public HttpServiceTracker(BundleContext bc, Instance<Object> instances, String contextRoot) {
        super(bc, HttpService.class.getName(), null);
        this.bc = bc;
        this.contextRoot = contextRoot;
        this.instances = instances;
    }

    @Override
    public Object addingService(ServiceReference serviceRef) {
        httpService = (HttpService) super.addingService(serviceRef);
        registerServlets();
        return httpService;
    }

    @Override
    public void removedService(ServiceReference ref, Object service) {
        if (httpService == service) {
            reg.unregister();
            httpService = null;
        }
        super.removedService(ref, service);
    }

    private void registerServlets() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JerseyApplication.class.getClassLoader());
        try {
            Properties props = new Properties();
            props.put("alias", contextRoot);
            props.put("init.javax.ws.rs.Application", JerseyApplication.class.getName());
            reg = bc.registerService(Servlet.class.getName(),
                    new CDIJAXRSContainer(instances) , props);
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }
}
