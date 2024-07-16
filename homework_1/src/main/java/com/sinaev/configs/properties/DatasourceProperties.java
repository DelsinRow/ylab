package com.sinaev.configs.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for the data source.
 * <p>
 * This class holds the properties required to configure a data source, including
 * the URL, username, and password.
 * </p>
 */
@Getter
@Setter
public class DatasourceProperties {
    private String url;
    private String username;
    private String password;

}
