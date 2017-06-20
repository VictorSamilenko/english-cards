package ua.cards.config.dataSources;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Configuration
@Profile("test")
public class TestDataConfig {
    @Bean
    public DataSource configureDataSource() {
        System.out.println("H2 test config");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:~/test");
        config.setUsername("root");
        config.setPassword("root");

        return new HikariDataSource(config);
    }

    @Bean(name = "hibProperties")
    public Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto","create");
        return properties;
    }

    @Bean
    public Connection connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test;", "root", "root");
        try {
            Statement statement = connection.createStatement();
            String sql =
                    "DROP TRIGGER if EXISTS check_delete_translate; CREATE TRIGGER check_delete_translate " +
                            "BEFORE DELETE " +
                            "ON words " +
                            "FOR EACH ROW " +
                            "CALL \"ua.cards.H2Trigger\"";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
