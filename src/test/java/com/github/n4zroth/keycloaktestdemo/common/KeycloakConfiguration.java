/*
 * KeycloakConfiguration.java
 *
 * Created on 06.11.2019
 */
package com.github.n4zroth.keycloaktestdemo.common;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfiguration {

    private final KeycloakSpringBootProperties properties;

    public KeycloakConfiguration(final KeycloakSpringBootProperties properties) {
        this.properties = properties;
    }

    public KeycloakSpringBootProperties getProperties() {
        return properties;
    }
}
