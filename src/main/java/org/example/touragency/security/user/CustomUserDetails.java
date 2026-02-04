package org.example.touragency.security.user;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import org.example.touragency.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    @OneToOne
    @JoinColumn(name = "user_id")
    private final User user;
    private final UUID id;
    private final String email;
    private final String password;
    private final String role;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
