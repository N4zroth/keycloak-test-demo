package com.github.n4zroth.keycloaktestdemo.controller;

import com.github.n4zroth.keycloaktestdemo.common.KeycloakTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestControllerTestWithKeycloak extends KeycloakTest {

    @Test
    public void testUnauthorized() throws Exception {
        mockMvc.perform(get("/test")).andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthorized() throws Exception {
        mockMvc.perform(get("/test")
                .header("Authorization",
                        "Bearer " + getAccessToken()))
                .andExpect(status().isOk());
    }

}
