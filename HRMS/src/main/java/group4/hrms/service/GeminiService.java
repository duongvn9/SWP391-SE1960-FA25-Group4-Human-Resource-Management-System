package group4.hrms.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import group4.hrms.dto.ChatResponse;
import group4.hrms.util.ConfigUtil;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import org.apache.hc.core5.http.ParseException;

/**
 * Service class để tích hợp với Gemini AI API
 * Xử lý việc gửi câu hỏi và nhận trả lời từ Gemini
 */
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    private static final Gson gson = new Gson();

    private final String apiKey;
    private final String apiUrl;
    private final CloseableHttpClient httpClient;

    /**
     * Constructor nhận API key từ configuration
     */
    public GeminiService() {
        this.apiKey = ConfigUtil.getProperty("gemini.api.key");
        this.apiUrl = ConfigUtil.getProperty("gemini.api.url");

        // Khởi tạo HTTP client với timeout 10 seconds
        int timeoutMs = ConfigUtil.getIntProperty("gemini.api.timeout", 10000);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(timeoutMs, TimeUnit.MILLISECONDS))
                .setResponseTimeout(Timeout.of(timeoutMs, TimeUnit.MILLISECONDS))
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        logger.info("GeminiService initialized with API URL: {}", apiUrl);
    }

    /**
     * Constructor cho testing với custom parameters
     */
    public GeminiService(String apiKey, String apiUrl, int timeoutMs) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(timeoutMs, TimeUnit.MILLISECONDS))
                .setResponseTimeout(Timeout.of(timeoutMs, TimeUnit.MILLISECONDS))
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        logger.info("GeminiService initialized with custom configuration");
    }

    /**
     * Build prompt từ system context và user question
     * Format theo Gemini API requirements
     * 
     * @param question  Câu hỏi từ user
     * @param qaContext Context từ chatbot-qa.json
     * @return Formatted prompt string
     */
    private String buildPrompt(String question, String qaContext) {
        StringBuilder promptBuilder = new StringBuilder();

        // Add system context từ chatbot-qa.json
        if (qaContext != null && !qaContext.isEmpty()) {
            promptBuilder.append(qaContext);
            promptBuilder.append("\n\n");
        }

        // Add instruction
        promptBuilder.append("=== USER QUESTION ===\n");
        promptBuilder.append("Hãy trả lời câu hỏi sau dựa trên thông tin trong Knowledge Base ở trên. ");
        promptBuilder.append(
                "Nếu không tìm thấy thông tin chính xác, hãy trả lời lịch sự rằng bạn không có thông tin đó.\n\n");

        // Add user question
        promptBuilder.append("Câu hỏi: ");
        promptBuilder.append(question);

        return promptBuilder.toString();
    }

    /**
     * Call Gemini API với prompt
     * Xử lý HTTP request/response và error handling
     * 
     * @param prompt Formatted prompt string
     * @return Generated text từ Gemini API
     * @throws SocketTimeoutException nếu request timeout
     * @throws IOException            nếu có network error
     */
    private String callGeminiApi(String prompt) throws IOException, ParseException {
        // Build API URL với API key
        String fullUrl = apiUrl + "?key=" + apiKey;

        // Tạo HTTP POST request
        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setHeader("Content-Type", "application/json");

        // Build request body theo Gemini API format
        JsonObject requestBody = new JsonObject();

        // Add contents array
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        requestBody.add("contents", contents);

        // Add generationConfig
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.7);
        generationConfig.addProperty("maxOutputTokens", 1024);
        generationConfig.addProperty("topP", 0.95);
        requestBody.add("generationConfig", generationConfig);

        // Set request body
        String requestBodyJson = gson.toJson(requestBody);
        httpPost.setEntity(new StringEntity(requestBodyJson, ContentType.APPLICATION_JSON));

        logger.debug("Calling Gemini API with prompt length: {}", prompt.length());

        // Execute request
        @SuppressWarnings("deprecation")
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try (response) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            logger.debug("Gemini API response status: {}", statusCode);

            // Handle HTTP errors
            if (statusCode >= 400) {
                logger.error("Gemini API error - Status: {}, Response: {}", statusCode, responseBody);

                if (statusCode == 429) {
                    throw new IOException("API rate limit exceeded");
                } else if (statusCode >= 500) {
                    throw new IOException("Gemini API server error: " + statusCode);
                } else {
                    throw new IOException("Gemini API client error: " + statusCode);
                }
            }

            // Parse response và extract generated text
            JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

            if (responseJson.has("candidates")) {
                JsonArray candidates = responseJson.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    if (candidate.has("content")) {
                        JsonObject contentObj = candidate.getAsJsonObject("content");
                        if (contentObj.has("parts")) {
                            JsonArray partsArray = contentObj.getAsJsonArray("parts");
                            if (partsArray.size() > 0) {
                                JsonObject partObj = partsArray.get(0).getAsJsonObject();
                                if (partObj.has("text")) {
                                    String generatedText = partObj.get("text").getAsString();
                                    logger.debug("Successfully extracted generated text, length: {}",
                                            generatedText.length());
                                    return generatedText;
                                }
                            }
                        }
                    }
                }
            }

            // Nếu không parse được response
            logger.error("Unable to parse Gemini API response: {}", responseBody);
            throw new IOException("Invalid response format from Gemini API");
        }
    }

    /**
     * Public method để gửi câu hỏi đến Gemini AI
     * Xử lý toàn bộ flow: build prompt, call API, handle errors
     * 
     * @param question  Câu hỏi từ user
     * @param qaContext Context từ chatbot-qa.json
     * @return ChatResponse với answer hoặc error message
     */
    public ChatResponse askQuestion(String question, String qaContext) {
        try {
            logger.info("Processing question: {}",
                    question.substring(0, Math.min(50, question.length())));

            // Validate API key
            if (apiKey == null || apiKey.isEmpty()) {
                logger.error("Gemini API key is not configured");
                return ChatResponse.error("Hệ thống chưa được cấu hình đúng. Vui lòng liên hệ IT support.");
            }

            // Build prompt
            String prompt = buildPrompt(question, qaContext);

            // Call Gemini API
            String answer = callGeminiApi(prompt);

            logger.info("Successfully generated answer, length: {}", answer.length());
            return ChatResponse.success(answer);

        } catch (SocketTimeoutException e) {
            logger.error("Gemini API timeout for question: " + question, e);
            return ChatResponse.error("Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau.");

        } catch (ParseException e) {
            logger.error("Error parsing Gemini API response: " + e.getMessage(), e);
            return ChatResponse.error("Không thể xử lý phản hồi từ hệ thống. Vui lòng thử lại sau.");

        } catch (IOException e) {
            String errorMessage = e.getMessage();
            logger.error("Network error calling Gemini API: " + errorMessage, e);

            // Handle specific error cases
            if (errorMessage != null && errorMessage.contains("rate limit")) {
                return ChatResponse.error("Hệ thống đang xử lý quá nhiều yêu cầu. Vui lòng đợi một chút.");
            } else if (errorMessage != null && errorMessage.contains("server error")) {
                return ChatResponse.error("Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau.");
            } else {
                return ChatResponse.error("Không thể kết nối đến dịch vụ AI. Vui lòng thử lại sau.");
            }

        } catch (Exception e) {
            logger.error("Unexpected error in chatbot service: " + e.getMessage(), e);
            return ChatResponse.error("Đã xảy ra lỗi không mong muốn. Vui lòng liên hệ IT support.");
        }
    }

    /**
     * Close HTTP client khi service bị destroy
     */
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
                logger.info("GeminiService HTTP client closed");
            }
        } catch (IOException e) {
            logger.error("Error closing HTTP client", e);
        }
    }
}
