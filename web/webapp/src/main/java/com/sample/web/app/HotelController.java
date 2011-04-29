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
import javax.ws.rs.core.MediaType;
import org.osgi.cdi.api.extension.Service;

@Path("hotels")
public class HotelController implements Controller {

    @Inject Service<HotelProvider> providers;

    @Inject App app;

    @GET @Path("all")
    @Produces(MediaType.TEXT_HTML)
    public String all() {
        Map<String, Object> context = new HashMap<String, Object>();
        List<Hotel> hotels = new ArrayList<Hotel>();
        for (HotelProvider provider : providers) {
            hotels.addAll(provider.hotels());
        }
        context.put("hotels", hotels);
        context.put("providers", providers);
        if (app.isValid()) {
            return new View("all.xhtml", context, getClass()).render();
        } else {
            return new View("none.xhtml", context, getClass()).render();
        }
    }

    @GET @Path("index")
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        return new View("index.xhtml", null, getClass()).render();
    }
}
