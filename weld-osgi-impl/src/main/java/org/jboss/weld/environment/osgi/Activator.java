package org.jboss.weld.environment.osgi;

import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: guillaume
 * Date: 27/01/11
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class Activator implements BundleActivator {

    private CDIContainerFactory factory = new WeldCDIContainerFactory();
    private ServiceRegistration reg = null;

    @Override
    public void start(BundleContext context) throws Exception {
        reg = context.registerService(CDIContainerFactory.class.getName(), factory, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
        SingletonProvider.reset();
    }
}
