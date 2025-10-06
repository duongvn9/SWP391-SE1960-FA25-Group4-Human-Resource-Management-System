<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Trang chủ" />
                <jsp:param name="pageCss" value="landing.css" />
            </jsp:include>
        </head>

        <body>
            <!-- Header -->
            <jsp:include page="../layout/header.jsp">
                <jsp:param name="currentPage" value="home" />
            </jsp:include>

            <!-- Hero Section -->
            <section class="hero-section">
                <div class="container">
                    <!-- Logout Success Message -->
                    <c:if test="${not empty logoutMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert" data-aos="fade-down">
                            <i class="fas fa-check-circle me-2"></i>${logoutMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <div class="row align-items-center">
                        <div class="col-lg-6">
                            <div class="hero-content" data-aos="fade-right">
                                <h1>Quản lý Nhân sự <br>Thông minh & Hiệu quả</h1>
                                <p>Hệ thống HRMS hiện đại giúp doanh nghiệp tối ưu hóa quy trình quản lý nhân sự, từ
                                    tuyển dụng đến phát triển nghề nghiệp.</p>
                                <div class="hero-buttons">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.userId}">
                                            <!-- User đã đăng nhập -->
                                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-hero">
                                                <i class="fas fa-tachometer-alt me-2"></i>Vào Dashboard
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- User chưa đăng nhập -->
                                            <a href="${pageContext.request.contextPath}/login" class="btn btn-hero">
                                                <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập ngay
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${pageContext.request.contextPath}/about" class="btn btn-hero-outline">
                                        <i class="fas fa-info-circle me-2"></i>Tìm hiểu thêm
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="text-center" data-aos="fade-left">
                                <i class="fas fa-chart-line" style="font-size: 20rem; opacity: 0.1;"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Features Section -->
            <section class="features-section" id="features">
                <div class="container">
                    <div class="section-title" data-aos="fade-up">
                        <h2>Tính năng nổi bật</h2>
                        <p>Khám phá những tính năng mạnh mẽ giúp doanh nghiệp quản lý nhân sự hiệu quả</p>
                    </div>

                    <div class="row">
                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="100">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h4>Quản lý Nhân viên</h4>
                                <p>Quản lý thông tin nhân viên toàn diện, từ hồ sơ cá nhân đến lịch sử công việc và đánh
                                    giá hiệu suất.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="200">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <h4>Chấm công Thông minh</h4>
                                <p>Hệ thống chấm công tự động với nhiều phương thức: web, mobile app, và tích hợp máy
                                    chấm công.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="300">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-money-bill-wave"></i>
                                </div>
                                <h4>Tính lương Tự động</h4>
                                <p>Tính toán lương chính xác dựa trên chấm công, phụ cấp, thưởng và các khoản khấu trừ
                                    theo quy định.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="400">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-calendar-alt"></i>
                                </div>
                                <h4>Quản lý Nghỉ phép</h4>
                                <p>Đăng ký, phê duyệt và theo dõi nghỉ phép trực tuyến với workflow tự động và thông báo
                                    real-time.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="500">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-chart-bar"></i>
                                </div>
                                <h4>Báo cáo & Phân tích</h4>
                                <p>Dashboard trực quan với các báo cáo chi tiết về nhân sự, hiệu suất và xu hướng phát
                                    triển.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="600">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-shield-alt"></i>
                                </div>
                                <h4>Bảo mật Cao cấp</h4>
                                <p>Bảo vệ dữ liệu nhân sự với mã hóa cao cấp, phân quyền chi tiết và tuân thủ các chuẩn
                                    bảo mật.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Stats Section -->
            <section class="stats-section">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-3 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="80">
                            <div class="stat-item">
                                <span class="stat-number" data-count="200">0</span>
                                <div class="stat-label">Doanh nghiệp tin dùng</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="200">
                            <div class="stat-item">
                                <span class="stat-number" data-count="5000">0</span>
                                <div class="stat-label">Nhân viên được quản lý</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="300">
                            <div class="stat-item">
                                <span class="stat-number" data-count="99">0</span>
                                <div class="stat-label">% Uptime hệ thống</div>
                            </div>
                        </div>
                        <div class="col-lg-3 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="400">
                            <div class="stat-item">
                                <span class="stat-number" data-count="24">0</span>
                                <div class="stat-label">Hỗ trợ 24/7</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Services Section -->
            <section class="services-section">
                <div class="container">
                    <div class="section-title" data-aos="fade-up">
                        <h2>Dịch vụ toàn diện</h2>
                        <p>Giải pháp HR từ A đến Z cho mọi quy mô doanh nghiệp</p>
                    </div>

                    <div class="row">
                        <div class="col-lg-6 mb-4" data-aos="fade-right" data-aos-delay="100">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-user-plus"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Tuyển dụng & Tuyển chọn</h4>
                                    <p>Quản lý toàn bộ quy trình tuyển dụng từ đăng tin, sàng lọc hồ sơ, phỏng vấn đến
                                        quyết định tuyển dụng.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-left" data-aos-delay="200">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-graduation-cap"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Đào tạo & Phát triển</h4>
                                    <p>Lập kế hoạch đào tạo, theo dõi tiến độ học tập và đánh giá hiệu quả chương trình
                                        phát triển nhân viên.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-right" data-aos-delay="300">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-star"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Đánh giá Hiệu suất</h4>
                                    <p>Hệ thống đánh giá KPI, 360 feedback và theo dõi mục tiêu cá nhân để nâng cao hiệu
                                        quả làm việc.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-left" data-aos-delay="400">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-cogs"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Tự động hóa Quy trình</h4>
                                    <p>Workflow tự động cho các quy trình HR, giảm thiểu công việc thủ công và tăng hiệu
                                        quả vận hành.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- CTA Section -->
            <section class="cta-section">
                <div class="container text-center" data-aos="fade-up">
                    <h2>Sẵn sàng chuyển đổi số HR?</h2>
                    <p>Bắt đầu hành trình số hóa quản lý nhân sự cùng HRMS ngay hôm nay</p>
                    <a href="${pageContext.request.contextPath}/contact" class="btn btn-hero">
                        <i class="fas fa-rocket me-2"></i>Liên hệ tư vấn
                    </a>
                </div>
            </section>

            <!-- Footer -->
            <jsp:include page="../layout/footer.jsp" />
        </body>

        </html>