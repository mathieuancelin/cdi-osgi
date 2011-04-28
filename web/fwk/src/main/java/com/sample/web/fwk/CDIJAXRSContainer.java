package com.sample.web.fwk;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.Map;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletException;

public class CDIJAXRSContainer extends ServletContainer {

    private static final long serialVersionUID = 1931878850157940335L;

    private WebApplication webapp;

    private final Instance<Object> controllers;

    @Inject
    public CDIJAXRSContainer(@Any Instance<Object> controllers) {
        this.controllers = controllers;
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig)
            throws ServletException {
        return new DefaultResourceConfig();
    }

    @Override
    protected void initiate(ResourceConfig config, WebApplication webapp) {
        this.webapp = webapp;
        webapp.initiate(config, new CDIBeansProvider());
    }

    public WebApplication getWebApplication() {
        return this.webapp;
    }

    public class CDIBeansProvider implements IoCComponentProviderFactory {

        @Override
        public IoCComponentProvider getComponentProvider(final Class<?> type) {
            return new IoCComponentProvider() {
                @Override
                public Object getInstance() {
                    return controllers.select(type).get();
                }
            };
        }

        @Override
        public IoCComponentProvider getComponentProvider(ComponentContext cc, final Class<?> type) {
            return new IoCComponentProvider() {
                @Override
                public Object getInstance() {
                    return controllers.select(type).get();
                }
            };
        }
    }
}
