package io.baozhu.dataplatform.source;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class DataSourceRepository {
    private final JdbcTemplate jdbc;
    public DataSourceRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public DataSource save(CreateDataSourceRequest request) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var statement = connection.prepareStatement(
                    "INSERT INTO data_source(name,type,connection_config) VALUES (?,?,?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.name());
            statement.setString(2, request.type().toUpperCase());
            statement.setString(3, request.connectionConfig());
            return statement;
        }, keys);
        return findById(((Number) keys.getKeys().get("ID")).longValue());
    }

    public List<DataSource> findAll() {
        return jdbc.query("SELECT * FROM data_source ORDER BY id DESC", this::map);
    }

    public DataSource findById(Long id) {
        return jdbc.query("SELECT * FROM data_source WHERE id = ?", this::map, id).stream()
                .findFirst().orElseThrow(() -> new io.baozhu.dataplatform.common.ResourceNotFoundException("Data source not found: " + id));
    }

    private DataSource map(ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new DataSource(rs.getLong("id"), rs.getString("name"), rs.getString("type"),
                rs.getString("connection_config"), rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(), rs.getTimestamp("updated_at").toLocalDateTime());
    }
}

