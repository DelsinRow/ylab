package com.sinaev.initializers;

import com.sinaev.configs.AppConfig;
import com.sinaev.configs.SwaggerConfig;
import com.sinaev.configs.WebConfig;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web application initializer.
 * <p>
 * This class initializes the web application with the specified configuration classes
 * and sets up the dispatcher servlet.
 * </p>
 */
public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * Specifies the root configuration classes.
     *
     * @return an array of root configuration classes
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    /**
     * Specifies the servlet configuration classes.
     *
     * @return an array of servlet configuration classes
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class, SwaggerConfig.class};
    }

    /**
     * Specifies the servlet mappings.
     *
     * @return an array of servlet mappings
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * Creates the dispatcher servlet and configures it to throw an exception if no handler is found.
     *
     * @param servletAppContext the web application context
     * @return the configured DispatcherServlet
     */
    @Override
    protected DispatcherServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
        final DispatcherServlet dispatcherServlet = (DispatcherServlet) super.createDispatcherServlet(servletAppContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }
}
