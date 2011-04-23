package org.osgi.cdi.impl.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;

import org.osgi.cdi.api.integration.CDIContainers;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ServicePublisher;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Guillaume Sauthier
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class IntegrationActivator implements BundleActivator, BundleListener, CDIContainers, ServiceListener {

    private Map<Long, CDIContainer> managed;
    private ServiceReference factoryRef = null;
    private BundleContext context;
    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void start(BundleContext context) throws Exception {

        managed = new HashMap<Long, CDIContainer>();
        this.context = context;
        ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
        if (refs != null && refs.length > 0) {
            factoryRef = refs[0];
            startCDIOSGi();
        }
        context.addServiceListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopCDIOSGi();

    }

    public void startCDIOSGi() throws Exception {
        started.set(true);
        managed = new HashMap<Long, CDIContainer>();
        for (Bundle bundle : context.getBundles()) {
            if (Bundle.ACTIVE == bundle.getState()) {
                startManagement(bundle);
            }
        }
        context.addBundleListener(this);
    }

    public void stopCDIOSGi() throws Exception {
        started.set(false);
        for (Bundle bundle : context.getBundles()) {
            CDIContainer holder = managed.get(bundle.getBundleId());
            if (holder != null) {
                stopManagement(holder.getBundle());
            }
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                startManagement(event.getBundle());
                break;
            case BundleEvent.STOPPED:
                stopManagement(event.getBundle());
                break;
        }
    }

    private void stopManagement(Bundle bundle) {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        CDIContainer holder = managed.get(bundle.getBundleId());
        if (holder != null) {
            Collection<ServiceRegistration> regs = holder.getRegistrations();
            for (ServiceRegistration reg : regs) {
                try {
                    reg.unregister();
                } catch (IllegalStateException e) {
                    // Ignore
                }
            }
            holder.shutdown();
            managed.remove(bundle.getBundleId());
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    private void startManagement(Bundle bundle) {
        boolean set = CDIOSGiExtension.currentBundle.get() != null;
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        //System.out.println("Starting management for bundle " + bundle);
        CDIContainer holder = ((CDIContainerFactory) context.getService(factoryRef)).container(bundle);
        holder.initialize(this);
        if (holder.isStarted()) {
            ServicePublisher publisher = new ServicePublisher(holder.getBeanClasses(),
                bundle, holder.getInstance(),
                ((CDIContainerFactory) context.getService(factoryRef)).getContractBlacklist());
            publisher.registerAndLaunchComponents();
            Collection<ServiceRegistration> regs = new ArrayList<ServiceRegistration>();

            BundleContext bundleContext = bundle.getBundleContext();
            try {
                regs.add(
                        bundleContext.registerService(Event.class.getName(),
                        holder.getEvent(),
                        null));

                regs.add(
                        bundleContext.registerService(BeanManager.class.getName(),
                        holder.getBeanManager(),
                        null));

                regs.add(
                        bundleContext.registerService(Instance.class.getName(),
                        holder.getInstance(),
                        null));
            } catch (Throwable t) {
                // Ignore
            }
            holder.setRegistrations(regs);
            managed.put(bundle.getBundleId(), holder);
        }
        if (!set) {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        try {
            ServiceReference[] refs = context.getServiceReferences(CDIContainerFactory.class.getName(), null);
            if (ServiceEvent.REGISTERED == event.getType()) {
                if (!started.get() && refs != null && refs.length > 0) {
                    factoryRef = refs[0];
                    startCDIOSGi();
                }
            } else if (ServiceEvent.UNREGISTERING == event.getType()) {
                if (started.get() && (refs == null || refs.length == 0)) {
                    factoryRef = null;
                    stopCDIOSGi();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Iterator<CDIContainer> iterator() {
        return managed.values().iterator();
    }
}
