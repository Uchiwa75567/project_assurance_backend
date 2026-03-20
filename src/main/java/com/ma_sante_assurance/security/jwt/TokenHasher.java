package com.ma_sante_assurance.security.jwt;

public interface TokenHasher {
    String hash(String value);
}
