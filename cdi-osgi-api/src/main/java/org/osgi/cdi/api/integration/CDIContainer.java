package org.osgi.cdi.api.integration;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author mathieu
 */
public interface CDIContainer {

    void fire(InterBundleEvent event);

    Bundle getBundle();

    Collection<ServiceRegistration> getRegistrations();

    boolean initialize(CDIContainers containers);

    void shutdown();

    boolean isStarted();

    BeanManager getBeanManager();

    Event getEvent();

    Instance<Object> getInstance();

    void setRegistrations(Collection<ServiceRegistration> registrations);

}
