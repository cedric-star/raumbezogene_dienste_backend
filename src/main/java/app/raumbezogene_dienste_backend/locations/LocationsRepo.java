package app.raumbezogene_dienste_backend.locations;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Repository
public class LocationsRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseEntity<String> getAllAsJson() {
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
            return ResponseEntity.ok(jdbcTemplate.queryForObject(sql, String.class));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<String> insertNewFromJson(String body) {
        if (body == null || body.isBlank()) {
            return ResponseEntity.badRequest().body("no body givent");
        }
        JSONObject jo = new JSONObject(body);

        //check object
        if (!jo.has("title")) return ResponseEntity.badRequest().body("<title> fehlt!");
        String title = jo.getString("title");

        if (!jo.has("description")) {jo.put("description", "");}
        String description = jo.getString("description");

        if (!jo.has("geo_data")) return ResponseEntity.badRequest().body("<geo_data> fehlt!");
        String geo_data = jo.getJSONObject("geo_data").toString();

        //check for existing
        String checkForExisting = "SELECT COUNT(*) FROM locations WHERE title = ?";
        Integer count = jdbcTemplate.queryForObject(checkForExisting, Integer.class, title);
        if (count != null && count > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("location with title: <" + title + "> already exists");
        }

        String sql = """
        INSERT INTO locations (title, description, geo_data)
        VALUES (?, ?, ST_GeomFromGeoJSON(?))
        """;

        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    title,
                    description,
                    geo_data
                    );
            if (rowsAffected > 0) {return ResponseEntity.status(HttpStatus.CREATED).body("insert successful"); }
            else { return ResponseEntity.internalServerError().body("no rows are affected"); }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Exception while executing sql: " + e.getMessage());
        }
    }

    public ResponseEntity<String> updateExistingFromJson(String body) {
        if (body == null || body.isBlank()) {
            return ResponseEntity.badRequest().body("no body givent");
        }
        JSONObject jo = new JSONObject(body);

        //id check
        if (!jo.has("id")) { return ResponseEntity.badRequest().body("no id given"); }
        int id = jo.getInt("id");

        // existenz check
        String checkForExisting = "SELECT COUNT(*) FROM locations WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkForExisting, Integer.class, id);
        if (count == null || count <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("location with id: <" + id + "> does not exist");
        }

        LinkedHashMap<String, String> setClauses = new LinkedHashMap<>();
        List<Object> params = new ArrayList<>();

        if (jo.has("title") && !jo.isNull("title") && !jo.getString("title").isBlank()) {
            setClauses.put("title", "title = ?");
            params.add(jo.getString("title"));
        }
        if (jo.has("description") && !jo.isNull("description") && !jo.getString("description").isBlank()) {
            setClauses.put("description", "description = ?");
            params.add(jo.getString("description"));
        }
        if (jo.has("geo_data") && !jo.isNull("geo_data")) {
            setClauses.put("geo_data", "geo_data = ST_GeomFromGeoJSON(?)");
            params.add(jo.getJSONObject("geo_data").toString());
        }

        if (setClauses.isEmpty()) {
            return ResponseEntity.badRequest().body("no attributes match");
        }

        String sql = "UPDATE locations SET "
                + String.join(", ", setClauses.values())
                + " WHERE id = ?";
        params.add(id);

        try {
            int rowsAffected = jdbcTemplate.update(sql, params.toArray());
            if (rowsAffected > 0) {
                return ResponseEntity.ok("update successful");
            } else {
                return ResponseEntity.internalServerError().body("no rows are affected");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("exception while executing sql: " + e.getMessage());
        }
    }



}
