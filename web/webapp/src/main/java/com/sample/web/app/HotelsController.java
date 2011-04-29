package com.sample.web.app;

import com.sample.web.api.Hotel;
import com.sample.web.api.HotelProvider;
import com.sample.web.fwk.View;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Required;

@ApplicationScoped
public class HotelsController extends HttpServlet {

    @Inject App app;

    @Inject @Required Service<HotelProvider> providers;

    public void doServe(HttpServletRequest req, HttpServletResponse resp)
               throws ServletException, IOException {
        
        Map<String, Object> context = new HashMap<String, Object>();
        List<Hotel> hotels = new ArrayList<Hotel>();
        for (HotelProvider provider : providers) {
            hotels.addAll(provider.hotels());
        }
        context.put("hotels", hotels);
        context.put("providers", providers);
        if (app.isValid()) {
            new View("all.xhtml", context, getClass()).render(resp);
        } else {
            new View("none.xhtml", context, getClass()).render(resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doServe(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doServe(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doServe(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doServe(req, resp);
    }

    private static class Page {
        private final String content;
        private final String title;
        public Page(String title, String content) {
            this.content = content;
            this.title = title;
        }

        public String get() {
            return "<html>"
                    + "<head>"
                        + "<title>" + title + "</title>"
                    + "</head>"
                    + "<body>"
                        + content
                    + "</body>"
                + "</html>";
        }
    }
}
