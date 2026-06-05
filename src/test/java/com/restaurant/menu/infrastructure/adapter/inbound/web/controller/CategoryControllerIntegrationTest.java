package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateCategoryRequest;
import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE menu.menu_item_allergens, menu.menu_item_modifier_groups, menu.menu_item_ingredients, menu.nutritional_info, menu.menu_sections, menu.modifier_options, menu.modifier_groups, menu.menu_items, menu.categories, menu.ingredients, menu.allergens, menu.users CASCADE");

        String encodedPassword = passwordEncoder.encode("admin123");
        jdbcTemplate.execute("INSERT INTO menu.users (id, email, password, name, role, active, created_at, updated_at) VALUES ('" + UUID.randomUUID() + "', 'admin@test.com', '" + encodedPassword + "', 'Admin', 'ADMIN', true, NOW(), NOW())");

        LoginRequest loginRequest = new LoginRequest("admin@test.com", "admin123");
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            "/api/v1/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        try {
            Map<String, Object> body = objectMapper.readValue(loginResponse.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            adminToken = (String) data.get("token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse login response", e);
        }
        assertThat(adminToken).isNotBlank();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        return headers;
    }

    @Test
    void shouldCreateAndGetCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Test Category", "Description", 1);

        HttpEntity<CreateCategoryRequest> requestEntity = new HttpEntity<>(request, authHeaders());
        ResponseEntity<String> created = restTemplate.exchange(
            "/api/v1/categories", HttpMethod.POST, requestEntity, String.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturn404ForNonExistentCategory() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/v1/categories/" + UUID.randomUUID(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn422ForInvalidCategory() {
        CreateCategoryRequest invalid = new CreateCategoryRequest("", null, -1);

        HttpEntity<CreateCategoryRequest> requestEntity = new HttpEntity<>(invalid, authHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/v1/categories", HttpMethod.POST, requestEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void shouldReturn409ForDuplicateCategoryName() {
        CreateCategoryRequest request = new CreateCategoryRequest("Unique Cat", null, 1);

        HttpEntity<CreateCategoryRequest> requestEntity = new HttpEntity<>(request, authHeaders());
        ResponseEntity<String> first = restTemplate.exchange(
            "/api/v1/categories", HttpMethod.POST, requestEntity, String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> second = restTemplate.exchange(
            "/api/v1/categories", HttpMethod.POST, requestEntity, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
