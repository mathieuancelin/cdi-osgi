package com.sample.web.app;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.Servlet;
import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.extension.events.Valid;
import org.osgi.framework.ServiceRegistration;

@ApplicationScoped
public class App {

    private ServiceRegistration reg;

    private AtomicBoolean valid = new AtomicBoolean(false);

    @Inject HotelsController controller;

    public void start(@Observes BundleContainerInitialized init) throws Exception {
        Properties props = new Properties();
        props.put("alias", "/hotels/all");
        reg = init.getBundleContext().registerService(Servlet.class.getName(), controller , props);
    }

    public void stop(@Observes BundleContainerShutdown stop) {
        try {
            reg.unregister();
        } catch (Exception e) {
            // nothing
        }
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
}
