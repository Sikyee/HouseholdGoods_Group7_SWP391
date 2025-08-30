package Security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Password hashing utility class
 * SECURITY WARNING: MD5 is included for educational purposes only
 * Use BCrypt, Argon2, or PBKDF2 in production applications
 */
public class PasswordHasher {
    
    // DEPRECATED: MD5 hashing (for educational purposes only)
    @Deprecated
    public static String hashMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
    
    // BETTER: SHA-256 with salt (still not recommended for passwords)
    public static String hashSHA256WithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    // Generate random salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // RECOMMENDED: PBKDF2 (Password-Based Key Derivation Function 2)
    public static String hashPBKDF2(String password, String salt, int iterations) {
        try {
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), 
                salt.getBytes(StandardCharsets.UTF_8), 
                iterations, 
                256
            );
            javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 hashing failed", e);
        }
    }
    
    // Verify PBKDF2 password
    public static boolean verifyPBKDF2(String password, String salt, String expectedHash, int iterations) {
        String actualHash = hashPBKDF2(password, salt, iterations);
        return actualHash.equals(expectedHash);
    }
    
    // Simple BCrypt alternative using PBKDF2
    public static class SecurePasswordHash {
        private final String salt;
        private final String hash;
        private final int iterations;
        
        public SecurePasswordHash(String password) {
            this.salt = generateSalt();
            this.iterations = 100000; // Recommended minimum
            this.hash = hashPBKDF2(password, salt, iterations);
        }
        
        public SecurePasswordHash(String salt, String hash, int iterations) {
            this.salt = salt;
            this.hash = hash;
            this.iterations = iterations;
        }
        
        public boolean verify(String password) {
            return verifyPBKDF2(password, salt, hash, iterations);
        }
        
        public String getSalt() { return salt; }
        public String getHash() { return hash; }
        public int getIterations() { return iterations; }
        
        // Format: salt:iterations:hash
        public String serialize() {
            return salt + ":" + iterations + ":" + hash;
        }
        
        public static SecurePasswordHash deserialize(String serialized) {
            String[] parts = serialized.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid serialized format");
            }
            return new SecurePasswordHash(parts[0], parts[2], Integer.parseInt(parts[1]));
        }
    }
}