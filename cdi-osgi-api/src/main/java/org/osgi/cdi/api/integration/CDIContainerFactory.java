package org.osgi.cdi.api.integration;

import org.osgi.framework.Bundle;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface CDIContainerFactory {

    Class<? extends CDIContainerFactory> delegateClass();

    String getID();

    CDIContainer container(Bundle bundle);
}
