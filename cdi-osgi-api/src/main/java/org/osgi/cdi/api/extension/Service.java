package org.osgi.cdi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Service<T> {
    T get();
}
