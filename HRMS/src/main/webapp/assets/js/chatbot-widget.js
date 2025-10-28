/**
 * ChatbotWidget - Gemini AI Chatbot Integration
 * Handles chatbot UI interactions and API communication
 */
class ChatbotWidget {
    constructor() {
        // DOM element references
        this.toggleBtn = document.getElementById('chatbot-toggle-btn');
        this.closeBtn = document.getElementById('chatbot-close-btn');
        this.chatWindow = document.getElementById('chatbot-window');
        this.messagesContainer = document.getElementById('chatbot-messages');
        this.suggestionsContainer = document.getElementById('chatbot-suggestions');
        this.inputField = document.getElementById('chatbot-input');
        this.sendBtn = document.getElementById('chatbot-send-btn');

        // State
        this.isProcessing = false;
        this.typingIndicatorElement = null;

        // Suggestion pool - 10 common questions
        this.suggestionPool = [
            "Làm sao để gửi đơn nghỉ phép?",
            "Tăng ca vào cuối tuần được tính lương như thế nào?",
            "Tôi có thể xem hồ sơ cá nhân ở đâu?",
            "Quy trình phê duyệt đơn OT như thế nào?",
            "Nghỉ không lương có giới hạn không?",
            "Những ngày lễ nào được nghỉ hưởng lương?",
            "Làm thế nào để gửi khiếu nại chấm công?",
            "Tôi có thể hủy đơn nghỉ phép đã gửi không?",
            "Một ngày tôi có thể tăng ca tối đa bao nhiêu giờ?",
            "Làm cách nào để cập nhật thông tin cá nhân?"
        ];
    }

    /**
     * Initialize chatbot widget
     * Bind event listeners and display initial state
     */
    init() {
        // Check if all required elements exist
        if (!this.toggleBtn || !this.closeBtn || !this.chatWindow || !this.messagesContainer || !this.inputField || !this.sendBtn) {
            console.error('Chatbot: Required DOM elements not found');
            return;
        }

        console.log('Chatbot: Initializing widget');

        // Bind event listeners
        this.toggleBtn.addEventListener('click', () => this.toggleWidget());
        this.closeBtn.addEventListener('click', () => this.toggleWidget());
        this.sendBtn.addEventListener('click', () => this.handleSendClick());

        // Handle Enter key in input field (Shift+Enter for new line)
        this.inputField.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.handleSendClick();
            }
        });

        // Auto-resize textarea as user types
        this.inputField.addEventListener('input', () => {
            this.inputField.style.height = 'auto';
            this.inputField.style.height = Math.min(this.inputField.scrollHeight, 100) + 'px';
        });

        // Display initial greeting message
        this.displayMessage('Xin chào! Tôi là trợ lý HR. Tôi có thể giúp gì cho bạn về chính sách và quy trình nhân sự?', false);

        // Display initial quick suggestions
        this.displayQuickSuggestions();

        // Restore widget state from sessionStorage
        const savedState = sessionStorage.getItem('chatbot-open');
        if (savedState === 'true') {
            this.chatWindow.classList.add('show');
        }
    }

    /**
     * Toggle chatbot window visibility
     */
    toggleWidget() {
        const isOpen = this.chatWindow.classList.toggle('show');
        console.log('Chatbot: Toggling widget, isOpen:', isOpen);

        // Update toggle button icon
        const icon = this.toggleBtn.querySelector('i');
        if (isOpen) {
            icon.classList.remove('fa-comments');
            icon.classList.add('fa-times');
        } else {
            icon.classList.remove('fa-times');
            icon.classList.add('fa-comments');
        }

        // Save state to sessionStorage
        sessionStorage.setItem('chatbot-open', isOpen);
    }

    /**
     * Display quick suggestion buttons
     * Selects 3 random suggestions from pool (no duplicates)
     */
    displayQuickSuggestions() {
        // Clear existing suggestions
        this.suggestionsContainer.innerHTML = '';

        // Select 3 random unique suggestions
        const shuffled = [...this.suggestionPool].sort(() => 0.5 - Math.random());
        const selectedSuggestions = shuffled.slice(0, 3);

        // Create suggestion buttons
        selectedSuggestions.forEach(suggestion => {
            const button = document.createElement('button');
            button.className = 'chatbot-suggestion-btn';
            button.textContent = suggestion;
            button.type = 'button';
            button.addEventListener('click', () => this.handleSuggestionClick(suggestion));
            this.suggestionsContainer.appendChild(button);
        });
    }

    /**
     * Handle suggestion button click
     * @param {string} suggestion - The suggestion text
     */
    handleSuggestionClick(suggestion) {
        // Clear suggestions container
        this.suggestionsContainer.innerHTML = '';

        // Send suggestion as message
        this.sendMessage(suggestion);
    }

    /**
     * Handle send button click
     */
    handleSendClick() {
        const message = this.inputField.value.trim();
        if (message) {
            this.sendMessage(message);
        }
    }

    /**
     * Send message to chatbot API
     * @param {string} message - The message text
     */
    async sendMessage(message) {
        // Validate message
        if (!message || message.length === 0) {
            return;
        }

        if (message.length > 500) {
            this.showError('Câu hỏi quá dài. Vui lòng nhập tối đa 500 ký tự.');
            return;
        }

        // Prevent duplicate submissions
        if (this.isProcessing) {
            return;
        }

        this.isProcessing = true;
        this.sendBtn.disabled = true;

        // Display user message
        this.displayMessage(message, true);

        // Clear input field
        this.inputField.value = '';
        this.inputField.style.height = 'auto';

        // Show typing indicator
        this.showTypingIndicator();

        try {
            // Call API
            const response = await fetch(`${window.location.origin}/HRMS/chatbot/ask`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ question: message })
            });

            // Handle HTTP errors
            if (!response.ok) {
                if (response.status === 429) {
                    throw new Error('Hệ thống đang xử lý quá nhiều yêu cầu. Vui lòng đợi một chút.');
                } else if (response.status === 500) {
                    throw new Error('Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau.');
                } else if (response.status === 401) {
                    throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
                } else {
                    throw new Error('Đã xảy ra lỗi. Vui lòng thử lại sau.');
                }
            }

            // Parse response
            const data = await response.json();

            // Hide typing indicator
            this.hideTypingIndicator();

            // Handle response
            if (data.success) {
                this.displayMessage(data.answer, false);
                // Display new quick suggestions after bot response
                this.displayQuickSuggestions();
            } else {
                this.showError(data.error || 'Đã xảy ra lỗi. Vui lòng thử lại sau.');
            }

        } catch (error) {
            // Hide typing indicator
            this.hideTypingIndicator();

            // Handle network errors
            if (error.name === 'TypeError' || error.message.includes('Failed to fetch')) {
                this.showError('Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.');
            } else {
                this.showError(error.message);
            }
        } finally {
            // Re-enable send button
            this.isProcessing = false;
            this.sendBtn.disabled = false;
        }
    }

    /**
     * Display a message in the chat
     * @param {string} message - The message text
     * @param {boolean} isUser - True if message is from user, false if from bot
     */
    displayMessage(message, isUser) {
        const messageDiv = document.createElement('div');
        messageDiv.className = isUser ? 'chatbot-message user-message' : 'chatbot-message bot-message';

        // Format text with line breaks and bullet points
        const formattedText = this.formatMessageText(message);
        messageDiv.innerHTML = formattedText;

        this.messagesContainer.appendChild(messageDiv);

        // Scroll to bottom
        this.scrollToBottom();
    }

    /**
     * Format message text with line breaks and bullet points
     * @param {string} text - The raw text
     * @returns {string} - Formatted HTML
     */
    formatMessageText(text) {
        // Escape HTML to prevent XSS
        const escaped = text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');

        // Convert line breaks to <br>
        let formatted = escaped.replace(/\n/g, '<br>');

        // Convert bullet points (- or *) to HTML list items
        formatted = formatted.replace(/^[\-\*]\s+(.+)$/gm, '<li>$1</li>');

        // Wrap list items in <ul> if they exist
        if (formatted.includes('<li>')) {
            formatted = formatted.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');
        }

        return formatted;
    }

    /**
     * Show typing indicator
     */
    showTypingIndicator() {
        if (this.typingIndicatorElement) {
            return; // Already showing
        }

        this.typingIndicatorElement = document.createElement('div');
        this.typingIndicatorElement.className = 'chatbot-message bot-message typing-indicator';
        this.typingIndicatorElement.innerHTML = `
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
        `;

        this.messagesContainer.appendChild(this.typingIndicatorElement);
        this.scrollToBottom();
    }

    /**
     * Hide typing indicator
     */
    hideTypingIndicator() {
        if (this.typingIndicatorElement) {
            this.typingIndicatorElement.remove();
            this.typingIndicatorElement = null;
        }
    }

    /**
     * Show error message
     * @param {string} errorMessage - The error message to display
     */
    showError(errorMessage) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'chatbot-message bot-message error-message';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${this.formatMessageText(errorMessage)}`;

        this.messagesContainer.appendChild(errorDiv);
        this.scrollToBottom();
    }

    /**
     * Scroll messages container to bottom
     */
    scrollToBottom() {
        this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    }
}

// Initialize chatbot when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const chatbot = new ChatbotWidget();
    chatbot.init();
});
