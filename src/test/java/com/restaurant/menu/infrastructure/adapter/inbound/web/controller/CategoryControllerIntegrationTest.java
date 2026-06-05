package com.restaurant.menu.infrastructure.adapter.inbound.web.controller;

import com.restaurant.menu.infrastructure.adapter.inbound.web.dto.request.CreateCategoryRequest;
import com.restaurant.menu.shared.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE menu.menu_item_allergens, menu.menu_item_modifier_groups, menu.menu_item_ingredients, menu.nutritional_info, menu.menu_sections, menu.modifier_options, menu.modifier_groups, menu.menu_items, menu.categories, menu.ingredients, menu.allergens CASCADE");
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class SecurityPermitAll {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void shouldCreateAndGetCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Test Category", "Description", 1);

        ResponseEntity<ApiResponse> created = restTemplate.postForEntity(
            "/api/v1/categories", request, ApiResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
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

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/v1/categories", invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void shouldReturn409ForDuplicateCategoryName() {
        CreateCategoryRequest request = new CreateCategoryRequest("Unique Cat", null, 1);

        ResponseEntity<ApiResponse> first = restTemplate.postForEntity(
            "/api/v1/categories", request, ApiResponse.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> second = restTemplate.postForEntity(
            "/api/v1/categories", request, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
