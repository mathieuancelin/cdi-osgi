package org.osgi.cdi.api.extension.annotation;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 *
 * @author Mathieu ANCELIN - SERLI (mathieu.ancelin@serli.com)
 */
@Qualifier
@Target({ PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Specification {
    Class value();
}
