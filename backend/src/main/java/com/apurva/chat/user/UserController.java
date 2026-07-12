package com.apurva.chat.user;

import com.apurva.chat.storage.FileStorageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/**
 * User directory + profile. Requires a valid token (enforced by SecurityConfig).
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    private final UserRepository users;
    private final FileStorageService storage;

    public UserController(UserRepository users, FileStorageService storage) {
        this.users = users;
        this.storage = storage;
    }

    /** Everyone except the caller — the people you can chat with. */
    @GetMapping
    public List<UserDto> all(Principal principal) {
        return users.findAll().stream()
                .filter(u -> !u.getUsername().equals(principal.getName()))
                .map(u -> new UserDto(u.getUsername(), u.getDisplayName(), u.getAvatarUrl()))
                .toList();
    }

    /** The currently logged-in user's own profile. */
    @GetMapping("/me")
    public UserDto me(Principal principal) {
        AppUser u = users.findByUsername(principal.getName()).orElseThrow();
        return new UserDto(u.getUsername(), u.getDisplayName(), u.getAvatarUrl());
    }

    /** Upload/replace my profile picture. */
    @PostMapping("/me/avatar")
    public UserDto uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal) {
        AppUser u = users.findByUsername(principal.getName()).orElseThrow();
        u.setAvatarUrl(storage.store(file));
        users.save(u);
        return new UserDto(u.getUsername(), u.getDisplayName(), u.getAvatarUrl());
    }
}
