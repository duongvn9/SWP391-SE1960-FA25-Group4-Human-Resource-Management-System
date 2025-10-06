<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <jsp:include page="layout/head.jsp">
                <jsp:param name="pageTitle" value="Liên hệ - HRMS" />
                <jsp:param name="pageCss" value="contact.css" />
            </jsp:include>
        </head>

        <body>
            <!-- Header -->
            <jsp:include page="layout/header.jsp">
                <jsp:param name="currentPage" value="contact" />
            </jsp:include>

            <!-- Hero Section -->
            <section class="hero-section">
                <div class="container">
                    <h1 data-aos="fade-up">Liên hệ với chúng tôi</h1>
                    <p data-aos="fade-up" data-aos-delay="200">Chúng tôi luôn sẵn sàng hỗ trợ bạn với các giải pháp quản
                        lý nhân sự tối ưu</p>
                </div>
            </section>

            <!-- Contact Section -->
            <section class="contact-section">
                <div class="container">
                    <!-- Success/Error Messages -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert" data-aos="fade-down">
                            <i class="fas fa-check-circle me-2"></i>
                            <c:out value="${successMessage}" />
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert" data-aos="fade-down">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <c:out value="${errorMessage}" />
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <div class="row">
                        <!-- Contact Information -->
                        <div class="col-lg-5 col-md-6 mb-5" data-aos="fade-right">
                            <div class="contact-info">
                                <h3><i class="fas fa-info-circle me-2"></i>Thông tin liên hệ</h3>

                                <div class="contact-item" data-aos="fade-up" data-aos-delay="100">
                                    <div class="contact-icon">
                                        <i class="fas fa-map-marker-alt"></i>
                                    </div>
                                    <div class="contact-details">
                                        <h5>Địa chỉ</h5>
                                        <p>Đại học FPT Hà Nội<br>Khu Công nghệ cao Hòa Lạc<br>Thạch Thất, Hà Nội</p>
                                    </div>
                                </div>

                                <div class="contact-item" data-aos="fade-up" data-aos-delay="200">
                                    <div class="contact-icon">
                                        <i class="fas fa-phone"></i>
                                    </div>
                                    <div class="contact-details">
                                        <h5>Điện thoại</h5>
                                        <p>Hotline: +84 123 456 789<br>Support: +84 987 654 321</p>
                                    </div>
                                </div>

                                <div class="contact-item" data-aos="fade-up" data-aos-delay="300">
                                    <div class="contact-icon">
                                        <i class="fas fa-envelope"></i>
                                    </div>
                                    <div class="contact-details">
                                        <h5>Email</h5>
                                        <p>support@hrms.com<br>info@hrms.com</p>
                                    </div>
                                </div>

                                <div class="contact-item" data-aos="fade-up" data-aos-delay="400">
                                    <div class="contact-icon">
                                        <i class="fas fa-clock"></i>
                                    </div>
                                    <div class="contact-details">
                                        <h5>Giờ làm việc</h5>
                                        <p>Thứ 2 - Thứ 6: 8:00 - 17:30<br>Thứ 7: 8:00 - 12:00<br>Chủ nhật: Nghỉ</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Contact Form -->
                        <div class="col-lg-7 col-md-6" data-aos="fade-left">
                            <div class="contact-form">
                                <h3><i class="fas fa-paper-plane me-2"></i>Gửi tin nhắn</h3>
                                <p>Hãy để lại thông tin, chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.</p>

                                <form action="${pageContext.request.contextPath}/contact" method="post"
                                    id="contactForm">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="name">Họ và tên <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="name" name="name"
                                                    placeholder="Nhập họ và tên của bạn" required value="${param.name}">
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="email">Email <span class="text-danger">*</span></label>
                                                <input type="email" class="form-control" id="email" name="email"
                                                    placeholder="Nhập địa chỉ email" required value="${param.email}">
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="phone">Số điện thoại</label>
                                                <input type="tel" class="form-control" id="phone" name="phone"
                                                    placeholder="Nhập số điện thoại" value="${param.phone}">
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="subject">Chủ đề</label>
                                                <select class="form-control" id="subject" name="subject">
                                                    <option value="">Chọn chủ đề</option>
                                                    <option value="support" ${param.subject=='support' ? 'selected' : ''
                                                        }>Hỗ trợ kỹ thuật</option>
                                                    <option value="demo" ${param.subject=='demo' ? 'selected' : '' }>Yêu
                                                        cầu demo</option>
                                                    <option value="pricing" ${param.subject=='pricing' ? 'selected' : ''
                                                        }>Báo giá sản phẩm</option>
                                                    <option value="partnership" ${param.subject=='partnership'
                                                        ? 'selected' : '' }>Hợp tác kinh doanh</option>
                                                    <option value="other" ${param.subject=='other' ? 'selected' : '' }>
                                                        Khác</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="message">Nội dung <span class="text-danger">*</span></label>
                                        <textarea class="form-control" id="message" name="message" rows="6"
                                            placeholder="Nhập nội dung tin nhắn của bạn..."
                                            required>${param.message}</textarea>
                                    </div>

                                    <div class="text-center">
                                        <button type="submit" class="btn btn-primary btn-lg">
                                            <i class="fas fa-paper-plane me-2"></i>Gửi tin nhắn
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Map Section -->
            <section class="map-section">
                <div class="container">
                    <h3 data-aos="fade-up">
                        <i class="fas fa-map-marked-alt me-2"></i>Vị trí của chúng tôi
                    </h3>
                    <div class="map-container" data-aos="fade-up" data-aos-delay="200">
                        <div class="map-placeholder">
                            <div>
                                <i class="fas fa-map-marker-alt fa-3x mb-3"></i>
                                <h5>Đại học FPT Hà Nội</h5>
                                <p>Khu Công nghệ cao Hòa Lạc, Thạch Thất, Hà Nội</p>
                                <small class="text-muted">Bản đồ sẽ được tích hợp trong phiên bản tiếp theo</small>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Footer -->
            <jsp:include page="layout/footer.jsp" />

            <script>
                // Form validation and enhancement
                document.addEventListener('DOMContentLoaded', function () {
                    const form = document.getElementById('contactForm');
                    const inputs = form.querySelectorAll('input, textarea, select');

                    // Add floating label effect
                    inputs.forEach(input => {
                        input.addEventListener('focus', function () {
                            this.parentNode.classList.add('focused');
                        });

                        input.addEventListener('blur', function () {
                            if (!this.value) {
                                this.parentNode.classList.remove('focused');
                            }
                        });

                        // Initial check for pre-filled values
                        if (input.value) {
                            input.parentNode.classList.add('focused');
                        }
                    });

                    // Form submission handling
                    form.addEventListener('submit', function (e) {
                        const submitBtn = form.querySelector('button[type="submit"]');
                        const originalText = submitBtn.innerHTML;

                        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang gửi...';
                        submitBtn.disabled = true;

                        // Re-enable after 3 seconds if form doesn't submit
                        setTimeout(function () {
                            if (submitBtn.disabled) {
                                submitBtn.innerHTML = originalText;
                                submitBtn.disabled = false;
                            }
                        }, 3000);
                    });

                    // Phone number formatting
                    const phoneInput = document.getElementById('phone');
                    phoneInput.addEventListener('input', function (e) {
                        let value = e.target.value.replace(/\D/g, '');
                        if (value.length > 0) {
                            if (value.length <= 3) {
                                value = value;
                            } else if (value.length <= 6) {
                                value = value.slice(0, 3) + ' ' + value.slice(3);
                            } else if (value.length <= 9) {
                                value = value.slice(0, 3) + ' ' + value.slice(3, 6) + ' ' + value.slice(6);
                            } else {
                                value = value.slice(0, 3) + ' ' + value.slice(3, 6) + ' ' + value.slice(6, 9);
                            }
                        }
                        e.target.value = value;
                    });
                });
            </script>
        </body>

        </html>