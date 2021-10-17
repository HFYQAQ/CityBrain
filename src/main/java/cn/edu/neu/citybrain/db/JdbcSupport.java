package cn.edu.neu.citybrain.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSupport {
    private Connection conn = null;
    private PreparedStatement statement = null;
    public static int cnt;

    public JdbcSupport() {
        cnt++;
        this.conn = DBConnection.getConnection();
    }

    public <T> List<T> query(String sql, Class<?> clazz, List<String> fieldList, Object... parameters) {
        List<T> results = null;

        try {
            this.statement = this.conn.prepareStatement(sql);
            formatPS(this.statement, parameters);
            ResultSet resultSet = this.statement.executeQuery();
            results = resolveRS(resultSet, clazz, fieldList);
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void formatPS(PreparedStatement statement, Object... parameters) throws SQLException {
        if (statement == null) {
            return;
        }

        statement.clearParameters();
        for (int i = 1; i <= parameters.length; i++) {
            statement.setObject(i, parameters[i - 1]);
        }
    }

    private <T> List<T> resolveRS(ResultSet resultSet, Class<?> clazz, List<String> fieldList) throws InstantiationException, IllegalAccessException, SQLException, NoSuchFieldException {
        if (resultSet == null) {
            return new ArrayList<>();
        }

        List<T> results = new ArrayList<>();

        while (resultSet.next()) {
            T result = (T) clazz.newInstance();
            for (String fieldName : fieldList) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object obj = resultSet.getObject(fieldName);
                if (obj instanceof Integer) {
                    field.set(result, ((Integer) obj).longValue());
                } else {
                    field.set(result, obj);
                }
            }
            results.add(result);
        }

        return results;
    }

    public void close() {
        try {
            if (this.statement != null) {
                this.statement.close();
            }
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
