package com.sample.web.fwk;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import java.io.StringWriter;
import java.util.Map;

public class View {

    private final String templateName;
    private final Map<String, Object> context;
    private static final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    public View(String templateName, Map<String, Object> context) {
        this.templateName = templateName;
        this.context = context;
    }

    public String render() {
        try {
            StringWriter builder = new StringWriter();
            Template template = engine.createTemplate(
                    getClass().getClassLoader().getResource("views/" + templateName));
            template.make(context).writeTo(builder);
            builder.flush();
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
