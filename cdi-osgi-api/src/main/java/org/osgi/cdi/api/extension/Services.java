package org.osgi.cdi.api.extension;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
public interface Services<T> extends Iterable<T> {

    int size();
}
