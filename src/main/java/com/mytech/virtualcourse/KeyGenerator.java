package com.mytech.virtualcourse;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // Tạo khóa bí mật phù hợp với HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Mã hóa khóa dưới dạng Base64
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Generated Secret Key: " + secret);
    }
}
