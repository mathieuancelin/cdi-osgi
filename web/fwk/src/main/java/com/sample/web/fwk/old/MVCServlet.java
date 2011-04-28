package com.sample.web.fwk.old;

import com.sample.web.fwk.api.Controller;
import com.sample.web.fwk.View;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class MVCServlet extends HttpServlet {

    @Inject @Any Instance<Controller> instances;
    
    private String context = "";

    public MVCServlet() {
    }

    public MVCServlet(Instance<Controller> instances) {
        this.instances = instances;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
        System.out.println("Registered controllers : ");
        for (Controller c : instances) {
            System.out.println("- " + c.getClass().getName());
        }
        System.out.println("");
    }

    @Override
    public void destroy() {
        super.destroy();
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

    protected void doServe(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            URI requestURI = new URI(req.getRequestURI());
            String controllerName = requestURI.getPath().replace(context, "");
            if (controllerName.startsWith("static") || controllerName.startsWith("/static")) {
                if (controllerName.startsWith("/")) {
                    controllerName = controllerName.substring(1);
                }
                InputStream in = null;
                try {
                    URL url = getClass().getClassLoader().getResource(controllerName);
                    URLConnection urlc = url.openConnection();
                    int length = urlc.getContentLength();
                    in = urlc.getInputStream();
                    resp.setContentLength(length);
                    int ch;
                    while ((ch = in.read()) != -1) {
                        out.print((char) ch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("Error Streaming the Data");
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } else {
                resp.setContentType("text/html");
                controllerName = controllerName.substring(1);
                controllerName = controllerName.substring(0, controllerName.indexOf("/"));
                String controllerMethod = requestURI.getPath().substring(1);
                controllerMethod = controllerMethod.replace(controllerName + "/", "");
                Controller controller = instances.select(new ServeAnnotation(controllerName)).get();
                if (controller == null) {
                    throw new IllegalStateException("Controller is null :(");
                }
                Method m = controller.getClass().getMethod(controllerMethod);
                if (!m.getReturnType().equals(View.class)) {
                    throw new IllegalStateException("You have to return a View");
                }
                View v = (View) m.invoke(controller);
                out.println(v.render());
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            resp.setContentType("text/html");
            out.println("<html><body><h1>Error</h1>");
            out.println(ex.getClass().getName());
            out.println(" : ");
            out.println(ex.getMessage());
            out.println("<br/>");
            for (StackTraceElement e : ex.getStackTrace()) {
                out.println(e.toString());
                out.println("<br/>");
            }
            if (ex.getCause() != null) {
                out.println("caused by : <br/>");
                out.println(ex.getCause().getClass().getName());
                out.println(" : ");
                out.println(ex.getCause().getMessage());
                out.println("<br/>");
                for (StackTraceElement e : ex.getCause().getStackTrace()) {
                    out.println(e.toString());
                    out.println("<br/>");
                }
            }
        }
        out.close();
    }

    public static class ServeAnnotation
            extends AnnotationLiteral<Serve>
            implements Serve {

        private final String value;

        public ServeAnnotation(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Serve.class;
        }
    }
}
