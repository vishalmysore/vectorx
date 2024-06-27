package com.vishal.myscale;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is just for demo/poc in real world we will be using better connecting pool
 */
public class MyScaleConnection {
    private static MyScaleConnection myScaleConnection;
   private HikariDataSource dataSource;

    private void initDS() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:clickhouse://msc-8cdd15a4.us-east-1.aws.myscale.com:443/default?ssl=true");
        config.setUsername("vishalmysore_org_default");
        config.setPassword(System.getenv("pass"));

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
    private MyScaleConnection() {
        initDS();
    }

    public static MyScaleConnection getMyScaleConnection(){
        if(myScaleConnection == null) {
            myScaleConnection = new MyScaleConnection();
        }
        return myScaleConnection;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
