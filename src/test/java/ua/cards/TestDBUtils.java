package ua.cards;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBUtils {
    private static JdbcTemplate template;

    public static final void clearDB(DataSource dataSource) throws SQLException {
        if (template == null)
            template = new JdbcTemplate(dataSource);
        template.update("delete from words");
        template.update("delete from groups");
        template.update("delete from users");
//        try (Connection connection = dataSource.getConnection();
//             Statement statement = connection.createStatement()) {
//            statement.executeUpdate("delete from words");
//            statement.executeUpdate("delete from groups");
//            statement.executeUpdate("delete from users");
//            statement.close();
//            connection.commit();
//        }

    }
}
