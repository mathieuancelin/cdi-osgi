/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.weld.environment.osgi;

import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.osgi.integration.BundleSingletonProvider;
import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public class WeldCDIContainerFactory implements CDIContainerFactory {

    @Override
    public void init() {
        SingletonProvider.initialize(new BundleSingletonProvider());
    }

    @Override
    public void shutdown() {
        SingletonProvider.reset();
    }

    @Override
    public CDIContainer container(Bundle bundle) {
        CDIContainer container = new WeldCDIContainer(bundle);
        return container;
    }
}
