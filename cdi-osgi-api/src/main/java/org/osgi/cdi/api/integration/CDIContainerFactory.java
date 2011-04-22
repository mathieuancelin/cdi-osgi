package org.osgi.cdi.api.integration;

import org.osgi.framework.Bundle;

/**
 *
 * @author mathieu
 */
public interface CDIContainerFactory {

    void init();

    void shutdown();

    CDIContainer container(Bundle bundle);
}
