package com.mytech.virtualcourse.security;

import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.repositories.AdminAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminAccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        return AccountDetailsImpl.build(account);
    }
}
