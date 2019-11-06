/*
 * KeycloakTest.java
 *
 * Created on 06.11.2019
 */
package com.github.n4zroth.keycloaktestdemo.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import javax.inject.Inject;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
public class KeycloakTest {

    @Inject
    private KeycloakConfiguration keycloakConfiguration;

    @Inject
    protected MockMvc mockMvc;

    @Container
    protected static final GenericContainer KEYCLOAK_CONTAINER =
            new GenericContainer("jboss/keycloak:7.0.1")
                    .withExposedPorts(8080)
                    .withEnv("KEYCLOAK_USER", "admin")
                    .withEnv("KEYCLOAK_PASSWORD", "password")
                    .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
                    .withClasspathResourceMapping("realm-export.json", "/tmp/realm.json", BindMode.READ_ONLY)
                    .withCopyFileToContainer(MountableFile.forClasspathResource("create-keycloak-user.sh", 700),
                            "/opt/jboss/create-keycloak-user.sh")
                    .waitingFor(Wait.forHttp("/auth"));

    protected static String keycloakHost;

    @BeforeAll
    public static void setupKeycloakContainer() throws IOException, InterruptedException {
        keycloakHost = "http://" + KEYCLOAK_CONTAINER.getContainerIpAddress() + ":" + KEYCLOAK_CONTAINER
                .getMappedPort(8080);
        final org.testcontainers.containers.Container.ExecResult commandResult = KEYCLOAK_CONTAINER
                .execInContainer("sh", "/opt/jboss/create-keycloak-user.sh");
        assertThat(commandResult.getExitCode(), equalTo(0));
    }

    @BeforeEach
    public void overwriteKeyCloakAuthServerUrl() {
        keycloakConfiguration.getProperties().setAuthServerUrl(keycloakHost + "/auth");
    }

    protected static String getAccessToken() {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "test-resource");
        map.add("username", "admin");
        map.add("password", "admin");
        final KeyCloakToken token = restTemplate
                .postForObject(keycloakHost + "/auth/realms/TestRealm/protocol/openid-connect/token",
                        new HttpEntity<>(map, headers), KeyCloakToken.class);

        assertThat(token, notNullValue());
        return token.getAccessToken();
    }

    private static class KeyCloakToken {

        private final String accessToken;

        @JsonCreator
        KeyCloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }
}
