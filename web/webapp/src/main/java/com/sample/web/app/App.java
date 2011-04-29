package com.sample.web.app;

import com.sample.web.fwk.CDIJAXRSContainer;
import com.sample.web.fwk.JerseyApplication;
import com.sample.web.fwk.api.Controller;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.Servlet;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.extension.events.Valid;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

@ApplicationScoped
public class App {

    private static final String CONTEXT_ROOT = "/app";

    @Inject @Any private Instance<Object> instances;
    
    @Inject HotelsControllerServlet controller;
    
    private BundleContext bc;
    private ServiceTracker tracker;
    private HttpService httpService = null;
    private ServiceRegistration reg;
    private AtomicBoolean valid = new AtomicBoolean(false);

    public void start(@Observes BundleContainerInitialized init) throws Exception {
        this.bc = init.getBundleContext();
        Properties props = new Properties();
        props.put("alias", "/hotels/all");
        reg = init.getBundleContext().registerService(Servlet.class.getName(), controller , props);
        this.tracker = new ServiceTracker(this.bc, HttpService.class.getName(), null) {

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
        };
        this.tracker.open();
    }
    
    public void validate(@Observes Valid event) {
        valid.compareAndSet(false, true);
    }

    public void invalidate(@Observes Invalid event) {
        valid.compareAndSet(true, false);
    }

    public boolean isValid() {
        return valid.get();
    }

    private void registerServlets() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JerseyApplication.class.getClassLoader());
        try {
            Properties props = new Properties();
            props.put("alias", CONTEXT_ROOT);
            props.put("init.javax.ws.rs.Application", JerseyApplication.class.getName());
            reg = bc.registerService(Servlet.class.getName(),
                    new CDIJAXRSContainer(instances) , props);
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }
}
