<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Câu hỏi thường gặp - HRMS</title>

            <!-- Common Styles -->
            <jsp:include page="/WEB-INF/views/layout/head.jsp" />

            <style>
                :root {
                    --primary-color: #2c5aa0;
                    --secondary-color: #f8f9fa;
                    --accent-color: #667eea;
                    --success-color: #28a745;
                    --text-dark: #333;
                }

                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    line-height: 1.6;
                    color: var(--text-dark);
                }

                /* Navigation */
                .navbar {
                    background: white;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    padding: 1rem 0;
                }

                .navbar-brand {
                    font-size: 1.8rem;
                    font-weight: bold;
                    color: var(--primary-color) !important;
                }

                .navbar-nav .nav-link {
                    color: var(--text-dark) !important;
                    font-weight: 500;
                    margin: 0 1rem;
                    transition: color 0.3s ease;
                }

                .navbar-nav .nav-link:hover,
                .navbar-nav .nav-link.active {
                    color: var(--primary-color) !important;
                }



                /* Hero Section */
                .hero-section {
                    background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
                    color: white;
                    padding: 100px 0 80px;
                    text-align: center;
                }

                .hero-section h1 {
                    font-size: 3rem;
                    font-weight: bold;
                    margin-bottom: 1rem;
                }

                .hero-section p {
                    font-size: 1.2rem;
                    opacity: 0.9;
                }

                /* Search Section */
                .search-section {
                    background: var(--secondary-color);
                    padding: 3rem 0;
                }

                .search-box {
                    max-width: 600px;
                    margin: 0 auto;
                    position: relative;
                }

                .search-box input {
                    border-radius: 50px;
                    padding: 15px 25px;
                    font-size: 1.1rem;
                    border: 2px solid #e9ecef;
                    width: 100%;
                    padding-right: 60px;
                }

                .search-box input:focus {
                    border-color: var(--accent-color);
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }

                .search-btn {
                    position: absolute;
                    right: 10px;
                    top: 50%;
                    transform: translateY(-50%);
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    border: none;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    color: white;
                }

                /* FAQ Section */
                .faq-section {
                    padding: 80px 0;
                }

                .faq-categories {
                    margin-bottom: 3rem;
                }

                .category-btn {
                    background: white;
                    border: 2px solid #e9ecef;
                    border-radius: 10px;
                    padding: 15px 25px;
                    margin: 5px;
                    transition: all 0.3s ease;
                    color: var(--text-dark);
                    text-decoration: none;
                    display: inline-block;
                }

                .category-btn:hover,
                .category-btn.active {
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    color: white;
                    border-color: var(--primary-color);
                    transform: translateY(-2px);
                }

                /* FAQ Accordion */
                .faq-accordion {
                    max-width: 900px;
                    margin: 0 auto;
                }

                .accordion-item {
                    border: none;
                    margin-bottom: 1rem;
                    border-radius: 10px;
                    overflow: hidden;
                    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
                }

                .accordion-header {
                    border: none;
                }

                .accordion-button {
                    background: white;
                    border: none;
                    padding: 1.5rem;
                    font-size: 1.1rem;
                    font-weight: 600;
                    color: var(--text-dark);
                }

                .accordion-button:not(.collapsed) {
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    color: white;
                    box-shadow: none;
                }

                .accordion-button:focus {
                    box-shadow: none;
                    border: none;
                }

                .accordion-button::after {
                    background-image: url("data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23333'><path fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/></svg>");
                }

                .accordion-button:not(.collapsed)::after {
                    background-image: url("data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23fff'><path fill-rule='evenodd' d='M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z'/></svg>");
                }

                .accordion-body {
                    padding: 1.5rem;
                    background: #fafbfc;
                    color: #666;
                    line-height: 1.7;
                }

                /* Stats Section */
                .stats-section {
                    background: var(--secondary-color);
                    padding: 60px 0;
                }

                .stat-item {
                    text-align: center;
                    padding: 2rem;
                }

                .stat-icon {
                    width: 80px;
                    height: 80px;
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    color: white;
                    font-size: 2rem;
                    margin: 0 auto 1rem;
                }

                .stat-number {
                    font-size: 2.5rem;
                    font-weight: bold;
                    color: var(--primary-color);
                    margin-bottom: 0.5rem;
                }

                .stat-label {
                    color: #666;
                    font-size: 1.1rem;
                }

                /* Footer */
                .footer {
                    background: var(--text-dark);
                    color: white;
                    padding: 3rem 0 1rem;
                    margin-top: 5rem;
                }

                .footer h5 {
                    color: var(--accent-color);
                    margin-bottom: 1rem;
                }

                .footer a {
                    color: #ccc;
                    text-decoration: none;
                    transition: color 0.3s ease;
                }

                .footer a:hover {
                    color: var(--accent-color);
                }

                .footer-bottom {
                    border-top: 1px solid #444;
                    padding-top: 1rem;
                    margin-top: 2rem;
                    text-align: center;
                    color: #999;
                }

                /* Hide/Show FAQ items based on search */
                .faq-item.hidden {
                    display: none;
                }

                .no-results {
                    text-align: center;
                    padding: 3rem;
                    color: #666;
                    display: none;
                }
            </style>
        </head>

        <body>
            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/layout/header.jsp" />

            <!-- Hero Section -->
            <section class="hero-section">
                <div class="container">
                    <h1>Câu hỏi thường gặp</h1>
                    <p>Tìm câu trả lời cho những thắc mắc phổ biến về HRMS</p>
                </div>
            </section>

            <!-- Search Section -->
            <section class="search-section">
                <div class="container">
                    <div class="search-box">
                        <input type="text" id="faqSearch" placeholder="Tìm kiếm câu hỏi...">
                        <button class="search-btn">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
            </section>

            <!-- FAQ Categories -->
            <section class="faq-section">
                <div class="container">
                    <div class="faq-categories text-center">
                        <a href="#" class="category-btn active" data-category="all">Tất cả</a>
                        <a href="#" class="category-btn" data-category="general">Tổng quan</a>
                        <a href="#" class="category-btn" data-category="employee">Nhân viên</a>
                        <a href="#" class="category-btn" data-category="attendance">Chấm công</a>
                        <a href="#" class="category-btn" data-category="payroll">Lương</a>
                        <a href="#" class="category-btn" data-category="leave">Nghỉ phép</a>
                        <a href="#" class="category-btn" data-category="technical">Kỹ thuật</a>
                    </div>

                    <!-- FAQ Accordion -->
                    <div class="faq-accordion">
                        <div class="accordion" id="faqAccordion">

                            <!-- General FAQs -->
                            <div class="accordion-item faq-item" data-category="general">
                                <div class="accordion-header" id="faq1">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse1">
                                        HRMS là gì?
                                    </button>
                                </div>
                                <div id="collapse1" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        HRMS (Human Resource Management System) là hệ thống quản lý nhân sự tích hợp,
                                        giúp doanh nghiệp số hóa và tự động hóa các quy trình HR như quản lý thông tin
                                        nhân viên, chấm công, tính lương, quản lý nghỉ phép và tuyển dụng.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="general">
                                <div class="accordion-header" id="faq2">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse2">
                                        Ai có thể sử dụng hệ thống HRMS?
                                    </button>
                                </div>
                                <div id="collapse2" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Hệ thống HRMS phù hợp với các doanh nghiệp từ nhỏ đến lớn, bao gồm HR Manager,
                                        Quản lý trực tiếp và nhân viên. Mỗi vai trò sẽ có quyền truy cập và chức năng
                                        phù hợp với công việc của họ.
                                    </div>
                                </div>
                            </div>

                            <!-- Employee Management FAQs -->
                            <div class="accordion-item faq-item" data-category="employee">
                                <div class="accordion-header" id="faq3">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse3">
                                        Làm thế nào để thêm nhân viên mới vào hệ thống?
                                    </button>
                                </div>
                                <div id="collapse3" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        HR Manager có thể thêm nhân viên mới thông qua menu "Quản lý nhân viên" > "Thêm
                                        nhân viên mới". Điền đầy đủ thông tin cá nhân, thông tin công việc và tài khoản
                                        đăng nhập cho nhân viên.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="employee">
                                <div class="accordion-header" id="faq4">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse4">
                                        Nhân viên có thể tự cập nhật thông tin cá nhân không?
                                    </button>
                                </div>
                                <div id="collapse4" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Có, nhân viên có thể cập nhật một số thông tin cá nhân như địa chỉ, số điện
                                        thoại, thông tin liên hệ khẩn cấp thông qua trang "Hồ sơ cá nhân". Các thông tin
                                        nhạy cảm khác cần được HR phê duyệt.
                                    </div>
                                </div>
                            </div>

                            <!-- Attendance FAQs -->
                            <div class="accordion-item faq-item" data-category="attendance">
                                <div class="accordion-header" id="faq5">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse5">
                                        Cách thức chấm công trong hệ thống như thế nào?
                                    </button>
                                </div>
                                <div id="collapse5" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Hệ thống hỗ trợ nhiều phương thức chấm công: web-based check-in/out, tích hợp
                                        máy chấm công, và mobile app. Nhân viên có thể chấm công trực tiếp trên hệ thống
                                        hoặc dữ liệu sẽ được đồng bộ từ thiết bị chấm công.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="attendance">
                                <div class="accordion-header" id="faq6">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse6">
                                        Nhân viên quên chấm công thì xử lý như thế nào?
                                    </button>
                                </div>
                                <div id="collapse6" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Nhân viên có thể gửi yêu cầu chỉnh sửa chấm công thông qua hệ thống. Quản lý
                                        trực tiếp hoặc HR sẽ xem xét và phê duyệt yêu cầu dựa trên chính sách công ty.
                                    </div>
                                </div>
                            </div>

                            <!-- Payroll FAQs -->
                            <div class="accordion-item faq-item" data-category="payroll">
                                <div class="accordion-header" id="faq7">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse7">
                                        Lương được tính toán như thế nào?
                                    </button>
                                </div>
                                <div id="collapse7" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Hệ thống tự động tính lương dựa trên dữ liệu chấm công, lương cơ bản, các khoản
                                        phụ cấp, thưởng và khấu trừ. Công thức tính lương có thể được tùy chỉnh theo
                                        chính sách của từng công ty.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="payroll">
                                <div class="accordion-header" id="faq8">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse8">
                                        Nhân viên có thể xem bảng lương của mình không?
                                    </button>
                                </div>
                                <div id="collapse8" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Có, nhân viên có thể xem bảng lương chi tiết, lịch sử lương và tải về phiếu
                                        lương của mình thông qua trang "Lương & Phúc lợi" trên tài khoản cá nhân.
                                    </div>
                                </div>
                            </div>

                            <!-- Leave Management FAQs -->
                            <div class="accordion-item faq-item" data-category="leave">
                                <div class="accordion-header" id="faq9">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse9">
                                        Cách đăng ký nghỉ phép trên hệ thống?
                                    </button>
                                </div>
                                <div id="collapse9" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Nhân viên truy cập menu "Nghỉ phép" > "Đăng ký nghỉ phép mới", chọn loại phép,
                                        thời gian nghỉ và lý do. Đơn sẽ được gửi đến quản lý trực tiếp để phê duyệt.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="leave">
                                <div class="accordion-header" id="faq10">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse10">
                                        Làm sao để kiểm tra số ngày phép còn lại?
                                    </button>
                                </div>
                                <div id="collapse10" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Thông tin số ngày phép còn lại được hiển thị trên dashboard cá nhân và trang
                                        "Nghỉ phép". Hệ thống tự động cập nhật số dư phép sau mỗi lần nghỉ được phê
                                        duyệt.
                                    </div>
                                </div>
                            </div>

                            <!-- Technical FAQs -->
                            <div class="accordion-item faq-item" data-category="technical">
                                <div class="accordion-header" id="faq11">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse11">
                                        Quên mật khẩu đăng nhập phải làm sao?
                                    </button>
                                </div>
                                <div id="collapse11" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Nhân viên có thể sử dụng chức năng "Quên mật khẩu" trên trang đăng nhập hoặc
                                        liên hệ với IT/HR để được hỗ trợ reset mật khẩu.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="technical">
                                <div class="accordion-header" id="faq12">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse12">
                                        Hệ thống có hỗ trợ trên thiết bị di động không?
                                    </button>
                                </div>
                                <div id="collapse12" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Có, hệ thống được thiết kế responsive, hoạt động tốt trên mọi thiết bị. Chúng
                                        tôi cũng đang phát triển ứng dụng mobile chuyên dụng để mang lại trải nghiệm tốt
                                        hơn.
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-item faq-item" data-category="technical">
                                <div class="accordion-header" id="faq13">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse13">
                                        Dữ liệu trong hệ thống có được bảo mật không?
                                    </button>
                                </div>
                                <div id="collapse13" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                    <div class="accordion-body">
                                        Chúng tôi áp dụng các biện pháp bảo mật cao cấp bao gồm mã hóa dữ liệu, xác thực
                                        đa lớp, phân quyền chi tiết và sao lưu định kỳ để đảm bảo an toàn thông tin.
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- No Results Message -->
                        <div class="no-results">
                            <i class="fas fa-search fa-3x mb-3" style="color: #ccc;"></i>
                            <h5>Không tìm thấy câu hỏi phù hợp</h5>
                            <p>Hãy thử từ khóa khác hoặc <a href="${pageContext.request.contextPath}/contact">liên hệ
                                    với chúng tôi</a> để được hỗ trợ.</p>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Stats Section -->
            <section class="stats-section">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <i class="fas fa-question-circle"></i>
                                </div>
                                <div class="stat-number">50+
                                <div class="stat-label">Câu hỏi thường gặp</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <div class="stat-number">1000+</div>
                                <div class="stat-label">Người dùng hài lòng</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <div class="stat-number">24/7</div>
                                <div class="stat-label">Hỗ trợ trực tuyến</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <i class="fas fa-star"></i>
                                </div>
                                <div class="stat-number">4.9/5</div>
                                <div class="stat-label">Đánh giá từ khách hàng</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Footer -->
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

            <!-- FAQ JavaScript -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const searchInput = document.getElementById('faqSearch');
                    const categoryBtns = document.querySelectorAll('.category-btn');
                    const faqItems = document.querySelectorAll('.faq-item');
                    const noResults = document.querySelector('.no-results');

                    // Search functionality
                    searchInput.addEventListener('input', function () {
                        const searchTerm = this.value.toLowerCase();
                        let hasResults = false;

                        faqItems.forEach(item => {
                            const question = item.querySelector('.accordion-button').textContent.toLowerCase();
                            const answer = item.querySelector('.accordion-body').textContent.toLowerCase();

                            if (question.includes(searchTerm) || answer.includes(searchTerm)) {
                                item.style.display = 'block';
                                item.classList.remove('hidden');
                                hasResults = true;
                            } else {
                                item.style.display = 'none';
                                item.classList.add('hidden');
                            }
                        });

                        noResults.style.display = hasResults ? 'none' : 'block';
                    });

                    // Category filtering
                    categoryBtns.forEach(btn => {
                        btn.addEventListener('click', function (e) {
                            e.preventDefault();

                            // Update active button
                            categoryBtns.forEach(b => b.classList.remove('active'));
                            this.classList.add('active');

                            const category = this.dataset.category;
                            let hasResults = false;

                            faqItems.forEach(item => {
                                if (category === 'all' || item.dataset.category === category) {
                                    item.style.display = 'block';
                                    item.classList.remove('hidden');
                                    hasResults = true;
                                } else {
                                    item.style.display = 'none';
                                    item.classList.add('hidden');
                                }
                            });

                            // Clear search when filtering by category
                            searchInput.value = '';
                            noResults.style.display = hasResults ? 'none' : 'block';
                        });
                    });
                });
            </script>
        </body>

        </html>