package com.example.demo.entities;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * User Entity
 * -----------------------------------------------------
 * ✔ Representa un usuario del sistema
 * ✔ Implementa UserDetails para integración con Spring Security
 * ✔ Incluye un único rol de aplicación: ROLE_ADMIN
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_ADMIN";

    // ✅ Relación opcional con Student
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Student student;

    // ✅ Relación opcional con Teacher
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Teacher teacher;

    /**
     * ✅ Devuelve la lista de roles del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Devuelve siempre una autoridad, para evitar problemas si el campo está vacío
        String effectiveRole = (role != null && !role.isBlank()) ? role : "ROLE_ADMIN";
        return List.of(new SimpleGrantedAuthority(effectiveRole));
    }

    /**
     * ✅ El username para Spring Security será el email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * ✅ Indica si la cuenta está activa
     */
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

