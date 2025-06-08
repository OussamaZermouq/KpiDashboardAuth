package com.inwi.KpiDashboardAuth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

public class DatabaseConfig {


    @Value("spring.datasource.url")
    private String jdbcUrl;
    @Value("spring.datasource.username")
    private String username;
    @Value("spring.datasource.password")

    private String password;
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        //to keep some opened connections
        config.setMinimumIdle(2);
        config.setMaxLifetime(300000);        // 5 minutes
        config.setConnectionTimeout(30000);   // 30 seconds
        config.setIdleTimeout(300000);        // 5 minutes
        //since we are using Neon as DB on FREE plan we need to make sure to not exceed 10 on the pool size
        config.setMaximumPoolSize(8);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }
}