package app.raumbezogene_dienste_backend.locations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    LocationsRepo locationsRepo;

    @GetMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> getAllAsJson() { return locationsRepo.getAllAsJson(); }

    @PostMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> postAsJson(@RequestBody String body) { return locationsRepo.insertNewFromJson(body); }

    @PatchMapping()
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> patchFromJson(@RequestBody String body) { return locationsRepo.updateExistingFromJson(body); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteById(@PathVariable int id) { return locationsRepo.deleteById(id); }
}
