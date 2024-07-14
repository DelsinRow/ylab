package com.sinaev.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class DatasourceProperties {
    private String url;
    private String username;
    private String password;

}
