package app.raumbezogene_dienste_backend.jwt;

import app.raumbezogene_dienste_backend.users.CustomUserDetailsService;
import app.raumbezogene_dienste_backend.users.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getName(),
                            authRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateToken(authRequest.getName());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            UserEntity user = userService.registerUser(request);
            return ResponseEntity.ok("User <" + user.getName() + "> added");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    //requestbodies als json werden direkt in instanzen der folgenden klassen umgebaut
    public static class AuthRequest {
        private String name;
        private String password;

        public String getName() { return name; }
        public void setUsername(String username) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        public AuthResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }

    public static class RegisterRequest {
        private String name;
        private String password;
        private String role; // "USER" oder "ADMIN"

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
