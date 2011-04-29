package com.sample.web.app;

import com.sample.web.fwk.CDIJAXRSContainer;
import com.sample.web.fwk.JerseyApplication;
import com.sample.web.fwk.api.Controller;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

@ApplicationScoped
public class JerseyApp {

    @Inject @Any Instance<Object> instances;

    @Inject @Any Instance<Controller> controllers;
    
    private BundleContext bc;
    private ServiceTracker tracker;
    private HttpService httpService = null;

    private ServiceRegistration reg;

    public void start(@Observes BundleContainerInitialized init) throws Exception {
        this.bc = init.getBundleContext();

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
                    unregisterServlets();
                    httpService = null;
                }
                super.removedService(ref, service);
            }
        };
        this.tracker.open();
    }

    public void stop(@Observes BundleContainerShutdown stop) {
//        tracker.close();
    }

    private void registerServlets() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            Set<Class<?>> result = new HashSet<Class<?>>();
            for (Controller c : controllers) {
                result.add(c.getClass());
            }
            JerseyApplication.classes.set(result);
            httpService.registerServlet("/app", new ServletContainer(), getJerseyServletParams(), null);
            //httpService.registerServlet("/app", new CDIJAXRSContainer(instances), getJerseyServletParams(), null);
        } catch (ServletException ex) {
            Logger.getLogger(JerseyApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamespaceException ex) {
            Logger.getLogger(JerseyApp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Thread.currentThread().setContextClassLoader(loader);
            JerseyApplication.classes.remove();
        }
    }

    private void unregisterServlets() {
        if (httpService != null) {
            httpService.unregister("/app");
        }
    }

    private Dictionary<String, String> getJerseyServletParams() {
        Dictionary<String, String> jerseyServletParams = new Hashtable<String, String>();
        jerseyServletParams.put("javax.ws.rs.Application", JerseyApplication.class.getName());
        return jerseyServletParams;
    }
}
