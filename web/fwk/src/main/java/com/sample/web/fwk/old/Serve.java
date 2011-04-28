package com.sample.web.fwk.old;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Serve {
    String value();
}
