package app.raumbezogene_dienste_backend.locations.gis;


import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/gis")
public class GisController {

    private static final Logger log = LoggerFactory.getLogger(GisController.class);
    private String metadata;


    public GisController() {
        try (InputStream is = new ClassPathResource("data/metadaten.json").getInputStream()) {
            this.metadata = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            log.info("loaded static resource metadata.json");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @GetMapping("/metadata")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> getMetaData() {
        if (this.metadata.isEmpty() || this.metadata.isBlank()) {
            return ResponseEntity.internalServerError().body("no file could be loaded");
        }
        return ResponseEntity.ok(metadata);
    }
}
