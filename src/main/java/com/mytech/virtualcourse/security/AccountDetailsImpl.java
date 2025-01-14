// src/main/java/com/mytech/virtualcourse/security/AccountDetailsImpl.java
package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;
    private boolean enable;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Phương thức để xây dựng AccountDetailsImpl từ đối tượng Account.
     *
     * @param account đối tượng Account
     * @return AccountDetailsImpl
     */
    public static AccountDetailsImpl build(Account account) {
        Set<ERole> roles = account.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        List<GrantedAuthority> authorities = roles.stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.name())) // Thêm "ROLE_" prefix
                .collect(Collectors.toList());

        return new AccountDetailsImpl(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getPassword(),
                account.getEnable(),
                authorities
        );
    }

    /**
     * Trả về trạng thái tài khoản không hết hạn.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Bạn có thể thêm logic tùy chỉnh nếu cần
    }

    /**
     * Trả về trạng thái tài khoản không bị khóa.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Bạn có thể thêm logic tùy chỉnh nếu cần
    }

    /**
     * Trả về trạng thái thông tin đăng nhập không hết hạn.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Bạn có thể thêm logic tùy chỉnh nếu cần
    }

    /**
     * Trả về trạng thái tài khoản có được kích hoạt hay không.
     */
    @Override
    public boolean isEnabled() {
        return this.enable;
    }
}
