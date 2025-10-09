<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Footer -->
        <footer class="footer">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-lg-4 col-md-6 mb-4">
                        <h5>HRMS</h5>
                        <p>Modern human resource management system that helps businesses optimize management processes
                            and effectively develop human resources.</p>
                        <div class="social-links">
                            <a href="#"><i class="fab fa-facebook-f"></i></a>
                            <a href="#"><i class="fab fa-twitter"></i></a>
                            <a href="#"><i class="fab fa-linkedin-in"></i></a>
                            <a href="#"><i class="fab fa-instagram"></i></a>
                        </div>
                    </div>
                    <div class="col-lg-4 col-md-6 mb-4 text-center">
                        <h5>Quick Links</h5>
                        <a href="${pageContext.request.contextPath}/">Home</a>
                        <a href="${pageContext.request.contextPath}/about">About</a>
                        <a href="${pageContext.request.contextPath}/contact">Contact</a>
                        <a href="${pageContext.request.contextPath}/faqs">FAQs</a>
                        <a href="#features">Features</a>
                    </div>
                    <div class="col-lg-4 col-md-6 mb-4 text-end">
                        <h5>Contact</h5>
                        <a href="mailto:support@hrms.com"><i class="fas fa-envelope me-2"></i>support@hrms.com</a>
                        <a href="tel:+84123456789"><i class="fas fa-phone me-2"></i>+84 123 456 789</a>
                        <a href="#"><i class="fas fa-map-marker-alt me-2"></i>Hanoi, Vietnam</a>
                    </div>
                </div>
                <div class="footer-bottom">
                    <p>&copy; 2025 HRMS. Developed by SWP391-SE1960NJ-FA25-Group4. All rights reserved.</p>
                </div>
            </div>
            <div class="col-lg-4 col-md-6 mb-4 text-center">
                <h5>Quick Links</h5>
                <a href="${pageContext.request.contextPath}/">Home</a>
                <a href="${pageContext.request.contextPath}/about">About</a>
                <a href="${pageContext.request.contextPath}/contact">Contact</a>
                <a href="${pageContext.request.contextPath}/faqs">FAQs</a>
                <a href="#features">Features</a>
            </div>
            <div class="col-lg-4 col-md-6 mb-4 text-end">
                <h5>Contact</h5>
                <a href="mailto:support@hrms.com"><i class="fas fa-envelope me-2"></i>support@hrms.com</a>
                <a href="tel:+84123456789"><i class="fas fa-phone me-2"></i>+84 123 456 789</a>
                <a href="#"><i class="fas fa-map-marker-alt me-2"></i>Hanoi, Vietnam</a>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2025 HRMS. Developed by SWP391-SE1960NJ-FA25-Group4. All rights reserved.</p>
        </div>
    </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<!-- AOS Animation JS -->
<script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>

<!-- Custom JavaScript -->
<script>
    // Initialize AOS
    AOS.init({
        duration: 1000,
        easing: 'ease-in-out',
        once: true
    });

    // Navbar scroll effect
    window.addEventListener('scroll', function () {
        const navbar = document.querySelector('.navbar');
        if (window.scrollY > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
    });

    // Counter animation
    function animateCounters() {
        const counters = document.querySelectorAll('.stat-number');
        counters.forEach(counter => {
            const target = parseInt(counter.getAttribute('data-count'));
            if (isNaN(target))
                return; // Skip if no valid number
            const originalText = counter.textContent;
            const suffix = originalText.replace(/^\d+/, ''); // Lấy phần ký tự sau số
            const increment = target / 200;
            let current = 0;
            const timer = setInterval(() => {
                current += increment;
                if (current >= target) {
                    counter.textContent = target + suffix;
                    clearInterval(timer);
                } else {
                    counter.textContent = Math.floor(current) + suffix;
                }
            }, 10);
        });
    }

    // Trigger counter animation when stats section is visible
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounters();
                observer.unobserve(entry.target);
            }
        });
    });

    const statsSection = document.querySelector('.stats-section');
    if (statsSection) {
        observer.observe(statsSection);
    }

    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
</script>