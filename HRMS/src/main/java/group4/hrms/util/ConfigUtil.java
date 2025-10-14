package group4.hrms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class để đọc configuration từ application.properties
 */
public class ConfigUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static final Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input != null) {
                properties.load(input);
                logger.info("Loaded configuration from application.properties");
            } else {
                logger.error("Unable to find application.properties");
            }
            
        } catch (IOException ex) {
            logger.error("Error loading application.properties", ex);
        }
    }
    
    /**
     * Lấy property value theo key
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Lấy property value theo key với default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Lấy property value dạng int
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}", key, properties.getProperty(key));
            return defaultValue;
        }
    }
    
    /**
     * Lấy property value dạng boolean
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // Google OAuth Configuration
    public static String getGoogleOAuthClientId() {
        return getProperty("google.oauth.client.id");
    }
    
    public static String getGoogleOAuthClientSecret() {
        return getProperty("google.oauth.client.secret");
    }
    
    public static String getGoogleOAuthRedirectUri() {
        return getProperty("google.oauth.redirect.uri");
    }
    
    public static String getGoogleOAuthAuthUri() {
        return getProperty("google.oauth.auth.uri");
    }
    
    public static String getGoogleOAuthTokenUri() {
        return getProperty("google.oauth.token.uri");
    }
    
    public static String getGoogleOAuthUserInfoUri() {
        return getProperty("google.oauth.userinfo.uri");
    }
    
    public static String getGoogleOAuthScopes() {
        return getProperty("google.oauth.scopes");
    }
    
    // BCrypt Configuration
    public static int getBCryptCost() {
        return getIntProperty("bcrypt.cost", 10);
    }
    
    // Session Configuration
    public static int getSessionTimeout() {
        return getIntProperty("session.timeout", 1800);
    }
    
    public static boolean isSessionSecure() {
        return getBooleanProperty("session.secure", false);
    }
    
    public static boolean isSessionHttpOnly() {
        return getBooleanProperty("session.http.only", true);
    }
    
    // CSRF Configuration
    public static String getCsrfTokenName() {
        return getProperty("csrf.token.name", "_csrf_token");
    }
    
    public static String getCsrfTokenHeader() {
        return getProperty("csrf.token.header", "X-CSRF-TOKEN");
    }
}