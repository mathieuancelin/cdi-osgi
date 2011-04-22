package org.jboss.weld.environment.osgi;

import org.jboss.weld.environment.osgi.integration.Weld;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public class WeldCDIContainerFactory implements CDIContainerFactory {

    @Override
    public CDIContainer container(Bundle bundle) {
        return new WeldCDIContainer(bundle);
    }

    @Override
    public Class<? extends CDIContainerFactory> delegateClass() {
        return WeldCDIContainerFactory.class;
    }

    @Override
    public String getID() {
        return Weld.class.getName();
    }
}
