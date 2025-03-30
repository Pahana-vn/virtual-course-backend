//package com.mytech.virtualcourse.configs;
//
//import com.mytech.virtualcourse.entities.Account;
//import com.mytech.virtualcourse.entities.Role;
//import com.mytech.virtualcourse.enums.AuthenticationType;
//import com.mytech.virtualcourse.enums.EAccountStatus;
//import com.mytech.virtualcourse.repositories.AccountRepository;
//import com.mytech.virtualcourse.repositories.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        if (!roleRepository.existsByName("ADMIN")) {
//            Role adminRole = new Role();
//            adminRole.setName("ADMIN");
//            adminRole.setDescription("Administrator role with all permissions");
//            roleRepository.save(adminRole);
//        }
//
//        if (!roleRepository.existsByName("STUDENT")) {
//            Role studentRole = new Role();
//            studentRole.setName("STUDENT");
//            studentRole.setDescription("Student role with limited permissions");
//            roleRepository.save(studentRole);
//        }
//
//        if (!roleRepository.existsByName("INSTRUCTOR")) {
//            Role instructorRole = new Role();
//            instructorRole.setName("INSTRUCTOR");
//            instructorRole.setDescription("Instructor role with permissions to create and manage courses");
//            roleRepository.save(instructorRole);
//        }
//
////        boolean adminExists = accountRepository.existsByRoleName("ADMIN");
////        if (!adminExists) {
////            Role adminRole = roleRepository.findByName("ADMIN")
////                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
////
////            Account admin = new Account();
////            admin.setUsername("admin");
////            admin.setEmail("admin@gmail.com");
////            admin.setPassword(passwordEncoder.encode("123456"));
////            admin.setStatus(EAccountStatus.ACTIVE);
////            admin.setVerifiedEmail(true);
////            admin.setVersion(1);
////            admin.setAuthenticationType(AuthenticationType.LOCAL);
////            admin.setRoles(Collections.singletonList(adminRole));
////
////            accountRepository.save(admin);
////        }
//
//        // Create the admin_only account with ALL roles if it doesn't exist
////        if (!accountRepository.existsByEmail("admin_only@gmail.com")) {
////            Role adminRole = roleRepository.findByName("ADMIN")
////                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
////            Role instructorRole = roleRepository.findByName("INSTRUCTOR")
////                    .orElseThrow(() -> new RuntimeException("Error: Role INSTRUCTOR is not found."));
////            Role studentRole = roleRepository.findByName("STUDENT")
////                    .orElseThrow(() -> new RuntimeException("Error: Role STUDENT is not found."));
////
////            Account adminOnly = new Account();
////            adminOnly.setUsername("admin_only");
////            adminOnly.setEmail("admin_only@gmail.com");
////            adminOnly.setPassword(passwordEncoder.encode("123456"));
////            adminOnly.setStatus(EAccountStatus.ACTIVE);
////            adminOnly.setVerifiedEmail(true);
////            adminOnly.setVersion(1);
////            adminOnly.setAuthenticationType(AuthenticationType.LOCAL);
////
////            // Assign ALL roles to admin_only
////            List<Role> allRoles = new ArrayList<>();
////            allRoles.add(adminRole);
//////            allRoles.add(instructorRole);
//////            allRoles.add(studentRole);
////            adminOnly.setRoles(allRoles);
////
////            accountRepository.save(adminOnly);
////        }
//    }
//}