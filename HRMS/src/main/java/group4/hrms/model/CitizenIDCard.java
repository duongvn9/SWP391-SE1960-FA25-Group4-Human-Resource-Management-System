package group4.hrms.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import group4.hrms.util.TextNormalizationUtil;

/**
 * Model class representing a Vietnamese Citizen ID Card
 * Contains normalized data with all text in uppercase and without diacritics
 */
public class CitizenIDCard {

    @SerializedName("SO_CAN_CUOC_CONG_DAN")
    private String soCCCD;

    @SerializedName("TEN")
    private String ten;

    @SerializedName("NGAY_SINH")
    private String ngaySinh;

    @SerializedName("GIOI_TINH")
    private String gioiTinh;

    @SerializedName("QUE_QUAN")
    private String queQuan;

    @SerializedName("NGAY_HET_HAN")
    private String ngayHetHan;

    /**
     * Default constructor
     */
    public CitizenIDCard() {
        // Initialize with empty strings to ensure consistency
        this.soCCCD = "";
        this.ten = "";
        this.ngaySinh = "";
        this.gioiTinh = "";
        this.queQuan = "";
        this.ngayHetHan = "";
    }

    /**
     * Constructor with automatic normalization
     * All text inputs will be normalized to uppercase without diacritics
     */
    public CitizenIDCard(String soCCCD, String ten, String ngaySinh, String gioiTinh, String queQuan, String ngayHetHan) {
        this.soCCCD = normalizeText(soCCCD);
        this.ten = normalizeText(ten);
        this.ngaySinh = TextNormalizationUtil.normalizeDateFormat(ngaySinh);
        this.gioiTinh = TextNormalizationUtil.normalizeGender(gioiTinh);
        this.queQuan = normalizeText(queQuan);
        this.ngayHetHan = TextNormalizationUtil.normalizeDateFormat(ngayHetHan);
    }

    // Getters
    public String getSoCCCD() {
        return soCCCD;
    }

    public String getTen() {
        return ten;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public String getQueQuan() {
        return queQuan;
    }

    public String getNgayHetHan() {
        return ngayHetHan;
    }

    // Setters with automatic normalization
    public void setSoCCCD(String soCCCD) {
        this.soCCCD = normalizeText(soCCCD);
    }

    public void setTen(String ten) {
        this.ten = normalizeText(ten);
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = TextNormalizationUtil.normalizeDateFormat(ngaySinh);
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = TextNormalizationUtil.normalizeGender(gioiTinh);
    }

    public void setQueQuan(String queQuan) {
        this.queQuan = normalizeText(queQuan);
    }

    public void setNgayHetHan(String ngayHetHan) {
        this.ngayHetHan = TextNormalizationUtil.normalizeDateFormat(ngayHetHan);
    }

    /**
     * Validates if all required fields are present
     * 
     * @return true if all fields are non-null and non-empty
     */
    public boolean isValid() {
        return TextNormalizationUtil.isNotEmpty(soCCCD) &&
                TextNormalizationUtil.isNotEmpty(ten) &&
                TextNormalizationUtil.isNotEmpty(ngaySinh) &&
                TextNormalizationUtil.isNotEmpty(gioiTinh) &&
                TextNormalizationUtil.isNotEmpty(queQuan);
    }

    /**
     * Checks if the card has any data
     * 
     * @return true if at least one field has data
     */
    public boolean hasData() {
        return TextNormalizationUtil.isNotEmpty(soCCCD) ||
                TextNormalizationUtil.isNotEmpty(ten) ||
                TextNormalizationUtil.isNotEmpty(ngaySinh) ||
                TextNormalizationUtil.isNotEmpty(gioiTinh) ||
                TextNormalizationUtil.isNotEmpty(queQuan) ||
                TextNormalizationUtil.isNotEmpty(ngayHetHan);
    }

    /**
     * Validates if CCCD has expiry date (required for application)
     * 
     * @return true if expiry date is present and not empty
     */
    public boolean hasExpiryDate() {
        return TextNormalizationUtil.isNotEmpty(ngayHetHan);
    }

    /**
     * Checks if CCCD is expired
     * 
     * @return true if CCCD is expired
     */
    public boolean isExpired() {
        if (!hasExpiryDate()) return false;
        
        try {
            java.time.LocalDate expiryDate = parseVietnameseDate(ngayHetHan);
            return expiryDate.isBefore(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if CCCD is expiring soon
     * 
     * @param daysThreshold number of days to check ahead
     * @return true if CCCD expires within the threshold
     */
    public boolean isExpiringSoon(int daysThreshold) {
        if (!hasExpiryDate()) return false;
        
        try {
            java.time.LocalDate expiryDate = parseVietnameseDate(ngayHetHan);
            java.time.LocalDate thresholdDate = java.time.LocalDate.now().plusDays(daysThreshold);
            return expiryDate.isBefore(thresholdDate) && !expiryDate.isBefore(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses Vietnamese date format DD/MM/YYYY to LocalDate
     */
    private java.time.LocalDate parseVietnameseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string is empty");
        }
        
        String[] parts = dateStr.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
        
        return java.time.LocalDate.of(
            Integer.parseInt(parts[2]), // year
            Integer.parseInt(parts[1]), // month
            Integer.parseInt(parts[0])  // day
        );
    }

    /**
     * Converts the object to JSON string
     * 
     * @return JSON representation of the CitizenIDCard
     */
    public String toJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        return gson.toJson(this);
    }

    /**
     * Creates CitizenIDCard object from JSON string
     * 
     * @param json JSON string
     * @return CitizenIDCard object
     */
    public static CitizenIDCard fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new CitizenIDCard();
        }

        try {
            Gson gson = new Gson();
            CitizenIDCard card = gson.fromJson(json, CitizenIDCard.class);
            return card != null ? card : new CitizenIDCard();
        } catch (Exception e) {
            return new CitizenIDCard();
        }
    }

    /**
     * Helper method to normalize text
     */
    private String normalizeText(String text) {
        return TextNormalizationUtil.normalizeVietnameseText(text);
    }

    @Override
    public String toString() {
        return "CitizenIDCard{" +
                "soCCCD='" + soCCCD + '\'' +
                ", ten='" + ten + '\'' +
                ", ngaySinh='" + ngaySinh + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", queQuan='" + queQuan + '\'' +
                ", ngayHetHan='" + ngayHetHan + '\'' +
                '}';
    }
}