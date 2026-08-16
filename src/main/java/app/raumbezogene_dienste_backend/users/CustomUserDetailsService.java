package app.raumbezogene_dienste_backend.users;

import app.raumbezogene_dienste_backend.jwt.AuthController.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User nicht gefunden: " + username));
        return new CustomUserDetails(user);
    }

    public UserEntity registerUser(RegisterRequest request) {
        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Nutzername bereits vergeben");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hashing hier
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        return userRepository.save(user);
    }
}
