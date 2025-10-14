package group4.hrms.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes for HRMS system
 * Can be used to create secure password hashes for new users
 */
public class HashGenerator {
    
    /**
     * Generate BCrypt hash for a password
     * @param password Plain text password
     * @param cost Cost factor (recommended: 10-12)
     * @return BCrypt hash
     */
    public static String generateHash(String password, int cost) {
        return BCrypt.hashpw(password, BCrypt.gensalt(cost));
    }
    
    /**
     * Generate BCrypt hash with default cost factor (10)
     * @param password Plain text password
     * @return BCrypt hash
     */
    public static String generateHash(String password) {
        return generateHash(password, 10);
    }
    
    /**
     * Verify if password matches the hash
     * @param password Plain text password
     * @param hash BCrypt hash
     * @return true if password matches hash
     */
    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
    
    /**
     * Command line utility to generate password hashes
     * Usage: java HashGenerator <password> [cost]
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("=== HRMS Password Hash Generator ===");
            System.out.println("Usage: java HashGenerator <password> [cost]");
            System.out.println("Example: java HashGenerator mypassword123 10");
            System.out.println();
            System.out.println("Default cost: 10 (recommended for production)");
            return;
        }
        
        String password = args[0];
        int cost = (args.length > 1) ? Integer.parseInt(args[1]) : 10;
        
        System.out.println("=== HRMS Password Hash Generator ===");
        System.out.println("Password: " + password);
        System.out.println("Cost Factor: " + cost);
        System.out.println();
        
        // Generate hash
        String hash = generateHash(password, cost);
        
        System.out.println("Generated BCrypt Hash:");
        System.out.println(hash);
        System.out.println();
        
        // Verify the hash works
        boolean isValid = verifyPassword(password, hash);
        System.out.println("Verification Test: " + (isValid ? "✅ SUCCESS" : "❌ FAILED"));
        System.out.println();
        
        // Provide SQL statement example
        System.out.println("Example SQL to update password in auth_local_credentials:");
        System.out.println("UPDATE auth_local_credentials SET password_hash = '" + hash + "' WHERE identity_id = ?;");
        System.out.println();
        
        System.out.println("💡 Tip: Store this hash in auth_local_credentials table, NOT in plain text!");
    }
}