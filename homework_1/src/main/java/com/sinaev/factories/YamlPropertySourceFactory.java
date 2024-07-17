package com.sinaev.factories;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.Properties;

/**
 * Factory for creating property sources from YAML files.
 * <p>
 * This class extends {@link DefaultPropertySourceFactory} to support loading properties from YAML files.
 * </p>
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * Creates a property source from the given resource.
     *
     * @param name the name of the property source
     * @param resource the resource to load properties from
     * @return the created property source
     * @throws IOException if an error occurs while reading the resource
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, null);
        }

        Properties propertiesFromYaml = loadYamlIntoProperties(resource);
        return new PropertiesPropertySource((name != null ? name : resource.getResource().getFilename()), propertiesFromYaml);
    }

    /**
     * Loads properties from a YAML resource.
     *
     * @param resource the resource to load properties from
     * @return the loaded properties
     * @throws IOException if an error occurs while reading the resource
     */
    private Properties loadYamlIntoProperties(EncodedResource resource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}

