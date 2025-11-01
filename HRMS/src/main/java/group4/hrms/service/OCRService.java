package group4.hrms.service;

import group4.hrms.model.CitizenIDCard;
import group4.hrms.util.TextNormalizationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service class for OCR processing using Gemini API
 * Focused solely on processing Vietnamese Citizen ID cards
 */
public class OCRService {

    private static final Logger LOGGER = Logger.getLogger(OCRService.class.getName());
    
    public OCRService() {
        initializeApiConfiguration();
    }

    // API configuration
    private static final String GEMINI_MODEL = "gemini-2.5-flash-lite";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";
    
    private String geminiApiKey;
    private String geminiApiUrl;

    // JSON parsing constants
    private static final String PARTS = "parts";
    private static final String CANDIDATES = "candidates";
    private static final String CONTENT = "content";
    private static final String TEXT = "text";

    /**
     * Processes an image using Gemini API for OCR
     * 
     * @param base64Image Base64 encoded image
     * @param mimeType    MIME type of the image
     * @return CitizenIDCard object with extracted and normalized information
     * @throws IOException           if API call fails
     * @throws IllegalStateException if API key is not configured
     */
    public CitizenIDCard processImage(String base64Image, String mimeType) throws IOException {
        validateApiConfiguration();

        LOGGER.log(Level.INFO, "Processing image with Gemini API - Model: {0}, API URL: {1}", 
            new Object[]{GEMINI_MODEL, geminiApiUrl});

        String prompt = buildOptimizedPrompt();
        LOGGER.log(Level.INFO, "Using prompt length: {0}", prompt.length());
        
        String ocrText = callGeminiAPI(base64Image, mimeType, prompt);
        LOGGER.log(Level.INFO, "Received OCR text length: {0}", ocrText != null ? ocrText.length() : 0);

        return parseOCRResponse(ocrText);
    }

    /**
     * Initialize API configuration from properties
     */
    private void initializeApiConfiguration() {
        try {
            // Try to get API key from system property first, then from properties file
            this.geminiApiKey = System.getProperty("gemini.api.key");
            if (this.geminiApiKey == null || this.geminiApiKey.isEmpty()) {
                // Load from application.properties
                java.util.Properties props = new java.util.Properties();
                try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                    if (is != null) {
                        props.load(is);
                        this.geminiApiKey = props.getProperty("gemini.api.key");
                    }
                }
            }
            
            if (this.geminiApiKey != null && !this.geminiApiKey.isEmpty()) {
                this.geminiApiUrl = GEMINI_BASE_URL + "/" + GEMINI_MODEL + ":generateContent?key=" + this.geminiApiKey;
                LOGGER.info("Gemini API configured successfully");
            } else {
                LOGGER.severe("Gemini API key not found in configuration");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize API configuration", e);
        }
    }

    /**
     * Validates that the API is properly configured
     * 
     * @throws IllegalStateException if API key is not configured
     */
    private void validateApiConfiguration() {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not configured. Please set gemini.api.key in application.properties or as system property.");
        }
    }

    /**
     * Builds an optimized prompt for Vietnamese Citizen ID card OCR
     * 
     * @return Formatted prompt string
     */
    private String buildOptimizedPrompt() {
        return "Hãy đọc các trường dữ liệu trong thẻ căn cước công dân Việt Nam này và trả về dưới dạng JSON với định dạng chính xác như sau:\\n\\n"
                +
                "{\\n" +
                "    \\\"SO_CAN_CUOC_CONG_DAN\\\": \\\"[số căn cước 12 chữ số]\\\",\\n" +
                "    \\\"TEN\\\": \\\"[họ và tên viết hoa không dấu]\\\",\\n" +
                "    \\\"NGAY_SINH\\\": \\\"[ngày sinh DD/MM/YYYY]\\\",\\n" +
                "    \\\"GIOI_TINH\\\": \\\"[NAM hoặc NU]\\\",\\n" +
                "    \\\"QUE_QUAN\\\": \\\"[quê quán viết hoa không dấu]\\\",\\n" +
                "    \\\"NGAY_HET_HAN\\\": \\\"[ngày hết hạn DD/MM/YYYY từ dòng 'Có giá trị đến']\\\"\\n" +
                "}\\n\\n" +
                "YÊU CẦU QUAN TRỌNG:\\n" +
                "- Trả về CHÍNH XÁC định dạng JSON như trên, không thêm bớt ký tự nào\\n" +
                "- TẤT CẢ chữ cái phải viết HOA và KHÔNG DẤU (ví dụ: Ê->E, Ô->O, Ư->U, Ă->A, Đ->D)\\n" +
                "- Đọc chính xác từng ký tự trên thẻ\\n" +
                "- NGAY_HET_HAN phải đọc từ dòng 'Có giá trị đến' trên thẻ CCCD\\n" +
                "- Tất cả ngày tháng phải theo định dạng DD/MM/YYYY\\n" +
                "- Nếu không tìm thấy thông tin nào, để trống \\\"\\\" trong trường đó\\n" +
                "- Chỉ trả về JSON, không thêm text giải thích";
    }

    /**
     * Calls Gemini API for image analysis
     * 
     * @param base64Image Base64 encoded image
     * @param mimeType    MIME type of the image
     * @param prompt      Text prompt for the API
     * @return Extracted text from the API response
     * @throws IOException if API call fails
     */
    private String callGeminiAPI(String base64Image, String mimeType, String prompt) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(geminiApiUrl);
            httpPost.setHeader("Content-Type", "application/json");

            JsonObject requestBody = buildRequestBody(base64Image, mimeType, prompt);
            StringEntity entity = new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                
                LOGGER.log(Level.INFO, "API Response Code: {0}", response.getCode());
                LOGGER.log(Level.INFO, "API Response Body length: {0}", responseBody.length());
                
                if (response.getCode() != 200) {
                    LOGGER.log(Level.SEVERE, "API Error Response: {0}", responseBody);
                    handleApiError(response.getCode(), responseBody);
                }

                return parseGeminiResponse(responseBody);
            }
        } catch (ParseException e) {
            throw new IOException("Failed to parse API response", e);
        }
    }

    /**
     * Builds the request body for Gemini API
     */
    private JsonObject buildRequestBody(String base64Image, String mimeType, String prompt) {
        JsonObject requestBody = new JsonObject();
        JsonObject content = new JsonObject();

        // Add text part
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        // Add image part
        JsonObject imagePart = new JsonObject();
        JsonObject inlineData = new JsonObject();
        inlineData.addProperty("mime_type", mimeType);
        inlineData.addProperty("data", base64Image);
        imagePart.add("inline_data", inlineData);

        // Combine parts
        content.add(PARTS, new Gson().toJsonTree(new Object[] {
                new Gson().fromJson(textPart.toString(), Object.class),
                new Gson().fromJson(imagePart.toString(), Object.class)
        }));

        requestBody.add("contents", new Gson().toJsonTree(new Object[] {
                new Gson().fromJson(content.toString(), Object.class)
        }));

        return requestBody;
    }

    /**
     * Handles API error responses
     */
    private void handleApiError(int statusCode, String responseBody) throws IOException {
        LOGGER.log(Level.SEVERE, "Gemini API error: {0} - {1}", new Object[] { statusCode, responseBody });

        try {
            JsonObject errorResponse = new Gson().fromJson(responseBody, JsonObject.class);
            if (errorResponse.has("error")) {
                JsonObject error = errorResponse.getAsJsonObject("error");
                String errorMessage = error.has("message") ? error.get("message").getAsString() : "Unknown error";
                throw new IOException("Gemini API error (" + statusCode + "): " + errorMessage);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not parse error response", e);
        }

        throw new IOException("Gemini API returned error: " + statusCode + " - " + responseBody);
    }

    /**
     * Parses Gemini API response to extract text
     * 
     * @param responseBody JSON response from Gemini API
     * @return Extracted text content
     */
    private String parseGeminiResponse(String responseBody) {
        try {
            JsonObject response = new Gson().fromJson(responseBody, JsonObject.class);

            if (response.has(CANDIDATES) && response.getAsJsonArray(CANDIDATES).size() > 0) {
                JsonObject candidate = response.getAsJsonArray(CANDIDATES).get(0).getAsJsonObject();

                if (candidate.has(CONTENT)) {
                    JsonObject content = candidate.getAsJsonObject(CONTENT);

                    if (content.has(PARTS) && content.getAsJsonArray(PARTS).size() > 0) {
                        JsonObject part = content.getAsJsonArray(PARTS).get(0).getAsJsonObject();

                        if (part.has(TEXT)) {
                            return part.get(TEXT).getAsString();
                        }
                    }
                }
            }

            LOGGER.log(Level.WARNING, "No text content found in API response");
            return "";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing Gemini response", e);
            return "";
        }
    }

    /**
     * Parses OCR response and creates CitizenIDCard object
     * 
     * @param ocrText Raw OCR text from Gemini API
     * @return CitizenIDCard object with extracted and normalized information
     */
    private CitizenIDCard parseOCRResponse(String ocrText) {
        if (ocrText == null || ocrText.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty OCR text received");
            return new CitizenIDCard();
        }

        LOGGER.log(Level.INFO, "Processing OCR response: {0}", ocrText.substring(0, Math.min(100, ocrText.length())));

        // Try to parse as JSON first
        CitizenIDCard card = parseJsonResponse(ocrText);

        if (!card.hasData()) {
            LOGGER.log(Level.INFO, "JSON parsing failed or returned empty data, falling back to text parsing");
            // Fallback: create empty card if JSON parsing fails
            card = new CitizenIDCard();
        }

        return card;
    }

    /**
     * Parses JSON response from Gemini API
     * 
     * @param jsonText JSON response text
     * @return CitizenIDCard object with extracted information
     */
    private CitizenIDCard parseJsonResponse(String jsonText) {
        try {
            String cleanJson = extractJsonFromText(jsonText);

            if (cleanJson.isEmpty()) {
                return new CitizenIDCard();
            }

            JsonObject jsonObject = new Gson().fromJson(cleanJson, JsonObject.class);
            CitizenIDCard card = new CitizenIDCard();

            // Extract and normalize each field
            if (jsonObject.has("SO_CAN_CUOC_CONG_DAN") && !jsonObject.get("SO_CAN_CUOC_CONG_DAN").isJsonNull()) {
                card.setSoCCCD(jsonObject.get("SO_CAN_CUOC_CONG_DAN").getAsString());
            }

            if (jsonObject.has("TEN") && !jsonObject.get("TEN").isJsonNull()) {
                card.setTen(jsonObject.get("TEN").getAsString());
            }

            if (jsonObject.has("NGAY_SINH") && !jsonObject.get("NGAY_SINH").isJsonNull()) {
                card.setNgaySinh(jsonObject.get("NGAY_SINH").getAsString());
            }

            if (jsonObject.has("GIOI_TINH") && !jsonObject.get("GIOI_TINH").isJsonNull()) {
                card.setGioiTinh(jsonObject.get("GIOI_TINH").getAsString());
            }

            if (jsonObject.has("QUE_QUAN") && !jsonObject.get("QUE_QUAN").isJsonNull()) {
                card.setQueQuan(jsonObject.get("QUE_QUAN").getAsString());
            }

            if (jsonObject.has("NGAY_HET_HAN") && !jsonObject.get("NGAY_HET_HAN").isJsonNull()) {
                card.setNgayHetHan(jsonObject.get("NGAY_HET_HAN").getAsString());
            }

            LOGGER.log(Level.INFO, "Successfully parsed JSON response");
            return card;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse JSON response: {0}", e.getMessage());
            return new CitizenIDCard();
        }
    }

    /**
     * Extracts JSON object from text that might contain additional content
     * 
     * @param text Input text that may contain JSON
     * @return Clean JSON string or empty string if not found
     */
    private String extractJsonFromText(String text) {
        if (text == null) {
            return "";
        }

        // Look for JSON object boundaries
        int startIndex = text.indexOf('{');
        int endIndex = text.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex + 1);
        }

        return "";
    }
}