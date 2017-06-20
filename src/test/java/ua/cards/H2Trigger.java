package ua.cards;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2Trigger implements Trigger {

    @Override
    public void init(Connection connection, String s, String s1, String s2, boolean b, int i) throws SQLException {
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRew) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "delete from word_translate where (word_translate.translate_word_id = ?) or (word_translate.native_word_id = ?);"
        )) {
            stmt.setObject(1, oldRow[0] );
            stmt.setObject(2, oldRow[0]);
            stmt.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public void remove() throws SQLException {
    }
}
