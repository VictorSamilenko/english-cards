package ua.cards.config.dataSources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

@Configuration
@Profile("heroku")
public class HerokuDataConfig {

    @Bean
    public DataSource configureHerokuDataSource() throws URISyntaxException {
        Map<String, String> map = System.getenv();

        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        System.out.println("heroku config");
        System.out.println(dbUri);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://"+dbUri.getHost()+":"+dbUri.getPort() + dbUri.getPath());
        config.setUsername(dbUri.getUserInfo().split(":")[0]);
        config.setPassword(dbUri.getUserInfo().split(":")[1]);

        return new HikariDataSource(config);
    }

    @Bean
    public Properties hibProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL81Dialect");
        properties.put("hibernate.show_sql", "false");
        return properties;
    }
}
