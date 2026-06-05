package com.restaurant.menu.infrastructure.config;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1000)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .build();
            }
            return Health.down()
                .withDetail("database", "Connection invalid")
                .build();
        } catch (Exception e) {
            return Health.down(e)
                .withDetail("database", "PostgreSQL")
                .build();
        }
    }
}
