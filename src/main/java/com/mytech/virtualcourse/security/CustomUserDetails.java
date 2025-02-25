package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    @Getter
    private Long accountId;
    @Getter
    private Long studentId;
    @Getter
    private Long instructorId;
    @Getter
    private String email;
    private List<GrantedAuthority> authorities;
    private String username;
    private String password;
    private boolean isEnabled;
    private boolean isAccountNonLocked;
    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;


    public CustomUserDetails(Account account) {
        this.accountId = account.getId();
        this.email = account.getEmail();
        this.studentId = (account.getStudent() != null) ? account.getStudent().getId() : null;
        this.instructorId = (account.getInstructor() != null) ? account.getInstructor().getId() : null;
        this.authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.isEnabled = account.getStatus() == EAccountStatus.ACTIVE;
        this.isAccountNonLocked = account.getStatus() != EAccountStatus.BANNED;
        this.isAccountNonExpired = account.getStatus() != EAccountStatus.INACTIVE &&
                account.getStatus() != EAccountStatus.BANNED &&
                account.getStatus() != EAccountStatus.SUSPENDED;
        this.isCredentialsNonExpired = account.getStatus() != EAccountStatus.SUSPENDED;
    }

    public CustomUserDetails(Long accountId, Long studentId, Long instructorId, Collection<? extends GrantedAuthority> authorities) {
        this.accountId = accountId;
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.authorities = List.copyOf(authorities);
        this.username = accountId != null ? "user_" + accountId : "unknown_user";
        this.password = null;
        this.isEnabled = true;
        this.isAccountNonLocked = true;
        this.isAccountNonExpired = true;
        this.isCredentialsNonExpired = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
