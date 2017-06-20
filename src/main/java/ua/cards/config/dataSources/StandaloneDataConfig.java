package ua.cards.config.dataSources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("dev")
public class StandaloneDataConfig {
    @Bean
    public DataSource configureDataSource() {
        System.out.println("standart config");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("postgres");

        return new HikariDataSource(config);
    }

    @Bean
    public Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL81Dialect");
        properties.put("hibernate.show_sql", "true");

        return properties;
    }
}
