package com.example.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class MurmurHash {
    public static void main(String[] args) {
        HashCode hashCode = Hashing.murmur3_128().hashString("hello word", StandardCharsets.UTF_8);
        System.out.println(hashCode.toString());
    }
}
