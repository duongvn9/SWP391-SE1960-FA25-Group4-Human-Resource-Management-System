package group4.hrms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class để load QA context cho chatbot
 */
public class ChatbotContextLoader {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotContextLoader.class);
    private static String cachedContext = null;

    /**
     * Load QA context từ file chatbot-qa.json
     * 
     * @return QA context string
     */
    public static String getContext() {
        if (cachedContext != null) {
            return cachedContext;
        }

        try {
            String qaFilePath = ConfigUtil.getProperty("chatbot.qa.file.path", "docs/chatbot-qa.json");

            // Load file từ classpath
            InputStream inputStream = ChatbotContextLoader.class.getClassLoader().getResourceAsStream(qaFilePath);
            if (inputStream == null) {
                logger.error("QA file not found: {}", qaFilePath);
                return "";
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            cachedContext = content.toString();
            logger.info("Successfully loaded QA context from: {}", qaFilePath);
            return cachedContext;

        } catch (Exception e) {
            logger.error("Error loading QA context: " + e.getMessage(), e);
            return "";
        }
    }

    /**
     * Clear cached context (for testing)
     */
    public static void clearCache() {
        cachedContext = null;
    }
}