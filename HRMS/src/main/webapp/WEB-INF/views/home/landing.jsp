<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="../layout/head.jsp">
                <jsp:param name="pageTitle" value="Home" />
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
                                <h1>Smart & Efficient <br>Human Resource Management</h1>
                                <p>Modern HRMS helps businesses optimize HR management processes, from recruitment to
                                    career development.</p>
                                <div class="hero-buttons">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.userId}">
                                            <!-- User logged in -->
                                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-hero">
                                                <i class="fas fa-tachometer-alt me-2"></i>Go to Dashboard
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- User not logged in -->
                                            <a href="${pageContext.request.contextPath}/login" class="btn btn-hero">
                                                <i class="fas fa-sign-in-alt me-2"></i>Login Now
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${pageContext.request.contextPath}/about" class="btn btn-hero-outline">
                                        <i class="fas fa-info-circle me-2"></i>Learn More
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
                        <h2>Key Features</h2>
                        <p>Discover powerful features that help businesses manage HR effectively</p>
                    </div>

                    <div class="row">
                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="100">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-users"></i>
                                </div>
                                <h4>Employee Management</h4>
                                <p>Comprehensive employee information management, from personal records to work history
                                    and performance evaluation.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="200">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-clock"></i>
                                </div>
                                <h4>Smart Attendance</h4>
                                <p>Automated attendance system with multiple methods: web, mobile app, and integrated
                                    time clock devices.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="300">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-money-bill-wave"></i>
                                </div>
                                <h4>Automatic Payroll</h4>
                                <p>Accurate salary calculation based on attendance, allowances, bonuses, and deductions
                                    according to regulations.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="400">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-calendar-alt"></i>
                                </div>
                                <h4>Leave Management</h4>
                                <p>Register, approve, and track leave online with automated workflow and real-time
                                    notifications.</p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="500">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-chart-bar"></i>
                                </div>
                                <h4>Reports & Analytics</h4>
                                <p>Intuitive dashboard with detailed reports on HR, performance, and development trends.
                                </p>
                            </div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-4" data-aos="fade-up" data-aos-delay="600">
                            <div class="feature-card">
                                <div class="feature-icon">
                                    <i class="fas fa-shield-alt"></i>
                                </div>
                                <h4>Advanced Security</h4>
                                <p>Protect HR data with advanced encryption, detailed permissions, and compliance with
                                    security standards.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

        

           

            <!-- Services Section -->
            <section class="services-section">
                <div class="container">
                    <div class="section-title" data-aos="fade-up">
                        <h2>Comprehensive Services</h2>
                        <p>Complete HR solutions from A to Z for businesses of all sizes</p>
                    </div>

                    <div class="row">
                        <div class="col-lg-6 mb-4" data-aos="fade-right" data-aos-delay="100">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-user-plus"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Recruitment & Selection</h4>
                                    <p>Manage the entire recruitment process from job posting, resume screening,
                                        interviews to hiring decisions.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-left" data-aos-delay="200">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-graduation-cap"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Training & Development</h4>
                                    <p>Plan training, track learning progress, and evaluate the effectiveness of
                                        employee development programs.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-right" data-aos-delay="300">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-star"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Performance Evaluation</h4>
                                    <p>KPI evaluation system, 360 feedback, and personal goal tracking to improve work
                                        efficiency.</p>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6 mb-4" data-aos="fade-left" data-aos-delay="400">
                            <div class="service-item">
                                <div class="service-icon">
                                    <i class="fas fa-cogs"></i>
                                </div>
                                <div class="service-content">
                                    <h4>Process Automation</h4>
                                    <p>Automated workflows for HR processes, minimizing manual work and increasing
                                        operational efficiency.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- CTA Section -->
            <section class="cta-section">
                <div class="container text-center" data-aos="fade-up">
                    <h2>Ready for HR Digital Transformation?</h2>
                    <p>Start your HR digitalization journey with HRMS today</p>
                    <a href="${pageContext.request.contextPath}/contact" class="btn btn-hero">
                        <i class="fas fa-rocket me-2"></i>Contact for Consultation
                    </a>
                </div>
            </section>

            <!-- Footer -->
            <jsp:include page="../layout/footer.jsp" />
        </body>

        </html>