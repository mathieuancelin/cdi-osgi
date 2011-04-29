package com.sample.web.app;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import com.sample.web.fwk.View;
import com.sample.web.fwk.api.Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.osgi.cdi.api.extension.Service;

@Path("hotels")
public class TestController implements Controller {

    @Inject Service<HotelProvider> providers;

    @GET @Path("all")
    @Produces("text/html")
    public View all() {
        Map<String, Object> context = new HashMap<String, Object>();
        List<Hotel> hotels = new ArrayList<Hotel>();
        for (HotelProvider provider : providers) {
            hotels.addAll(provider.hotels());
        }
        context.put("hotels", hotels);
        return new View("all.xhtml", context, getClass());
    }

    @GET @Path("index")
    @Produces("text/html")
    public View index() {
        return new View("index.xhtml", null, getClass());
    }
}
