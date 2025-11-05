<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="layout/head.jsp">
                <jsp:param name="pageTitle" value="Contact" />
                <jsp:param name="pageCss" value="contact.css" />
            </jsp:include>
        </head>

        <body>
            <jsp:include page="layout/header.jsp">
                <jsp:param name="currentPage" value="contact" />
            </jsp:include>

            <section class="hero-section">
                <div class="container">
                    <h1 data-aos="fade-up">Contact Us</h1>
                    <p data-aos="fade-up" data-aos-delay="200">We are always ready to support you with optimal HR
                        management solutions</p>
                </div>
            </section>

            <section class="contact-section">
                <div class="container">
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
                        <div class="col-lg-5 col-md-6 mb-5" data-aos="fade-right">
                            <div class="contact-info">
                                <h3><i class="fas fa-info-circle me-2"></i>Contact Information</h3>
                                <div class="contact-item" data-aos="fade-up" data-aos-delay="100">
                                    <div class="contact-icon"><i class="fas fa-map-marker-alt"></i></div>
                                    <div class="contact-details">
                                        <h5>Address</h5>
                                        <p>FPT University Hanoi<br>Hoa Lac Hi-Tech Park<br>Thach That, Hanoi</p>
                                    </div>
                                </div>
                                <div class="contact-item" data-aos="fade-up" data-aos-delay="200">
                                    <div class="contact-icon"><i class="fas fa-phone"></i></div>
                                    <div class="contact-details">
                                        <h5>Phone</h5>
                                        <p>Hotline: +84 982188435</p>
                                    </div>
                                </div>
                                <div class="contact-item" data-aos="fade-up" data-aos-delay="300">
                                    <div class="contact-icon"><i class="fas fa-envelope"></i></div>
                                    <div class="contact-details">
                                        <h5>Email</h5>
                                        <p>hrms8386@gmail.com</p>
                                    </div>
                                </div>
                                <div class="contact-item" data-aos="fade-up" data-aos-delay="400">
                                    <div class="contact-icon"><i class="fas fa-clock"></i></div>
                                    <div class="contact-details">
                                        <h5>Working Hours</h5>
                                        <p>Monday - Friday: 8:00 AM - 5:30 PM<br>Saturday: 8:00 AM - 12:00 PM<br>Sunday:
                                            Closed</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-7 col-md-6" data-aos="fade-left">
                            <div class="contact-form">
                                <h3><i class="fas fa-paper-plane me-2"></i>Send Message</h3>
                                <p>Leave your information, and we will contact you as soon as possible.</p>
                                <div id="formAlertContainer"></div>

                                <form id="contactForm">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="fullName">Full Name <span
                                                        class="text-danger">*</span></label>
                                                <input type="text" class="form-control" id="fullName" name="fullName"
                                                    placeholder="Enter your full name">
                                                <div class="error-msg"></div>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="email">Email <span class="text-danger">*</span></label>
                                                <input type="email" class="form-control" id="email" name="email"
                                                    placeholder="Enter email address">
                                                <div class="error-msg"></div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="phone">Phone Number</label>
                                                <input type="tel" class="form-control" id="phone" name="phone"
                                                    placeholder="Enter phone number">
                                                <div class="error-msg"></div>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="form-group">
                                                <label for="contactType">Contact Type <span
                                                        class="text-danger">*</span></label>
                                                <select class="form-control" id="contactType" name="contactType">
                                                    <option value="">Select contact type</option>
                                                    <c:forEach var="type" items="${contactTypes}">
                                                        <option value="${type.name()}">${type.displayName}</option>
                                                    </c:forEach>
                                                </select>
                                                <div class="error-msg"></div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="subject">Subject <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="subject" name="subject"
                                            placeholder="Enter subject">
                                        <div class="error-msg"></div>
                                    </div>

                                    <div class="form-group">
                                        <label for="message">Message <span class="text-danger">*</span></label>
                                        <textarea class="form-control" id="message" name="message" rows="6"
                                            placeholder="Enter your message..."></textarea>
                                        <div class="error-msg"></div>
                                        <small class="form-text text-muted"><span id="charCount">0</span>/5000
                                            characters</small>
                                    </div>

                                    <div class="text-center">
                                        <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                                            <i class="fas fa-paper-plane me-2"></i>Send Message
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <section class="map-section">
                <div class="container">
                    <h3 data-aos="fade-up"><i class="fas fa-map-marked-alt me-2"></i>Our Location</h3>
                    <div class="map-container" data-aos="fade-up" data-aos-delay="200">
                        <div class="map-placeholder">
                            <div>
                                <i class="fas fa-map-marker-alt fa-3x mb-3"></i>
                                <h5>FPT University Hanoi</h5>
                                <p>Hoa Lac Hi-Tech Park, Thach That, Hanoi</p>
                                <small class="text-muted">Map will be integrated in the next version</small>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <jsp:include page="layout/footer.jsp" />

            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const form = document.getElementById('contactForm');
                    const submitBtn = document.getElementById('submitBtn');
                    const alertContainer = document.getElementById('formAlertContainer');

                    // Character counter
                    const messageTextarea = document.getElementById('message');
                    const charCount = document.getElementById('charCount');
                    messageTextarea.addEventListener('input', () => charCount.textContent = messageTextarea.value.length);

                    // Phone formatting
                    document.getElementById('phone').addEventListener('input', function (e) {
                        let value = e.target.value.replace(/\D/g, '');
                        if (value.length > 10) value = value.slice(0, 10);
                        if (value.length > 6) value = value.slice(0, 3) + ' ' + value.slice(3, 6) + ' ' + value.slice(6);
                        else if (value.length > 3) value = value.slice(0, 3) + ' ' + value.slice(3);
                        e.target.value = value;
                    });

                    // Validation
                    function validate() {
                        let isValid = true;
                        document.querySelectorAll('.error-msg').forEach(el => el.textContent = '');
                        document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

                        const fullName = document.getElementById('fullName').value.trim();
                        const email = document.getElementById('email').value.trim();
                        const phone = document.getElementById('phone').value.replace(/\s/g, '');
                        const contactType = document.getElementById('contactType').value;
                        const subject = document.getElementById('subject').value.trim();
                        const message = document.getElementById('message').value.trim();

                        if (fullName.length < 2) {
                            showError('fullName', 'Please enter your full name (at least 2 characters)');
                            isValid = false;
                        }
                        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                            showError('email', 'Please enter a valid email address');
                            isValid = false;
                        }
                        if (phone && phone.length !== 10) {
                            showError('phone', 'Phone number must be 10 digits');
                            isValid = false;
                        }
                        if (!contactType) {
                            showError('contactType', 'Please select a contact type');
                            isValid = false;
                        }
                        if (subject.length < 5) {
                            showError('subject', 'Subject must be at least 5 characters');
                            isValid = false;
                        }
                        if (message.length < 10) {
                            showError('message', 'Message must be at least 10 characters');
                            isValid = false;
                        }
                        return isValid;
                    }

                    function showError(fieldId, msg) {
                        const field = document.getElementById(fieldId);
                        field.classList.add('is-invalid');
                        field.parentElement.querySelector('.error-msg').textContent = msg;
                    }

                    // Submit
                    form.addEventListener('submit', function (e) {
                        e.preventDefault();
                        if (!validate()) return;

                        const originalText = submitBtn.innerHTML;
                        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Sending...';
                        submitBtn.disabled = true;

                        fetch('${pageContext.request.contextPath}/contact/submit', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({
                                fullName: document.getElementById('fullName').value.trim(),
                                email: document.getElementById('email').value.trim(),
                                phone: document.getElementById('phone').value.replace(/\s/g, ''),
                                contactType: document.getElementById('contactType').value,
                                subject: document.getElementById('subject').value.trim(),
                                message: document.getElementById('message').value.trim()
                            })
                        })
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    alertContainer.innerHTML = '<div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i>' + data.message + (data.contactId ? '<br><small>Reference ID: ' + data.contactId + '</small>' : '') + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>';
                                    form.reset();
                                    charCount.textContent = '0';
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                } else {
                                    alertContainer.innerHTML = '<div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-triangle me-2"></i>' + data.message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>';
                                }
                            })
                            .catch(() => {
                                alertContainer.innerHTML = '<div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-triangle me-2"></i>An error occurred. Please try again later.<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>';
                            })
                            .finally(() => {
                                submitBtn.innerHTML = originalText;
                                submitBtn.disabled = false;
                            });
                    });
                });
            </script>
        </body>

        </html>