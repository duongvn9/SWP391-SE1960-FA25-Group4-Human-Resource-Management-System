package group4.hrms.dto;

/**
 * DTO cho việc đăng nhập
 * Chứa thông tin cần thiết cho authentication
 */
public class LoginDto {
    private String username;
    private String password;
    private String email;
    private Boolean rememberMe;

    // Constructors
    public LoginDto() {}

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
        this.rememberMe = false;
    }

    public LoginDto(String username, String password, Boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    // Static factory methods
    public static LoginDto withUsername(String username, String password) {
        return new LoginDto(username, password);
    }

    public static LoginDto withEmail(String email, String password) {
        LoginDto dto = new LoginDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRememberMe(false);
        return dto;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    // Business methods
    public boolean isLoginByUsername() {
        return this.username != null && !this.username.trim().isEmpty();
    }

    public boolean isLoginByEmail() {
        return this.email != null && !this.email.trim().isEmpty();
    }

    public String getLoginIdentifier() {
        return isLoginByUsername() ? this.username : this.email;
    }

    public boolean hasValidCredentials() {
        return (isLoginByUsername() || isLoginByEmail()) && 
               this.password != null && 
               !this.password.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", rememberMe=" + rememberMe +
                ", password='[HIDDEN]'" +
                '}';
    }
}