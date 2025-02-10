package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Account account;
    private Long studentId; // ✅ Thêm studentId

    public CustomUserDetails(Account account) {
        this.account = account;
        this.studentId = (account.getStudent() != null) ? account.getStudent().getId() : null;
    }

    public Account getAccount() {
        return account;
    }

    public Long getAccountId() {
        return account.getId();
    }

    public Long getStudentId() {
        return studentId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
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
