package com.vishal.myscale;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is just for demo/poc in real world we will be using better connecting pool
 */

@Configuration
@Log
public class MyScaleConnection {
    @Value("${clickhouse.url}")
    private String jdbcUrl;

    @Value("${clickhouse.username}")
    private String username;

    @Value("${clickhouse.password}")
    private String password;
    public MyScaleConnection() {

    }
   private HikariDataSource dataSource;
   @Autowired

    private void initDS() {
       HikariConfig config = new HikariConfig();
       config.setJdbcUrl(jdbcUrl);
       config.setUsername(username);
       config.setPassword(password);

        // Set the maximum lifetime of a connection in the pool in milliseconds (e.g., 30 minutes)
        config.setMaxLifetime(1800000);

        // Set the maximum amount of time a connection is allowed to sit idle in the pool (e.g., 10 minutes)
        config.setIdleTimeout(600000);

        // Set the minimum number of idle connections that HikariCP tries to maintain in the pool
        config.setMinimumIdle(2);

        // Set the maximum size that the pool is allowed to reach, including both idle and in-use connections
        config.setMaximumPoolSize(10);

         dataSource = new HikariDataSource(config);
         // The HikariDataSource will automatically close idle connections after the specified timeout
    }




    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
