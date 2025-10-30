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

        // Suggestion pool - Đa dạng câu hỏi từ tất cả các category
        this.suggestionPool = [
            // Chính sách Nghỉ phép
            "Làm sao để gửi đơn nghỉ phép?",
            "Hệ thống có cho đăng ký nghỉ nửa ngày không?",
            "Nghỉ không lương có giới hạn như thế nào?",
            "Tôi có thể hủy đơn nghỉ phép đã gửi không?",
            "Những ngày lễ nào được nghỉ hưởng lương?",
            "Các loại nghỉ phép trong hệ thống có những gì?",
            "Làm sao để kiểm tra số ngày phép còn lại?",
            "Nếu ngày nghỉ phép trùng vào ngày nghỉ lễ thì sao?",

            // Chính sách Tăng ca
            "Làm sao để gửi đơn xin tăng ca?",
            "Tăng ca vào cuối tuần được tính lương như thế nào?",
            "Tăng ca vào ngày lễ được tính lương như thế nào?",
            "Một ngày tôi có thể tăng ca tối đa bao nhiêu giờ?",
            "Làm sao biết đơn xin tăng ca đã được duyệt chưa?",
            "Các loại tăng ca trong hệ thống có gì khác nhau?",
            "Tôi có cần đồng ý trước khi được tăng ca không?",

            // Khiếu nại chấm công
            "Làm thế nào để gửi khiếu nại chấm công?",
            "Quy trình xử lý khiếu nại chấm công như thế nào?",
            "Ai sẽ phê duyệt đơn khiếu nại chấm công?",
            "Tôi có thể chỉnh sửa đơn khiếu nại đã gửi không?",

            // Xử lý đơn từ/Phê duyệt
            "Quy trình phê duyệt đơn OT như thế nào?",
            "Quy trình duyệt đơn nghỉ phép được thực hiện ra sao?",
            "Làm sao để tạo và gửi đơn trên hệ thống?",
            "Tôi có thể theo dõi trạng thái đơn từ không?",
            "Đơn đề nghị tuyển dụng được xử lý như thế nào?",
            "Ngày mai tôi muốn nghỉ không lương thì bây giờ làm đơn có được không?",

            // Hồ sơ cá nhân & Hợp đồng
            "Tôi có thể xem hồ sơ cá nhân ở đâu?",
            "Làm cách nào để cập nhật thông tin cá nhân?",
            "Hệ thống có quản lý hợp đồng lao động không?",
            "Ai có thể xem thông tin hợp đồng lao động?",
            "Nếu tôi ký hợp đồng mới, hệ thống có cập nhật không?",

            // Quản lý tài khoản & Đăng nhập
            "Làm sao để đăng nhập vào hệ thống HRMS?",
            "Tài khoản của tôi bị khóa, phải làm sao?",
            "Làm sao để đổi mật khẩu tài khoản?",

            // Chấm công & Báo cáo thời gian
            "Hệ thống chấm công hoạt động như thế nào?",
            "Tôi có thể xem lịch sử chấm công không?",
            "Kỳ chấm công bị khóa có nghĩa là gì?",
            "HR có thể import dữ liệu chấm công từ Excel không?",

            // Phòng ban & Chức vụ
            "Làm sao để biết tôi thuộc phòng ban nào?",
            "Chức vụ của tôi trong hệ thống là gì?",

            // Tuyển dụng & Ứng tuyển
            "Làm sao để tạo đề nghị tuyển dụng?",
            "Ứng viên nộp hồ sơ như thế nào?",
            "Quy trình tuyển dụng có những bước nào?",

            // Lương & Phúc lợi
            "Làm sao để xem thông tin lương của tôi?",
            "Lịch sử thay đổi lương có được lưu trữ không?",

            // Câu hỏi chung về hệ thống
            "Hệ thống HRMS có những chức năng gì?",
            "Làm sao để liên hệ với bộ phận HR?",
            "Tôi cần hỗ trợ kỹ thuật, phải làm sao?",
            "Hệ thống có hỗ trợ tiếng Việt không?",
            "Dữ liệu cá nhân của tôi có được bảo mật không?",
            "Tôi có thể truy cập hệ thống từ điện thoại không?",
            "Làm sao để xuất báo cáo từ hệ thống?",
            "Hệ thống có gửi thông báo qua email không?"
        ];

        // Categorized suggestions for smarter recommendations
        this.categorizedSuggestions = {
            'leave': [
                "Làm sao để gửi đơn nghỉ phép?",
                "Hệ thống có cho đăng ký nghỉ nửa ngày không?",
                "Nghỉ không lương có giới hạn như thế nào?",
                "Làm sao để kiểm tra số ngày phép còn lại?",
                "Các loại nghỉ phép trong hệ thống có những gì?"
            ],
            'overtime': [
                "Làm sao để gửi đơn xin tăng ca?",
                "Tăng ca vào cuối tuần được tính lương như thế nào?",
                "Các loại tăng ca trong hệ thống có gì khác nhau?",
                "Một ngày tôi có thể tăng ca tối đa bao nhiêu giờ?",
                "Tôi có cần đồng ý trước khi được tăng ca không?"
            ],
            'attendance': [
                "Làm thế nào để gửi khiếu nại chấm công?",
                "Hệ thống chấm công hoạt động như thế nào?",
                "Tôi có thể xem lịch sử chấm công không?",
                "Kỳ chấm công bị khóa có nghĩa là gì?"
            ],
            'account': [
                "Làm sao để đăng nhập vào hệ thống HRMS?",
                "Tài khoản của tôi bị khóa, phải làm sao?",
                "Làm sao để đổi mật khẩu tài khoản?",
                "Tôi có thể xem hồ sơ cá nhân ở đâu?"
            ],
            'general': [
                "Quy trình phê duyệt đơn OT như thế nào?",
                "Tôi có thể theo dõi trạng thái đơn từ không?",
                "Hệ thống HRMS có những chức năng gì?",
                "Dữ liệu cá nhân của tôi có được bảo mật không?"
            ]
        };
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

        // Auto-resize textarea as user types and hide suggestions when typing
        this.inputField.addEventListener('input', () => {
            this.inputField.style.height = 'auto';
            this.inputField.style.height = Math.min(this.inputField.scrollHeight, 100) + 'px';

            // Hide suggestions when user starts typing
            this.handleInputChange();
        });

        // Additional measures to disable autocomplete and suggestions
        this.inputField.setAttribute('autocomplete', 'new-password'); // Trick to disable autocomplete
        this.inputField.setAttribute('autocorrect', 'off');
        this.inputField.setAttribute('autocapitalize', 'off');
        this.inputField.setAttribute('data-lpignore', 'true'); // LastPass ignore
        this.inputField.setAttribute('data-form-type', 'other'); // Prevent form detection

        // Prevent context menu and other suggestion triggers
        this.inputField.addEventListener('contextmenu', (e) => {
            // Allow context menu but prevent suggestion-related items
            // This is optional - you can uncomment the line below to completely disable context menu
            // e.preventDefault();
        });

        // Handle focus and blur events
        this.inputField.addEventListener('focus', () => {
            this.inputField.setAttribute('autocomplete', 'off');
            // Check if input has content to decide whether to show suggestions
            this.handleInputChange();
        });

        this.inputField.addEventListener('blur', () => {
            // Small delay to allow suggestion clicks to register
            setTimeout(() => {
                // If input is empty when losing focus, show suggestions
                if (this.inputField.value.trim() === '') {
                    this.showQuickSuggestions();
                }
            }, 150);
        });

        // Additional cleanup when widget is opened
        this.toggleBtn.addEventListener('click', () => {
            setTimeout(() => {
                if (this.chatWindow.classList.contains('show')) {
                    this.inputField.value = '';
                    this.inputField.blur();
                    this.inputField.focus();
                }
            }, 100);
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
     * Selects 3 random suggestions from pool of 50+ questions (no duplicates)
     */
    displayQuickSuggestions() {
        // Clear existing suggestions
        this.suggestionsContainer.innerHTML = '';

        // Select 3 random unique suggestions from diverse pool
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

        // Show suggestions container
        this.suggestionsContainer.style.display = 'flex';
    }

    /**
     * Display contextual suggestions based on keywords
     * @param {string} context - Context keyword (leave, overtime, attendance, etc.)
     */
    displayContextualSuggestions(context = null) {
        // Clear existing suggestions
        this.suggestionsContainer.innerHTML = '';

        let suggestionsToShow = [];

        if (context && this.categorizedSuggestions[context]) {
            // Show suggestions from specific category
            const categoryQuestions = this.categorizedSuggestions[context];
            const shuffled = [...categoryQuestions].sort(() => 0.5 - Math.random());
            suggestionsToShow = shuffled.slice(0, 3);
        } else {
            // Fallback to random suggestions from all categories
            const allCategories = Object.keys(this.categorizedSuggestions);
            const randomCategory = allCategories[Math.floor(Math.random() * allCategories.length)];
            const categoryQuestions = this.categorizedSuggestions[randomCategory];
            const shuffled = [...categoryQuestions].sort(() => 0.5 - Math.random());
            suggestionsToShow = shuffled.slice(0, 3);
        }

        // Create suggestion buttons
        suggestionsToShow.forEach(suggestion => {
            const button = document.createElement('button');
            button.className = 'chatbot-suggestion-btn';
            button.textContent = suggestion;
            button.type = 'button';
            button.addEventListener('click', () => this.handleSuggestionClick(suggestion));
            this.suggestionsContainer.appendChild(button);
        });

        // Show suggestions container
        this.suggestionsContainer.style.display = 'flex';
    }

    /**
     * Detect question context based on keywords
     * @param {string} question - User's question
     * @returns {string|null} - Context category or null
     */
    detectQuestionContext(question) {
        const lowerQuestion = question.toLowerCase();

        // Keywords for different contexts
        const contextKeywords = {
            'leave': ['nghỉ phép', 'nghỉ', 'phép', 'leave', 'ngày phép', 'đơn nghỉ', 'xin nghỉ'],
            'overtime': ['tăng ca', 'ot', 'overtime', 'làm thêm', 'thêm giờ', 'đơn ot', 'xin tăng ca'],
            'attendance': ['chấm công', 'attendance', 'khiếu nại', 'công', 'giờ làm', 'check in', 'check out'],
            'account': ['đăng nhập', 'login', 'tài khoản', 'account', 'mật khẩu', 'password', 'username', 'bị khóa'],
            'general': ['hệ thống', 'system', 'hrms', 'chức năng', 'hỗ trợ', 'help', 'giúp đỡ']
        };

        // Find matching context
        for (const [context, keywords] of Object.entries(contextKeywords)) {
            if (keywords.some(keyword => lowerQuestion.includes(keyword))) {
                return context;
            }
        }

        return null; // No specific context detected
    }

    /**
     * Hide quick suggestions with smooth animation
     */
    hideQuickSuggestions() {
        this.suggestionsContainer.style.opacity = '0';
        this.suggestionsContainer.style.maxHeight = '0';
        this.suggestionsContainer.style.padding = '0 20px';

        // Hide completely after animation
        setTimeout(() => {
            if (this.suggestionsContainer.style.opacity === '0') {
                this.suggestionsContainer.style.display = 'none';
            }
        }, 300);
    }

    /**
     * Show quick suggestions with smooth animation
     */
    showQuickSuggestions() {
        // Only show if there are suggestions
        if (this.suggestionsContainer.children.length > 0) {
            this.suggestionsContainer.style.display = 'flex';
            this.suggestionsContainer.style.padding = '0 20px 12px 20px';

            // Trigger animation
            setTimeout(() => {
                this.suggestionsContainer.style.opacity = '1';
                this.suggestionsContainer.style.maxHeight = '200px';
            }, 10);
        }
    }

    /**
     * Handle input field changes
     * Hide suggestions when user is typing, show when input is empty
     */
    handleInputChange() {
        const inputValue = this.inputField.value.trim();

        if (inputValue.length > 0) {
            // User is typing - hide suggestions
            this.hideQuickSuggestions();
        } else {
            // Input is empty - show suggestions
            this.showQuickSuggestions();
        }
    }

    /**
     * Handle suggestion button click
     * @param {string} suggestion - The suggestion text
     */
    handleSuggestionClick(suggestion) {
        // Hide suggestions immediately when clicked
        this.hideQuickSuggestions();

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
                // Display contextual suggestions based on user question
                const context = this.detectQuestionContext(message);
                if (context) {
                    this.displayContextualSuggestions(context);
                } else {
                    this.displayQuickSuggestions();
                }
            } else {
                this.showError(data.error || 'Đã xảy ra lỗi. Vui lòng thử lại sau.');
                // Show suggestions even on error
                this.showQuickSuggestions();
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

            // Show suggestions on error
            this.displayContextualSuggestions('general');
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
        messageDiv.className = `chatbot-message ${isUser ? 'user' : 'bot'}`;

        // Create message content wrapper
        const messageContent = document.createElement('div');
        messageContent.className = 'chatbot-message-content';

        // Format text with line breaks and bullet points
        const formattedText = this.formatMessageText(message);
        messageContent.innerHTML = formattedText;

        messageDiv.appendChild(messageContent);
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

        // Remove markdown formatting (** for bold)
        let formatted = escaped.replace(/\*\*(.*?)\*\*/g, '$1');

        // Remove other markdown symbols
        formatted = formatted.replace(/\*(.*?)\*/g, '$1'); // Remove single asterisks
        formatted = formatted.replace(/__(.*?)__/g, '$1'); // Remove underscores
        formatted = formatted.replace(/`(.*?)`/g, '$1'); // Remove backticks

        // Convert line breaks to <br>
        formatted = formatted.replace(/\n/g, '<br>');

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
        this.typingIndicatorElement.className = 'chatbot-message bot';

        const typingContent = document.createElement('div');
        typingContent.className = 'chatbot-typing show';
        typingContent.innerHTML = `
            <div class="chatbot-typing-dot"></div>
            <div class="chatbot-typing-dot"></div>
            <div class="chatbot-typing-dot"></div>
        `;

        this.typingIndicatorElement.appendChild(typingContent);
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
        errorDiv.className = 'chatbot-message bot error';

        const errorContent = document.createElement('div');
        errorContent.className = 'chatbot-message-content';
        errorContent.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${this.formatMessageText(errorMessage)}`;

        errorDiv.appendChild(errorContent);
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
