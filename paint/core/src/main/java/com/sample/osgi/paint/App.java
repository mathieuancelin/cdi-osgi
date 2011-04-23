package com.sample.osgi.paint;

import com.sample.osgi.paint.gui.PaintFrame;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.osgi.cdi.api.extension.events.BundleContainerInitialized;
import org.osgi.cdi.api.extension.events.BundleContainerShutdown;
import org.osgi.cdi.api.extension.events.Invalid;
import org.osgi.cdi.api.extension.events.Valid;

@ApplicationScoped
public class App {

    @Inject PaintFrame frame;

    public void onStartup(@Observes BundleContainerInitialized event) {
        System.out.println("CDI Container for bundle "
                + event.getBundleContext().getBundle() + " started");
        frame.start();
    }

    public void onShutdown(@Observes BundleContainerShutdown event) {
        frame.stop();
    }

    public void validListen(@Observes Valid valid) {
        frame.start();
    }

    public void invalidListen(@Observes Invalid valid) {
        frame.stop();
    }
}
