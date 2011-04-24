package org.jboss.weld.environment.osgi.integration;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainers;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldEmbedded {

    private final Weld weld;
    private final BundleContext context;
    private Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

    private WeldEmbedded(Weld weld, BundleContext context) {
        this.weld = weld;
        this.context = context;
    }

    public static WeldEmbedded startFor(BundleContext context) throws Exception {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(context.getBundle().getBundleId());
        WeldEmbedded embedded =
                new WeldEmbedded(new Weld(context.getBundle()),
                context);
        try {
            embedded.regs.add(
                    context.registerService(Event.class.getName(),
                    embedded.weld.getEvent(),
                    null));

            embedded.regs.add(
                    context.registerService(BeanManager.class.getName(),
                    embedded.weld.getBeanManager(),
                    null));

            embedded.regs.add(
                    context.registerService(Instance.class.getName(),
                    embedded.weld.getInstance(),
                    null));
        } catch (Throwable t) {
            // Ignore
        }
        embedded.weld.initialize();
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
        return embedded;
    }

    public void shutdown() throws Exception {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(context.getBundle().getBundleId());
        for (ServiceRegistration reg : regs) {
            try {
                reg.unregister();
            } catch (IllegalStateException e) {
                // Ignore
            }
        }
        weld.shutdown();
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    public Event event() {
        return weld.getInstance().select(Event.class).get();
    }

    public BeanManager beanManager() {
        return weld.getBeanManager();
    }

    public Instance<Object> instance() {
        return weld.getInstance();
    }
}
