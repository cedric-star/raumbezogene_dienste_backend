package app.raumbezogene_dienste_backend.locations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locations")
public class LocationsController {
    @Autowired
    LocationsRepo locationsRepo;

    @GetMapping("/getall")
    public String getAllAsJson() {
        return locationsRepo.getAllAsJson();
    }

    @GetMapping("/getsafe")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN'")
    public String getAllAsJsonSafe() {
        return locationsRepo.getAllAsJson();
    }

}
