package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.EAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    // Thêm getter cho Account nếu cần thiết
    private Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Thêm tiền tố ROLE_
        return account.getRoles().stream()
                .map(Role::getName)
                .map(roleName -> "ROLE_" + roleName) // Thêm tiền tố ROLE_
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return account.getStatus() != EAccountStatus.INACTIVE &&
                account.getStatus() != EAccountStatus.BANNED &&
                account.getStatus() != EAccountStatus.SUSPENDED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return account.getStatus() != EAccountStatus.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return account.getStatus() != EAccountStatus.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        return account.getStatus() == EAccountStatus.ACTIVE;
    }

}