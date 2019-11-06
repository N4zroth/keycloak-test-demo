/*
 * MyKeycloakSpringBootConfigResolver.java
 *
 * Created on 04.11.2019
 */
package com.github.n4zroth.keycloaktestdemo.configuration;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.context.annotation.Configuration;

// See https://issues.jboss.org/browse/KEYCLOAK-11282 this should probably be obsolete in future versions
@Configuration
public class MyKeycloakSpringBootConfigResolver extends KeycloakSpringBootConfigResolver {
    private final KeycloakDeployment keycloakDeployment;

    public MyKeycloakSpringBootConfigResolver(final KeycloakSpringBootProperties properties) {
        keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
    }

    @Override
    public KeycloakDeployment resolve(final HttpFacade.Request facade) {
        return keycloakDeployment;
    }
}
