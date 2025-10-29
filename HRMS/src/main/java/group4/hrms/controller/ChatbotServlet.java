package group4.hrms.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import group4.hrms.dto.ChatResponse;
import group4.hrms.service.GeminiService;
import group4.hrms.util.ChatbotContextLoader;
import group4.hrms.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Servlet xử lý các request từ chatbot widget
 * Endpoint: /chatbot/ask
 */
public class ChatbotServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotServlet.class);
    private static final Gson gson = new Gson();

    private GeminiService geminiService;

    @Override
    public void init() throws ServletException {
        super.init();

        // Initialize Gemini service
        this.geminiService = new GeminiService();

        logger.info("ChatbotServlet initialized successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response headers
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Enable CORS for development
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            // Check authentication
            if (!SessionUtil.isUserLoggedIn(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(ChatResponse.error("Authentication required to use chatbot")));
                return;
            }
            // Read request body
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            // Parse JSON request
            JsonObject requestJson;
            try {
                requestJson = gson.fromJson(requestBody.toString(), JsonObject.class);
            } catch (com.google.gson.JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ChatResponse.error("Invalid JSON format")));
                return;
            }

            if (requestJson == null || !requestJson.has("question")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ChatResponse.error("Question is required")));
                return;
            }

            String question = requestJson.get("question").getAsString().trim();

            // Validate question
            if (question.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ChatResponse.error("Question is required")));
                return;
            }

            if (question.length() > 500) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(ChatResponse.error("Question too long (max 500 characters)")));
                return;
            }

            logger.info("Processing chatbot question: {}",
                    question.substring(0, Math.min(50, question.length())));

            // Get QA context
            String qaContext = ChatbotContextLoader.getContext();

            // Call Gemini service
            ChatResponse chatResponse = geminiService.askQuestion(question, qaContext);

            // Return response - Always return 200 OK for successful request processing
            // The success/error status is indicated in the ChatResponse object
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(chatResponse));

        } catch (Exception e) {
            logger.error("Error processing chatbot request: " + e.getMessage(), e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(
                    ChatResponse.error("An unexpected error occurred. Please try again later.")));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Handle CORS preflight request
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void destroy() {
        if (geminiService != null) {
            geminiService.close();
        }
        super.destroy();
        logger.info("ChatbotServlet destroyed");
    }
}