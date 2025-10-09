<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Về chúng tôi - HRMS</title>

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

                /* Header */
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

                /* Breadcrumb */
                .breadcrumb-section {
                    background: var(--secondary-color);
                    padding: 1rem 0;
                }

                .breadcrumb {
                    background: none;
                    margin: 0;
                }

                .breadcrumb-item a {
                    color: var(--primary-color);
                    text-decoration: none;
                }

                /* Features Section */
                .features-section {
                    padding: 80px 0;
                }

                .feature-box {
                    text-align: center;
                    padding: 2rem;
                    border-radius: 10px;
                    transition: transform 0.3s ease, box-shadow 0.3s ease;
                    height: 100%;
                }

                .feature-box:hover {
                    transform: translateY(-10px);
                    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                }

                .feature-icon {
                    width: 80px;
                    height: 80px;
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 0 auto 1.5rem;
                    color: white;
                    font-size: 2rem;
                }

                .feature-box h4 {
                    color: var(--primary-color);
                    margin-bottom: 1rem;
                    font-weight: 600;
                }

                /* Story Section */
                .story-section {
                    background: var(--secondary-color);
                    padding: 80px 0;
                }

                .story-content h2 {
                    color: var(--primary-color);
                    font-weight: bold;
                    margin-bottom: 1rem;
                }

                .story-content h5 {
                    color: var(--accent-color);
                    margin-bottom: 1.5rem;
                }

                .story-content p {
                    margin-bottom: 1.5rem;
                    text-align: justify;
                }

                .story-image {
                    border-radius: 15px;
                    overflow: hidden;
                    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
                    position: relative;
                    max-width: 800px;
                    margin: 0 auto;
                }

                .story-image img {
                    width: 100%;
                    height: auto;
                    object-fit: cover;
                    background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
                    background-image: url('https://images.unsplash.com/photo-1522071820081-009f0129c71c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&h=500&q=80');
                    background-size: cover;
                    background-position: center;
                    max-height: none;
                    min-height: 400px;
                    display: block;
                }

                .story-content p {
                    margin-bottom: 1.5rem;
                    font-size: 1.1rem;
                    line-height: 1.8;
                }

                .story-content h2 {
                    margin-bottom: 1rem;
                    font-weight: bold;
                }

                .story-content h5 {
                    margin-bottom: 2rem;
                }

                /* Values Section */
                .values-section {
                    padding: 80px 0;
                }

                .section-title {
                    text-align: center;
                    margin-bottom: 3rem;
                }

                .section-title h2 {
                    color: var(--primary-color);
                    font-weight: bold;
                    margin-bottom: 1rem;
                }

                .section-title p {
                    font-size: 1.1rem;
                    color: #666;
                }

                .value-item {
                    background: white;
                    border-radius: 10px;
                    padding: 2rem;
                    margin-bottom: 2rem;
                    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
                    transition: transform 0.3s ease;
                }

                .value-item:hover {
                    transform: translateY(-5px);
                }

                .value-item .fas {
                    color: var(--accent-color);
                    font-size: 2.5rem;
                    margin-bottom: 1rem;
                }

                .value-item h4 {
                    color: var(--primary-color);
                    margin-bottom: 1rem;
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
            </style>
        </head>

        <body>
            <!-- Navigation -->
            <jsp:include page="/WEB-INF/views/layout/header.jsp" />

            <!-- Hero Section -->
            <section class="hero-section">
                <div class="container">
                    <h1>Về chúng tôi</h1>
                    <p>Khám phá câu chuyện và tầm nhìn của HRMS</p>
                </div>
            </section>

            <!-- Breadcrumb -->
            <section class="breadcrumb-section">
                <div class="container">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/">Trang chủ</a>
                            </li>
                            <li class="breadcrumb-item active">Về chúng tôi</li>
                        </ol>
                    </nav>
                </div>
            </section>

            <!-- Features Section -->
            <section class="features-section">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="feature-box">
                                <div class="feature-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h4>Quản lý nhân sự</h4>
                                <p>Hệ thống quản lý thông tin nhân viên toàn diện với các tính năng hiện đại và tiện
                                    lợi.</p>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="feature-box">
                                <div class="feature-icon">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <h4>Chấm công thông minh</h4>
                                <p>Theo dõi thời gian làm việc chính xác với công nghệ chấm công hiện đại và báo cáo chi
                                    tiết.</p>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="feature-box">
                                <div class="feature-icon">
                                    <i class="fas fa-money-bill-wave"></i>
                                </div>
                                <h4>Quản lý lương thưởng</h4>
                                <p>Tính toán lương, thưởng, phụ cấp tự động với các công thức linh hoạt và minh bạch.
                                </p>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4">
                            <div class="feature-box">
                                <div class="feature-icon">
                                    <i class="fas fa-chart-bar"></i>
                                </div>
                                <h4>Báo cáo thống kê</h4>
                                <p>Dashboard trực quan với các báo cáo phân tích sâu để hỗ trợ quyết định quản lý.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Story Section -->
            <section class="story-section">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-12">
                            <div class="text-center mb-5">
                                <h2 style="color: var(--primary-color); font-weight: bold;">Câu chuyện của chúng tôi
                                </h2>
                                <h5 style="color: var(--accent-color);">Khởi đầu từ một ý tưởng đơn giản</h5>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12">
                            <div class="story-image mb-4">
                                <img src="${pageContext.request.contextPath}/assets/images/Company.jpg" alt="HRMS">
                            </div>
                        </div>
                    </div>
                    <div class="row justify-content-center">
                        <div class="col-lg-8">
                            <div class="story-content text-center">
                                <p>
                                    HRMS được sinh ra từ nhu cầu thực tế của các doanh nghiệp trong việc quản lý nhân sự
                                    một cách hiệu quả.
                                    Chúng tôi nhận thấy rằng nhiều công ty vẫn đang sử dụng các phương pháp thủ công
                                    hoặc các hệ thống lỗi thời,
                                    gây khó khăn trong việc theo dõi và quản lý thông tin nhân viên.
                                </p>
                                <p>
                                    Với đội ngũ phát triển trẻ và nhiệt huyết, chúng tôi đã nghiên cứu và xây dựng một
                                    hệ thống quản lý nhân sự
                                    hiện đại, dễ sử dụng và phù hợp với các doanh nghiệp Việt Nam.
                                </p>
                                <p>
                                    Hành trình của chúng tôi bắt đầu từ môi trường học thuật tại FPT University, nơi
                                    chúng tôi được học hỏi
                                    và áp dụng các công nghệ tiên tiến để tạo ra sản phẩm có giá trị thực tế.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Values Section -->
            <section class="values-section">
                <div class="container">
                    <div class="section-title">
                        <h2>Giá trị cốt lõi</h2>
                        <p>Những nguyên tắc định hướng mọi hoạt động của chúng tôi</p>
                    </div>

                    <div class="row">
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-lightbulb"></i>
                                <h4>Đổi mới sáng tạo</h4>
                                <p>Chúng tôi luôn tìm kiếm những giải pháp công nghệ mới để cải thiện trải nghiệm người
                                    dùng và nâng cao hiệu quả quản lý.</p>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-shield-alt"></i>
                                <h4>Bảo mật tuyệt đối</h4>
                                <p>Thông tin nhân sự là tài sản quý giá. Chúng tôi cam kết bảo vệ dữ liệu với các tiêu
                                    chuẩn bảo mật cao nhất.</p>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-handshake"></i>
                                <h4>Hỗ trợ tận tâm</h4>
                                <p>Đội ngũ hỗ trợ chuyên nghiệp luôn sẵn sàng giúp đỡ khách hàng 24/7 với thái độ nhiệt
                                    tình và chuyên nghiệp.</p>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-cogs"></i>
                                <h4>Tùy biến linh hoạt</h4>
                                <p>Hiểu rằng mỗi doanh nghiệp có nhu cầu riêng, chúng tôi thiết kế hệ thống có thể tùy
                                    biến theo yêu cầu cụ thể.</p>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-rocket"></i>
                                <h4>Hiệu suất cao</h4>
                                <p>Sử dụng công nghệ hiện đại để đảm bảo hệ thống hoạt động nhanh chóng, ổn định và đáng
                                    tin cậy.</p>
                            </div>
                        </div>
                        <div class="col-lg-4 col-md-6">
                            <div class="value-item">
                                <i class="fas fa-graduation-cap"></i>
                                <h4>Học hỏi không ngừng</h4>
                                <p>Chúng tôi không ngừng học hỏi và cập nhật kiến thức để mang đến những giải pháp tốt
                                    nhất cho khách hàng.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Footer -->
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </body>

        </html>