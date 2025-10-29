<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Chatbot Widget CSS -->
        <link href="${pageContext.request.contextPath}/assets/css/chatbot-widget.css" rel="stylesheet">

        <!-- Chatbot Widget JavaScript -->
        <script src="${pageContext.request.contextPath}/assets/js/chatbot-widget.js" defer></script>

        <!-- Chatbot Widget -->
        <div id="chatbot-container">
            <!-- Toggle Button -->
            <button id="chatbot-toggle-btn" class="chatbot-toggle" type="button" aria-label="Open chatbot">
                <i class="fas fa-comments"></i>
            </button>

            <!-- Chat Window -->
            <div id="chatbot-window" class="chatbot-window">
                <!-- Chat Header -->
                <div class="chatbot-header">
                    <div class="chatbot-header-title">
                        <i class="fas fa-robot"></i>
                        <span>HR Assistant</span>
                    </div>
                    <button id="chatbot-close-btn" class="chatbot-close" type="button" aria-label="Close chatbot">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <!-- Messages Container -->
                <div id="chatbot-messages" class="chatbot-messages">
                    <!-- Messages will be dynamically added here -->
                </div>

                <!-- Quick Suggestions Container -->
                <div id="chatbot-suggestions" class="chatbot-suggestions">
                    <!-- Suggestion buttons will be dynamically added here -->
                </div>

                <!-- Input Area -->
                <div class="chatbot-input-area">
                    <textarea id="chatbot-input" class="chatbot-input" placeholder="Nhập câu hỏi của bạn..." rows="1"
                        maxlength="500"></textarea>
                    <button id="chatbot-send-btn" class="chatbot-send-btn" type="button" aria-label="Send message">
                        <i class="fas fa-paper-plane"></i>
                    </button>
                </div>
            </div>
        </div>