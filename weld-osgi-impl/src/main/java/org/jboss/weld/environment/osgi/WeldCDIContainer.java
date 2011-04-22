/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.weld.environment.osgi;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainers;
import org.osgi.cdi.impl.extension.CDIOSGiExtension;
import org.osgi.cdi.impl.extension.ExtensionActivator.SentAnnotation;
import org.osgi.cdi.impl.extension.ExtensionActivator.SpecificationAnnotation;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author mathieu
 */
public class WeldCDIContainer implements CDIContainer {

    private final Bundle bundle;
    private Weld container;
    private Collection<ServiceRegistration> registrations;

    public WeldCDIContainer(Bundle bundle) {
        this.bundle = bundle;
        container = new Weld(bundle);
    }

    @Override
    public void setRegistrations(Collection<ServiceRegistration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public Collection<ServiceRegistration> getRegistrations() {
        return registrations;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public void shutdown() {
        container.shutdown();
    }

    @Override
    public void fire(InterBundleEvent event) {
        Long set = CDIOSGiExtension.currentBundle.get();
        CDIOSGiExtension.currentBundle.set(bundle.getBundleId());
        container.getEvent().select(InterBundleEvent.class,
                new SpecificationAnnotation(event.type()),
                new SentAnnotation()).fire(event);
        if (set != null) {
            CDIOSGiExtension.currentBundle.set(set);
        } else {
            CDIOSGiExtension.currentBundle.remove();
        }
    }

    @Override
    public boolean initialize(CDIContainers containers) {
        return container.initialize(this, containers);
    }

    @Override
    public boolean isStarted() {
        return container.isStarted();
    }

    @Override
    public Event getEvent() {
        return container.getInstance().select(Event.class).get();
    }

    @Override
    public BeanManager getBeanManager() {
        return container.getBeanManager();
    }

    @Override
    public Instance<Object> getInstance() {
        return container.getInstance();
    }
}
