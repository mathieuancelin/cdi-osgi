package com.sample.web.fwk;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.ServletResponse;

public class View {

    private final String templateName;
    private final Map<String, Object> context;
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();
    private final ClassLoader loader;

    public View(String templateName, Map<String, Object> context, Class<?> from) {
        this.templateName = templateName;
        this.context = context;
        this.loader = from.getClassLoader();
    }

    public void render(ServletResponse resp) {
        try {
            resp.setContentType("text/html");
            PrintWriter w = resp.getWriter();
            w.println(render());
            w.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String render() {
        try {
            StringWriter builder = new StringWriter();
            Template template = engine.createTemplate(
                    loader.getResource("views/" + templateName));
            template.make(context).writeTo(builder);
            builder.flush();
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
