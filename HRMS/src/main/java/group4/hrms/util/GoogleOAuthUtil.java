package group4.hrms.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class để xử lý Google OAuth2 authentication
 */
public class GoogleOAuthUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * DTO để chứa thông tin user từ Google
     */
    public static class GoogleUserInfo {
        private String id;
        private String email;
        private String name;
        private String picture;
        private boolean emailVerified;
        
        // Constructors
        public GoogleUserInfo() {}
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
        
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
        
        @Override
        public String toString() {
            return "GoogleUserInfo{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    ", emailVerified=" + emailVerified +
                    '}';
        }
    }
    
    /**
     * Tạo URL để redirect user đến Google OAuth
     */
    public static String createAuthorizationUrl(String state) {
        try {
            String clientId = ConfigUtil.getGoogleOAuthClientId();
            String redirectUri = ConfigUtil.getGoogleOAuthRedirectUri();
            String scope = ConfigUtil.getGoogleOAuthScopes();
            String authUri = ConfigUtil.getGoogleOAuthAuthUri();
            
            StringBuilder url = new StringBuilder(authUri);
            url.append("?response_type=code");
            url.append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8));
            url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
            url.append("&scope=").append(URLEncoder.encode(scope, StandardCharsets.UTF_8));
            url.append("&access_type=offline");
            url.append("&include_granted_scopes=true");
            
            if (state != null && !state.trim().isEmpty()) {
                url.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8));
            }
            
            logger.debug("Created Google OAuth authorization URL");
            return url.toString();
            
        } catch (Exception e) {
            logger.error("Error creating Google OAuth authorization URL", e);
            throw new RuntimeException("Error creating authorization URL", e);
        }
    }
    
    /**
     * Generate random state parameter để bảo mật OAuth flow
     */
    public static String generateState() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Exchange authorization code để lấy access token
     */
    public static String exchangeCodeForAccessToken(String authorizationCode) throws IOException, ParseException {
        String clientId = ConfigUtil.getGoogleOAuthClientId();
        String clientSecret = ConfigUtil.getGoogleOAuthClientSecret();
        String redirectUri = ConfigUtil.getGoogleOAuthRedirectUri();
        String tokenUri = ConfigUtil.getGoogleOAuthTokenUri();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUri);
            
            // Set request parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("code", authorizationCode));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("redirect_uri", redirectUri));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));
            
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() != 200) {
                    logger.error("Error exchanging code for token. Status: {}, Body: {}", 
                            response.getCode(), responseBody);
                    throw new IOException("Failed to exchange authorization code for access token");
                }
                
                // Parse JSON response
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String accessToken = jsonNode.get("access_token").asText();
                
                logger.debug("Successfully exchanged authorization code for access token");
                return accessToken;
            }
        }
    }
    
    /**
     * Lấy thông tin user từ Google bằng access token
     */
    public static GoogleUserInfo getUserInfo(String accessToken) throws IOException, ParseException {
        String userInfoUri = ConfigUtil.getGoogleOAuthUserInfoUri();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(userInfoUri);
            httpGet.setHeader("Authorization", "Bearer " + accessToken);
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (response.getCode() != 200) {
                    logger.error("Error getting user info. Status: {}, Body: {}", 
                            response.getCode(), responseBody);
                    throw new IOException("Failed to get user information from Google");
                }
                
                // Parse JSON response
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                GoogleUserInfo userInfo = new GoogleUserInfo();
                userInfo.setId(jsonNode.get("id").asText());
                userInfo.setEmail(jsonNode.get("email").asText());
                userInfo.setName(jsonNode.get("name").asText());
                userInfo.setEmailVerified(jsonNode.get("verified_email").asBoolean());
                
                if (jsonNode.has("picture")) {
                    userInfo.setPicture(jsonNode.get("picture").asText());
                }
                
                logger.debug("Successfully retrieved user info from Google: {}", userInfo);
                return userInfo;
            }
        }
    }
}