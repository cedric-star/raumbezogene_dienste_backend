package app.raumbezogene_dienste_backend.locations.gis;

import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GisRepo {
    private static final Logger log = LoggerFactory.getLogger(GisRepo.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String metaDataStr;
    private JSONObject metaData;

    @PostConstruct
    private void init() {
        try (InputStream is = new ClassPathResource("data/metadaten.json").getInputStream()) {
            this.metaDataStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            log.info("loaded static resource metadata.json");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        this.metaData = new JSONObject(this.metaDataStr);
    }
    public String getMetaDataStr() { return metaDataStr; }

    public ResponseEntity<String> execGis(String body) {
        JSONObject input = new JSONObject(body);
        if (!input.has("name")) { return ResponseEntity.badRequest().body("no geo function given, use key <name> for that"); }
        if (!input.has("inputs")) { return ResponseEntity.badRequest().body("inputs given, use key <inputs> for that"); }


        boolean existsName = false;
        JSONObject function2Exec = null;
        JSONArray functions = metaData.getJSONArray("functions");
        for (int i = 0; i < functions.length(); i++) {
            JSONObject obj = functions.getJSONObject(i);
            if (obj.getString("name").equals(input.getString("name"))) {
                existsName = true;
                function2Exec = obj;
                break;
            }
        }
        if (!existsName) { return ResponseEntity.badRequest().body("function with name: <" + input.getString("name") + "> doesnt exists"); }

        ArrayList<String> inputIds = new ArrayList<>(2);
        try {
            for (int i = 0; i < input.getJSONArray("inputs").length(); i++) {
                inputIds.add(Integer.toString(input.getJSONArray("inputs").getInt(i)));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("inputs must contain only Integer numbers");
        }

        String innerSql = "";
        innerSql = inputIds.stream().map(id -> {
            return String.format("(SELECT geo_data::geometry FROM locations WHERE id = %s)", id);
        }).collect(Collectors.joining(", "));

        String sql;
        String fnName = function2Exec.getString("name");


        if (function2Exec.getBoolean("returnsObj")) {
            sql = String.format("SELECT ST_AsGeoJSON(%s(%s)) AS result;", fnName, innerSql);
        } else {
            sql = String.format("SELECT %s(%s) AS result", fnName, innerSql);
        }

        log.info("executing gis query: {}", sql);

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql);
            Object result = row.get("result");
            log.info(result.toString());
            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            log.error("couldnt execute query: {}, error: {}", sql, e.getMessage());
            return ResponseEntity.internalServerError().body("query failed");
        }

    }
}
