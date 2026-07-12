package com.apurva.chat.auth;

import com.apurva.chat.security.JwtService;
import com.apurva.chat.user.AppUser;
import com.apurva.chat.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Registration, login, and password reset. All PUBLIC (no token needed).
 *
 * Note: we STORE the user's email at registration, but sending real reset
 * emails over SMTP is future scope. For now forgot-password returns the token.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetService passwordResetService;

    public AuthController(UserRepository users,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          PasswordResetService passwordResetService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        if (req.username() == null || req.username().isBlank()
                || req.password() == null || req.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }
        if (users.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        String display = (req.displayName() == null || req.displayName().isBlank())
                ? req.username() : req.displayName();
        AppUser user = new AppUser(req.username(), passwordEncoder.encode(req.password()), display);
        if (req.email() != null && !req.email().isBlank()) {
            user.setEmail(req.email());
        }
        users.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getAvatarUrl());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        AppUser user = users.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getDisplayName(), user.getAvatarUrl());
    }

    /** Step 1 of reset: issue a short-lived token. (Emailing it is future scope.) */
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        if (req.username() == null || !users.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account with that username");
        }
        String token = passwordResetService.createToken(req.username());
        return Map.of(
                "token", token,
                "note", "Demo: token returned. Emailing it over SMTP is future scope.");
    }

    /** Step 2 of reset: exchange a valid token for a new password. */
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestBody ResetPasswordRequest req) {
        if (req.token() == null || req.newPassword() == null || req.newPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token and new password are required");
        }
        String username = passwordResetService.consume(req.token());
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }
        AppUser user = users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        users.save(user);
        return Map.of("message", "Password reset successful. Please log in.");
    }
}
