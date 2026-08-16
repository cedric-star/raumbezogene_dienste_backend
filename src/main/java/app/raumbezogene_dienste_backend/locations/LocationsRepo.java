package app.raumbezogene_dienste_backend.locations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
public class LocationsRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getAllAsJson() {
        String sql = """
        SELECT json_agg(
            json_build_object(
                'id', id,
                'title', title,
                'description', description,
                'geo_data', ST_AsGeoJSON(geo_data)::jsonb
            )
        )
        FROM locations
        """;
        try {
            return jdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }
    }
}
