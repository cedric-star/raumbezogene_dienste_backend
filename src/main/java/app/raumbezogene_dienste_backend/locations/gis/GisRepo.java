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
        JSONObject jo = new JSONObject(body);
        if (!jo.has("name")) { return ResponseEntity.badRequest().body("no geo function given, use key <name> for that"); }

        boolean existsName = false;
        JSONArray functions = metaData.getJSONArray("functions");
        for (int i = 0; i < functions.length(); i++) {
            JSONObject obj = functions.getJSONObject(i);
            if (obj.getString("name").equals(jo.getString("name"))) {
                existsName = true;
                break;
            }
        }
        if (!existsName) { return ResponseEntity.badRequest().body("function with name: <" + jo.getString("name") + "> doesnt exists"); }



        return null;
    }
}
