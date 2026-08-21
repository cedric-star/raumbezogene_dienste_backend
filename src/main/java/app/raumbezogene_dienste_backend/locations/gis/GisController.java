package app.raumbezogene_dienste_backend.locations.gis;


import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/gis")
public class GisController {

    @Autowired
    GisRepo gisRepo;

    private static final Logger log = LoggerFactory.getLogger(GisController.class);



    @GetMapping("/metadata")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> getMetaData() {
        String metadata = gisRepo.getMetaDataStr();

        if (metadata.isEmpty() || metadata.isBlank()) {
            return ResponseEntity.internalServerError().body("no file could be loaded");
        }
        return ResponseEntity.ok(metadata);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> execGisFunction(@RequestBody String body) {
        return gisRepo.execGis(body);
    }
}
